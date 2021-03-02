package com.example.practiceservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    private boolean jobFlag = false;
    private MyService myService;
    JobScheduler jobScheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        myService = new MyService();


    }

    public void stop(View view) {
        //stopService(new Intent(this,MyService.class));
        stopJob();
    }

    public void start(View view) {

        startService(new Intent(this, MyService.class));

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


    private void stopJob() {
        jobScheduler.cancel(101);
    }
}