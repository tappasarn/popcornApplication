package com.popcorn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.popcorn.utils.ValidationUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class SignUpActivity extends AppCompatActivity {

    // Volley
    private RequestQueue requestQueue;

    // UIs
    private EditText emailEditText, passwordEditTExt, passwordConfirmEditText;

    // SharedPreference
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // UIs
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditTExt = (EditText) findViewById(R.id.passwordEditText);
        passwordConfirmEditText = (EditText) findViewById(R.id.passwordConfirmEditText);

        // Volley
        requestQueue = Volley.newRequestQueue(SignUpActivity.this);

        // SharedPreference
        sharedPreferences = getApplication().getSharedPreferences(
                Configurations.SHARED_PREF_KEY, MODE_PRIVATE);


    }

    public void onSignUpBtnClick(View view) {

        boolean jsonBuildError = false;

        String email = emailEditText.getText().toString();
        String password = passwordEditTExt.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();

        if (!ValidationUtils.isEmailValid(email)) {
            SnackbarUtils.show(view, "Email is invalid.");
            return;
        }

        if (!ValidationUtils.isPasswordValid(password)) {
            SnackbarUtils.show(view, "Password needs to be at least 4 characters long.");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            SnackbarUtils.show(view, "Password doesn't match :(");
            return;
        }

        JSONObject credentialObj = new JSONObject();
        try {
            credentialObj.put("email", emailEditText.getText().toString());
            credentialObj.put("password", passwordEditTExt.getText().toString());
        } catch (JSONException e) {
            jsonBuildError = true;
            e.printStackTrace();
        }

        JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("account", credentialObj);
        } catch (JSONException e) {
            jsonBuildError = true;
            e.printStackTrace();
        }

        if (!jsonBuildError) {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, Configurations.API.SIGNUP_URL, requestObj,

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("DEBUG", response.toString());
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

                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    intent.putExtra(Configurations.REVALIDATE_TOKEN, false);
                                    startActivity(intent);

                                    finish();
                                } else {
                                    JSONArray errorMessages = response.getJSONArray("msg");
                                    Log.d("DEBUG", errorMessages.toString());
                                    String errorString = "";
                                    for (int i = 0; i < errorMessages.length(); i++) {
                                        JSONObject error = errorMessages.getJSONObject(i);
                                        Log.d("DEBUG", error.toString());
                                        Iterator<String> keys = error.keys();
                                        while (keys.hasNext()) {
                                            String key = keys.next();
                                            errorString += key + " " + error.getJSONArray(key).getString(0) + " ";
                                        }
                                    }
                                    Toast.makeText(SignUpActivity.this, errorString, Toast.LENGTH_SHORT)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sing_up_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
