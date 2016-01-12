package co.familyplay.androidprototype.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/*This class is used to make displaying message more simple
All methods are static so no need to instantiate.*/

public class Msg {

    // Show toast in a much simpler way
    // Msg.Toast("Hello world", context);
	public static void Toast(String message, Context c) {
		Toast.makeText(c, message, Toast.LENGTH_LONG).show();
	}

    //Print message in the console
    // Msg.Print("Hellow");
    public static void Print(String message){
        System.out.println(message);
    }

    // Display an alertdialog
    // Msg.AlertDialog(context, "Title", "message")
    public static void AlertDialog(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
	
}
