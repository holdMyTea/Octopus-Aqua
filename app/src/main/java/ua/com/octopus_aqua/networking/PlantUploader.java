package ua.com.octopus_aqua.networking;


import android.app.Activity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import ua.com.octopus_aqua.database.PlantRow;
import ua.com.octopus_aqua.files.ImageFilesManager;

class PlantUploader implements Runnable{

    public final String JSON_TAG = "json";
    public final String IMG_TAG = "img";
    public final String CONTROL_LENGTH_TAG = "len";

    private final String REQ_TAG = RequestHandlerActivity.REQ_TAG;

    private final long INTERVAL_WAIT = 250; //wait between checks, mills
    private final long TOTAL_WAIT_TIME = 3 * 1000; //total time given for a single thread execution, mills

    private PlantRow row;
    private String url;

    long timeStarted;
    Counter counter;

    RequestHandlerActivity requestHandlerActivity;

    PlantUploader(PlantRow row, String url,RequestHandlerActivity requestHandlerActivity) {
        if(url.equals(RequestQueueSingleton.DELETE_PLANT)){
            //preventing file upload, when deleting
            row.setPic("*");
        }

        this.row = row;
        this.url = url;
        this.requestHandlerActivity = requestHandlerActivity;
    }

    @Override
    public void run() {
        timeStarted = System.currentTimeMillis();
        String picPath = row.getPic();

        if (ImageFilesManager.isFile(picPath)) {
            Log.d("MY_TAG", "is file");
            counter = new Counter(1);

            String imgFile = ImageFilesManager.imgFileToByteArrayString(picPath);

            Map<String,String> params = new HashMap<>(3);
            params.put(JSON_TAG,row.encodeJSON());
            params.put(IMG_TAG,imgFile);
            params.put(CONTROL_LENGTH_TAG,Integer.toString(imgFile.length()));

            StringRequest request = makeStringPost(url, params, REQ_TAG);

            if (!RequestQueueSingleton.getInstance().addRequest(request)) {
                requestHandlerActivity.finishAll(Activity.RESULT_CANCELED);
            }
        } else {
            Log.d("MY_TAG", "not file");
            counter = new Counter(1);
            StringRequest request = makeStringPost(url, row.encodeJSON(), REQ_TAG);

            if (!RequestQueueSingleton.getInstance().addRequest(request)) {
                requestHandlerActivity.finishAll(Activity.RESULT_CANCELED);
            }
        }

        check();
    }

    public StringRequest makeStringPost(String url, String json, String tag){
        Map<String,String> params = new HashMap<>();
        params.put(JSON_TAG,json);

        return makeStringPost(url,params,tag);
    }

    public StringRequest makeStringPost(String url,final Map<String,String> params, String tag) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MY_TAG", "request successful");
                        counter.add(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MY_TAG", "request failed");
                        Log.d("MY_TAG", error.toString());
                        requestHandlerActivity.finishAll(Activity.RESULT_CANCELED);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Charset", Charset.defaultCharset().displayName());
                headers.put("Params-Count", Integer.toString(params.size()));
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        request.setTag(tag);
        return request;
    }


    private void check() {
        long pauseCount =
                TOTAL_WAIT_TIME%INTERVAL_WAIT==0
                        ? TOTAL_WAIT_TIME/INTERVAL_WAIT
                        : (TOTAL_WAIT_TIME/INTERVAL_WAIT) + 1;

        Thread thread = Thread.currentThread();

        for(int i=0;i<pauseCount;i++) {

            if(thread.isInterrupted()){
                return;
            }

            boolean finish = true;
            for (boolean b : counter.getArr()) {
                if (!b) {
                    finish = false;
                    break;
                }
            }

            if (!finish) {
                try {
                    synchronized (thread) {
                        thread.wait(INTERVAL_WAIT);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                requestHandlerActivity.finishAll(Activity.RESULT_OK);
            }
        }

        requestHandlerActivity.finishAll(Activity.RESULT_CANCELED);
    }

}
