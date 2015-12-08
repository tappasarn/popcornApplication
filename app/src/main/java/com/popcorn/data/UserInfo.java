package com.popcorn.data;

import android.content.SharedPreferences;

import com.popcorn.config.Configurations;

public class UserInfo {

    private String token;
    private String email;
    private String profilePicUrl;
    private String readableId;

    private boolean valid;

    private UserInfo(
            String token, String email, String profilePicUrl, String readableId) {
        this.token = token;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
        this.readableId = readableId;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getReadableId() {
        return readableId;
    }

    @Override
    public String toString() {
        return String.format("[USER] email: %s, readable_id: %s, profile_pic_url: %s, token: %s",
                email, readableId, profilePicUrl, token);
    }

    public static UserInfo from(SharedPreferences sharedPreferences) {

        String email = sharedPreferences.getString(Configurations.USER_EMAIL, "");
        String token = sharedPreferences.getString(Configurations.USER_TOKEN, "");
        String readableId = sharedPreferences.getString(Configurations.USER_READABLE_ID, "");
        String profilePicUrl = sharedPreferences.getString(Configurations.USER_PROFILE_PIC_URL, "");

        return new UserInfo(token, email, profilePicUrl, readableId);
    }
}
