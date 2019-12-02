package com.gauravandleo.analyticsforspotify;

public class Song {

    private String id;
    private String name;
    private String artist;
    private String url;
    private String albumArtUrl;
    private int duration_ms;
    private String uri;

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

    public void setAlbumArtUrl(String url) {
        albumArtUrl = url;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getUrl() {
        return  url;
    }

    public String getAlbumArtUrl() {
        return albumArtUrl;
    }

    public int getDuration_ms() {
        return duration_ms;
    }

    public String getUri() {
        return uri;
    }

    public boolean equals(Object other) {
        Song song = (Song) other;
        return name.equals(song.name);
    }

    public String toString() {
        return name + " by " + artist + " url " + url + " duration " + duration_ms + " albumArtUrl " + albumArtUrl ;
    }
}
