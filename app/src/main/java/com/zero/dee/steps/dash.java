package com.zero.dee.steps;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class dash extends AppCompatActivity {

    public static final String CHANNEL_ID ="exampleServiceChannel";
    DBHandler dbHandler;
    TextView tv_steps, allStepsView;
    String cache_steps;
    boolean isStart = false;

    String AllStepInit;



    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

//            printDataBase();
//            Toast.makeText(dash.this, "Check", Toast.LENGTH_SHORT).show();
            Cursor cursor = dbHandler.getData();
            if(cursor.getCount() == 0){
                Toast.makeText(dash.this, "No Data", Toast.LENGTH_SHORT).show();
            }else{
                while (cursor.moveToNext()){
//                    Toast.makeText(dash.this, "Data Get: " + cursor.getString(0), Toast.LENGTH_SHORT).show();
                    tv_steps.setText(cursor.getString(0));
                    allStepsView.setText(cursor.getString(1));
                    Toast.makeText(dash.this, "Data Check: " + cursor.getString(0), Toast.LENGTH_SHORT).show();

                    cache_steps = cursor.getString(0);
                }

            }
            handler.postDelayed(this, 5000);
        }
    };

//Start

    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

        //Setup SQLITE
        dbHandler = new DBHandler(this, null, null, 1);
        //Setup Notifications/Service
        createNotificationChannel();
        IntentFilter intentFilter = new IntentFilter();

        tv_steps = (TextView) findViewById(R.id.count);
        allStepsView = (TextView) findViewById(R.id.allView);

        //Get Data to populate all time steps
        Cursor cursor = dbHandler.getData();
        if(cursor.getCount() == 0){
            Toast.makeText(dash.this, "No Initial Data", Toast.LENGTH_SHORT).show();
            allStepsView.setText("0");

        }else{
            while (cursor.moveToNext()){
                Toast.makeText(dash.this, "Found Initial Data " + cursor.getString(1), Toast.LENGTH_SHORT).show();
                allStepsView.setText(cursor.getString(1));
                AllStepInit = cursor.getString(1);
                Toast.makeText(this, "Check Allstep :: " + AllStepInit, Toast.LENGTH_SHORT).show();


            }
        }

        tv_steps.setText("0");
    }


    @Override
    protected void onResume() {

        Toast.makeText(this, "RESUME BIYATCH", Toast.LENGTH_SHORT).show();

        if (isStart){
            //Output steps from last db call
            tv_steps.setText(this.cache_steps);

            //reset Hnadler for db calls
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 5000);
        }else {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 5000);
        }


        super.onResume();
    }

    @Override
    protected void onPause() {
        Toast.makeText(this, "PAUSE BIYATCH", Toast.LENGTH_SHORT).show();

        handler.removeCallbacks(runnable);
        super.onPause();
    }

    public void printDataBase(){
        Toast.makeText(this, "CALLED PRINT!!!!", Toast.LENGTH_SHORT).show();
        String dbString = dbHandler.printDB();
        tv_steps.setText(dbString);
    }


    public void startService(View v){
        Intent serviceIntent = new Intent(this, StepService.class);
        serviceIntent.putExtra("inputExtra", "0");

        serviceIntent.putExtra("startPoint", this.AllStepInit);
        dbHandler = new DBHandler(this, null, null, 1);

        startService(serviceIntent);
        handler.postDelayed(runnable, 5000);
        isStart = true;

    }




    public void stopService(View v){

        Intent serviceIntent = new Intent(this, StepService.class);
        stopService(serviceIntent);
        Cursor cursor = dbHandler.getData();
        if(cursor.getCount() == 0){
            Toast.makeText(dash.this, "No Initial Data", Toast.LENGTH_SHORT).show();
            allStepsView.setText("0");

        }else{
            while (cursor.moveToNext()){
                AllStepInit = cursor.getString(1);
                Toast.makeText(this, "Updating All Steps:: " + AllStepInit, Toast.LENGTH_SHORT).show();


            }
        }
        handler.removeCallbacks(runnable);
        isStart = false;
        cache_steps = "0";
        dbHandler.endSession();
        tv_steps.setText(cache_steps);



    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "eXAMPLE sERVICE cHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT

            );

            serviceChannel.setVibrationPattern(new long[]{ 0 });
            serviceChannel.enableVibration(true);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }



}




