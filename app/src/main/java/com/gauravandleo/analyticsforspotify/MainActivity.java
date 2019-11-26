package com.gauravandleo.analyticsforspotify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.w3c.dom.Text;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class MainActivity extends AppCompatActivity {

    private List<Song> topTracks;
    private TopTracksRequest topTracksRequest;

    final int radius = 5;
    final int margin = 5;
    final Transformation transformation = new RoundedCornersTransformation(radius, margin);

    LinearLayout songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView userView = findViewById(R.id.user);
        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        userView.setText(sharedPreferences.getString("userid", "No User"));

        Activity auth = new AuthActivity();
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(unused -> {
            AuthenticationClient.stopLoginActivity(auth, 1337);
            backToAuthActivity();
        });

        RadioGroup timeFrame = findViewById(R.id.timeFrame);
        songList = findViewById(R.id.songList);
        int id = timeFrame.getCheckedRadioButtonId();
        if (id == -1 || id == R.id.allTime) {

            //time_range can be  long_term, medium_term, short_term
            topTracksRequest = new TopTracksRequest(getApplicationContext(), "long_term");
            getTopTracks();
            // TODO: add the average song length

        } else if (id == R.id.sixMonths) {
            topTracksRequest = new TopTracksRequest(getApplicationContext(), "medium_term");
            getTopTracks();
        } else if (id == R.id.oneMonth) {
            topTracksRequest = new TopTracksRequest(getApplicationContext(), "short_term");
            getTopTracks();
        }
    }

    private void getTopTracks() {
        topTracksRequest.getTopTracks(() -> {
            topTracks = topTracksRequest.getSongs();
            addSongs();
        });

    }

    private void addSongs() {
        System.out.println("TOP TRACK SIZE " + topTracks.size());
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
            System.out.println(i + " " + song.getName());
        }
        System.out.println("CHILD COUNT " + songList.getChildCount());
    }

    private void backToAuthActivity() {
        Intent newIntent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(newIntent);
    }
}
