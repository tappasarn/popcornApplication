package com.popcorn;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class NewReviewFragment extends Fragment {

    private ArrayAdapter<String> mAdapter;
    private String[] dataSet = new String[]{"Tae", "Tang", "Mhaii", "Robroo", "Time", "Ireen"};

    private int rating;


    // UIs
    private ImageView[] stars = new ImageView[5];
    private Button addReviewBtn;
    private AutoCompleteTextView movieEditText;
    private EditText commentEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_review, container, false);

        initializeStars(view);
        initializeButtons(view);
        initializeEditTexts(view);

        AppCompatAutoCompleteTextView autoCompleteTextView =
                (AppCompatAutoCompleteTextView) view.findViewById(R.id.movie_autocomplete);

        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataSet);
        autoCompleteTextView.setAdapter(mAdapter);

        return view;
    }

    private void initializeStars(View view) {
        stars[0] = (ImageView) view.findViewById(R.id.star1);
        stars[1] = (ImageView) view.findViewById(R.id.star2);
        stars[2] = (ImageView) view.findViewById(R.id.star3);
        stars[3] = (ImageView) view.findViewById(R.id.star4);
        stars[4] = (ImageView) view.findViewById(R.id.star5);

        for (int i = 0; i < 5; i++) {
            final int loopIndex = i;
            stars[i].setOnClickListener(new View.OnClickListener() {
                final int ratingValue = loopIndex + 1;
                @Override
                public void onClick(View v) {
                    rating = ratingValue;
                    redrawSelectedStars();
                }
            });
        }
    }

    private void initializeButtons(View view) {
        addReviewBtn = (Button) view.findViewById(R.id.addReviewBtn);
        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initializeEditTexts(View view) {
        movieEditText = (AutoCompleteTextView) view.findViewById(R.id.movie_autocomplete);
        commentEditText = (EditText) view.findViewById(R.id.commentEditText);
    }

    private void redrawSelectedStars() {
        for (ImageView star : stars) {
            star.setImageDrawable(getActivity().getDrawable(R.drawable.ic_star_outline_grey600_48dp));
        }
        for (int i = 0; i < rating; i++) {
            stars[i].setImageDrawable(getActivity().getDrawable(R.drawable.ic_star_grey600_48dp));
        }
    }



}
