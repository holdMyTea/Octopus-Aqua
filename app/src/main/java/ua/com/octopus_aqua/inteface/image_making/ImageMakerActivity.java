package ua.com.octopus_aqua.inteface.image_making;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ua.com.octopus_aqua.MainActivity;
import ua.com.octopus_aqua.files.ImageFilesManager;
import ua.com.octopus_aqua.inteface.NewPlantActivity;
import ua.com.octopus_aqua.inteface.R;

public class ImageMakerActivity extends AppCompatActivity {

    /*
    kinda doubt should i made it as a separate activity
    but as a connection between the activities of image making and activity it was started from
    work nice, probably
     */

    private final int REQUEST_LOAD_IMAGE = 1;
    private final int REQUEST_TAKE_PHOTO = 0;
    private final int REQUEST_DEFAULT_IMAGE = 2;    //was -1, but didn't work, 'cause RESULT_OK also == -1, prbbly

    private final int REQUEST_CROP_IMAGE = 3;

    private String photoFilePath;       // var holding the path to file img are/should be written to

    private final int maxX = 576;       // the X size of img it will be resized to, Y is calculated
    // in respect to proportions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // actually, the activity itself is not to be shown
        // it just serves as an launcher for the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select image");

        String[] buttons = {"Take photo", "Choose existing", "Choose default"};

        builder.setItems(buttons, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                switch (which) {

                    case 0:
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        try {
                            File file = ImageFilesManager.createImageFile();

                            photoFilePath = file.getAbsolutePath();

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                        break;

                    case 1:
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_LOAD_IMAGE);
                        break;

                    case 2:
                        intent = new Intent(getApplication(), DefaultImageActivity.class);
                        startActivityForResult(intent, REQUEST_DEFAULT_IMAGE);
                        break;

                }
            }
        });

        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_LOAD_IMAGE) {
                //after image is picked, it is resized and saved into folder
                try {
                    InputStream inputStream = ImageMakerActivity.this.getContentResolver().openInputStream(data.getData());

                    Bitmap bitmap = getBitmap(inputStream);

                    endActivity(
                            bitmap,
                            ImageFilesManager.createImageFile()
                    );

                } catch (FileNotFoundException finuex) {
                    Log.d("MY_TAG", "no file found(((");
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }

            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                //after photo is taken it is saved into folder
                Log.d("MY_TAG", "photoFilePath: " + photoFilePath);
                Bitmap bitmap = getBitmap(photoFilePath);

                endActivity(bitmap, new File(photoFilePath));

            } else if (requestCode == REQUEST_DEFAULT_IMAGE) {
                //after default image is picked its id is passed to intent
                String picturePath = data.getStringExtra(DefaultImageActivity.RES);

                Intent intent = new Intent();
                intent.putExtra(NewPlantActivity.PICTURE_PATH, picturePath);
                setResult(RESULT_OK, intent);
                finish();
            }

        } else {
            Log.d("MY_TAG", "RESULT NOT OK");
            finish();
        }
    }


    public void endActivity(Bitmap bitmap, File file) {
        //just packed all stuff into function, as it was called in two cases out of 3
        String filePath = file.getAbsolutePath();

        Log.d("MY_TAG", "previous width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());
        bitmap = compressBitmap(bitmap, maxX);
        Log.d("MY_TAG", "changed width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());
        ImageFilesManager.writeBitmapToFile(bitmap, filePath);

        Intent intent = new Intent();
        intent.putExtra(NewPlantActivity.PICTURE_PATH, filePath);
        setResult(RESULT_OK, intent);
        finish();
    }

    private Bitmap compressBitmap(Bitmap bitmap, int maxXSize) {
        //compresses given bitmap to a given maxX size, proportions are kept
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxXSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxXSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }


    private Bitmap getBitmap(InputStream instream) {
        //decodes bitmap from instream, handles OutOfMemoryError, but quality can be damaged
        int sampleSize = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        while (true) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(instream, null, options);
                return bitmap;
            } catch (OutOfMemoryError oute) {
                sampleSize *= 2;
                options.inSampleSize = sampleSize;
                Log.d("MY_TAG", "outta memory, new sampleSize: " + sampleSize);
            } catch (NullPointerException nullex) {
                //for christmas miracle cases
                Log.d("MY_TAG", "nulled**********************************************");
            }
        }
    }

    public static Bitmap getBitmap(String pathOrDrawable) {
        //just identifies, whether the given string refers to RES or actual path
        // and returns either decoded Bitmap or default Null Bitmap otherwise
        Context context = MainActivity.getAppContext();
        try {
            if (pathOrDrawable.charAt(0) != '*') {
                //all ids is stored in int, while filepathes starts with '/'
                File file = new File(pathOrDrawable);
                if (file.exists()) {
                    //decodes bitmap from path, handles OutOfMemoryError, but quality can be damaged
                    int sampleSize = 1;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    options.inSampleSize = sampleSize;
                    while (true) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile(pathOrDrawable, options);
                            return bitmap;
                        } catch (OutOfMemoryError oute) {
                            sampleSize *= 2;
                            options.inSampleSize = sampleSize;
                            Log.d("MY_TAG", "outta memory, new sampleSize: " + sampleSize);
                        } catch (NullPointerException nullex) {
                            Log.d("MY_TAG", "nulled");
                        }
                    }
                } else {
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.null_img);
                }
            } else {
                switch (pathOrDrawable){
                    case DefaultImageActivity.NULL_PIC:
                        return BitmapFactory.decodeResource(context.getResources(), R.drawable.null_img);
                    case DefaultImageActivity.DEF_PIC1:
                        return BitmapFactory.decodeResource(context.getResources(), R.drawable.def1);
                    case DefaultImageActivity.DEF_PIC2:
                        return BitmapFactory.decodeResource(context.getResources(), R.drawable.def2);
                    case DefaultImageActivity.DEF_PIC3:
                        return BitmapFactory.decodeResource(context.getResources(), R.drawable.def3);
                    default:
                        return BitmapFactory.decodeResource(context.getResources(), R.drawable.null_img);
                }
            }
        } catch (NullPointerException nullEx) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.null_img);
        }
    }
}
