package com.sabiha.islamicapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public LayoutInflater inflater;
    private Dialog progressDialog;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inputMethodManager = (InputMethodManager) this.getSystemService(BaseActivity.INPUT_METHOD_SERVICE);

        progressDialog();
    }

    @Override
    public void onClick(View view) {

    }

    private void progressDialog() {
        progressDialog = new Dialog(this);
        View view = View.inflate(this, R.layout.progress_dialog, null);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(view);
        assert progressDialog.getWindow() != null;
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
    }

    public void startProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            try {
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    //checking internet connection if internet is connected then it will return true
    // otherwise it will return false
    public boolean netCheck(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();

        boolean isConnected = nInfo != null && nInfo.isConnectedOrConnecting();
        return isConnected;
    }
}
