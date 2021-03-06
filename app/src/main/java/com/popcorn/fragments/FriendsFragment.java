package com.popcorn.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.popcorn.AddFriendActivity;
import com.popcorn.FriendsListAdapter;
import com.popcorn.R;
import com.popcorn.config.Configurations;
import com.popcorn.data.UserInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    // Lists for holding dataSets
    private List<String> myDataSet;
    private List<String> imageSet;
    private List<String> idSet;
    private List<Integer> reviewCountList;

    private FriendsListAdapter listAdapter;

    // UIs
    private ListView friendsListView;
    private TextView emptyTextView;
    private Button addButton;

    private UserInfo userinfo;
    private String token;

    private RequestQueue requestQueue;
    private JSONObject jsonObj;
    private JSONObject jsonProfile;

    private JSONArray jsonArray;
    private int keeppos;

    private String removeToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myDataSet = new ArrayList<>();
        imageSet = new ArrayList<>();
        idSet = new ArrayList<>();
        reviewCountList = new ArrayList<>();

        // Set up request queue
        requestQueue = Volley.newRequestQueue(getActivity());

        // Get shared preference
        sharedPreferences = getActivity().getApplication().getSharedPreferences(
                Configurations.SHARED_PREF_KEY, Context.MODE_PRIVATE);

        // Get UserInfo and Token
        userinfo = UserInfo.from(sharedPreferences);
        token = userinfo.getToken();

        // Send the token to validateToken function
        validateToken(token);

        // Inflat XML into view
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        // Connects to UIs
        emptyTextView = (TextView) view.findViewById(R.id.emptyTextView);
        friendsListView = (ListView) view.findViewById(R.id.friendsListView);

        // Adapter
        listAdapter = new FriendsListAdapter(getActivity(), myDataSet, imageSet, reviewCountList);
        friendsListView.setAdapter(listAdapter);

        // Set button listener
        addButton = (Button)view.findViewById(R.id.addFriend);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        Log.d("RECHECK", myDataSet.toString());

        // Set a long click listener for the list view
        friendsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                keeppos = pos;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Remove a User");
                builder.setMessage("Are you sure ?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // remove user name and image away from the list view
                                myDataSet.remove(keeppos);
                                imageSet.remove(keeppos);
                                String removeID = idSet.get(keeppos);
                                idSet.remove(keeppos);
                                Log.d("removeID", removeID);
                                listAdapter.notifyDataSetChanged();

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                        Request.Method.POST, String.format(Configurations.API.REMOVE_FRIEND, token, removeID), "",
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    if (!response.getBoolean("error")) {
                                                        Toast toast = Toast.makeText(getActivity(), "Friend removed", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                    } else {
                                                        Toast toast = Toast.makeText(getActivity(), "Removing friend results in error.", Toast.LENGTH_SHORT);
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
                                                Log.d("DEBUG", error.toString());
                                            }
                                        }
                                );
                                requestQueue.add(jsonObjectRequest);
                            }
                        }
                );

                // set negative Button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()

                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }

                );

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });

        return view;
    }

    public void parseResponseToLists(JSONObject response) {

        // Set response to global jsonObj
        jsonObj = response;
        Log.d("jsonObj", jsonObj.toString());

        // Get profile json obj
        try {
            jsonProfile = jsonObj.getJSONObject("profile");
            Log.d("jsonProfile", jsonProfile.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Get json string
        try {
            jsonArray = jsonProfile.getJSONArray("friends");
            Log.d("jsonArray", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Loop over json array
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                idSet.add(jsonArray.getJSONObject(i).getString("id"));
                myDataSet.add(jsonArray.getJSONObject(i).getString("readable_id"));
                imageSet.add(jsonArray.getJSONObject(i).getString("profile_pic"));
                reviewCountList.add(jsonArray.getJSONObject(i).getInt("review_count"));

                Log.d("myDataSet", jsonArray.getJSONObject(i).getString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (idSet.size() > 0) {
            emptyTextView.setVisibility(View.INVISIBLE);
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
        }

        listAdapter.notifyDataSetChanged();
    }

    private void validateToken(String token) {

        String endpointURL = Configurations.API.USER_INFO_URL + token;
        Log.d("DEBUG", token.toString());
        Log.d("DEBUG", endpointURL);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, endpointURL, "",

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("DEBUG", response.toString());
                        try {
                            if (!response.getBoolean("error")) {

                                parseResponseToLists(response);

                            } else {
                                Toast toast = Toast.makeText(getActivity(), "Error fetching friends", Toast.LENGTH_SHORT);
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
