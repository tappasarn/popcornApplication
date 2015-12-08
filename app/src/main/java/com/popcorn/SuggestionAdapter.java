package com.popcorn;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.popcorn.data.Suggestion;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    private List<Suggestion> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        public ViewHolder(CardView v) {
            super(v);
            mCardView = v;
        }
    }

    public SuggestionAdapter(List<Suggestion> dataSet) {
        mDataset = dataSet;
    }

    @Override
    public SuggestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder((CardView) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TextView movieText = (TextView) holder.mCardView.findViewById(R.id.cardText);
        TextView ratingText = (TextView) holder.mCardView.findViewById(R.id.ratingText);

        movieText.setText(mDataset.get(position).getTitle());
        ratingText.setText(String.valueOf(mDataset.get(position).getRating()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
