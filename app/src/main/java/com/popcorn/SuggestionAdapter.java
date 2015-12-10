package com.popcorn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.popcorn.config.Configurations;
import com.popcorn.data.Movie;
import com.popcorn.data.Suggestion;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    private List<Suggestion> mDataset;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        public ViewHolder(CardView v) {
            super(v);
            mCardView = v;
        }
    }

    public SuggestionAdapter(Context context, List<Suggestion> dataSet) {
        this.mDataset = dataSet;
        this.context = context;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // Setup Listener
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String movieTitle = mDataset.get(position).getTitle();
                String plot = mDataset.get(position).getPlot();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(movieTitle);
                builder.setMessage(plot);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        TextView movieText = (TextView) holder.mCardView.findViewById(R.id.cardText);
        TextView ratingText = (TextView) holder.mCardView.findViewById(R.id.ratingText);
        TextView genreText = (TextView) holder.mCardView.findViewById(R.id.genreText);
        TextView plotText = (TextView) holder.mCardView.findViewById(R.id.plotText);

        movieText.setText(mDataset.get(position).getTitle());
        ratingText.setText(String.valueOf(mDataset.get(position).getRating()));
        genreText.setText(mDataset.get(position).getGenre());
        plotText.setText(mDataset.get(position).getPlot());

        if (mDataset.get(position).getGenre().length() == 0) {
            genreText.setText("N/A");
        }
        if (mDataset.get(position).getPlot().length() == 0) {
            plotText.setText("N/A");
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
