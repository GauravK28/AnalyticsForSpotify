package com.gauravandleo.analyticsforspotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePlaylistRequest {

    private String playlistEndpoint;
    private String addTracksEndpoint;

    private List<Song> songs;

    private List<Playlist> playlists = new ArrayList();

    private String newPlaylistName;
    private String newPlaylistId;

    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private Context context;

    //TODO: GET request to get the user's playlist to check if a "TOP" playlist has already been created
    //TODO:        if it has, open a alertdialog to ask if they want to replace the playlist's tracks with the updated "TOP"
    //TODO:                 so then need to create a PUT request to replace the track in a playlist
    //TODO: add a toast to notify user that the playlist was created

    //after creating the new playlist
    //do a get request to a get a list of the user's playlist to obtain the new playlist's ID
    //then do POST request to add the songs to the playlist

    public CreatePlaylistRequest(Context context, String name, List<Song> list) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        this.context = context;

        newPlaylistName = name;

        String userId = sharedPreferences.getString("userid", "No User");
        playlistEndpoint = "https://api.spotify.com/v1/users/" + userId + "/playlists";

        songs = list;
    }

    public void preparePlaylistPayload() {
        JSONObject payload = new JSONObject();
        try {
            payload.put("name", newPlaylistName);
            newPlaylist(payload);
        } catch (JSONException e) {
            System.out.println("JSON EXCEPTION");
            System.out.println(e);
        }
    }

    private void setNewPlaylistId(String name) {
        for (int i = 0; i <  playlists.size(); i++) {
            System.out.println(playlists.get(i).getName());
            if (playlists.get(i).getName().equals(name)) {
                newPlaylistId = playlists.get(i).getId();
                return;
            }
        }
    }

    private void prepareSongPayload() {
        JSONObject payload = new JSONObject();
        JSONArray songUris = new JSONArray();
        for (int i = 0; i < songs.size(); i++) {
            songUris.put(songs.get(i).getUri());
        }

        try {
            payload.put("uris", songUris);
            System.out.println("ADD TRACKS ENDPINT " + addTracksEndpoint);
            JsonObjectRequest jsonObjectRequest = addTracks(payload);
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            System.out.println(e);
        }
    }

    private JsonObjectRequest addTracks(JSONObject payload) {
        return new JsonObjectRequest(Request.Method.POST, addTracksEndpoint, payload, response -> {
            Toast toast = Toast.makeText(context, newPlaylistName + " playlist created", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }, error -> System.out.println("Uh oh, Volley POST Request failed -addTracks()")) {

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

    private void getPlaylists(VolleyCallBack callBack) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(playlistEndpoint, null, response -> {
            System.out.println("RESPONSE " + response.toString());
            Gson gson = new Gson();
            JSONArray items = null;
            JSONObject playlist = null;
            try {
                items = response.getJSONArray("items");
            } catch (JSONException e) {
                System.out.println(e);
            }
            System.out.println(items.toString());
            System.out.println("ITEM LENGTH " + items.length());
            for (int i = 0; i < items.length(); i++) {
                try {
                    playlist = items.getJSONObject(i);
                } catch (JSONException e) {
                    System.out.println(e);
                }
                Playlist p = gson.fromJson(playlist.toString(), Playlist.class);
                playlists.add(p);
            }

            callBack.onSuccess();

        }, error ->System.out.println("Uh oh, Volley GET Request failed - getPlaylists()")) {
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

    private void newPlaylist(JSONObject payload) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, playlistEndpoint, payload, response -> {
            getPlaylists(() -> {
                setNewPlaylistId(newPlaylistName);
                addTracksEndpoint = "https://api.spotify.com/v1/playlists/" + newPlaylistId + "/tracks";
                prepareSongPayload();
            });
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
        queue.add(jsonObjectRequest);


    }


}
