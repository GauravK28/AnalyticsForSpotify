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

    public UserRequest(RequestQueue queue, SharedPreferences sharedPreferences) {
        this.queue = queue;
        this.sharedPreferences = sharedPreferences;
    }

    public User getUser() {
        return user;
    }

    //SAMPLE GET REQUEST
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
