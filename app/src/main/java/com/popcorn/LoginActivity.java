package com.popcorn;

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

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    // Volley
    private RequestQueue requestQueue;

    // UIs
    private EditText emailEditText, passwordEditTExt;

    // Shared Preference
    private SharedPreferences sharedPreferences;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestQueue = Volley.newRequestQueue(this);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditTExt = (EditText) findViewById(R.id.passwordEditText);

       // Shared Preferences
       sharedPreferences = getApplication().getSharedPreferences(
               Configurations.SHARED_PREF_KEY, MODE_PRIVATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onLoginBtnClick(View view){

        boolean jsonError = false;

        JSONObject credentialObj = new JSONObject();
        try {
            credentialObj.put("email", emailEditText.getText().toString());
            credentialObj.put("password", passwordEditTExt.getText().toString());
        } catch (JSONException e) {
            jsonError = true;
            e.printStackTrace();
        }

        JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("account", credentialObj);
        } catch (JSONException e) {
            jsonError = true;
            e.printStackTrace();
        }

        if (!jsonError) {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, Configurations.LOGIN_URL, requestObj,

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (!response.getBoolean("error")) {

                                    JSONObject profile = response.getJSONObject("profile");
                                    String token = profile.getString("token");
                                    String readableId = profile.getString("id");
                                    String email = profile.getString("email");
                                    String profilePicUrl = profile.getString("profile_pic");

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(Configurations.USER_TOKEN, token);
                                    editor.putString(Configurations.USER_EMAIL, email);
                                    editor.putString(Configurations.USER_READABLE_ID, readableId);
                                    editor.putString(Configurations.USER_PROFILE_PIC_URL, profilePicUrl);
                                    editor.commit();

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "Incorrect username/password :3", Toast.LENGTH_SHORT)
                                            .show();
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

    public void onSignUpBtnClick(View view){
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
