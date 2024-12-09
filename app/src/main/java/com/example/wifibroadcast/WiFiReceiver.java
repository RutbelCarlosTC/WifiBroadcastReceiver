package com.example.wifibroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.widget.Toast;


public class WiFiReceiver extends BroadcastReceiver {
     public interface WiFiStateListener {
        void onWiFiConnected();
        void onWiFiDisconnected();
    }

    private final WiFiStateListener listener;

    public WiFiReceiver(WiFiStateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            NetworkCapabilities networkCapabilities =
                    connectivityManager.getNetworkCapabilities(activeNetwork);

            boolean isWiFiConnected = networkCapabilities != null &&
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);

            if (isWiFiConnected) {
                Toast.makeText(context, "Wifi encendido, iniciando descarga", Toast.LENGTH_SHORT).show();
                listener.onWiFiConnected();
                Intent serviceIntent = new Intent(context, DownloadService.class);
                serviceIntent.setAction("INIT");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startService(serviceIntent);
                }
            } else {
                Toast.makeText(context, "Wifi apagado, cancelando descarga", Toast.LENGTH_SHORT).show();
                listener.onWiFiDisconnected();
                Intent serviceIntent = new Intent(context, DownloadService.class);
                serviceIntent.setAction("STOP");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startService(serviceIntent);
                }
            }
        }
    }
}