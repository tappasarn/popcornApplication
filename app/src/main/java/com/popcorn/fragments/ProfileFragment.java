package com.popcorn.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.popcorn.R;
import com.popcorn.config.Configurations;


public class ProfileFragment extends Fragment {

    // UIs
    private ImageView profileImage;
    private TextView emailText, readableIdText;
    private Button editProfileBtn;

    // Shared Preferences
    private SharedPreferences sharedPreferences;

    // Volley
    private RequestQueue requestQueue;

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

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeTextViews(view);
        initializeImageViews(view);
        initializeButtons(view);

        return view;
    }

    private void initializeTextViews(View view) {
        emailText = (TextView) view.findViewById(R.id.emailText);
        readableIdText = (TextView) view.findViewById(R.id.readableIdText);

        emailText.setText(sharedPreferences.getString(Configurations.USER_EMAIL, ""));
        readableIdText.setText(sharedPreferences.getString(Configurations.USER_READABLE_ID, ""));
    }

    private void initializeImageViews(View view) {
        profileImage = (ImageView) view.findViewById(R.id.profileImage);

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

    private void initializeButtons(View view) {
        editProfileBtn = (Button) view.findViewById(R.id.editProfileBtn);
    }

}
