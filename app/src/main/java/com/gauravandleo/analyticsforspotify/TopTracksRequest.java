package com.gauravandleo.analyticsforspotify;

import android.content.Context;
import android.content.SharedPreferences;

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

public class TopTracksRequest {

    //GETS all time top tracks
    private String ENDPOINT =  "https://api.spotify.com/v1/me/top/tracks?limit=50&time_range=";
    private List<Song> songs = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    public TopTracksRequest (Context context, String time_range) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        ENDPOINT = ENDPOINT + time_range;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public List<Song> getTopTracks(final VolleyCallBack callBack){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, ENDPOINT, null, response -> {

                    Gson gson = new Gson();
                    try {
                        JSONArray items = response.getJSONArray("items");
                        System.out.println("ITEMS SIZE" + items.length());
                        Song song;
                        for (int i = 0; i < items.length(); i++) {
                            //gets song name, duration, and id
                            JSONObject track = items.getJSONObject(i);
                            song = gson.fromJson(track.toString(), Song.class);

                            //gets artist
                            JSONArray artists = track.getJSONArray("artists");
                            String artist = artists.getJSONObject(0).getString("name");
                            song.setArtist(artist);

                            //gets album art
                            JSONObject album = track.getJSONObject("album");
                            JSONArray images = album.getJSONArray("images");
                            JSONObject image = images.getJSONObject(2);
                            String imageUrl = image.getString("url");
                            song.setAlbumArtUrl(imageUrl);

                            //gets the song's spotify url
                            JSONObject externalUrls = track.getJSONObject("external_urls");
                            String spotifyUrl = externalUrls.getString("spotify");
                            song.setUrl(spotifyUrl);

                            songs.add(song);
                        }
                        for(Song track: songs) {
                            System.out.println(track);
                        }
                    } catch (JSONException e) {
                        System.out.println("JSON response for getTopTracks is null");
                        System.out.println(e);
                    }

                    callBack.onSuccess();
                }, error -> System.out.println("Uh oh, Volley Request failed - getTopTrack()")) {

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
