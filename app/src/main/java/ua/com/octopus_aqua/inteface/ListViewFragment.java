package ua.com.octopus_aqua.inteface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import ua.com.octopus_aqua.MainActivity;
import ua.com.octopus_aqua.database.PlantBaseHandler;
import ua.com.octopus_aqua.inteface.PlantList.MainList;
import ua.com.octopus_aqua.networking.RequestHandlerActivity;
import ua.com.octopus_aqua.notification.NotificationMaker;
import ua.com.octopus_aqua.preferences.PreferencesActivity;

public class ListViewFragment extends Fragment {

    private MainList mainList;
    private ListView listView;

    private PlantBaseHandler dbHandler;
    //private CustomAdapter adapter;  //Adapter for ListView

    //requestCodes for startActivityForResult();
    public static final int ADD_PLANT = 1;
    public static int INFO_PLANT = 2;
    public static int UPDATE_BASE = 3;


    //tags for returned extras from other activities
    public static final String ID = "id";



    public ListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d("MY_TAG", "ListViewFragment created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentHolder = inflater.inflate(R.layout.fragment_listview, container, false);

        dbHandler = PlantBaseHandler.getInstance();

        listView = (ListView) fragmentHolder.findViewById(R.id.listView);
        mainList = new MainList(ListViewFragment.this);

        Log.d("MY_TAG", "ListViewFragment views created");

        return fragmentHolder;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_list, menu);
        inflater.inflate(R.menu.menu_options, menu);
        Log.d("MY_TAG", "ListViewFragment menued");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MY_TAG", "ListViewFragment menu itemed");

        if (item.getItemId() == R.id.menu_add_plant) {
            Intent intent = new Intent(getContext(), NewPlantActivity.class);
            startActivityForResult(intent, ADD_PLANT);
            return true;
        } else if (item.getItemId() == R.id.dropDB) {
            //TODO: prbbly delete db dropping button
            dbHandler.dropTable();
            mainList.updateList();
            return true;
        } else if (item.getItemId() == R.id.menu_notif) {
            //TODO: prbbly delete notifcation showing button
            Log.d("MY_TAG", "notify");
            NotificationMaker.getInstance().
                    makeNotification(new Intent(getContext(), MainActivity.class),
                            "Title", "Text", R.drawable.null_img, "TICKER!!1");
            return true;
        } else if (item.getItemId() == R.id.menu_options_options) {
            startActivity(new Intent(getActivity(), PreferencesActivity.class));
            return true;
        } else if (item.getItemId() == R.id.menu_updateDB) {
            Intent intent = new Intent(getContext(),RequestHandlerActivity.class);
            intent.putExtra(RequestHandlerActivity.TAG_REQUEST, RequestHandlerActivity.UPDATE_DATA_BASE);
            startActivityForResult(intent,UPDATE_BASE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == UPDATE_BASE){
            if(resultCode == Activity.RESULT_OK){
                Log.d("MY_TAG", "DBUpdate returned");
                mainList.updateList();
            } else if(resultCode == Activity.RESULT_CANCELED){
                Log.d("MY_TAG", "DBUpdate returned");
                Toast.makeText(
                        MainActivity.getAppContext(),
                        "Server unavailable",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else if(requestCode == ADD_PLANT){
            if(resultCode == Activity.RESULT_OK) {
                Log.d("MY_TAG", "NewPlant returned OK");
                Intent intent = new Intent(getContext(), RequestHandlerActivity.class);
                intent.putExtra(RequestHandlerActivity.TAG_REQUEST, RequestHandlerActivity.UPDATE_DATA_BASE);
                startActivityForResult(intent, UPDATE_BASE);
            } else if(resultCode == Activity.RESULT_CANCELED){
                Log.d("MY_TAG", "NewPlant returned BAD");
                Log.d("MY_TAG", "DBUpdate returned");
                Toast.makeText(
                        MainActivity.getAppContext(),
                        "Server unavailable",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else if(requestCode == INFO_PLANT){
            //TODO: prbbly make custom RESULT_CODES for readability
            if(resultCode == Activity.RESULT_OK){
                Log.d("MY_TAG", "Info plant returned with updates");
                Intent intent = new Intent(getContext(),RequestHandlerActivity.class);
                intent.putExtra(RequestHandlerActivity.TAG_REQUEST, RequestHandlerActivity.UPDATE_DATA_BASE);
                startActivityForResult(intent,UPDATE_BASE);
            } else if(resultCode == Activity.RESULT_CANCELED){
                Log.d("MY_TAG", "Info plant returned with error");
                Toast.makeText(
                        MainActivity.getAppContext(),
                        "Server unavailable",
                        Toast.LENGTH_SHORT
                ).show();
            } else if(resultCode == Activity.RESULT_FIRST_USER){
                Log.d("MY_TAG", "Info plant returned with no changes");
            }
        }

        /*
        //kinda doubt should it be runned here, but no exception detected for the moment
        Log.d("MY_TAG", "Deleting redundant files");
        try {
            new Thread(
                    new FilesDeleter(
                            dbHandler.getColumn(PlantBaseHandler.PIC_COLUMN),
                            MainActivity.getPhotoDir()
                    )
            ).start();
        } catch (CursorIndexOutOfBoundsException curseEx) {
            Log.d("MY_TAG", "Null db, null row, no deletion");
        }
        */
    }

    public ListView getListView() {
        return listView;
    }
}