package com.sabiha.islamicapp.ui.prayertime;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sabiha.islamicapp.R;
import com.sabiha.islamicapp.adapters.PrayerTimeAdapter;
import com.sabiha.islamicapp.modelviewpresenter.MvpPresenter;
import com.sabiha.islamicapp.modelviewpresenter.MvpView;
import com.sabiha.islamicapp.modelviewpresenter.PrayerTimePresenter;
import com.sabiha.islamicapp.ui.BaseFragment;
import com.sabiha.islamicapp.utils.CheckInternetConnection;
import com.sabiha.islamicapp.utils.Constant;
import com.sabiha.islamicapp.utils.SharedPrefStore;

import static com.sabiha.islamicapp.ui.qibla.QiblaFragment.hasPermissions;

public class PrayerTimeFragment extends BaseFragment implements MvpView.PrayerTimeView {

    private RecyclerView prayerTimeRV;
    private View view;
    private MvpPresenter.PrayerTimePresenter presenter;
    private TextView cityTv;
    private TextView errorNoInternetTv;
    private SwipeRefreshLayout refreshLayout;
    private CheckInternetConnection internetConnectionTest;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private SharedPrefStore savedData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_prayer_time, container, false);
        initializeAll();
        return view;
    }

    /*
     * initialize all necessary variable or initialize object etc
     * */
    private void initializeAll() {
        presenter = new PrayerTimePresenter(this);
        prayerTimeRV = view.findViewById(R.id.prayerTimeRV);//prayerTime recyclerView(RV)
        prayerTimeRV.setHasFixedSize(true);
        prayerTimeRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        cityTv = view.findViewById(R.id.cityNameTv);
        errorNoInternetTv = view.findViewById(R.id.errorNoInternetTv);

        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(refreshListener);
        internetConnectionTest = new CheckInternetConnection();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(connectionStatusReceiver
                , new IntentFilter(Constant.BROADCAST_CONNECTION_STATUS));

        savedData = new SharedPrefStore(getContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        callPresenter();
    }

    /*
     * set Recycler view adapter
     * */
    @Override
    public void initializeRecyclerView(PrayerTimeAdapter adapter) {
        if (adapter != null) {
            prayerTimeRV.setAdapter(adapter);
        }
    }

    @Override
    public void setCityName(String cityName) {
        cityTv.setText(cityName);
    }

    /*
     * if GPS is not turn on this method will show a alert dialog to user to setting enable GPS
     * */
    @Override
    public void showGpsSettingAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        // Setting Dialog Title
        alertDialog.setTitle(R.string.gps_setting_title);

        // Setting Dialog Message
        alertDialog.setMessage(R.string.gps_setting_message);

        // On pressing Settings button
        alertDialog.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getContext().startActivity(intent);

            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                visibleErrorTv(getContext().getString(R.string.gps_setting_message));
            }
        });

        // Showing Alert Message
        alertDialog.show();


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void visibleErrorTv(String message){
        errorNoInternetTv.setVisibility(View.VISIBLE);
        errorNoInternetTv.setText(message);
    }
    public void unVisibleErrorTv(){
        errorNoInternetTv.setVisibility(View.GONE);
    }

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            callPresenter();
            refreshLayout.setRefreshing(false);
        }
    };

    private void callPresenter() {

        /** We have old lat and long so we can calculate by old report */
        if (savedData.getLong() != 0 && savedData.getLat() != 0) {
            presenter.startCalculationPrayerTime();
        }
        if (internetConnectionTest.netCheck(getContext())) {

            if (checkLocationPermission()) {
                presenter.startCalculationPrayerTime();
                unVisibleErrorTv();
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.gps_setting_message), Toast.LENGTH_SHORT).show();
                visibleErrorTv(getContext().getResources().getString(R.string.gps_setting_message));
            }

        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.required_data_connection), Toast.LENGTH_SHORT).show();
            errorNoInternetTv.setText(getContext().getResources().getString(R.string.required_data_connection));
            errorNoInternetTv.setVisibility(View.VISIBLE);
        }
    }

    /*
     * Taking runtime permission for location
     * */

    public boolean checkLocationPermission() {

        final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(!hasPermissions(getContext(), PERMISSIONS)){

            ActivityCompat.requestPermissions(getActivity(),
                    PERMISSIONS,
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        } else {
            return true;
            //permission already granted
            //like android version < 5(lollipop) don,t need runtime permission

        }
    }

    /*
     * check is already permission granted or not
     * */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //permission granted
                    }

                } else {
                    permissionDenied();
                }
                return;
            }

        }
    }

    /*
     * if user deny to give permission this method will be called
     * */
    private void permissionDenied() {
        Toast.makeText(getContext(), R.string.permission_denied,Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver connectionStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString(Constant.CONNECTION_STATUS);

            if (bundle.getInt(Constant.STATUS_CODE) == Constant.ALL_CONNECTED) {
                unVisibleErrorTv();
                presenter.startCalculationPrayerTime();
            } else if (bundle.getInt(Constant.STATUS_CODE) == Constant.NO_CONNECTION_CODE) {
                visibleErrorTv(message);
            }

        }
    };
}