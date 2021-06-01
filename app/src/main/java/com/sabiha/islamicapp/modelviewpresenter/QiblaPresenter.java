package com.sabiha.islamicapp.modelviewpresenter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.fragment.app.Fragment;

import com.sabiha.islamicapp.utils.CalculationForQiblaDistance;
import com.sabiha.islamicapp.utils.GpsTracker;

public class QiblaPresenter implements MvpPresenter.QiblaPresenter{

    private Context context;
    private SensorManager sensorManager;
    private Sensor mSensor;
    private GpsTracker gps;
    double latitude, longitude;

    int Distance;
    double Qlati = 21.42243;
    double Qlongi = 39.82624;
    public static double degree;

    private CalculationForQiblaDistance calculation;
    MvpView.QiblaView mvpView;


    private final SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mvpView.changeCompassDirection(event.values[0], event.values[1], (float) degree);
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public QiblaPresenter(Fragment fragment, Context context) {
        this.context = context;
        mvpView = (MvpView.QiblaView) fragment;
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        calculation = new CalculationForQiblaDistance();
//        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

    }

    @Override
    public void startCalculatingLocation() {
        if (!checkSensorAvailability(sensorManager)){

            //magnetic sensor not available in this device show this message to user
            mvpView.showSensorNotAvailable();
            return;
        }

        getLocation();//it will take location from location tracker
        Distance = calculation.getDistanceBetween(Qlati, Qlongi, latitude, longitude);
        degree = calculation.bearing(latitude, longitude, Qlati, Qlongi);
        calculateQiblaInfo();
    }

    @Override
    public boolean checkSensorAvailability(SensorManager mSensorManager){
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void calculateQiblaInfo() {
        String qiblaDegree = String.format("%.2f", degree) + (char) 0x00B0;
        String distance = String.valueOf(Distance / 1000);

        mvpView.setQiblaInfo(qiblaDegree, distance);
    }

    private void getLocation() {
        // TODO Auto-generated method stub
        gps = new GpsTracker(context);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }
    }
    @Override
    public void onResume() {
        sensorManager.registerListener(mListener,mSensor, SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    public void onPause() {
        sensorManager.unregisterListener(mListener);
    }

}
