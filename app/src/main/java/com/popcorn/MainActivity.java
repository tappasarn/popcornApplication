package com.popcorn;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.popcorn.config.Configurations;
import com.popcorn.data.UserInfo;
import com.popcorn.fragments.Friends;
import com.popcorn.fragments.NewReviewFragment;
import com.popcorn.fragments.ProfileFragment;
import com.popcorn.fragments.SuggestionFragment;
import com.popcorn.gcm.RegistrationIntentService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private String[] titles = new String[]{"Recommend", "Create Review", "Friends", "Profile", "Sign out"};
    private ListView drawerList;
    private List<SimpleDrawerItem> items;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    // Volley
    private RequestQueue requestQueue;

    // Shared Preference
    private SharedPreferences sharedPreferences;

    private ProgressDialog loadingDialog;
    private int checkNewFriend;

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkNewFriend = 0;
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initDrawer(savedInstanceState);

        // SharedPreferences
        sharedPreferences = getApplication().getSharedPreferences(
                Configurations.SHARED_PREF_KEY, MODE_PRIVATE);

        // Volley
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        // Intent
        Intent receivedIntent = getIntent();

        // Reload friends if precious activity request so
        boolean reloadFriends = receivedIntent.getBooleanExtra(Configurations.NOTIFY_FRIEND_ADDED, false);
        if (reloadFriends){
            selectItem(2);
        }

        // Perform validation of token if previous activity request so
        boolean revalidateToken = receivedIntent.getBooleanExtra(Configurations.REVALIDATE_TOKEN, true);
        if (revalidateToken) {
            loadingDialog = ProgressDialog.show(
                    MainActivity.this, "Validating your credentials", "Please wait .. This may takes serveral seconds");
            validateToken(sharedPreferences.getString(Configurations.USER_TOKEN, ""));
        }

        // Register for GCM
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Log.d("DEBUG", "Starting service");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

    }

    private void initDrawer(Bundle savedInstanceState) {
        drawerList = (ListView) findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Setup items in the drawer
        SimpleDrawerItem[] items = new SimpleDrawerItem[5];
        items[0] = new SimpleDrawerItem(R.drawable.ic_fiber_new_black_24dp, "Recommend");
        items[1] = new SimpleDrawerItem(R.drawable.ic_rate_review_black_24dp, "Create Review");
        items[2] = new SimpleDrawerItem(R.drawable.ic_people_black_24dp, "Friends");
        items[3] = new SimpleDrawerItem(R.drawable.ic_face_black_24dp, "Profile");
        items[4] = new SimpleDrawerItem(R.drawable.ic_input_black_24dp, "Sign out");

        drawerList.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_activated_1, titles));

        drawerList.setAdapter(new DrawerAdapter(getApplication(), items));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (savedInstanceState == null) {
            selectItem(0);
        }

        // Create the ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer, R.string.close_drawer) {

            // Called when a drawer has settled in a completely closed state
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void selectItem(int position) {

        Fragment fragment = new SuggestionFragment();
        switch(position) {
            case 1: fragment = new NewReviewFragment(); break;
            case 2: fragment = new Friends(); break;
            case 3: fragment = new ProfileFragment(); break;
            case 4:
                // Sign out, clear the shared preference
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                // Start LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                // Make a call to finish, so that event if user comes back to this activity,
                // the activity will terminate anyway.
                finish();
                // Explicit return will avoid ft replacing current fragment with a null
                return;
            default: fragment = new SuggestionFragment();
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        // Set the action bar title
        setActionBarTitle(position);

        // Close drawer
        drawerLayout.closeDrawer(drawerList);
    }

    private void setActionBarTitle(int position) {
        String title;
        if (position == 0){
            title = getResources().getString(R.string.app_name);
        } else {
            title = titles[position];
        }
        getSupportActionBar().setTitle(title);
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
                                loadingDialog.cancel();
                            }
                            else {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}

