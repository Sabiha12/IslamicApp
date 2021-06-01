package com.sabiha.islamicapp.modelviewpresenter;

import android.hardware.SensorManager;
import android.view.LayoutInflater;

import com.sabiha.islamicapp.model.Surah;
import java.util.ArrayList;

public interface MvpPresenter {

    interface QuranPresenter{
        void prepareSearchAdapter(LayoutInflater inflater, ArrayList<Surah> surahs);
    }

    interface QiblaPresenter{
        void startCalculatingLocation();

        boolean checkSensorAvailability(SensorManager mSensorManager);

        void calculateQiblaInfo();

        void onResume();

        void onPause();
    }

    interface PrayerTimePresenter{
        void startCalculationPrayerTime();
    }
}
