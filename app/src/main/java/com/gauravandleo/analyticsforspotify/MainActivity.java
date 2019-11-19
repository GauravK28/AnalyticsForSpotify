package com.gauravandleo.analyticsforspotify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;


import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "bb4f93613f0c40d7952b83554e681ec5";
    private static final String REDIRECT_URI = "https://www.spotify.com/us/";
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Setup for authorizing user for built-in auth flow
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();
        System.out.println("YEET");
        //Creates instance of SpotifyAppRemote
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                        System.out.println("did it reach here");
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);
                        System.out.println("or here");
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }
    private void connected() {
        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Aaand we will finish off here.
    }
}
