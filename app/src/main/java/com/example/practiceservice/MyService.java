package com.example.practiceservice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyService extends Service {

    private static final String TAG = MyService.class.getSimpleName();

    MediaPlayer mediaPlayer;
    private boolean mIsRunning = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        Toast.makeText(this,"Created",Toast.LENGTH_LONG).show();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        mIsRunning = true;

        Toast.makeText(this,"Running",Toast.LENGTH_LONG).show();

        //Add logic to kill foreground
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mIsRunning = false;
        mediaPlayer.stop();
        Log.d(TAG,"Player Stopped");
        Toast.makeText(this,"Stopped",Toast.LENGTH_LONG).show();



    }

    public boolean ismIsRunning() {
        return mIsRunning;
    }
}
