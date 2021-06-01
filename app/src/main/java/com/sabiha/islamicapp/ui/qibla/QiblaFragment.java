package com.sabiha.islamicapp.ui.qibla;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.sabiha.islamicapp.R;
import com.sabiha.islamicapp.modelviewpresenter.MvpPresenter;
import com.sabiha.islamicapp.modelviewpresenter.MvpView;
import com.sabiha.islamicapp.modelviewpresenter.QiblaPresenter;
import com.sabiha.islamicapp.ui.BaseFragment;
import com.sabiha.islamicapp.utils.DrawCompass;

public class QiblaFragment extends BaseFragment implements MvpView.QiblaView {
    private RelativeLayout directionContainer;
    private DrawCompass drawCompass;
    View view;
    TextView qiblaDistance, qiblaDegree;

    private MvpPresenter.QiblaPresenter presenter;
    private SwipeRefreshLayout refreshLayout;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private Context context;
    private TextView errorTv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_qibla, container, false);
        init();
        return view;
    }

    private void init() {
        qiblaDistance = view.findViewById(R.id.idDistance);
        qiblaDegree = view.findViewById(R.id.idDegree);

        directionContainer = view.findViewById(R.id.cantainer_layout);
        drawCompass = new DrawCompass(context);
        presenter = new QiblaPresenter(this,context);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        errorTv = view.findViewById(R.id.errorTv);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        directionContainer.addView(drawCompass);
        drawCompass.invalidate();

        refreshLayout.setOnRefreshListener(refreshListener);

        //check is all type of run time permission enabled or not if not enabled then show again
        checkPermission();
        presenter.startCalculatingLocation();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        presenter.onResume();
        super.onResume();
    }

    @Override
    public void setQiblaInfo(String qiblaDegree, String qiblaDistance) {
        this.qiblaDegree.setText(qiblaDegree);
        this.qiblaDistance.setText(qiblaDistance);
    }

    @Override
    public void changeCompassDirection(float directionsNorth, float directionsQibla, float degree) {
        drawCompass.setDirections(directionsNorth, directionsQibla, degree);
    }

    @Override
    public void notifyNoInternetConnection() {
        showToast(getResources().getString(R.string.no_internet_connection));
    }

    @Override
    public void showSensorNotAvailable() {
        Snackbar.make(view, R.string.magnetic_sensor_not_available,Snackbar.LENGTH_SHORT).show();
        errorTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void notifyNotEnabledGPS() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        // Setting Dialog Title
        alertDialog.setTitle(getContext().getResources().getString(R.string.gps_setting_title));

        // Setting Dialog Message
        alertDialog.setMessage(getContext().getResources().getString(R.string.gps_setting_message));

        // On pressing Settings button
        alertDialog.setPositiveButton(getContext().getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton(getContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
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



    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            presenter.startCalculatingLocation();
            refreshLayout.setRefreshing(false);
        }
    };

    public boolean checkPermission() {

        final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(!hasPermissions(getContext(), PERMISSIONS)){

            ActivityCompat.requestPermissions(getActivity(),
                    PERMISSIONS,
                    MY_PERMISSIONS_REQUEST_LOCATION);

            return false;
        } else {
            //permission already granted
            //like android version < 5(lollipop) don,t need runtime permission

            return true;

        }
    }

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

    private void permissionDenied() {
        showToast(getResources().getString(R.string.permission_denied));
    }
}