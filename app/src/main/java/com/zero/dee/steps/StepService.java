package com.zero.dee.steps;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.nio.channels.Channel;
import java.util.Calendar;

import static com.zero.dee.steps.dash.CHANNEL_ID;

public class StepService extends Service implements SensorEventListener {
    SensorManager sensorManager;
    Sensor countSensor;

    boolean running = false;
    boolean isInitilise = true;
    float initialValue = 0;
    float sessionValue = 0;
    Calendar calendar = Calendar.getInstance();

    boolean isStart = true;

    Data data;

    class steps_cache{
        float monday_session = 0;

        float tuesday_session = 0;

        float wednesday_session =0;

        float thursday_session =0;

        float friday_session = 0;

        float saturday_session = 0;

        float sunday_session =0;

        float allSteps = 0;

        float previous_steps = 0;
    }

    Notification notification;

    StepService.steps_cache sCache = new StepService.steps_cache();

    DBHandler dbHandler;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            stepData();
            handler.postDelayed(this, 5000);
        }
    };


    private static final int notif_id=1;

    @Override
    public void onDestroy() {
        sCache.monday_session = 0;
        sCache.tuesday_session = 0;
        sCache.wednesday_session = 0;
        sCache.thursday_session = 0;
        sCache.friday_session = 0;
        sCache.saturday_session = 0;
        sCache.sunday_session = 0;
        sensorManager.unregisterListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER));
        Toast.makeText(this, "Refreshing Cache ", Toast.LENGTH_SHORT).show();
        handler.removeCallbacks(runnable);

        super.onDestroy();
    }

    @Override
    public void onCreate() {

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        dbHandler = new DBHandler(this, null, null, 1);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        String initAll = intent.getStringExtra("startPoint");

//        Toast.makeText(this, "pre yehahahaha! " + initAll, Toast.LENGTH_SHORT).show();
        //Get Starting point for all steps
        this.sCache.allSteps = Float.valueOf(initAll);
        Toast.makeText(this, "pre yehahahaha gOT aLL STEPS! " + this.sCache.allSteps, Toast.LENGTH_SHORT).show();
        if (countSensor  != null &&  isStart){
            Toast.makeText(this, "yehahahaha!", Toast.LENGTH_SHORT).show();

            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
            isStart = false;
            running = true;
        }
        else{
            Toast.makeText(this, "Sensor not found or already started!", Toast.LENGTH_SHORT).show();
        }

        Intent notificationIntent = new Intent(this, dash.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Mech Counter")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] { 0L })
                .setColorized(true)
                .build();

        startForeground(notif_id, notification);



        handler.postDelayed(runnable, 5000);

        Toast.makeText(this, "BOOL init " + isInitilise, Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    public void newNotification(String newNoti){

        Intent notificationIntent = new Intent(this, dash.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Mech Counter")
                .setContentText(newNoti)
                .setSmallIcon(R.drawable.ic_android)
                .setVibrate(new long[] { 0L })
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notif_id, notification);
    }





    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_android)
            .setContentTitle("Mech Counter")
            .setContentText("teehee")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    @Override
    public void onSensorChanged(SensorEvent event) {
//        Toast.makeText(this, "OUTER!", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Service C " + sCache.allSteps , Toast.LENGTH_SHORT).show();

        if (running){
            if (isInitilise){
                initialValue =  Float.valueOf(event.values[0]);
                Toast.makeText(this, String.valueOf(initialValue), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "--Setting Initial Val--  -> " + initialValue, Toast.LENGTH_SHORT).show();

                isInitilise = false;
            }

            float stepCalculated = Float.valueOf(event.values[0]) - initialValue;
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            sCache.allSteps += (stepCalculated - sCache.previous_steps);
//            Toast.makeText(this, "Service C2 " + stepCalculated + " - " + sCache.previous_steps , Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "Service C3 " + sCache.allSteps, Toast.LENGTH_SHORT).show();

            switch (day) {
                case Calendar.MONDAY:
                    sCache.monday_session = stepCalculated;//adds up total steps

                    sCache.previous_steps = stepCalculated;
                    newNotification(String.valueOf(stepCalculated));

//                    Toast.makeText(this, "val " + String.valueOf(stepCalculated) , Toast.LENGTH_SHORT).show();

                    break;
                case Calendar.TUESDAY:
                    sCache.tuesday_session = stepCalculated;

                    newNotification(String.valueOf(stepCalculated));
//                    Toast.makeText(this, "val " + String.valueOf(stepCalculated) , Toast.LENGTH_SHORT).show();

                    break;
                case Calendar.WEDNESDAY:
                    Toast.makeText(this, "Steps Calc -> " + stepCalculated, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(this, "Math:: " + Float.valueOf(event.values[0]) + " - " + initialValue, Toast.LENGTH_SHORT).show();

                    sCache.wednesday_session = stepCalculated;
                    newNotification(String.valueOf(stepCalculated));
//                    Toast.makeText(this, "val " + String.valueOf(stepCalculated) , Toast.LENGTH_SHORT).show();

                    break;
                case Calendar.THURSDAY:
                    sCache.thursday_session = stepCalculated;

                    newNotification(String.valueOf(stepCalculated));
//                    Toast.makeText(this, "val " + String.valueOf(stepCalculated) , Toast.LENGTH_SHORT).show();

                    break;
                case Calendar.FRIDAY:
                    sCache.friday_session = stepCalculated;

                    newNotification(String.valueOf(stepCalculated));
//                    Toast.makeText(this, "val " + String.valueOf(stepCalculated) , Toast.LENGTH_SHORT).show();

                    break;
                case Calendar.SATURDAY:
                    sCache.saturday_session = stepCalculated;

                    newNotification(String.valueOf(stepCalculated));
//                    Toast.makeText(this, "val " + String.valueOf(stepCalculated) , Toast.LENGTH_SHORT).show();
                    break;
                case Calendar.SUNDAY:
                    sCache.sunday_session = stepCalculated;

                    newNotification(String.valueOf(stepCalculated));
//                    Toast.makeText(this, "val " + String.valueOf(stepCalculated) , Toast.LENGTH_SHORT).show();

                    break;
            }

            sCache.previous_steps = stepCalculated;

        }
    }

    public void stepData(){
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        Data dataOut = null;
        switch (day) {
            case Calendar.MONDAY:
                dataOut = new Data(String.valueOf(this.sCache.allSteps), String.valueOf(this.sCache.monday_session));

                break;
            case Calendar.TUESDAY:
                dataOut = new Data(String.valueOf(this.sCache.allSteps), String.valueOf(this.sCache.tuesday_session));

                break;
            case Calendar.WEDNESDAY:
                dataOut = new Data(String.valueOf(this.sCache.allSteps), String.valueOf(this.sCache.wednesday_session));
//                Toast.makeText(this, "Service AS " + dataOut.get_Allsteps() , Toast.LENGTH_SHORT).show();

                break;
            case Calendar.THURSDAY:
                dataOut = new Data(String.valueOf(this.sCache.allSteps), String.valueOf(this.sCache.thursday_session));

                break;
            case Calendar.FRIDAY:
                dataOut = new Data(String.valueOf(this.sCache.allSteps), String.valueOf(this.sCache.friday_session));

                break;
            case Calendar.SATURDAY:
                dataOut = new Data(String.valueOf(this.sCache.allSteps), String.valueOf(this.sCache.saturday_session));
                break;
            case Calendar.SUNDAY:

                dataOut = new Data(String.valueOf(this.sCache.allSteps), String.valueOf(this.sCache.sunday_session));
                break;
        }

                        Toast.makeText(this, "Service AS " + dataOut.get_Allsteps() , Toast.LENGTH_SHORT).show();

        String errMsg = dbHandler.updateData(dataOut);



    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}




