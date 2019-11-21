package com.gauravandleo.analyticsforspotify;

import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class UserRequest {
    private static final String ENDPOINT = "https://api.spotify.com/v1/me";
    private SharedPreferences msharedPreferences;
    private RequestQueue mqueue;
    private User user;

    public UserRequest(RequestQueue queue, SharedPreferences sharedPreferences) {
        mqueue = queue;
        msharedPreferences = sharedPreferences;
    }

    public User getUser() {
        return user;
    }

    //SAMPLE GET REQUEST
    public void get(final VolleyCallBack callBack) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(ENDPOINT, null, response -> {
            //Gson gson = new Gson();
            //user = gson.fromJson(response.toString(), User.class);
            user = new User();
            String json = response.toString();
            JsonObject currentUser = new JsonParser().parse(json).getAsJsonObject();
            user.setId(currentUser.get("id").getAsString());
            user.setCountry(currentUser.get("country").getAsString());
            user.setDisplay_name(currentUser.get("display_name").getAsString());
            user.setEmail(currentUser.get("email").getAsString());

            callBack.onSuccess();

        }, error -> get(() -> {

        })) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = msharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        mqueue.add(jsonObjectRequest);
    }

}
