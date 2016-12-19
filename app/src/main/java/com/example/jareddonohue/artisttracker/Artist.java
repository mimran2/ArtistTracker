package com.example.jareddonohue.artisttracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by jareddonohue on 12/2/16.
 */

public class Artist implements Parcelable{

    private String artistName;
    private int numAlbums;
    private int numSongs;
    private boolean hasPhoto;

    public Artist(String artistName) {
        this.artistName = artistName;
    }

    protected Artist(Parcel in) {
        artistName = in.readString();
        numAlbums = in.readInt();
        numSongs = in.readInt();
        hasPhoto = in.readByte() != 0;
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artistName);
        dest.writeInt(numAlbums);
        dest.writeInt(numSongs);
        dest.writeByte((byte) (hasPhoto ? 1 : 0));
    }
}
