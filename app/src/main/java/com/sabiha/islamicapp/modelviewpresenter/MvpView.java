package com.sabiha.islamicapp.modelviewpresenter;

import com.sabiha.islamicapp.adapters.CustomSuggestionsAdapter;
import com.sabiha.islamicapp.adapters.PrayerTimeAdapter;

public interface MvpView {
    interface QuranView{
        void initializeSearchView(CustomSuggestionsAdapter adapter);
    }

    interface QiblaView{
        void setQiblaInfo(String qiblaDegree, String qiblaDistance);

        //this will generate new compass direction
        void changeCompassDirection(float directionsNorth, float directionsQibla, float degree);

        void notifyNoInternetConnection();

        void showSensorNotAvailable();

        void notifyNotEnabledGPS();
    }

    interface PrayerTimeView{

        void initializeRecyclerView(PrayerTimeAdapter adapter);


        void setCityName(String cityName);

        void showGpsSettingAlert();
    }

}
