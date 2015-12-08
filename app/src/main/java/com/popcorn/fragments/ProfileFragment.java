package com.popcorn.fragments;

import android.app.Fragment;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.popcorn.R;


public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView displayNameText, userIdText;
    private Button editProfileBtn;

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
        displayNameText = (TextView) view.findViewById(R.id.displayNameText);
        userIdText = (TextView) view.findViewById(R.id.userIdText);
    }

    private void initializeImageViews(View view) {
        profileImage = (ImageView) view.findViewById(R.id.profileImage);
    }

    private void initializeButtons(View view) {
        editProfileBtn = (Button) view.findViewById(R.id.editProfileBtn);
    }

}
