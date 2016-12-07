package com.example.jareddonohue.artisttracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

/**
 * Created by jareddonohue on 11/19/16.
 */

public class ArtistsActivity extends AppCompatActivity{
    private ArrayList<Artist> artistList;
    private ListView artistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        /*
         Get ArtistList from Intent
         */
        Intent intentFromMain = getIntent();
        artistList = intentFromMain.getParcelableArrayListExtra(MainActivity.ARTIST_LIST);

        artistView = (ListView) findViewById(R.id.artists_list);

        //create new instance of adapter class and set it to listView
        ArtistAdapter artistAdt = new ArtistAdapter(this, artistList);
        artistView.setAdapter(artistAdt);

        /*
        action listener for News button in top nav bar
        right now it only opens the MainActivity and does not pass any data
         */
        Button openMainActivityBtn = (Button) findViewById(R.id.mainNewsBtn);
        openMainActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //action for button goes here
                Intent openMainActivityIntent = new Intent(ArtistsActivity.this, MainActivity.class);
                startActivity(openMainActivityIntent);
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
                openPlaylistActivityIntent.putParcelableArrayListExtra(MainActivity.ARTIST_LIST,artistList);
                startActivity(openPlaylistActivityIntent);
            }
        });
    }
}
