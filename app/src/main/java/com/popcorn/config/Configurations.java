package com.popcorn.config;

/**
 * Configuration class that contains FLAG and Configurations
 * that are used across the application. *
 */
public class Configurations {

    public static final String SHARED_PREF_KEY = "com.popcorn.sharedPreferences";

    // ------------------ User information --------------------------------------
    public static final String USER_TOKEN = "user.token";
    public static final String USER_READABLE_ID = "user.readable_id";
    public static final String USER_EMAIL = "user.email";
    public static final String USER_PROFILE_PIC_URL = "user.profile_pic_url";


    // ------------------ APIs --------------------------------------------------
    public static class API {

        // Base URI
        public static final String API_BASE_URL = "http://popcornplus-dev.hibikiledo.me";

        // Users related endpoints
        public static final String LOGIN_URL = API_BASE_URL + "/users/login";
        public static final String SIGNUP_URL = API_BASE_URL + "/users/create";
        public static final String USER_INFO_URL = API_BASE_URL + "/users/";
        public static final String UPDATE_PROFILE_IMAGE_URL = API_BASE_URL + "/users/%s/upload";
        public static final String UPDATE_PROFILE_DISPLAY_NAME = API_BASE_URL + "/users/%s/update";
        public static final String ADD_FRIEND = API_BASE_URL + "/users/%s/befriend/%s";
        public static final String REMOVE_FRIEND = API_BASE_URL + "/users/%s/unfriend/%s";

        // Movie related endpoints
        public static final String MOVIE_SEARCH_URL = API_BASE_URL + "/movies/search/";

        // Review
        public static final String REVIEW_CREATE_URL = API_BASE_URL + "/reviews/%s/create";

        // Suggestion related endpoints
        public static final String SUGGESTION_URL = API_BASE_URL + "/suggestions/%s";

        // ------------------ Static resource ---------------------------------------
        public static class RESOURCE {
            public static final String PROFILE_IMG_URL = API_BASE_URL + "/uploads/%s";
        }

    }

    // ------------------ Intent Flags ---------------------------------------------
    public static final String REVALIDATE_TOKEN = "com.popcorn.revalidate_token";
    public static final String NOTIFY_FRIEND_ADDED = "com.popcorn.notify_friend_added";

}
