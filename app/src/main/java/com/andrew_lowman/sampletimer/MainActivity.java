package com.andrew_lowman.sampletimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView ctDownText;
    private Button startButton;
    private myService mBoundService;
    private boolean mIsBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((myService.LocalBinder)service).getService();

            Toast.makeText(MainActivity.this,
                    "Making connection",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
            Toast.makeText(MainActivity.this,"Disconnected",Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService(){
        bindService(new Intent(MainActivity.this,myService.class),mConnection,Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService(){
        if(mIsBound){
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.FOREGROUND_SERVICE}, PackageManager.PERMISSION_GRANTED);

        ctDownText = findViewById(R.id.countdownTextView);
        startButton = findViewById(R.id.button);

        doBindService();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Counter");

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long time = intent.getLongExtra("timeRemaining",0);
                ctDownText.setText(convert(time));
                //System.out.println("Received!");
            }
        };

        registerReceiver(broadcastReceiver,intentFilter);

    }

    public void setStartButton(View view){
            mBoundService.start();
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

    public boolean foreRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(myService.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

}