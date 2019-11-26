package com.gauravandleo.analyticsforspotify;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class Summary {

    private List<Song> topTracksAllTime;
    private List<Song> topTracksSixMonths;
    private List<Song> topTracksOneMonth;

    public Summary (List<Song> allTime, List<Song> sixMonths, List<Song> oneMonth) {
        topTracksAllTime = allTime;
        topTracksSixMonths = sixMonths;
        topTracksOneMonth = oneMonth;
    }

    public void display() {
        Activity mainActivty = new MainActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivty.getApplicationContext());
        builder.setMessage("Are you sure you want to end the game? This cannot be undone.");
        //builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }


}
