package com.gauravandleo.analyticsforspotify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;


import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class MainActivity extends AppCompatActivity {

    private TopTracksRequest topTracksRequest;

    private List<Song> topTracks;
    private List<Song> topTracksAllTime = new ArrayList<>();
    private List<Song> topTracksSixMonths = new ArrayList<>();
    private List<Song> topTracksOneMonth = new ArrayList<>();

    final int radius = 5;
    final int margin = 5;
    final Transformation transformation = new RoundedCornersTransformation(radius, margin);

    LinearLayout songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        //userView.setText(sharedPreferences.getString("userid", "No User"));

        Activity auth = new AuthActivity();
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(unused -> {
            AuthenticationClient.stopLoginActivity(auth, 1337);
            backToAuthActivity();
        });


        //TODO:  Create data structures for tracks 6months, 1month
        //TODO: add summary page
        //TODO: allow user to create a playlist with top songs
        //TODO: add sports standing thing
        //TODO: add clickable links to the songs

        songList = findViewById(R.id.songList);

        //time_range can be  long_term, medium_term, short_term
        topTracksRequest = new TopTracksRequest(getApplicationContext(), "long_term");
        getTopTracks();

        initializeButtons();
    }

    private void initializeButtons() {
        Button allTime = findViewById(R.id.allTime);
        Button sixMonths = findViewById(R.id.sixMonths);
        Button oneMonth = findViewById(R.id.oneMonth);
        allTime.setTextColor(Color.BLACK);
        sixMonths.setTextColor(Color.LTGRAY);
        oneMonth.setTextColor(Color.LTGRAY);

        allTime.setOnClickListener(unused -> {
            allTime.setTextColor(Color.BLACK);
            sixMonths.setTextColor(Color.LTGRAY);
            oneMonth.setTextColor(Color.LTGRAY);
            topTracksRequest = new TopTracksRequest(getApplicationContext(), "long_term");
            getTopTracks();
        });
        sixMonths.setOnClickListener(unused -> {
            allTime.setTextColor(Color.LTGRAY);
            sixMonths.setTextColor(Color.BLACK);
            oneMonth.setTextColor(Color.LTGRAY);
            topTracksRequest = new TopTracksRequest(getApplicationContext(), "medium_term");
            getTopTracks();
        });
        oneMonth.setOnClickListener(unused -> {
            allTime.setTextColor(Color.LTGRAY);
            sixMonths.setTextColor(Color.LTGRAY);
            oneMonth.setTextColor(Color.BLACK);
            topTracksRequest = new TopTracksRequest(getApplicationContext(), "short_term");
            getTopTracks();
        });
    }




    private void getTopTracks() {
        topTracksRequest.getTopTracks(() -> {
            topTracks = topTracksRequest.getSongsAllTime();
            addSongs();
        });
    }

    private void addSongs() {
        songList.removeAllViews();
        for (int i = 0; i < topTracks.size(); i++) {
            View trackChunk = getLayoutInflater().inflate(R.layout.chunk_tracks, songList, false);
            Song song = topTracks.get(i);

            //Number
            TextView number = trackChunk.findViewById(R.id.number);
            number.setText(Integer.toString(i + 1));

            //Album art
            ImageView albumArt = trackChunk.findViewById(R.id.albumArt);
            String imageUrl = song.getAlbumArtUrl();
            Picasso.get().load(imageUrl).transform(transformation).into(albumArt);

            //Song title
            TextView title = trackChunk.findViewById(R.id.song);
            title.setText(song.getName());

            //Song artist
            TextView artist = trackChunk.findViewById(R.id.artist);
            artist.setText(song.getArtist());

            songList.addView(trackChunk);
        }
    }

    private void backToAuthActivity() {
        Intent newIntent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(newIntent);
    }
}
