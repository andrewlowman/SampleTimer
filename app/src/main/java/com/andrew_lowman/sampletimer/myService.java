package com.andrew_lowman.sampletimer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class myService extends Service {

    private static final String CHANNEL_ID = "NotificationChannelID";

    private CountDownTimer ct;
    private long millis;
    private int timeRemaining = 10000;
    private final IBinder mBinder = new LocalBinder();
    private long milliseconds;

    private long startTime;
    private long updateTime;
    private long currentTime;
    final Handler mainHandler = new Handler();

    public class LocalBinder extends Binder {
        myService getService() {
            return myService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CountDownTimer timer = new CountDownTimer(10000,1) {
            @Override
            public void onTick(long millisUntilFinished) {
                millis = millisUntilFinished;
                Intent intenLocal = new Intent();
                intenLocal.setAction("Counter");
                intenLocal.putExtra("timeRemaining",millis);
                sendBroadcast(intenLocal);
                NotificationUpdate(millis);
            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();

        /*final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Intent intent1local = new Intent();
                intent1local.setAction("Counter");
                timeRemaining--;
                if (timeRemaining<= 0){
                    timer.cancel();
                }
                intent1local.putExtra("timeRemaining", timeRemaining);
                sendBroadcast(intent1local);
            }
        }, 0,1000);*/
        return super.onStartCommand(intent, flags, startId);
    }

    public void NotificationUpdate(long timeLeft){
        try{
            Intent notificationIntent = new Intent(this,MainActivity.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_IMMUTABLE);
            final Notification[] notifications = {new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setContentTitle("My Stopwatch")
                    .setContentText("Time Remaining : " + convert(updateTime))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build()};

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"My Counter Service", NotificationManager.IMPORTANCE_LOW);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            startForeground(1,notifications[0]);


        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public String convert(long time){
        int hours = (int) ((time / (1000*60*60)) % 24);
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int secs = (int) (time / 1000) % 60;
        //int mils = (int) time % 1000;
        /*int secs = (int) time / 1000;
        int minutes = secs / 60;
        secs = secs % 60;
        int mils = (int) time % 1000;*/

        if(hours==0){
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
        }else{
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
        }
    }
    public void start() {
        /*ct = new CountDownTimer(10000, 1) {

            @Override
            public void onTick(long millisUntilFinished) {
                milliseconds = millisUntilFinished;
                Intent intenLocal = new Intent();
                intenLocal.setAction("Counter");
                intenLocal.putExtra("timeRemaining",milliseconds);
                sendBroadcast(intenLocal);
                NotificationUpdate(milliseconds);
            }

            @Override
            public void onFinish() {

            }
        };

        ct.start();*/

        startTime = SystemClock.elapsedRealtime();
        mainHandler.postDelayed(mainRunnable,0);
        currentTime += milliseconds;
    }

    public void pause(){
        ct.cancel();
    }

    public void restart(long timer){
        ct = new CountDownTimer(timer, 1) {

            @Override
            public void onTick(long millisUntilFinished) {
                milliseconds = millisUntilFinished;
                Intent intenLocal = new Intent();
                intenLocal.setAction("Counter");
                intenLocal.putExtra("timeRemaining",milliseconds);
                sendBroadcast(intenLocal);
                NotificationUpdate(milliseconds);
            }

            @Override
            public void onFinish() {

            }
        };

        ct.start();
    }

    Runnable mainRunnable = new Runnable() {
        @Override
        public void run() {
            milliseconds = SystemClock.elapsedRealtime() - startTime;
            updateTime = milliseconds + currentTime;

            /*int secs = (int) updateTime / 1000;
            int minutes = secs / 60;
            secs = secs % 60;
            int mils = (int) updateTime % 1000;*/

            Intent intenLocal = new Intent();
            intenLocal.setAction("Counter");
            intenLocal.putExtra("timeRemaining",updateTime);
            sendBroadcast(intenLocal);
            NotificationUpdate(updateTime);

            mainHandler.postDelayed(this, 0);
        }
    };




    /*ct = new CountDownTimer(originalTime, 1) {

        @Override
        public void onTick(long millisUntilFinished) {
            milliseconds = millisUntilFinished;
                *//*int secs = (int) millisUntilFinished / 1000;
                int minutes = secs / 60;
                secs = secs % 60;
                int mils = (int) millisUntilFinished % 1000;

                String time = String.format(Locale.getDefault(), "%02d:%02d:%03d", minutes, secs, mils);*//*
            countdownTimerTextView.setText(convert(millisUntilFinished));
        }

        @Override
        public void onFinish() {
            countdownTimerTextView.setText("00:00:000");
            countdownTimerTextView.startAnimation(blinkingAnimation);
            mp.start();
        }
    };

        ct.start();*/
}
