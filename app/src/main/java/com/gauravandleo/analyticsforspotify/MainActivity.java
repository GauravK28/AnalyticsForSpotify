package com.gauravandleo.analyticsforspotify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Song> topTracksAllTime;
    private TopTracksRequest topTracksRequest;

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


        //time_range can be  long_term, medium_term, short_term
        topTracksRequest = new TopTracksRequest(getApplicationContext(), "long_term");
        getTopTracks();
    }

    private void getTopTracks() {
        topTracksRequest.getTopTracks(() -> {

        });
    }

    private void backToAuthActivity() {
        Intent newIntent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(newIntent);
    }

    private void addAlbumArt(ImageView albumArt) {
        albumArt = findViewById(R.id.albumArt);
        final int radius = 5;
        final int margin = 5;
        final Transformation transformation = new RoundedCornersTransformation(radius, margin);
        Picasso.get().load("https://i.scdn.co/image/5a73a056d0af707b4119a883d87285feda543fbb").transform(transformation).into(albumArt);
    }
}
