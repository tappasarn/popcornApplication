package com.popcorn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.popcorn.config.Configurations;
import com.popcorn.utils.SnackbarUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class AddFriendActivity extends AppCompatActivity {
    private EditText friendID;
    private SharedPreferences sharedPreferences;
    private String token;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        friendID = (EditText)findViewById(R.id.friendID);
        friendID.setSingleLine(true);

        // Get shared preference and retrieve token
        sharedPreferences = getSharedPreferences(
                Configurations.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        token = sharedPreferences.getString(Configurations.USER_TOKEN, "");

        // Get request queue
        requestQueue = Volley.newRequestQueue(this);

        // Set up back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onAddClick(final View view){
        // Send new friend ID to server
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, String.format(Configurations.API.ADD_FRIEND,token,friendID.getText().toString()), "",

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (!response.getBoolean("error")) {
                                Toast.makeText(AddFriendActivity.this, "Friend Added!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(AddFriendActivity.this,MainActivity.class);
                                intent.putExtra(Configurations.NOTIFY_FRIEND_ADDED, true);
                                intent.putExtra(Configurations.REVALIDATE_TOKEN,false);
                                startActivity(intent);
                            }
                            else {
                                Toast toast = Toast.makeText(AddFriendActivity.this, "ID error", Toast.LENGTH_SHORT);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_friend_id, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(AddFriendActivity.this,MainActivity.class);
                intent.putExtra(Configurations.NOTIFY_FRIEND_ADDED, true);
                intent.putExtra(Configurations.REVALIDATE_TOKEN,false);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
