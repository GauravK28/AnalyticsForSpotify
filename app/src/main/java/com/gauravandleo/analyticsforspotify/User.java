package com.gauravandleo.analyticsforspotify;

public class User {
    private String display_name;
    private String email;
    private String id;

    public String getDisplay_name() {
        return display_name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return "Email: " + email+ " id: " + id;
    }
}
