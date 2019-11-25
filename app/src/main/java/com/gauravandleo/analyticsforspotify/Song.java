package com.gauravandleo.analyticsforspotify;

public class Song {

    private String id;
    private String name;
    private String artist;
    private String url;
    private String albumArtUrl;

    public Song(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAlbumArtUrl() {

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name + " by " + artist + " url " + url + " albumArt " + albumArtUrl;
    }
}
