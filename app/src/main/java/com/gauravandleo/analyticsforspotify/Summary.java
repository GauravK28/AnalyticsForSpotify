package com.gauravandleo.analyticsforspotify;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class Summary {

    private List<Song> topTracksAllTime;
    private List<Song> topTracksSixMonths;
    private List<Song> topTracksOneMonth;

    private Context context;

    /**
     * Gets the lists from main activity so that they can be used for calculations
     * @param context
     * @param allTime
     * @param sixMonths
     * @param oneMonth
     */
    public Summary (Context context, List<Song> allTime, List<Song> sixMonths, List<Song> oneMonth) {
        this.context = context;
        topTracksAllTime = allTime;
        topTracksSixMonths = sixMonths;
        topTracksOneMonth = oneMonth;
    }

    /**
     * Creates an AlertDialog to display average song lengths and the largest drop/rise
     */
    public void display() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Summary")
                .setMessage("Average song length for... \n"
                + "    All Time: " +  averageTime(topTracksAllTime) + "\n"
                + "    Six Months: " +  averageTime(topTracksSixMonths) + "\n"
                + "    One Month: " +  averageTime(topTracksOneMonth) + "\n\n"
                + largestRise() + "\n"
                + largestDrop());
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    /**
     * Calculates the average song length of a list
     * @param list the top 50 list currenty being used
     * @return a string containing the time that will be displayed in the AlertDialog
     */
    private String averageTime(List<Song> list) {
        int totalMs = 0;
        for (int i = 0; i < list.size(); i++) {
            totalMs += list.get(i).getDuration_ms();
        }
        int avgMs = totalMs / list.size();
        int minutes = avgMs / 60000;
        int seconds = (avgMs % 60000) / 1000;
        return minutes + "min " + seconds + "s";
    }

    /**
     * Finds the song that increased in the most number of ranks from all time to 1 month
     * @return a string containing the song name and the number of ranks it rose to be displayed
     * in the AlertDialog
     */
    private String largestRise() {
        Song song = null;
        int initial = 0;
        int end = 0;
        int diff= 0;
        for (int i = 0; i < topTracksAllTime.size(); i++) {
            if (topTracksOneMonth.contains(topTracksAllTime.get(i))) {
                if (index(topTracksAllTime.get(i)) < i && Math.abs(index(topTracksAllTime.get(i)) - i) > diff) {
                    initial = i;
                    end = index(topTracksAllTime.get(i));
                    diff = Math.abs(index(topTracksAllTime.get(i)) - i);
                    song = topTracksAllTime.get(i);
                }
            }
        }
        initial += 1;
        end += 1;
        if (song == null) {
            return "Largest song rise: N/A \n";
        }
        return "Largest song rise: " +  song.getName() + "            \nFrom "
                + initial + getSuffix(initial) + " to " + end + getSuffix(end) + "\n";
    }

    /**
     * Finds the song that decreased the most number of ranks from all time to 1 month
     * @return a string containing the song name and the number of ranks it dropped to be displayed
     * in the AlertDialog
     */
    private String largestDrop() {
        Song song = null;
        int initial = 0;
        int end = 0;
        int diff= 0;
        for (int i = 0; i < topTracksAllTime.size(); i++) {
            if (topTracksOneMonth.contains(topTracksAllTime.get(i))) {
                if (index(topTracksAllTime.get(i)) > i && Math.abs(index(topTracksAllTime.get(i)) - i) > diff) {
                    initial = i;
                    end = index(topTracksAllTime.get(i));
                    diff = Math.abs(index(topTracksAllTime.get(i)) - i);
                    song = topTracksAllTime.get(i);
                }
            }
        }
        initial += 1;
        end += 1;
        if (song == null) {
            return "Largest song drop: N/A \n";
        }
        return "Largest song drop: " +  song.getName() + "            \nFrom "
                + initial + getSuffix(initial) + " to " + end + getSuffix(end) + "\n";
    }

    private int index(Song song) {
        for (int i = 0; i < topTracksOneMonth.size(); i++) {
            if (song.equals(topTracksOneMonth.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the suffix of the rank number difference so that the number can be displayed as a placement
     * @param n the rank number difference of a song
     * @return
     */
    String getSuffix(int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }
}
