package com.gauravandleo.analyticsforspotify;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SongRequest {

    private final String ENDPOINT =  "https://api.spotify.com/v1/me/player/recently-played";

    private ArrayList<Song> songs = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    public SongRequest(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public ArrayList<Song> getRecentlyPlayedTracks(final VolleyCallBack callBack) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, ENDPOINT, null, response -> {
//                    Gson gson = new Gson();
//                    JSONArray jsonArray = response.optJSONArray("items");
//                    for (int n = 0; n < jsonArray.length(); n++) {
//                        try {
//                            JSONObject object = jsonArray.getJSONObject(n);
//                            object = object.optJSONObject("track");
//                            Song song = gson.fromJson(object.toString(), Song.class);
//                            songs.add(song);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }

                    String json = response.toString();
                    JsonObject recentSongs = new JsonParser().parse(json).getAsJsonObject();
                    JsonArray items = recentSongs.get("items").getAsJsonArray();
                    for (int i = 0; i < items.size(); i++) {
                        JsonObject track = items.get(i).getAsJsonObject().get("track").getAsJsonObject();
                        String id = track.get("id").getAsString();
                        String name = track.get("name").getAsString();
                        Song song = new Song(id, name);
                        songs.add(song);
                    }

                    callBack.onSuccess();

                }, error -> {
                    // TODO: Handle error

                }) {
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
        return songs;
    }
}
