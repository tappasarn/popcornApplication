package com.popcorn.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.popcorn.R;
import com.popcorn.SuggestionAdapter;
import com.popcorn.config.Configurations;
import com.popcorn.data.Suggestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SuggestionFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Shared Preference
    private SharedPreferences sharedPreferences;

    // Volley
    private RequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Shared Preference
        sharedPreferences = getActivity().getSharedPreferences(
                Configurations.SHARED_PREF_KEY, Context.MODE_PRIVATE);

        // Volley
        requestQueue = Volley.newRequestQueue(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recommend_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        initializeSuggestions();

        return view;
    }

    private void initializeSuggestions() {

        final List<Suggestion> suggestionsDataSet = new ArrayList<>();

        String token = sharedPreferences.getString(Configurations.USER_TOKEN, "");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, String.format(Configurations.API.SUGGESTION_URL, token), "",

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("DEBUG", response.toString());
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray suggestions = response.getJSONArray("suggestions");
                                for (int i = 0; i < suggestions.length(); i++) {
                                    JSONObject suggestion = suggestions.getJSONObject(i);
                                    Log.d("DEBUG", suggestion.toString());

                                    String title = suggestion.getString("movie_title");
                                    int rating = suggestion.getInt("rating");
                                    String genre = suggestion.getString("genre");
                                    String plot = suggestion.getString("plot");

                                    suggestionsDataSet.add(new Suggestion(title, rating, genre, plot));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            mAdapter = new SuggestionAdapter(getActivity(), suggestionsDataSet);
                            mRecyclerView.setAdapter(mAdapter);
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

}
