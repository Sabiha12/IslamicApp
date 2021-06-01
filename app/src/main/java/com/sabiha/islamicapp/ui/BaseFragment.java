package com.sabiha.islamicapp.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sabiha.islamicapp.BaseActivity;

public class BaseFragment extends Fragment {
    public BaseActivity baseActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = (BaseActivity) getActivity();
    }

    public void showToast(String msg) {
        baseActivity.showToast(msg);
    }
}
