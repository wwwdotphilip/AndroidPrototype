package co.familyplay.androidprototype.storage;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

//Use to store and fetch Images into the device
//To use this simply call
// Data.storeImage(Bitmap bitmap, String fileName, String FolderName, Context context);

public class Data {

    // this method stores the image in the device
    public static String storeImage(Bitmap bitmap, String fileName, String FolderName, Context context) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(FolderName, Context.MODE_PRIVATE);
        File path = new File(directory, fileName);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    // this method fetches the stored image from the device
    public static Bitmap getImage(String fileName, String folderName, Context context) {
        ContextWrapper cw = new ContextWrapper(
                context);
        File directory = cw.getDir(folderName,
                Context.MODE_PRIVATE);
        try {
            File f = new File(directory, fileName);
            return BitmapFactory
                    .decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
