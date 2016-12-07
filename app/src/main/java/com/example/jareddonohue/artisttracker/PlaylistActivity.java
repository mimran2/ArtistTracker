package com.example.jareddonohue.artisttracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class PlaylistActivity extends AppCompatActivity {
    private ArrayList<Song> songList;
    private ArrayList<Artist> artistList;
    private ListView songView;
    final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    /*
            on create need to pull information about audio files on users device
            and populate a listview with artist name/audio file name

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        Intent intentFromMain = getIntent();
        artistList = intentFromMain.getParcelableArrayListExtra(MainActivity.ARTIST_LIST);

        songView = (ListView) findViewById(R.id.song_list);
        songList = new ArrayList<Song>();

        getPermissions();
        fetchSongList();

        /*
        action listener for News button in top nav bar
        right now it only opens the MainActivity and does not pass any data
         */
        Button openMainActivityBtn = (Button) findViewById(R.id.mainNewsBtn);
        openMainActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //action for button goes here
                Intent openMainActivityIntent = new Intent(PlaylistActivity.this, MainActivity.class);
                startActivity(openMainActivityIntent);
            }
        });

                /*
        action listener for Artists button in top nav bar
        right now it only opens the ArtistsActivity and does not pass any data
         */
        Button openArtistsActivityBtn = (Button) findViewById(R.id.mainArtistsBtn);
        openArtistsActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //action for button goes here
                Intent openArtistActivityIntent = new Intent(PlaylistActivity.this, ArtistsActivity.class);
                openArtistActivityIntent.putParcelableArrayListExtra(MainActivity.ARTIST_LIST,artistList);
                startActivity(openArtistActivityIntent);
            }
        });

        //create new instance of adapter class and set it to listView
        SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);
    }

    private void getPermissions(){
        //ask for permissions at runtime
        if (ContextCompat.checkSelfPermission(PlaylistActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PlaylistActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(), "Need permission to read storage.", Toast.LENGTH_LONG).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(PlaylistActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
        //end of runtime permissions code block
    }

    //method to request runtime permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void fetchSongList() {
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
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }

        //sort the songs alphabetically
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }
}
