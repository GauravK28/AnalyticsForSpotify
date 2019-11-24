package com.gauravandleo.analyticsforspotify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private SongRequest songRequest;
    private ArrayList<Song> recentlyPlayedTracks;
    private Song song;
    private TopTracksRequest topTracksRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songRequest = new SongRequest(getApplicationContext());
        TextView userView = findViewById(R.id.user);

        Activity auth = new AuthActivity();
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(unused -> {
            AuthenticationClient.stopLoginActivity(auth, 1337);
            backToAuthActivity();
        });

        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        userView.setText(sharedPreferences.getString("userid", "No User"));
        getTracks();
        topTracksRequest = new TopTracksRequest(getApplicationContext());
        getTopTracks();
    }
    private void getTopTracks() {
        topTracksRequest.getTopTracks(() -> {

        });
    }

    private void getTracks() {
        songRequest.getRecentlyPlayedTracks(() -> {
            recentlyPlayedTracks = songRequest.getSongs();
            updateSong();
        });
    }

    private void updateSong() {
        if (recentlyPlayedTracks.size() > 0) {
            TextView songView = findViewById(R.id.song);
            songView.setText(recentlyPlayedTracks.get(0).getName());
            song = recentlyPlayedTracks.get(0);
        }
    }

    private void backToAuthActivity() {
        Intent newIntent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(newIntent);
    }
}
