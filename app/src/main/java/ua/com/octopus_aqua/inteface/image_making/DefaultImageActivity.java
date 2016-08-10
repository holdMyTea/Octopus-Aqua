package ua.com.octopus_aqua.inteface.image_making;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import ua.com.octopus_aqua.inteface.R;

public class DefaultImageActivity extends AppCompatActivity implements View.OnClickListener {

    //just shows the ImageViews, and returns the id of drawable

    public static final String NULL_PIC = "*";
    public static final String DEF_PIC1 = "*1";
    public static final String DEF_PIC2 = "*2";
    public static final String DEF_PIC3 = "*3";

    public final static String RES = "res";

    private final int[] drawables = {R.drawable.def1, R.drawable.def2, R.drawable.def3}; //array for default drawables
    ImageView[] imageDefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_image);

        //coulda woulda shoulda make this stuff autocreate drawable for each image
        imageDefs = new ImageView[]{
                (ImageView) findViewById(R.id.imgDef1),
                (ImageView) findViewById(R.id.imgDef2),
                (ImageView) findViewById(R.id.imgDef3)
        };

        //doing this stuff because haven't found correct way to get hardcoded drawable from XML
        //thought, it is even better not to hardcode drawables in XML
        //doing this stuff because hardcoding drawables is stupid
        for (int i = 0; i < imageDefs.length; i++) {
            imageDefs[i].setImageBitmap(BitmapFactory.decodeResource(this.getResources(),drawables[i]));
            imageDefs[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        String drawableID = "*1";   //default variant for default img
        for (int i = 0; i < imageDefs.length; i++) {
            if(v == imageDefs[i]){
                drawableID = "*"+Integer.valueOf(i+1).toString();
                Log.d("MY_TAG", "got deafult res: " + drawableID + "which is # " + i);
            }
        }
        Intent intent = new Intent();
        intent.putExtra(RES, drawableID);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static boolean isDefaultPic(String pic){
        return pic.equals(NULL_PIC) ? true :
                pic.equals(DEF_PIC1) ? true :
                        pic.equals(DEF_PIC2) ? true :
                                pic.equals(DEF_PIC3) ? true : false;
    }
}
