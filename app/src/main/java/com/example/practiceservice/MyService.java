package com.example.practiceservice;

import android.app.ActivityManager;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

public class MyService extends Service {

    private static final String TAG = MyService.class.getSimpleName();


    MediaPlayer mediaPlayer;
    private boolean mIsRunning = false;

    private ForegroundService foregroundService = new ForegroundService();
    JobScheduler jobScheduler;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        Toast.makeText(this, "Created", Toast.LENGTH_LONG).show();
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);


        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        mIsRunning = true;

        startJob();

        Toast.makeText(this, "Running", Toast.LENGTH_LONG).show();

        if (isMyServiceRunning(ForegroundService.class)) {
            foregroundService.stopService();

        }
        //Add logic to kill foreground
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mIsRunning = false;
        mediaPlayer.stop();
        Log.d(TAG, "Player Stopped");
        Toast.makeText(this, "Stopped", Toast.LENGTH_LONG).show();


    }

    public boolean ismIsRunning() {
        return mIsRunning;
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startJob() {

        ComponentName componentName = new ComponentName(this, HandlerJobService.class);

        JobInfo jobInfo = new JobInfo.Builder(101, componentName)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .setRequiresCharging(false)
                .build();


        if (jobScheduler.schedule(jobInfo) == JobScheduler.RESULT_SUCCESS) {
            Log.i(TAG, "MainActivity thread id: " + Thread.currentThread().getId() + ", job successfully scheduled");

        } else {
            Log.i(TAG, "MainActivity thread id: " + Thread.currentThread().getId() + ", job could not be scheduled");

        }

    }

}
