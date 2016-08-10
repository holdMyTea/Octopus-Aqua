package ua.com.octopus_aqua.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import ua.com.octopus_aqua.MainActivity;

public class RequestQueueSingleton {
    private static RequestQueueSingleton instance;
    private static Context context;

    public static final String URL = "http://192.168.0.30:8009";
    //public static final String URL = "http://192.168.43.65:8009/Servlets";


    public static final String GET_COUNT = URL + "/getCount";
    public static final String GET_ROW = URL + "/getRow?id=";
    public static final String GET_PIC = URL + "/getPic?id=";
    public static final String ADD_PLANT = URL + "/addRow";
    public static final String EDIT_PLANT = URL + "/editRow";
    public static final String DELETE_PLANT = URL + "/deleteRow";
    public static final String GET_PLANTS = URL + "/getPlants";

    private RequestQueue queue;
    private ArrayList<String> requestList;

    private final int TIMEOUT = 1500;

    private RequestQueueSingleton(){
        context = MainActivity.getAppContext();
        queue = Volley.newRequestQueue(context);
        requestList = new ArrayList<>();
    }

    public static RequestQueueSingleton getInstance(){
        if(instance == null){
            instance = new RequestQueueSingleton();
        }
        return instance;
    }

    public RequestQueue getQueue(){
        return queue;
    }

    public boolean addRequest(Request request){
        //if(isOnline()){
            //request.setTag((String)request.getTag()+"*"+System.currentTimeMillis());
            requestList.add((String)request.getTag());
            /*request.setRetryPolicy(
                    new DefaultRetryPolicy(TIMEOUT, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            );*/
            queue.add(request);
            return true;
        /*} else{
            return false;
        }*/
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void cleanQueue(String activityTAG){
        queue.cancelAll(activityTAG);
    }

}
