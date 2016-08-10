package ua.com.octopus_aqua;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import ua.com.octopus_aqua.inteface.ListViewFragment;
import ua.com.octopus_aqua.inteface.R;
import ua.com.octopus_aqua.inteface.login.LoginFragment;
import ua.com.octopus_aqua.networking.RequestHandlerActivity;

public class MainActivity extends AppCompatActivity {

    LoginFragment loginFragment;
    ListViewFragment listViewFragment;

    Toolbar toolbar;

    //singleton of appContext for singletons
    private static Context appContext;

    /*
    private static String PHOTO_DIR;
    private String dirName = "/pictures/";
    */


    private static String PHOTO_DIR = Environment.getExternalStorageDirectory().getPath() + "/Octopus-Aqua/.pictures/photo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext = this.getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.login));
        setSupportActionBar(toolbar);

        loginFragment = new LoginFragment();
        listViewFragment = new ListViewFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, loginFragment).commit();

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                getResources().getString(R.string.notif_plant_water),true)){
            Log.d("MY_TAG", "Notification service started");
        } else{
            Log.d("MY_TAG", "Notification wasnt started");
        }

        new FragmentSwapper().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestHandlerActivity.UPDATE_DATA_BASE){
            if(resultCode == RESULT_OK){
                Log.d("MY_TAG", "DBUpdate returned OK");
            }else if(resultCode == RESULT_CANCELED){
                Log.d("MY_TAG", "DBUpdate returned CANCEL ((9(9((9");
            }
            toolbar.setTitle("Smth to input");
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(loginFragment);
            transaction.add(R.id.fragmentContainer, listViewFragment);
            transaction.commit();
        }
    }

    public static String getPhotoDir(){
        return PHOTO_DIR;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public static Context getAppContext(){
        return appContext;
    }

    // waiting for LoginFragment, to initiate fragment swap
    private class FragmentSwapper extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            while(true){
                //Log.d("MY_TAG", "MAIN ALWAYS ON!!!!!!!!!!!!!!!!!!!");
                if(loginFragment.isChange()){
                    Log.d("MY_TAG", "changing from login to list");
                    Intent intent = new Intent(MainActivity.this,RequestHandlerActivity.class);
                    intent.putExtra(RequestHandlerActivity.TAG_REQUEST, RequestHandlerActivity.UPDATE_DATA_BASE);
                    startActivityForResult(intent, RequestHandlerActivity.UPDATE_DATA_BASE);
                    /*synchronized (this) {
                        this.cancel(true);
                    }*/
                    return null;
                }
            }
        }
    }
}
