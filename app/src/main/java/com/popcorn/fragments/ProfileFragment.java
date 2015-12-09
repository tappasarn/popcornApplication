package com.popcorn.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.popcorn.R;
import com.popcorn.config.Configurations;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;


public class ProfileFragment extends Fragment {

    // UIs
    private ImageView profileImage;
    private TextView emailText, readableIdText;

    // Shared Preferences
    private SharedPreferences sharedPreferences;

    // Volley
    private RequestQueue requestQueue;

    // Define Result ID
    private static final int ACTIVITY_SELECT_IMAGE = 0;
    private static final int ACTIVITY_CHANGE_ID = 1;

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            String token = sharedPreferences.getString(Configurations.USER_TOKEN, "");
            try {
                // Convert URI into bitmap
                InputStream is = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                // Convert URI into base64 String
                byte[] imageBytes = new byte[10240];
                String base64EncodedString = null;
                try {
                    is = getActivity().getContentResolver().openInputStream(data.getData());
                    is.read(imageBytes);
                    base64EncodedString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Update current image
                profileImage.setImageBitmap(bitmap);

                JSONObject pictureObj = new JSONObject();
                try {
                    pictureObj.put("fname", "original_name.jpg");
                    pictureObj.put("base64", base64EncodedString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject requestObj = new JSONObject();
                try {
                    requestObj.put("picture", pictureObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Send a request to Volley to update image on the server
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST, String.format(Configurations.API.UPDATE_PROFILE_IMAGE_URL, token), requestObj,

                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("DEBUG", response.toString());
                                try {
                                    if (!response.getBoolean("error")) {
                                        String profilePicName = response.getString("profile_pic");

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(Configurations.USER_PROFILE_PIC_URL, profilePicName);
                                        editor.commit();

                                        Toast.makeText(getActivity(), "Profile image updated :)", Toast.LENGTH_SHORT);
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

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

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

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeTextViews(view);
        initializeImageViews(view);

        return view;
    }

    private void initializeTextViews(View view) {
        emailText = (TextView) view.findViewById(R.id.emailText);
        readableIdText = (TextView) view.findViewById(R.id.readableIdText);

        emailText.setText(sharedPreferences.getString(Configurations.USER_EMAIL, ""));
        readableIdText.setText(sharedPreferences.getString(Configurations.USER_READABLE_ID, ""));

        readableIdText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Inflate
                View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.update_id_form, null);
                /// Retrieve edittext
                final EditText idEditText = (EditText) dialogView.findViewById(R.id.userIdEditText);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Update user id");
                builder.setView(dialogView);


                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("DEBUG", "Dialog clicked");

                        final String newId = idEditText.getText().toString();
                        final String token = sharedPreferences.getString(Configurations.USER_TOKEN, "");

                        JSONObject profileObj = new JSONObject();
                        try {
                            profileObj.put("readable_id", newId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONObject requestObj = new JSONObject();
                        try {
                            requestObj.put("profile", profileObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest request = new JsonObjectRequest(
                                Request.Method.POST, String.format(Configurations.API.UPDATE_PROFILE_DISPLAY_NAME, token), requestObj,

                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("DEBUG", response.toString());
                                        try {
                                            if (!response.getBoolean("error")) {
                                                readableIdText.setText(newId);
                                                Toast.makeText(getActivity(), "User Id Updated", Toast.LENGTH_SHORT)
                                                        .show();

                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString(Configurations.USER_READABLE_ID, newId);
                                                editor.commit();
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
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();


            }
        });
    }

    private void initializeImageViews(View view) {
        profileImage = (ImageView) view.findViewById(R.id.profileImage);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
            }
        });

        String imageURL = sharedPreferences.getString(Configurations.USER_PROFILE_PIC_URL, "default.png");
        Log.d("DEBUG", imageURL);

        // Call to an API to get image
        ImageRequest imageRequest = new ImageRequest(
                String.format(Configurations.API.RESOURCE.PROFILE_IMG_URL, imageURL),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        profileImage.setImageBitmap(response);
                    }
                },
                0,
                0,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.ALPHA_8,

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        profileImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_error_outline_black_24dp));
                    }
                }
        );
        requestQueue.add(imageRequest);

    }

}
