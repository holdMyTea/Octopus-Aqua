package ua.com.octopus_aqua.files;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ua.com.octopus_aqua.MainActivity;
import ua.com.octopus_aqua.inteface.image_making.ImageMakerActivity;

public class ImageFilesManager {

    public static String DEF_NAME = "img";
    public static String SUFFIX = "png";


    public static String imgFileToByteArrayString(String path){

        Bitmap bitmap = ImageMakerActivity.getBitmap(path);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
        byte[] bytes = out.toByteArray();

        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    public static String saveBase64asImg(String base64){
        File file;
        try{
            file = createImageFile();

            if(file != null){
                writeBitmapToFile(Base64toBitmap(base64),file.getAbsolutePath());
                return file.getAbsolutePath();
            } else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static File createImageFile() throws IOException {
        //creates file
        String dirPath = MainActivity.getPhotoDir();

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dirPath, DEF_NAME + "0." + SUFFIX);
        for (int i = 1; file.exists(); i++) {
            file = new File(dirPath, DEF_NAME + i + "." + SUFFIX);
        }

        file.createNewFile(); // no need to check return value, as existence of
        // such file is checked above

        return file;
    }

    public static Bitmap Base64toBitmap(String base64){
        byte[] bytes = Base64.decode(base64,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    public static void writeBitmapToFile(Bitmap bitmap, String path) {
        // writes given bitmap to file in path
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(path));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (FileNotFoundException fileEx) {
            fileEx.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }
        }
    }

    public static boolean isFile(String bitmapPath) {
        //checks whether the given bitmap refers to a file or resource
        try {
            if (bitmapPath.charAt(0) != '*') {
                //all ids is stored in int, while filepathes starts with '/'
                return new File(bitmapPath).exists();
            }
            else{
                return false;
            }
        } catch (NullPointerException nullEx) {
            return false;
        }
    }


}


