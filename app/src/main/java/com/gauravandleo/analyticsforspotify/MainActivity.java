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

    final int RADIUS = 5;
    final int MARGIN = 5;
    final Transformation transformation = new RoundedCornersTransformation(RADIUS, MARGIN);

    private LinearLayout allTimeList;
    private LinearLayout sixMonthsList;
    private LinearLayout oneMonthList;

    private ScrollView allTimeScroll;
    private ScrollView sixMonthScroll;
    private ScrollView oneMonthScroll;

    private TextView moved;
    private ImageView placement;

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

        //TODO: Make class for headers to remove repeated code
        //TODO: add long press clickable Spotify links to the songs
        //TODO: add info page
        //TODO: add ability to auto sign-in, or just make that the default feature

        allTimeList = findViewById(R.id.allTimeList);
        sixMonthsList = findViewById(R.id.sixMonthsList);
        oneMonthList = findViewById(R.id.oneMonthList);

        topTracksRequest = new TopTracksRequest(getApplicationContext());
        getTopTracks();

        initializeButtons();
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

            setPlaylistButton();
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

            //Standings from All Time
            moved = trackChunk.findViewById(R.id.moved);
            moved.setVisibility(View.VISIBLE);

            placement = trackChunk.findViewById(R.id.standing);
            placement.setVisibility(View.VISIBLE);

            if (list.getId() == R.id.allTimeList) {
                moved.setVisibility(View.GONE);
                placement.setVisibility(View.GONE);
            } else if (list.getId() == R.id.sixMonthsList) {
               setStanding(topTracksSixMonths, song);
            } else {
                setStanding(topTracksOneMonth, song);
            }

            list.addView(trackChunk);
        }
    }

    private void setStanding(List<Song> list, Song song) {
        moved.setText(String.valueOf(Math.abs(getDifference(list, song))));
        //song went down
        if (getDifference(list, song) < 0) {
            placement.setImageResource(R.drawable.down_arrow);
        //song went up
        } else if (getDifference(list, song) != 51 && getDifference(list, song) > 0) {
            placement.setImageResource(R.drawable.up_arrow);
        //song is in same position
        } else if (getDifference(list, song) == 0){
            moved.setVisibility(View.INVISIBLE);
            placement.setImageResource(R.drawable.dash);
        //newly added song
        } else {
            moved.setVisibility(View.INVISIBLE);
            placement.setImageResource(R.drawable.plus);
        }
    }

    private int getDifference(List<Song> list, Song song) {
        int initial = 0;
        int end = 0;
        if (topTracksAllTime.contains(song)) {
            initial = index(topTracksAllTime, song);
            end = index(list, song);
            return initial - end;
        }
        return 51;
    }

    private int index(List<Song> list, Song song) {
        for (int i = 0; i < list.size(); i++) {
            if (song.equals(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private void initializeButtons() {
        Button allTime = findViewById(R.id.allTime);
        Button sixMonths = findViewById(R.id.sixMonths);
        Button oneMonth = findViewById(R.id.oneMonth);

        allTime.setTextColor(Color.parseColor("#1DB954"));
        sixMonths.setTextColor(Color.WHITE);
        oneMonth.setTextColor(Color.WHITE);

        allTimeScroll = findViewById(R.id.allTimeScroll);
        sixMonthScroll = findViewById(R.id.sixMonthScroll);
        oneMonthScroll = findViewById(R.id.oneMonthScroll);

        allTimeScroll.setVisibility(View.VISIBLE);
        sixMonthScroll.setVisibility(View.GONE);
        oneMonthScroll.setVisibility(View.GONE);

        allTime.setOnClickListener(unused -> {
            allTime.setTextColor(Color.parseColor("#1DB954"));
            sixMonths.setTextColor(Color.WHITE);
            oneMonth.setTextColor(Color.WHITE);
            allTimeScroll.setVisibility(View.VISIBLE);
            sixMonthScroll.setVisibility(View.GONE);
            oneMonthScroll.setVisibility(View.GONE);

        });
        sixMonths.setOnClickListener(unused -> {
            allTime.setTextColor(Color.WHITE);
            sixMonths.setTextColor(Color.parseColor("#1DB954"));
            oneMonth.setTextColor(Color.WHITE);
            allTimeScroll.setVisibility(View.GONE);
            sixMonthScroll.setVisibility(View.VISIBLE);
            oneMonthScroll.setVisibility(View.GONE);

        });
        oneMonth.setOnClickListener(unused -> {
            allTime.setTextColor(Color.WHITE);
            sixMonths.setTextColor(Color.WHITE);
            oneMonth.setTextColor(Color.parseColor("#1DB954"));
            allTimeScroll.setVisibility(View.GONE);
            sixMonthScroll.setVisibility(View.GONE);
            oneMonthScroll.setVisibility(View.VISIBLE);

        });
    }

    private void setPlaylistButton() {
        Button createPlaylist = findViewById(R.id.createPlaylist);
        createPlaylist.setOnClickListener(unused -> {
            if (allTimeScroll.getVisibility() == View.VISIBLE) {
                CreatePlaylistRequest createPlaylistRequest =
                        new CreatePlaylistRequest(this, "All Time Top Songs", topTracksAllTime);
                createPlaylistRequest.preparePlaylistPayload();

            } else if (sixMonthScroll.getVisibility() == View.VISIBLE) {
                CreatePlaylistRequest createPlaylistRequest =
                        new CreatePlaylistRequest(this, "6 Month Top Songs", topTracksSixMonths);
                createPlaylistRequest.preparePlaylistPayload();

            } else {
                CreatePlaylistRequest createPlaylistRequest =
                        new CreatePlaylistRequest(this, "One Month Top Songs", topTracksOneMonth);
                createPlaylistRequest.preparePlaylistPayload();
            }
        });
    }

    private void getSummary(List<Song> allTime, List<Song> sixMonths, List<Song> oneMonth) {
        Summary summary = new Summary(this, allTime, sixMonths, oneMonth);
        Button sum = findViewById(R.id.summary);
        sum.setOnClickListener(unused -> summary.display());
    }

    private void backToAuthActivity() {
        Intent newIntent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(newIntent);
    }
}
