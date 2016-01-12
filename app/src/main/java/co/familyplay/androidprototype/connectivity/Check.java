package co.familyplay.androidprototype.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*Check for internet connection.
*
* Simply call Check.hasNetworkConnection(context)
*
* */
public class Check {

    public static boolean hasNetworkConnection(Context context) {
        boolean connectedToWIFI = false;
        boolean connectedToMobileData = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    connectedToWIFI = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    connectedToMobileData = true;
        }
        return connectedToWIFI || connectedToMobileData;
    }
}
