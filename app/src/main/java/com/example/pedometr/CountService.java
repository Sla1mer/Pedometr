package com.example.pedometr;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

public class CountService extends Service implements SensorEventListener{
    private static String LOG_TAG="CountService";
    private SensorManager sensorManager;
    private Sensor sensor;
    private float[] rotationMatrix; //матрица поворота
    private float[] accelerometer;  //данные с акселерометра
    private float[] orientation;
    private float[] geomagnetism;
    private int steps = 0;


    public CountService() {
    }
    public final IBinder binder = new LocalCountBinder();
    public class LocalCountBinder extends Binder{
        public CountService getService(){
            return CountService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_NORMAL );

        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        loadSensorData(event); // получаем данные с датчика
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometer,geomagnetism); //получаем матрицу поворота
        SensorManager.getOrientation(rotationMatrix, orientation);

        if(accelerometer[0]>=10&&accelerometer[0]<=20) {steps++;}

    }
    public Integer getSteps(){

        return steps;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE); //получаем объект менеджера датчиков
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI );
        rotationMatrix = new float[16];
        accelerometer = new float[3];
        geomagnetism = new float[3];
        orientation = new float[3];
    }
    private void loadSensorData(SensorEvent event) {
        final int type = event.sensor.getType(); //определяем тип датчика
        if (type == Sensor.TYPE_ACCELEROMETER) { //если акселерометр
            accelerometer = event.values.clone();
        }
        if (type == Sensor.TYPE_MAGNETIC_FIELD) { //если геомагнитный датчик
            geomagnetism = event.values.clone();
        }
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return  this.binder;
    }
}