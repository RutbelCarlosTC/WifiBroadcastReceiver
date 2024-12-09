package com.example.wifibroadcast;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class DownloadService extends Service {

    private static final String CHANNEL_ID = "DownloadChannel";
    private static final int NOTIFICATION_ID = 1;
    private volatile boolean isDownloading = false;
    private volatile boolean stopRequested = false;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if("INIT".equals(action)){
            if(!isDownloading){
                stopRequested = false;
                startForegroundService();
            }
        } else if ("STOP".equals(action)) {
            stopRequested = true;
        }

        return START_STICKY;
    }

    private void stopForegroundService() {
        stopRequested = true;
        stopForeground(true); // Detiene el servicio en primer plano
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID); // Cancela la notificación
        }
        stopSelf(); // Detiene el servicio
        Log.d("DownloadService", "Servicio detenido y notificación cancelada");
    }

    private void startForegroundService() {
        isDownloading = true;
        stopRequested = false; // Reinicia el estado
        int steps = 10;
        // Simular la descarga en un hilo separado
        new Thread(() -> {
            try {
                for (int i = 1; i <= steps; i++) {
                    if (stopRequested) {
                        Log.d("DownloadService", "Descarga detenida por solicitud del usuario");
                        stopForegroundService();
                        return;
                    }
                    int progress = (i * 100) / steps; // Calcular progreso en porcentaje
                    updateNotification(progress, i,steps );
                    Thread.sleep(1000); // Simula el tiempo de descarga
                }
                if (!stopRequested) {
                    completeDownload();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                isDownloading = false;
            }
        }).start();

    }
    private void updateNotification(int progress, int currentStep, int totalSteps) {
        String progressText = "Descargando datos... " + currentStep + "/" + totalSteps;
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Progreso de descarga")
                .setContentText(progressText)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setProgress(100, progress, false)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
    private void completeDownload() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Descarga completada") // Título obligatorio
                .setContentText("La descarga se completó exitosamente.") // Texto final
                .setSmallIcon(android.R.drawable.stat_sys_download_done) // Ícono obligatorio
                .setPriority(NotificationCompat.PRIORITY_LOW) // Nivel de prioridad
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
        //stopForegroundService();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Canal de Descarga",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}