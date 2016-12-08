package com.example.jareddonohue.artisttracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class PlaylistActivity extends AppCompatActivity {
    private ArrayList<Song> songList;
    private ArrayList<Artist> artistList;
    //private ListView songView;
    final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private MediaPlayer mediaPlayer = new MediaPlayer();
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

        ListView songView = (ListView) findViewById(R.id.song_list);
        songList = new ArrayList<>();

        //mediaPlayer = new  MediaPlayer();

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

        getPermissions();
        fetchSongList();

        //create new instance of adapter class and set it to listView
        // dont forget to uncomment this if crashes
        SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);

        //TODO: move this to a separate function
        //play the song once the corresponding list item is clicked
        //setContentView(songView); // look into this line
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Toast.makeText(PlaylistActivity.this, "position: "+position+"\nsongTitle: "+songList.get(position).getTitle()+"\npath:"+songList.get(position).getPath().toString() , Toast.LENGTH_LONG).show();
                                                //try playing media file here, might have to make media player a global variable
                                                //to avoid it from being created every time a song is picked
                                                //TODO: add a method in Song to return the path to the audio file
                                                // try catch block to avoid IOException
                                                // plays song at this point
                                                try {
                                                    mediaPlayer.setDataSource(songList.get(position).getPath().toString());
                                                    mediaPlayer.prepare();
                                                    mediaPlayer.start();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }
        );
    }

    // need to handle runtime permissions in a better fashion
    // as of right now the app crashes the first time this activity is opened
    // TODO:
    private void getPermissions() {
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
        //Uri musicUri = Uri.parse(Environment.getExternalStorageDirectory().getPath()+ "/Music/");

        // some audio may be explicitly marked as not being music
        // when this is passed to the musicResolver.queury it does not fetch
        // files that are externally marked as non music audio files
        String selection = android.provider.MediaStore.Audio.Media.IS_MUSIC + " !=0";

        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            // TODO: here should also fetch the path to the song
            // according to android docs android.provider.MediaStore.Audio.Media.Data
            int pathColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisPath = musicCursor.getString(pathColumn);
                //add path to the song object
                songList.add(new Song(thisId, thisTitle, thisArtist,thisPath));
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