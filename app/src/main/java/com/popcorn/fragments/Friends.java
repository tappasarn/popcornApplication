package com.popcorn.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.popcorn.FriendsListAdapter;
import com.popcorn.LoginActivity;
import com.popcorn.R;
import com.popcorn.config.Configurations;
import com.popcorn.data.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Friends extends Fragment {
    private SharedPreferences sharedPreferences;
    private List<String>  myDataSet;
    private FriendsListAdapter listAdapter;
    private ListView friendsListView;
    private UserInfo userinfo;
    private String token;
    private RequestQueue requestQueue;
    private JSONObject jsonObj;
    private JSONObject jsonProfile;
    private JSONArray jsonArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myDataSet = new ArrayList<>();
        // set up request queue
        requestQueue = Volley.newRequestQueue(getActivity());

        // get list of friends from sharepref.
        // first ge the sharedpref obj.
        sharedPreferences = getActivity().getApplication().getSharedPreferences(
                Configurations.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        // get UserInfo
        userinfo = UserInfo.from(sharedPreferences);
        token = userinfo.getToken();

        // send the token to validateToken function
        validateToken(token);

        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        friendsListView = (ListView)view.findViewById(R.id.friendsListView);
        Log.d("recheck",myDataSet.toString());
        listAdapter = new FriendsListAdapter(getActivity(), myDataSet);
        friendsListView.setAdapter(listAdapter);

        return view;
    }

    public void setJSONtoList(JSONObject response){
        //set response to global jsonObj
        jsonObj = response;
        Log.d("jsonObj",jsonObj.toString());
        // get profile json obj
        try {
            jsonProfile = jsonObj.getJSONObject("profile");
            Log.d("jsonProfile", jsonProfile.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get json string
        try {
            jsonArray = jsonProfile.getJSONArray("friends");
            Log.d("jsonArray", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // loop over json array
        for (int i = 0; i<jsonArray.length(); i++){
            try {
                myDataSet.add(jsonArray.getJSONObject(i).getString("email"));
                Log.d("myDataSet", jsonArray.getJSONObject(i).getString("email").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    private void validateToken(String token) {

        String endpointURL = Configurations.API.USER_INFO_URL + token;
        Log.d("DEBUG", endpointURL);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, endpointURL, "",

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("DEBUG", response.toString());
                        try {
                            if (!response.getBoolean("error")) {
                                Toast toast = Toast.makeText(getActivity(), "no error bitch", Toast.LENGTH_SHORT);
                                toast.show();
                                setJSONtoList(response);

                            }
                            else {
                                // error !!!
                                Toast toast = Toast.makeText(getActivity(), "error calling friends", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
