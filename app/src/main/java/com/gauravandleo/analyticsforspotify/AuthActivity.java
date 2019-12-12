package com.gauravandleo.analyticsforspotify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

/**
 * Authentication logic used from
 * https://towardsdatascience.com/using-the-spotify-api-with-your-android-application-the-essentials-1a3c1bc36b9e
 */
public class AuthActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "bb4f93613f0c40d7952b83554e681ec5";
    private static final String REDIRECT_URI = "http://com.gauravandleo.analyticsforspotify/callback";
    private static final int REQUEST_CODE = 1337;
    private static final String SCOPES = "user-read-recently-played,"
                    + "user-top-read,"
                    + "user-library-modify,"
                    + "playlist-modify-private,"
                    + "playlist-modify-public,"
                    + "user-follow-read,"
                    + "user-read-email,"
                    + "user-read-private";

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    /**
     * Setups up onclick listenenr so that user can be sent to Spotify Auth after pressing "login"
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_auth);

        Button login = findViewById(R.id.login);
        login.setOnClickListener(unused -> authenticateSpotify());

        sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);
    }

    /**
     * Sends user to the Spotify login screen after they click "login" button
     */
    private void authenticateSpotify() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest
                .Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setShowDialog(true)
                .setScopes(new String[]{SCOPES});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    /**
     * Gets AUTH token so that app can access user's data through Spotify WebAPI
     * @param requestCode code to start request
     * @param resultCode coded after request (access granted or denied)
     * @param intent stores the auth data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    Log.d("STARTING", "GOT AUTH TOKEN");
                    editor.apply();
                    waitForUserInfo();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    /**
     * Stores the User ID in a User object so that it can be used later on in the app
     */
    private void waitForUserInfo() {
        UserRequest userRequest = new UserRequest(queue, sharedPreferences);
        userRequest.get(() -> {
            User user = userRequest.getUser();
            editor = getSharedPreferences("SPOTIFY", 0).edit();
            editor.putString("userid", user.getId());
            Log.d("STARTING", "GOT USER INFORMATION");
            editor.commit();
            startMainActivity();
        });
    }

    /**
     * Switches to the main screen
     */
    private void startMainActivity() {
        Intent newIntent = new Intent(AuthActivity.this, MainActivity.class);
        startActivity(newIntent);
    }
}
