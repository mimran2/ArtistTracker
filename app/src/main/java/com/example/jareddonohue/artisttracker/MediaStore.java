package com.example.jareddonohue.artisttracker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jareddonohue on 11/26/16.
 */

public class MediaStore extends AppCompatActivity {
    private ArrayList<Song> songList;

    public MediaStore(){
    }

    public ArrayList<Song> getSongList(){
        return songList;
    }

    public ArrayList<Song> getArtistList(){
        return songList;
    }
}
