package com.example.jareddonohue.artisttracker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by jareddonohue on 11/19/16.
 */

public class ArtistsActivity extends AppCompatActivity{
    private ArrayList<Artist> artistList;
    private HashSet<Artist> artistsInList;
    private ListView artistView;
    final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;



    /*
           on create need to pull information about audio files on users device
           and populate a listview with artist name/audio file name

    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        artistView = (ListView) findViewById(R.id.artists_list);
        artistList = new ArrayList<>();
        artistsInList = new HashSet<>();

        /*
        action listener for News button in top nav bar
        right now it only opens the MainActivity and does not pass any data
         */
        Button openMainActivityBtn = (Button) findViewById(R.id.mainNewsBtn);
        openMainActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //action for button goes here
                Intent openPlaylistActivityIntent = new Intent(ArtistsActivity.this, MainActivity.class);
                startActivity(openPlaylistActivityIntent);
            }
        });
        /*
        action listener for Playlist button in top nav bar
        right now it only opens the PlaylistActivity and does not pass any data
         */
        Button openPlaylistActivityBtn = (Button) findViewById(R.id.mainPlaylistsBtn);
        openPlaylistActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //action for button goes here
                Intent openPlaylistActivityIntent = new Intent(ArtistsActivity.this, PlaylistActivity.class);
                startActivity(openPlaylistActivityIntent);
            }
        });

        getPermissions();
        fetchArtistList();

        //create new instance of adapter class and set it to listView
        ArtistAdapter artistAdt = new ArtistAdapter(this, artistList);
        artistView.setAdapter(artistAdt);
    }


    private void getPermissions(){
        //ask for permissions at runtime
        if (ContextCompat.checkSelfPermission(ArtistsActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ArtistsActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(), "Need permission to read storage.", Toast.LENGTH_LONG).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(ArtistsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
        //end of runtime permissions code block
    }



    public void fetchArtistList() {
        //retrieve song info
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                if(artistsInList.add(new Artist(thisArtist))) {
                    artistList.add(new Artist(thisArtist));
                }
            }
            while (musicCursor.moveToNext());
        }
//
//        //sort the songs alphabetically
//        Collections.sort(artistList, new Comparator<Song>() {
//            public int compare(Song a, Song b) {
//                return a.getArtist().compareTo(b.getArtist());
//            }
//        });
    }
}
