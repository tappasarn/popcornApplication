package com.popcorn.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.popcorn.AddFriendID;
import com.popcorn.FriendsListAdapter;
import com.popcorn.LoginActivity;
import com.popcorn.MainActivity;
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
    private List<String> myDataSet;
    private List<String> imageSet;
    private List<String> idSet;
    private FriendsListAdapter listAdapter;
    private ListView friendsListView;
    private UserInfo userinfo;
    private String token;
    private RequestQueue requestQueue;
    private JSONObject jsonObj;
    private JSONObject jsonProfile;
    private JSONArray jsonArray;
    private Button addButton;
    private int keeppos;
    private String removeToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myDataSet = new ArrayList<>();
        imageSet = new ArrayList<>();
        idSet = new ArrayList<>();

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

        // set button listener
        addButton = (Button)view.findViewById(R.id.addFriend);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFriendID.class);
                startActivity(intent);
            }
        });

        friendsListView = (ListView) view.findViewById(R.id.friendsListView);

        Log.d("recheck", myDataSet.toString());
        listAdapter = new FriendsListAdapter(getActivity(), myDataSet, imageSet);

        // set a long click listenner for the list view
        friendsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                keeppos = pos;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Remove a User");
                builder.setMessage("are you sure ?");

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
                                        Request.Method.POST, String.format(Configurations.API.REMOVE_FRIEND,token,removeID),"",
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    if (!response.getBoolean("error")) {
                                                        Toast toast = Toast.makeText(getActivity(), "friend removed", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                    }
                                                    else {
                                                        Toast toast = Toast.makeText(getActivity(), "remove is not completed", Toast.LENGTH_SHORT);
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
        friendsListView.setAdapter(listAdapter);

        return view;
    }

    public void setJSONtoList(JSONObject response) {
        //set response to global jsonObj
        jsonObj = response;
        Log.d("jsonObj", jsonObj.toString());
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
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                idSet.add(jsonArray.getJSONObject(i).getString("id"));
                myDataSet.add(jsonArray.getJSONObject(i).getString("readable_id"));
                imageSet.add(jsonArray.getJSONObject(i).getString("profile_pic"));
                Log.d("myDataSet", jsonArray.getJSONObject(i).getString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

                                setJSONtoList(response);

                            } else {
                                // error !!!
                                Toast toast = Toast.makeText(getActivity(), "error fetching friends", Toast.LENGTH_SHORT);
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
