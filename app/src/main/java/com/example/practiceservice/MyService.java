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

import java.util.Random;


/**
 * The MyService class is the background service that plays alarm
 */


public class MyService extends Service {

    private static final String TAG = MyService.class.getSimpleName();


    MediaPlayer mediaPlayer;
    private boolean mIsRunning = false;
    private boolean mIsRandomGeneratorOn;

    private int mRandomNumber;
    private final int MIN = 0;
    private final int MAX = 100;


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

//        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
//        mediaPlayer.setLooping(true);
//        mediaPlayer.start();
//        mIsRunning = true;

        doBackgroundWork();

        //Start the jobScheduler
        startJob();

        Toast.makeText(this, "Running", Toast.LENGTH_LONG).show();

        //If the Foreground service is running then stop it
        if (isMyServiceRunning(ForegroundService.class)) {
            foregroundService.stopService();
        }

        return START_STICKY;
    }

    private void doBackgroundWork() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                mIsRandomGeneratorOn = true;

                startRandomNumberGenerator();
            }
        }).start();
    }


    private void startRandomNumberGenerator() {
        while (mIsRandomGeneratorOn) {
            try {
                Thread.sleep(1000);
                if (mIsRandomGeneratorOn) {
                    mRandomNumber = new Random().nextInt(MAX) + MIN;
                    Log.i(TAG, "Thread id: " + Thread.currentThread().getId() + ", Random Number: " + mRandomNumber);
                }
            } catch (InterruptedException e) {
                Log.i(TAG, "Thread Interrupted");
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mIsRunning = false;
       // mediaPlayer.stop();
        Log.d(TAG, "Service Stopped");
        Toast.makeText(this, "Stopped", Toast.LENGTH_LONG).show();


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


    //Method to start the jobScheduler
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
