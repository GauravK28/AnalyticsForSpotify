package com.gauravandleo.analyticsforspotify;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class Summary {

    private List<Song> topTracksAllTime;
    private List<Song> topTracksSixMonths;
    private List<Song> topTracksOneMonth;

    private Context context;

    public Summary (Context context, List<Song> allTime, List<Song> sixMonths, List<Song> oneMonth) {
        this.context = context;
        topTracksAllTime = allTime;
        topTracksSixMonths = sixMonths;
        topTracksOneMonth = oneMonth;
    }

    public void display() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Summary")
                .setMessage("Average song length for... \n"
                + "    All Time: " +  averageTime(topTracksAllTime) + "\n"
                + "    Six Months: " +  averageTime(topTracksSixMonths) + "\n"
                + "    One Month: " +  averageTime(topTracksOneMonth) + "\n\n"
                + "Largest song drop:             \n From  to \n"
                + "Largest song rise:             \n From  to \n");
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    private String averageTime(List<Song> list) {
        int totalMs = 0;
        for (int i = 0; i < list.size(); i++) {
            totalMs += list.get(i).getDuration_ms();
        }
        int avgMs = totalMs / list.size();
        System.out.println(avgMs);
        int minutes = avgMs / 60000;
        int seconds = (avgMs % 60000) / 1000;
        return minutes + "min " + seconds + "s";
    }
}
