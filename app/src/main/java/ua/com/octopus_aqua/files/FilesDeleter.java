package ua.com.octopus_aqua.files;

import android.util.Log;

import java.io.File;

public class FilesDeleter implements Runnable{
    String[] requiredFiles;
    String dirPath;

    FilesDeleter(String[] requiredFiles, String dirPath){
        this.requiredFiles = requiredFiles;
        this.dirPath = dirPath;
    }

    @Override
    public void run() {
        File dir = new File(dirPath);
        if(dir.exists() && dir.isDirectory()){

            File[] files = dir.listFiles();

            boolean toDelete = true;

            for(int i=0;i<files.length;i++){

                toDelete = true;

                for(int j=0;j< requiredFiles.length;j++){

                    if(files[i].getAbsolutePath().equals(requiredFiles[j])){
                        toDelete = false;
                        break;
                    }

                }

                if(toDelete){
                    files[i].delete();
                    Log.d("MY_TAG", "File " + files[i].getAbsolutePath()+" was deleted");
                }
            }

            Log.d("MY_TAG", "Deletion completed");
        } else{
            Log.d("MY_TAG", "Bad directory path: "+dirPath);
        }
    }
}
