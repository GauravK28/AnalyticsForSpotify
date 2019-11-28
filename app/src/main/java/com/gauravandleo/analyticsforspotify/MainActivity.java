package com.gauravandleo.analyticsforspotify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class MainActivity extends AppCompatActivity {

    private TopTracksRequest topTracksRequest;

    private List<Song> topTracksAllTime;
    private List<Song> topTracksSixMonths;
    private List<Song> topTracksOneMonth;

    final int radius = 5;
    final int margin = 5;
    final Transformation transformation = new RoundedCornersTransformation(radius, margin);

    private LinearLayout allTimeList;
    private LinearLayout sixMonthsList;
    private LinearLayout oneMonthList;

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

        //TODO: allow user to create a playlist with top songs
        //TODO: add sports standing thing
        //TODO: add clickable links to the songs

        allTimeList = findViewById(R.id.allTimeList);
        sixMonthsList = findViewById(R.id.sixMonthsList);
        oneMonthList = findViewById(R.id.oneMonthList);

        //time_range can be  long_term, medium_term, short_term
        topTracksRequest = new TopTracksRequest(getApplicationContext());
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

        ScrollView allTimeScroll = findViewById(R.id.allTimeScroll);
        ScrollView sixMonthScroll = findViewById(R.id.sixMonthScroll);
        ScrollView oneMonthScroll = findViewById(R.id.oneMonthScroll);
        allTimeScroll.setVisibility(View.VISIBLE);
        sixMonthScroll.setVisibility(View.GONE);
        oneMonthScroll.setVisibility(View.GONE);

        allTime.setOnClickListener(unused -> {
            allTime.setTextColor(Color.BLACK);
            sixMonths.setTextColor(Color.LTGRAY);
            oneMonth.setTextColor(Color.LTGRAY);
            allTimeScroll.setVisibility(View.VISIBLE);
            sixMonthScroll.setVisibility(View.GONE);
            oneMonthScroll.setVisibility(View.GONE);

        });
        sixMonths.setOnClickListener(unused -> {
            allTime.setTextColor(Color.LTGRAY);
            sixMonths.setTextColor(Color.BLACK);
            oneMonth.setTextColor(Color.LTGRAY);
            allTimeScroll.setVisibility(View.GONE);
            sixMonthScroll.setVisibility(View.VISIBLE);
            oneMonthScroll.setVisibility(View.GONE);

        });
        oneMonth.setOnClickListener(unused -> {
            allTime.setTextColor(Color.LTGRAY);
            sixMonths.setTextColor(Color.LTGRAY);
            oneMonth.setTextColor(Color.BLACK);
            allTimeScroll.setVisibility(View.GONE);
            sixMonthScroll.setVisibility(View.GONE);
            oneMonthScroll.setVisibility(View.VISIBLE);

        });
    }

    private void getTopTracks() {
        topTracksRequest.getTopTracks(() -> {
            topTracksAllTime = topTracksRequest.getSongsAllTime();
            addSongs(allTimeList, topTracksAllTime);

            topTracksSixMonths = topTracksRequest.getSongsSixMonth();
            addSongs(sixMonthsList, topTracksSixMonths);

            topTracksOneMonth = topTracksRequest.getSongsOneMonth();
            addSongs(oneMonthList, topTracksOneMonth);

            getSummary(topTracksAllTime, topTracksSixMonths, topTracksOneMonth);
        });
    }

    private void addSongs(LinearLayout list, List<Song> tracks) {
        list.removeAllViews();
        for (int i = 0; i < tracks.size(); i++) {
            View trackChunk = getLayoutInflater().inflate(R.layout.chunk_tracks,list, false);
            Song song = tracks.get(i);

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

            list.addView(trackChunk);
        }
    }

    private void getSummary(List<Song> allTime, List<Song> sixMonths, List<Song> oneMonth) {
        Summary summary = new Summary(this, allTime, sixMonths, oneMonth);
        Button sum = findViewById(R.id.summary);
        sum.setOnClickListener(unused -> {
            summary.display();
        });
    }

    private void backToAuthActivity() {
        Intent newIntent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(newIntent);
    }
}
