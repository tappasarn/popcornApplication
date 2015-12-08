package com.popcorn.config;


public class Configurations {

    public static final String SHARED_PREF_KEY = "com.popcorn.sharedPreferences";

    // ------------------ User information --------------------------------------
    public static final String USER_TOKEN = "user.token";
    public static final String USER_READABLE_ID = "user.readable_id";
    public static final String USER_EMAIL = "user.email";
    public static final String USER_PROFILE_PIC_URL = "user.profile_pic_url";


    // ------------------ APIs --------------------------------------------------

    public static class API {

        public static final String API_BASE_URL = "http://172.29.50.45:3000";

        // Users related endpoints
        public static final String LOGIN_URL = API_BASE_URL + "/users/login";
        public static final String SIGNUP_URL = API_BASE_URL + "/users/create";
        public static final String USER_INFO_URL = API_BASE_URL + "/users/";

        // add new friend
        public static final String ADD_FRIEND = API_BASE_URL + "/users/%s/befriend/%s";
        // Movie related endpoints
        public static final String MOVIE_SEARCH_URL = API_BASE_URL + "/movies/search/";

        // Review
        public static final String REVIEW_CREATE_URL = API_BASE_URL + "/reviews/%s/create";

        // ------------------ Static resource ---------------------------------------
        public static class RESOURCE {

            public static final String PROFILE_IMG_URL = API_BASE_URL + "/uploads/%s";

        }

    }

    // ------------------ Intent Flags ------------------------------------------

    public static final String REVALIDATE_TOKEN = "com.popcorn.revalidate_token";

}
