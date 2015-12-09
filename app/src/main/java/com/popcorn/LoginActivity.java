package com.popcorn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.popcorn.config.Configurations;
import com.popcorn.gcm.RegistrationIntentService;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements SensorEventListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    //sensor
    private SensorManager sensorManager;
    private long lastUpdate;

    // Volley
    private RequestQueue requestQueue;

    // UIs
    private EditText emailEditText, passwordEditTExt;

    // Shared Preference
    private SharedPreferences sharedPreferences;

    private Button button;


   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button = (Button)findViewById(R.id.signUpBtn);
        button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //set lastUpDate time
        lastUpdate = System.currentTimeMillis();

         //set sensor service
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        requestQueue = Volley.newRequestQueue(this);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditTExt = (EditText) findViewById(R.id.passwordEditText);

       // Shared Preferences
       sharedPreferences = getApplication().getSharedPreferences(
               Configurations.SHARED_PREF_KEY, MODE_PRIVATE);

       // Register for GCM
       if (checkPlayServices()) {
           // Start IntentService to register this application with GCM.
           Log.d("DEBUG", "Starting service");
           Intent intent = new Intent(this, RegistrationIntentService.class);
           startService(intent);
       }


       // Start MainActivity right away if token exists
       // However, put an extra flag to tells MainActivity to do validation again
       String token = sharedPreferences.getString(Configurations.USER_TOKEN, "");
       if (token.length() > 0) {
           Intent intent = new Intent(LoginActivity.this, MainActivity.class);
           intent.putExtra(Configurations.REVALIDATE_TOKEN, true);
           startActivity(intent);
           finish();
       }

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
                    Request.Method.POST, Configurations.API.LOGIN_URL, requestObj,

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
                                    intent.putExtra(Configurations.REVALIDATE_TOKEN, false);
                                    startActivity(intent);

                                    finish();
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 5) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            //onLoginBtnClick();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
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
