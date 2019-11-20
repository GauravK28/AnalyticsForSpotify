package com.gauravandleo.analyticsforspotify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private TextView userView;
    private TextView songView;
    private Button addBtn;
    private Song song;

    private SongRequest songRequest;
    private ArrayList<Song> recentlyPlayedTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songRequest = new SongRequest(getApplicationContext());
        userView = (TextView) findViewById(R.id.user);
        songView = (TextView) findViewById(R.id.song);
        addBtn = (Button) findViewById(R.id.add);

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
            songView.setText(recentlyPlayedTracks.get(0).getName());
            song = recentlyPlayedTracks.get(0);
        }
    }
}
