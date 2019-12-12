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

    /**
     * Setups up the UI and starts the calls method
     * for the web request to get the top songs for each list
     * @param savedInstanceState
     */
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

    /**
     * Uses the toptracksrequest class to start the POST request to receive top song data and
     * setups up other UI functionalities
     */
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

    /**
     * Adds a song to a list (alltime, 6months, or one month)
     * @param list the UI list for the specific time frame
     * @param tracks the array that contains the top 50 songs
     *               for a list(all time, 6months, or one month)
     */
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

            //hides the lists that don't need to be displayed
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

    /**
     * Adds an up arrow, down arrow, or dash to indicate a songs position in the 6 month or 1 month
     * compared to the all time list
     * Also adds the specific number of ranks a song went up/down
     * @param list list of the songs
     * @param song specific song object
     */
    private void setStanding(List<Song> list, Song song) {
        moved.setText(String.valueOf(Math.abs(getDifference(list, song))));

        //song went down
        if (getDifference(list, song) < 0) {
            placement.setImageResource(R.drawable.red_down_arrow);

        //song went up
        } else if (getDifference(list, song) != 51 && getDifference(list, song) > 0) {
            placement.setImageResource(R.drawable.green_up_arrow);

        //song is in same position
        } else if (getDifference(list, song) == 0){
            moved.setVisibility(View.INVISIBLE);
            placement.setImageResource(R.drawable.dash);

        //newly added song
        } else {
            moved.setVisibility(View.INVISIBLE);
            placement.setImageResource(R.drawable.green_plus);
        }
    }

    /**
     * Gets the difference of a songs rank between its 1/6 month position to its all time position
     * @param list list of the songs in that time frame
     * @param song specific song object
     * @return
     */
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

    /**
     * SHOULD BE REPLACED with indexOf(Object o) method  instead of creating new method
     * Gets the index of a song in a list
     * @param list
     * @param song
     * @return
     */
    private int index(List<Song> list, Song song) {
        for (int i = 0; i < list.size(); i++) {
            if (song.equals(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * NEEDS TO BE REDUCED/SIMPLIFIED
     * Initializes and sets onclick methods for all the time frame buttons and dynamically changes
     * UI according to what is clicked
     */
    private void initializeButtons() {
        Button allTime = findViewById(R.id.allTime);
        Button sixMonths = findViewById(R.id.sixMonths);
        Button oneMonth = findViewById(R.id.oneMonth);

        allTime.setTextColor(Color.parseColor("#1DB954"));
        sixMonths.setTextColor(Color.GRAY);
        oneMonth.setTextColor(Color.GRAY);

        allTimeScroll = findViewById(R.id.allTimeScroll);
        sixMonthScroll = findViewById(R.id.sixMonthScroll);
        oneMonthScroll = findViewById(R.id.oneMonthScroll);

        allTimeScroll.setVisibility(View.VISIBLE);
        sixMonthScroll.setVisibility(View.GONE);
        oneMonthScroll.setVisibility(View.GONE);

        allTime.setOnClickListener(unused -> {
            allTime.setTextColor(Color.parseColor("#1DB954"));
            sixMonths.setTextColor(Color.GRAY);
            oneMonth.setTextColor(Color.GRAY);
            allTimeScroll.setVisibility(View.VISIBLE);
            sixMonthScroll.setVisibility(View.GONE);
            oneMonthScroll.setVisibility(View.GONE);

        });
        sixMonths.setOnClickListener(unused -> {
            allTime.setTextColor(Color.GRAY);
            sixMonths.setTextColor(Color.parseColor("#1DB954"));
            oneMonth.setTextColor(Color.GRAY);
            allTimeScroll.setVisibility(View.GONE);
            sixMonthScroll.setVisibility(View.VISIBLE);
            oneMonthScroll.setVisibility(View.GONE);

        });
        oneMonth.setOnClickListener(unused -> {
            allTime.setTextColor(Color.GRAY);
            sixMonths.setTextColor(Color.GRAY);
            oneMonth.setTextColor(Color.parseColor("#1DB954"));
            allTimeScroll.setVisibility(View.GONE);
            sixMonthScroll.setVisibility(View.GONE);
            oneMonthScroll.setVisibility(View.VISIBLE);

        });
    }

    /**
     * Sets up the "create playlist" button and start POST web request to add a list to a user's
     * Spotify account
     */
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

    /**
     * Displays the average songs lengths for each list and the highest drop/rise as an AlertDialog
     * @param allTime all time song list
     * @param sixMonths 6 month song list
     * @param oneMonth 1 month song list
     */
    private void getSummary(List<Song> allTime, List<Song> sixMonths, List<Song> oneMonth) {
        Summary summary = new Summary(this, allTime, sixMonths, oneMonth);
        Button sum = findViewById(R.id.summary);
        sum.setOnClickListener(unused -> summary.display());
    }

    /**
     * Sends user back to login page.
     */
    private void backToAuthActivity() {
        Intent newIntent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(newIntent);
    }
}
