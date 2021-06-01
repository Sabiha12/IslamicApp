package com.sabiha.islamicapp.modelviewpresenter;
import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;

import com.sabiha.islamicapp.adapters.CustomSuggestionsAdapter;
import com.sabiha.islamicapp.model.Surah;

import java.util.ArrayList;

public class QuranPresenter implements MvpPresenter.QuranPresenter {
    private ArrayList<Surah> suggestions;
    private CustomSuggestionsAdapter customSuggestionsAdapter;
    private MvpView.QuranView quranView;
    private Fragment fragment;

    public QuranPresenter(Fragment fragment) {
        quranView = (MvpView.QuranView) fragment;
        this.fragment = fragment;
    }

    @Override
    public void prepareSearchAdapter(LayoutInflater inflater, ArrayList<Surah> surahs) {
        suggestions = surahs;

        try {
            if (suggestions == null){
                customSuggestionsAdapter = null;

            }else {
                customSuggestionsAdapter = new CustomSuggestionsAdapter(inflater,fragment);
                customSuggestionsAdapter.setSuggestions(suggestions);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        quranView.initializeSearchView(customSuggestionsAdapter);
    }
}
