package com.example.jareddonohue.artisttracker;

import java.util.ArrayList;

/**
 * Created by jareddonohue on 12/2/16.
 */

public class Artist {

    private String artistName;
    private int numAlbums;
    private int numSongs;
    private boolean hasPhoto;

    public Artist(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getNumAlbums() {
        return numAlbums;
    }

    public int getNumSongs() {
        return numSongs;
    }

    public boolean hasPhoto() {
        return hasPhoto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        return artistName.equals(artist.artistName);

    }

    @Override
    public int hashCode() {
        return artistName.hashCode();
    }
}
