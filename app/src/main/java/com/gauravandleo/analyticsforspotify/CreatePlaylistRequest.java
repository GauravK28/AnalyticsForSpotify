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

    /**
     * Setting up a playlist to be created by initialized the name of the playlist and the specific
     * endpoint that needs to be access based on the user's id
     * @param context context of the main activity
     * @param name name of the playlist being created
     * @param list the list that is being accessed
     */
    public CreatePlaylistRequest(Context context, String name, List<Song> list) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        this.context = context;

        newPlaylistName = name;

        String userId = sharedPreferences.getString("userid", "No User");
        playlistEndpoint = "https://api.spotify.com/v1/users/" + userId + "/playlists";

        songs = list;
    }

    /**
     * Creates JSON object for creating a playlist
     */
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

    /**
     * Sets the id of a playlist so that it can be used later in a POST request to populate the
     * newly created playlist with the top 50 songs
     * @param name name of the playlist
     */
    private void setNewPlaylistId(String name) {
        for (int i = 0; i <  playlists.size(); i++) {
            if (playlists.get(i).getName().equals(name)) {
                newPlaylistId = playlists.get(i).getId();
                return;
            }
        }
    }

    /**
     * Creates JSON Object containing all 50 songs that are to be added to the playlist
     */
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

    /**
     * POST request to add all the songs to the created newly created playlist
     * @param payload the JSON Object containing all the top 50 songs
     * @return
     */
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

    /**
     * GET request to get a list of all the user's playlist because the newly created playlist's
     * ID was unknown upon doing the POST request to create the new playlist
     * the playlists are then added to a list which is then called in setNewPlaylistId() to
     * access the new playlists ID
     * @param callBack indicating if web request was successful
     */
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

    /**
     * POST request to create the new playlist
     * @param payload the JSON Object containing the playlist's name
     */
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
