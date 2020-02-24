package com.example.pedometr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    private boolean binded=false;
    private String steps;
    private Thread thread;
    private TextView countSteps;
    private CountService countService;
    private boolean starting;

    ServiceConnection countserviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CountService.LocalCountBinder binder=(CountService.LocalCountBinder)service;
            countService=binder.getService();
            binded=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binded=false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countSteps = findViewById(R.id.countSteps);

        thread=new Thread (new AnotherRunnable());
        thread.start();
    }
    
    class AnotherRunnable implements Runnable{
        @Override
        public void run() {
            while(!thread.isInterrupted())
                try{
                    setCountSteps();
                } catch (Exception e) {
                    e.printStackTrace(); }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void setCountSteps(){
        steps = Integer.toString(countService.getSteps());
        if (!(countSteps.getText().equals(steps))){
            countSteps.setText(steps);}

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent=new Intent(this,CountService.class);
        startService(intent);
        this.bindService(intent,countserviceConnection,Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(binded){
            this.unbindService(countserviceConnection);
            binded=false;
        }
    }
}
