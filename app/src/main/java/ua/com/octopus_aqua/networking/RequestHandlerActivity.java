package ua.com.octopus_aqua.networking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ua.com.octopus_aqua.database.PlantRow;


public class RequestHandlerActivity extends Activity {

    public static String URL = "url";
    public static String QUERY = "que";

    public static String TAG_REQUEST = "req";
    public static int REQUEST_UPLOAD_PLANT = 1;
    public static int UPDATE_DATA_BASE = 2;

    public static final String REQ_TAG = RequestHandlerActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MY_TAG", "UpdateActivity created!!!1");
        super.onCreate(savedInstanceState);
        Log.d("MY_TAG", getCallingActivity().toString() + "!!!!!");

        RequestQueueSingleton reqHandler = RequestQueueSingleton.getInstance();
        reqHandler.cleanQueue(REQ_TAG);

        Intent intent = getIntent();
        //if (reqHandler.isOnline()) {
            if (intent.getIntExtra(TAG_REQUEST, 0) == UPDATE_DATA_BASE) {
                new Thread(new DataBaseUpdater(this)).start();

            } else if (intent.getIntExtra(TAG_REQUEST, 0) == REQUEST_UPLOAD_PLANT) {
                new Thread(new PlantUploader(
                        new PlantRow(intent.getStringExtra(QUERY)
                        ),intent.getStringExtra(URL),this
                )).start();

            }
        /*} else {
            Log.d("MY_TAG", Boolean.toString(reqHandler.isOnline()));
            Toast.makeText(
                    MainActivity.getAppContext(),
                    "No Internet connection",
                    Toast.LENGTH_SHORT
            ).show();
            setResult(RESULT_CANCELED);
            finish();
        }*/
    }

    void finishAll(int result) {
        Thread.currentThread().interrupt();
        RequestQueueSingleton.getInstance().cleanQueue(REQ_TAG);
        setResult(result);
        Log.d("MY_TAG", "Finished ALL");
        finish();
    }

}