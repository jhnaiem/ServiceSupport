package com.example.practiceservice;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.StandardOpenOption;

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


    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void start(View view) {

        startService(new Intent(this, MyService.class));


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Subscribe
    public void onEvent( LogEvent log )
    {
        //you can do whatever you want releted with UI
        Log.i(TAG,log.getLogMessage());
        //Toast.makeText(this, log.getLogMessage(), Toast.LENGTH_LONG).show();
        writeLOGtoStorage(log.getLogMessage(),this,"savedLogs.txt");

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String writeLOGtoStorage(String logMessage, Context context, String filename) {
        File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), filename);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(dir,true);
            fileOutputStream.write(logMessage.getBytes());
            fileOutputStream.close();
            return dir.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static boolean checkFileExist(File dir) {


        boolean exists = dir.exists();

        return exists;


    }

    private void stopJob() {
        jobScheduler.cancel(101);
    }
}