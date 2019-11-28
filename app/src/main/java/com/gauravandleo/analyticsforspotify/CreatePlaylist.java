package com.gauravandleo.analyticsforspotify;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePlaylist {

    private String newPlaylistEndpoint;

    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    //TODO: add functionality in main activity
    //TODO: add POST request to add songs to playlist
    //TODO: GET request to get the user's playlist to check if a "TOP" playlist has already been created
    //TODO:        if it has, open a alertdialog to ask if they want to replace the playlist's tracks with the updated "TOP"
    //TODO:                 so then need to create a PUT request to replace the track in a playlist
    //TODO: add a toast to notify user that the playlist was created

    //after creating the new playlist
    //do a get request to a get a list of the user's playlist to obtain the new playlist's ID
    //then do POST request to add the songs to the playlist

    public CreatePlaylist (Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);

        String userId = sharedPreferences.getString("userid", "No User");
        newPlaylistEndpoint = "https://api.spotify.com/v1/users/" + userId + "/playlists";
    }

    private void preparePayload(String name) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("name", name);
        } catch (JSONException e) {
            System.out.println("JSON EXCEPTION");
            System.out.println(e);
        }
        JsonObjectRequest jsonObjectRequest = newPlaylist(payload);
        queue.add(jsonObjectRequest);
    }

    public void addTracks(JSONObject songs) {

    }

    public String getPlaylists(VolleyCallBack callBack) {
        return "";
    }

    public JsonObjectRequest newPlaylist(JSONObject payload) {
        return new JsonObjectRequest(Request.Method.POST, newPlaylistEndpoint, payload, response -> {

        }, error -> System.out.println("Uh oh, Volley POST Request failed - newPlaylist()")) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
    }


}
