package ro.vodafone.mcare.android.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

public class NetworkReceiver extends BroadcastReceiver {

    public static NetworkReceiver registerReceiver(AppCompatActivity appCompatActivity){
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        NetworkReceiver networkReceiver = new NetworkReceiver();
        appCompatActivity.registerReceiver(networkReceiver, filter);
        return networkReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        try {
            ((NetworkEventReceiver)context).onreceive(networkInfo);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    public interface NetworkEventReceiver{
        public void onreceive(NetworkInfo networkInfo);
    }

}