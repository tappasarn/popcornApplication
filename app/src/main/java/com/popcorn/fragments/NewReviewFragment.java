package com.popcorn.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.popcorn.R;
import com.popcorn.config.Configurations;
import com.popcorn.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class NewReviewFragment extends Fragment {

    private ArrayAdapter<Movie> mAdapter;
    private List<Movie> autoCompleteDataSet = new ArrayList<>();

    private int rating;

    // UIs
    private ImageView[] stars = new ImageView[5];
    private Button addReviewBtn;
    private AutoCompleteTextView movieEditText;
    private EditText commentEditText;
    private AutoCompleteTextView autoCompleteTextView;

    // Volley
    private RequestQueue requestQueue;

    // Shared Preference
    private SharedPreferences sharedPreferences;

    // Selected Movie
    Movie selectedMovie = null;

    // Progress Dialog
    ProgressDialog loadingDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Volley
        requestQueue = Volley.newRequestQueue(getActivity());

        // Shared Preference
        sharedPreferences = getActivity().getSharedPreferences(
                Configurations.SHARED_PREF_KEY, Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_review, container, false);

        initializeStars(view);
        initializeButtons(view);
        initializeEditTexts(view);
        initializeAutoCompleteTextView(view);

        return view;
    }

    private void initializeAutoCompleteTextView(View view) {

        autoCompleteTextView =
                (AppCompatAutoCompleteTextView) view.findViewById(R.id.movie_autocomplete);

        autoCompleteTextView.setThreshold(1);

        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        autoCompleteTextView.setAdapter(mAdapter);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String searchQuery = URLEncoder.encode(autoCompleteTextView.getText().toString()).replace("+", "%20");
                String endpointUrl = Configurations.API.MOVIE_SEARCH_URL + searchQuery;

                Log.d("DEBUG", searchQuery);
                Log.d("DEBUG", endpointUrl);

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.GET, endpointUrl, "",

                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    mAdapter.clear();
                                    Log.d("DEBUG", response.toString());
                                    JSONArray movies = response.getJSONArray("movies");

                                    for (int i = 0; i < movies.length(); i++) {
                                        JSONObject movie = movies.getJSONObject(i);
                                        long id = movie.getLong("id");
                                        String title = movie.getString("title");
                                        int year = movie.getInt("year");

                                        mAdapter.add(new Movie(id, title, year));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                );
                requestQueue.add(request);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMovie = mAdapter.getItem(position);
                Log.d("DEBUG", "Selected item = " + selectedMovie);
            }
        });

    }

    private void initializeStars(View view) {
        stars[0] = (ImageView) view.findViewById(R.id.star1);
        stars[1] = (ImageView) view.findViewById(R.id.star2);
        stars[2] = (ImageView) view.findViewById(R.id.star3);
        stars[3] = (ImageView) view.findViewById(R.id.star4);
        stars[4] = (ImageView) view.findViewById(R.id.star5);

        // Toggle on first star
        stars[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rating == 1) {
                    rating = 0;
                } else {
                    rating = 1;
                }
                redrawSelectedStars();
            }
        });

        for (int i = 1; i < 5; i++) {
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

                String token = sharedPreferences.getString(Configurations.USER_TOKEN, "");

                if (selectedMovie != null) {

                    JSONObject reviewObj = new JSONObject();
                    try {
                        reviewObj.put("movie_id", selectedMovie.getId());
                        reviewObj.put("comment", commentEditText.getText().toString());
                        reviewObj.put("stars", rating);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject requestObj = new JSONObject();
                    try {
                        requestObj.put("review", reviewObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    loadingDialog = ProgressDialog.show(getActivity(), "Submiting review", "Publishing review. This may take some time.");

                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST, String.format(Configurations.API.REVIEW_CREATE_URL, token), requestObj,

                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    loadingDialog.cancel();
                                    try {
                                        if (!response.getBoolean("error")) {
                                            Toast.makeText(getActivity(), "Review saved !", Toast.LENGTH_SHORT)
                                                    .show();
                                            // reset inputs frields into default value
                                            movieEditText.setText("");
                                            commentEditText.setText("");
                                            rating = 0;
                                            redrawSelectedStars();
                                            ;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), "Something went wrong. Contact Hibiki", Toast.LENGTH_SHORT)
                                                .show();
                                    }

                                }
                            },

                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }
                    );
                    requestQueue.add(request);

                }

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
