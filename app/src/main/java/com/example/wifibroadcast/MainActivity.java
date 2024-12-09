package com.example.wifibroadcast;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements WiFiReceiver.WiFiStateListener {
    private WiFiReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiReceiver = new WiFiReceiver(this);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(wifiReceiver, filter);

        findViewById(R.id.btn_init).setOnClickListener(v -> controlDownload("INIT"));
        findViewById(R.id.btn_stop).setOnClickListener(v -> controlDownload("STOP"));
    }

    @Override
    public void onWiFiConnected() {
        findViewById(R.id.btn_init).setEnabled(true);
        //Toast.makeText(this, "WiFi conectado. Puedes iniciar la descarga.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWiFiDisconnected() {
        findViewById(R.id.btn_init).setEnabled(false);
        Toast.makeText(this, "WiFi desconectado. No puedes iniciar la descarga.", Toast.LENGTH_SHORT).show();
    }

    private void controlDownload(String action) {
        Intent intent = new Intent(this, DownloadService.class);
        intent.setAction(action);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }
}