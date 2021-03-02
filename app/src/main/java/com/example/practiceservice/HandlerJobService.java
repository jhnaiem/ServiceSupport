package com.example.practiceservice;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class HandlerJobService extends JobService {

    MyService myService = new MyService();
    private  Intent serviceIntent;





    @Override
    public boolean onStartJob(JobParameters params) {

        Log.i("Check", "Handler");

        if (!myService.ismIsRunning()){
            startForegroundService();
            //startService(new Intent(this,MyService.class));
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }


    public void startForegroundService(){
        serviceIntent=new Intent(getApplicationContext(),ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this,serviceIntent);

    }
}
