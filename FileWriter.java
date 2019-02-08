package com.myapp.thiranja.showwifipassword;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static android.content.ContentValues.TAG;

public class FileWriter {




    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public File getPublicStorageDir(String dir) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), dir);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }

    public boolean WriteToFile(String fileName, String fileContent){
        boolean isStorageReady = isExternalStorageWritable();
        if (isStorageReady) {
            File file = getPublicStorageDir("Show WiFi Password");
            try {
                FileOutputStream f = new FileOutputStream(new File(file,fileName));
                PrintWriter pw = new PrintWriter(f);
                pw.println(fileContent);
                pw.flush();
                pw.close();
                f.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(TAG, "******* File not found. Did you" +
                        " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
