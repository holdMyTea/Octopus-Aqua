package ua.com.octopus_aqua.networking;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;

import ua.com.octopus_aqua.database.PlantBaseHandler;
import ua.com.octopus_aqua.database.PlantRow;
import ua.com.octopus_aqua.files.ImageFilesManager;
import ua.com.octopus_aqua.inteface.image_making.DefaultImageActivity;


class DataBaseUpdater implements Runnable{

    private final String REQ_TAG = RequestHandlerActivity.REQ_TAG;

    private final long INTERVAL_WAIT = 250; //wait between checks, mills
    private final long TOTAL_WAIT_TIME = 7 * 1000; //total time given for a single thread execution, mills

    //kinda buffer for rows to be inserted into db if all the requests shall be successful
    ArrayList<PlantRow> listRows;

    long timeStarted;
    Counter counter;

    RequestHandlerActivity requestHandlerActivity;

    DataBaseUpdater(RequestHandlerActivity requestHandlerActivity){
        this.requestHandlerActivity = requestHandlerActivity;
    }

    //sends /getCount request
    //then /getRows requests and waits for results
    //if there is a pic file in row, then sends /getPic
    @Override
    public void run() {
        timeStarted = System.currentTimeMillis();
        Log.d("MY_TAG", "Time start:"+timeStarted);

        counter = new Counter(1);
        Log.d("MY_TAG", "upThread started");
        StringRequest request = new StringRequest(Request.Method.GET, RequestQueueSingleton.GET_PLANTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MY_TAG", "/getPlant got:\n\r"+response);

                        String[] str = response.split("&");
                        PlantRow[] rows = new PlantRow[str.length];
                        for(int i = 0; i<str.length;i++){
                            rows[i] = new PlantRow(str[i]);
                        }

                        counter = new Counter(str.length);
                        listRows = new ArrayList<PlantRow>();

                        for(PlantRow row : rows){
                            if(DefaultImageActivity.isDefaultPic(row.getPic())){
                                listRows.add(row);
                                counter.add(true);
                            } else{
                                RequestQueueSingleton.getInstance().addRequest(makeGetPicRequest(row));
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MY_TAG", "Got /getPlants error");
                        requestHandlerActivity.finishAll(Activity.RESULT_CANCELED);
                    }
                }
        );
        RequestQueueSingleton.getInstance().addRequest(request);
        check();
    }

    private void check(){
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
                PlantBaseHandler db = PlantBaseHandler.getInstance();
                db.dropTable();
                for(PlantRow row : listRows){
                    db.insertRow(row);
                }
                requestHandlerActivity.finishAll(Activity.RESULT_OK);
            }
        }

        requestHandlerActivity.finishAll(Activity.RESULT_CANCELED);
    }


    private StringRequest makeGetPicRequest(final PlantRow row){
        String url = RequestQueueSingleton.GET_PIC+row.getId();

        Log.d("MY_TAG", "Making /getPic request: "+url);
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Log.d("MY_TAG", "/getPic got: "+response);
                        if(!response.isEmpty()){
                            String picPath = ImageFilesManager.saveBase64asImg(response);
                            Log.d("MY_TAG", "/getPic is saved to: "+picPath);
                            row.setPic(picPath);
                            Log.d("MY_TAG", "Row now: "+row.encodeJSON());
                            listRows.add(row);
                        }
                        counter.add(true);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MY_TAG", "/getPic error");
                        requestHandlerActivity.finishAll(Activity.RESULT_CANCELED);
                    }
                }
        );
        request.setTag(REQ_TAG);
        return request;
    }
}