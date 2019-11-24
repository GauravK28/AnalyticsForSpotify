package com.gauravandleo.analyticsforspotify;

public class Song {

    private String id;
    private String name;
    private String artist;

    public Song(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String toString() {
        return name + " by " + artist;
    }
}
