package com.gauravandleo.analyticsforspotify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private SongRequest songRequest;
    private ArrayList<Song> recentlyPlayedTracks;
    private Song song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songRequest = new SongRequest(getApplicationContext());
        TextView userView = findViewById(R.id.user);
        TextView songView = findViewById(R.id.song);
        Button addBtn = findViewById(R.id.add);

//        addBtn.setOnClickListener(unused -> {
//            songRequest.addSongToLibrary(this.song);
//            if (recentlyPlayedTracks.size() > 0) {
//                recentlyPlayedTracks.remove(0);
//            }
//            updateSong();
//        });

        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        userView.setText(sharedPreferences.getString("userid", "No User"));

        getTracks();
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
}
