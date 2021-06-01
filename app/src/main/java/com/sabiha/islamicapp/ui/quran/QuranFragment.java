package com.sabiha.islamicapp.ui.quran;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.sabiha.islamicapp.R;
import com.sabiha.islamicapp.adapters.CustomSuggestionsAdapter;
import com.sabiha.islamicapp.interfaces.OnSearchItemClick;
import com.sabiha.islamicapp.model.Surah;
import com.sabiha.islamicapp.modelviewpresenter.MvpPresenter;
import com.sabiha.islamicapp.modelviewpresenter.MvpView;
import com.sabiha.islamicapp.modelviewpresenter.QuranPresenter;
import com.sabiha.islamicapp.ui.BaseFragment;
import com.shockwave.pdfium.PdfDocument;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class QuranFragment extends BaseFragment implements MvpView.QuranView, OnPageChangeListener, OnLoadCompleteListener, OnSearchItemClick {

    private View view;
   public static final String PDF_FILE = "quran.pdf";
//public static final String PDF_FILE = "quraan.pdf";
    private LayoutInflater inflater;
    PDFView pdfView;
    String pdfFileName;
    private ArrayList<Surah> surahs;
    private MaterialSearchBar searchBar;
    private CustomSuggestionsAdapter customSuggestionsAdapter;
    private MvpPresenter.QuranPresenter presenter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_quran, container, false);
        init();
        return view;
    }

    private void init() {
        pdfView = view.findViewById(R.id.pdfView);
        surahs = new ArrayList<>();

        inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        searchBar = view.findViewById(R.id.searchBar);

        presenter = new QuranPresenter(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayFromAsset(PDF_FILE,0);//default pdf will be start from 0 page

        /**
         * search bar default settings*/
        try {
            searchBar.setMaxSuggestionCount(2);
            searchBar.setHint(getString(R.string.find_surah));

            searchBar.addTextChangeListener(textWatcher);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initializeSearchView(CustomSuggestionsAdapter adapter) {
        if (adapter != null) {
            try {
                searchBar.setCustomSuggestionAdapter(adapter);
                this.customSuggestionsAdapter = adapter;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void loadComplete(int nbPages) {
        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        getActivity().setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    @Override
    public void onSearchItemClick(String indexNo) {
        searchBar.disableSearch();
        pdfView.jumpTo(Integer.parseInt(indexNo));
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            try {
                // send the entered text to our filter and let it manage everything
                customSuggestionsAdapter.getFilter().filter(searchBar.getText());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    /**
     * initialize PDF view [PDF is located on asset folder]
     * */
    private void displayFromAsset(String pdfFile, int i) {
        pdfFileName = pdfFile;

        pdfView.fromAsset(PDF_FILE)
                .defaultPage(i)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this.getContext()))
                .load();
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            surahs.add(new Surah(b.getTitle(),b.getPageIdx()));
            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }

        presenter.prepareSearchAdapter(inflater,surahs);


    }
}