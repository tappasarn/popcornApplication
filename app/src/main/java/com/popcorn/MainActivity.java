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
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.popcorn.config.Configurations;
import com.popcorn.data.UserInfo;
import com.popcorn.fragments.Friends;
import com.popcorn.fragments.NewReviewFragment;
import com.popcorn.fragments.ProfileFragment;
import com.popcorn.fragments.SuggestionFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private String[] titles;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    // Volley
    private RequestQueue requestQueue;

    private SharedPreferences sharedPreferences;

    private ProgressDialog loadingDialog;

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Perform validation of token if previous activity request so
        boolean revalidateToken = receivedIntent.getBooleanExtra(Configurations.REVALIDATE_TOKEN, true);
        if (revalidateToken) {
            loadingDialog = ProgressDialog.show(
                    MainActivity.this, "Validating your credentials", "Please wait .. This may takes serveral seconds");
            validateToken(sharedPreferences.getString(Configurations.USER_TOKEN, ""));
        }



    }

    private void initDrawer(Bundle savedInstanceState) {
        titles = getResources().getStringArray(R.array.titles);
        drawerList = (ListView)findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerList.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_activated_1, titles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (savedInstanceState == null) {
            selectItem(0);
        }

        // Create the ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer, R.string.close_drawer) {

            //Called when a drawer has settled in a completely closed state
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
            //Called when a drawer has settled in a completely open state.
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
            case 1: fragment = new NewReviewFragment();  break;
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

}

