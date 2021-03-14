package com.example.practiceservice;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.greenrobot.eventbus.EventBus;

import java.io.CharArrayWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * The Foreground class is used to show notification and start the background service(MyService)
 */


public class ForegroundService extends Service {

    private static final String TAG = ForegroundService.class.getSimpleName();
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private StringBuilder stringBuilder = new StringBuilder();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String input = intent.getStringExtra("inputExtra");

        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground  Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);


        //Re Start the background service(MyService)
        startService(new Intent(this, MyService.class));


        stopForegroundService();
        doLogPassWork("Foreground Service is running");
        Log.i(TAG, "Foreground Service is running");


        return START_NOT_STICKY;
    }

    private void doLogPassWork(String msg) {

        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        stringBuilder.append(date + " " + msg + "\n");

        LogEvent logEvent = new LogEvent();
        logEvent.setLogMessage(stringBuilder.toString());

        EventBus.getDefault().post(logEvent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    //TO stop the foreground service
    public void stopForegroundService() {

        if (isMyServiceRunning(ForegroundService.class)){
            stopForeground(true);
            stopSelf();
        }



        Log.i(TAG,"Condition: " + isMyServiceRunning(ForegroundService.class));


    }

    //TO check if a service is running or not
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doLogPassWork(" Foreground service is Stopped");
        Log.i(TAG, "Foreground service is Destroyed");
    }
}
