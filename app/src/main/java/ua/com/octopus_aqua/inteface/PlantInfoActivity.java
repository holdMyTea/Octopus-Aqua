package ua.com.octopus_aqua.inteface;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ua.com.octopus_aqua.MainActivity;
import ua.com.octopus_aqua.database.PlantBaseHandler;
import ua.com.octopus_aqua.database.PlantRow;
import ua.com.octopus_aqua.inteface.image_making.DefaultImageActivity;
import ua.com.octopus_aqua.inteface.image_making.ImageMakerActivity;
import ua.com.octopus_aqua.networking.RequestHandlerActivity;
import ua.com.octopus_aqua.networking.RequestQueueSingleton;
import ua.com.octopus_aqua.notification.NotificationMaker;
import ua.com.octopus_aqua.preferences.PreferencesActivity;

public class PlantInfoActivity extends AppCompatActivity {

    private EditText editInfoName;
    private EditText editInfoType;
    private EditText editInfoInfo;
    private TextView textInfoPath;

    private GroupSpinner spinnerGroup;

    private ImageView imageInfoPicture;

    private final int CHANGE_PICTURE = 34;  //requestCode for ImageMaker
    private final int DELETE_PLANT = 35;

    private int idInList;  //again, for imgFile to have id, as in listView

    private String picPath;

    private boolean flagChanged = false; //used to determine, whether were made any changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Info");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arr_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MY_TAG", "ActionBar onClicked");
                makeBackDialog().show();
            }
        });

        editInfoName = ((EditText) findViewById(R.id.textInfoName));
        editInfoType = ((EditText) findViewById(R.id.textInfoType));
        editInfoInfo = ((EditText) findViewById(R.id.textInfoInfo));
        textInfoPath = (TextView) findViewById(R.id.textInfoPath);

        spinnerGroup = new GroupSpinner(this,(Spinner) findViewById(R.id.spinnerInfoGroup));

        editInfoName.setEnabled(false);
        editInfoType.setEnabled(false);
        editInfoInfo.setEnabled(false);

        spinnerGroup.getSpinner().setEnabled(false);

        editInfoName.setOnEditorActionListener(new IMEOptionsApplier(editInfoName));
        editInfoType.setOnEditorActionListener(new IMEOptionsApplier(editInfoType));
        editInfoInfo.setOnEditorActionListener(new IMEOptionsApplier(editInfoInfo));

        Intent data = getIntent();

        idInList = (int) data.getLongExtra(ListViewFragment.ID, 0);

        Log.d("MY_TAG", "Got id from extra: " + idInList);

        PlantRow row = PlantBaseHandler.getInstance().getRow(idInList);

        picPath = row.getPic();

        Log.d("MY_TAG", "GOT into info: " + row.getName() + ", " + row.getType() + ", " + row.getInfo() + ", " + picPath);

        editInfoName.setText(row.getName());
        editInfoType.setText(row.getType());
        editInfoInfo.setText(row.getInfo());
        textInfoPath.setText(picPath);

        spinnerGroup.setSelection(row.getGroup());

        imageInfoPicture = (ImageView) findViewById(R.id.imageInfoPicture);
        imageInfoPicture.setImageBitmap(ImageMakerActivity.getBitmap(picPath));
        imageInfoPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagChanged = true;
                Intent intent = new Intent(PlantInfoActivity.this, ImageMakerActivity.class);
                startActivityForResult(intent, CHANGE_PICTURE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_plantinfo, menu);
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_info_delete) {

            //deletion confirmation

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Delete this item?");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String query = new PlantRow(
                            idInList,
                            editInfoName.getText().toString(),
                            editInfoInfo.getText().toString(),
                            editInfoType.getText().toString(),
                            DefaultImageActivity.NULL_PIC,
                            spinnerGroup.getSelection()
                    ).encodeJSON();
                    Log.d("MY_TAG", query + "!!!!!!!!!!!!!!!!!!!");
                    Intent intent = new Intent(PlantInfoActivity.this, RequestHandlerActivity.class);
                    intent.putExtra(RequestHandlerActivity.TAG_REQUEST, RequestHandlerActivity.REQUEST_UPLOAD_PLANT);
                    intent.putExtra(RequestHandlerActivity.URL, RequestQueueSingleton.DELETE_PLANT);
                    intent.putExtra(RequestHandlerActivity.QUERY, query);
                    startActivityForResult(intent, DELETE_PLANT);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.setCancelable(true);

            builder.show();
        } else if (item.getItemId() == R.id.menu_info_edit) {
            //enabling editTexts and adding saveButton
            editInfoName.setEnabled(true);
            editInfoType.setEnabled(true);
            editInfoInfo.setEnabled(true);
            spinnerGroup.getSpinner().setEnabled(true);
            addSaveButton();
            flagChanged = true;

        } else if (item.getItemId() == R.id.menu_options_options) {
            startActivity(new Intent(this, PreferencesActivity.class));
        } else if (item.getItemId() == R.id.menu_info_not) {
            NotificationMaker.getInstance().makePlantInfoNotification(idInList);
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //in order to finish activity, while finish() doesn't work in lifecycle methods
        //'cause it calls lifecycle methods BrokeBack
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(flagChanged) {
                makeBackDialog().show();
            } else{
                finishEdit();
            }
        }

        return false;
    }

    private void finishEdit() {
        if(flagChanged){
            Intent intent = new Intent(this, RequestHandlerActivity.class);

            intent.putExtra(RequestHandlerActivity.TAG_REQUEST, RequestHandlerActivity.REQUEST_UPLOAD_PLANT);
            intent.putExtra(RequestHandlerActivity.URL,RequestQueueSingleton.EDIT_PLANT);
            intent.putExtra(RequestHandlerActivity.QUERY,
                    new PlantRow(
                            idInList,
                            editInfoName.getText().toString(),
                            editInfoInfo.getText().toString(),
                            editInfoType.getText().toString(),
                            picPath,
                            spinnerGroup.getSelection()
                    ).encodeJSON());

            startActivityForResult(intent, RequestHandlerActivity.REQUEST_UPLOAD_PLANT);
        } else{
            setResult(RESULT_OK);
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHANGE_PICTURE && resultCode == RESULT_OK) {
            picPath = data.getStringExtra(NewPlantActivity.PICTURE_PATH);
            textInfoPath.setText(picPath);
            Log.d("MY_TAG", "Pic is in: " + picPath);
            imageInfoPicture.setImageBitmap(ImageMakerActivity.getBitmap(picPath));
            flagChanged = true;
        } else if (requestCode == RequestHandlerActivity.REQUEST_UPLOAD_PLANT) {
            Log.d("MY_TAG", "Upload in plantInfo ended");
            Toast.makeText(
                    MainActivity.getAppContext(),
                    "Changes saved",
                    Toast.LENGTH_SHORT
            ).show();
            flagChanged = false;
            finishEdit();
        } else if (requestCode == DELETE_PLANT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(
                        MainActivity.getAppContext(),
                        "Plant deleted",
                        Toast.LENGTH_SHORT
                ).show();
                flagChanged = false;
                finishEdit();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(
                        MainActivity.getAppContext(),
                        "Server unavailable",
                        Toast.LENGTH_SHORT
                ).show();
                setResult(RESULT_CANCELED);
                finish();
            }
        }

    }

    private void addSaveButton(){
        Button saveButton = new Button(this);

        saveButton.setText(R.string.info_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagChanged = true;
                finishEdit();
            }
        });

        RelativeLayout parent = (RelativeLayout) findViewById(R.id.plantInfoRoot);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                parent.getLayoutParams()
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.setMargins(20,20,20,20);
        //params.addRule(RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        parent.addView(saveButton,params);
    }

    private AlertDialog.Builder makeBackDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save changes?");

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishEdit();
            }
        });

        builder.setNegativeButton("Don't save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                flagChanged = false;
                finishEdit();
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setCancelable(true);

        return builder;
    }


    class IMEOptionsApplier implements TextView.OnEditorActionListener {

        EditText editText;

        IMEOptionsApplier(EditText editText) {
            this.editText = editText;
        }

        //by pressing DONE on soft keyboard the current editText is set disabled
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                return true;
            }
            return false;
        }
    }
}
