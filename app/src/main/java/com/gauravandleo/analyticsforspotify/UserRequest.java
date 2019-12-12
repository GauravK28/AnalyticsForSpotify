package com.gauravandleo.analyticsforspotify;

import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class UserRequest {

    private static final String ENDPOINT = "https://api.spotify.com/v1/me";
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private User user;

    /**
     * Gets the shared Preferences and sets up Volley queue
     * @param queue
     * @param sharedPreferences
     */
    public UserRequest(RequestQueue queue, SharedPreferences sharedPreferences) {
        this.queue = queue;
        this.sharedPreferences = sharedPreferences;
    }

    /**
     * Gets the user
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * GET request to receive the current user and stored the user's email and name in a User object
     * @param callBack indicating if web request was successful
     */
    public void get(final VolleyCallBack callBack) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(ENDPOINT, null, response -> {

            Gson gson = new Gson();
            user = gson.fromJson(response.toString(), User.class);

            callBack.onSuccess();

        }, error -> get(() -> {

        })) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }
}
