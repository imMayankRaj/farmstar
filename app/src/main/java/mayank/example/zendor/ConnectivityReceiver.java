package mayank.example.zendor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import xendorp1.application_classes.AppController;

/**
 * Created by mayank on 1/24/2018.
 */

public class ConnectivityReceiver extends BroadcastReceiver {


    public ConnectivityReceiver() {
        super();
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        Toast.makeText(context, "Network state"+ isConnected, Toast.LENGTH_SHORT).show();

    }

}
