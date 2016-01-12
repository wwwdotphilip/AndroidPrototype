package co.familyplay.androidprototype.object;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;

/*
This class extends the ParseObject class. It's main purpose is to ack as intermediate for the UserPhoto class
in Parse server.

Visit the offical documentation of parse for more details.
https://www.parse.com/docs/android/guide
 */

@ParseClassName("UserPhoto")
public class UserPhoto extends ParseObject {

    public Bitmap getPhoto(){
        Bitmap bmp = null;
        byte[] data;
        try {
            data = getParseFile("photo").getData();
            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public void setPhoto(Bitmap photo){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 60, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile file = new ParseFile("photo.png", byteArray);
        put("photo", file);
    }

    public String getUserId(){
        return getString("userId");
    }

    public void setUserId(String userId){
        put("userId", userId);
    }
}
