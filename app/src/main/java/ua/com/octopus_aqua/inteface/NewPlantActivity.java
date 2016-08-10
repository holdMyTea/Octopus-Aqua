package ua.com.octopus_aqua.inteface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import ua.com.octopus_aqua.database.PlantRow;
import ua.com.octopus_aqua.inteface.image_making.DefaultImageActivity;
import ua.com.octopus_aqua.inteface.image_making.ImageMakerActivity;
import ua.com.octopus_aqua.networking.RequestHandlerActivity;
import ua.com.octopus_aqua.networking.RequestQueueSingleton;

public class NewPlantActivity extends AppCompatActivity {

    private EditText editName, editType, editInfo;
    private GroupSpinner spinner;

    //TextView textImagePath;   //used for path to image (Cap)
    private Button buttonImage, buttonFinish;
    private ImageView imageNewPlant;

    private String picPath = DefaultImageActivity.NULL_PIC;

    private final int GET_PIC = 42; //requestCode for ImageMaker
    private final int REQUEST_ADD = 43; //requestCode for Updating

    public final static String PICTURE_PATH = "picturePath";    //tag for extra from intent


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        editName = (EditText) findViewById(R.id.editName);
        editType = (EditText) findViewById(R.id.editType);
        editInfo = (EditText) findViewById(R.id.editInfo);

        spinner = new GroupSpinner(this, (Spinner) findViewById(R.id.spinnerGroup));

        imageNewPlant = (ImageView) findViewById(R.id.imageNewPlant);

        buttonImage = (Button) findViewById(R.id.buttonImage);
        buttonFinish = (Button) findViewById(R.id.buttonFinish);

        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPlantActivity.this, ImageMakerActivity.class);
                startActivityForResult(intent, GET_PIC);
            }
        });

        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPlantActivity.this, RequestHandlerActivity.class);

                intent.putExtra(RequestHandlerActivity.TAG_REQUEST, RequestHandlerActivity.REQUEST_UPLOAD_PLANT);
                intent.putExtra(RequestHandlerActivity.URL, RequestQueueSingleton.ADD_PLANT);
                intent.putExtra(RequestHandlerActivity.QUERY,
                        new PlantRow(
                                1,
                                editName.getText().toString(),
                                editType.getText().toString(),
                                editInfo.getText().toString(),
                                picPath,
                                spinner.getSelection()
                        ).encodeJSON());
                startActivityForResult(intent, REQUEST_ADD);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_PIC) {
            try {
                picPath = data.getExtras().getString(PICTURE_PATH);
                imageNewPlant.setImageBitmap(ImageMakerActivity.getBitmap(picPath));
            } catch (NullPointerException nullEx) {
            }
        } else if (requestCode == REQUEST_ADD) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Server unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    private void doSpinner(){
        spinnerList = new ArrayList<>();
        spinnerList.add("New group");
        spinnerList.addAll(PlantBaseHandler.getInstance().groupsAsSet());
        spinnerList.add(PlantRow.NO_GROUP);

        spinnerGroup = (Spinner) findViewById(R.id.spinnerGroup);
        makeAdapter();

        spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(id == 0){
                    doNewGroup();
                } else {
                    group = spinnerList.get((int)id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerGroup.setSelection(spinnerList.indexOf(PlantRow.NO_GROUP));
    }

    private void doNewGroup(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.new_group);
        builder.setMessage(R.string.input_group);

        final EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        builder.setView(editText);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = editText.getText().toString();
                //absence of '!' took 3 hours
                if(! str.isEmpty()){
                    spinnerList.add(1,str);
                    makeAdapter();
                    spinnerGroup.setSelection(1);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                group = PlantRow.NO_GROUP;
                dialog.cancel();
            }
        });

        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                group = PlantRow.NO_GROUP;
            }
        });

        builder.show();
    }

    private void makeAdapter(){
        spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,spinnerList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(spinnerAdapter);
    }

    */
}
