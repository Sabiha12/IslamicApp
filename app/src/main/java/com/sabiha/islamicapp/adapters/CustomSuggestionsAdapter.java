package com.sabiha.islamicapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.sabiha.islamicapp.R;
import com.sabiha.islamicapp.interfaces.OnSearchItemClick;
import com.sabiha.islamicapp.model.Surah;

import java.util.ArrayList;

import static com.sabiha.islamicapp.adapters.CustomSuggestionsAdapter.*;

public class CustomSuggestionsAdapter extends SuggestionsAdapter<Surah, SuggestionHolder> {
    private OnSearchItemClick onSearchItemClick;

    public CustomSuggestionsAdapter(LayoutInflater inflater, Fragment fragment) {
        super(inflater);
        onSearchItemClick = (OnSearchItemClick) fragment;
    }

    @Override
    public int getSingleViewHeight() {
        return 100;
    }

    @Override
    public SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_custom_suggestion, parent, false);
        return new SuggestionHolder(view);
    }

    @Override
    public void onBindSuggestionHolder(final Surah suggestion, SuggestionHolder holder, int position) {
        holder.title.setText(suggestion.getName());
        holder.subtitle.setText(String.valueOf(suggestion.getIndexNo()+1));


        holder.layoutSuggestionLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchItemClick.onSearchItemClick(String.valueOf(suggestion.getIndexNo()));
            }
        });
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                String term = constraint.toString();
                if (term.isEmpty())
                    suggestions = suggestions_clone;
                else {
                    suggestions = new ArrayList<>();
                    for (Surah item : suggestions_clone) {

                        if (item.getName().toLowerCase().contains(term.toLowerCase())) {
                            suggestions.add(item);
                        } else if (String.valueOf(item.getIndexNo()).contains(term)) {
                            suggestions.add(item);
                        }
                    }
                }
                results.values = suggestions;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                suggestions = (ArrayList<Surah>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    static class SuggestionHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView subtitle;
        protected LinearLayout layoutSuggestionLl;

        public SuggestionHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            layoutSuggestionLl = itemView.findViewById(R.id.layoutSuggestionLl);
        }
    }
}