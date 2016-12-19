package com.example.jareddonohue.artisttracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

/**
 * Created by jareddonohue on 11/19/16.
 */

public class ArtistsActivity extends AppCompatActivity{
    private ArrayList<Artist> artistList;
    private ArrayList<NewsItem> listItems;
    private ArrayList<String> artistNames;
    private String COUNTRY = "us";
    private String LANGUAGE = "en";
    private ListView artistView;
    private String finalUrl;
    private HandleXML xmlHandler;
    ArrayList<NewsItem> googleNewsItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        listItems = new ArrayList<>();

        /*
         Get ArtistList from Intent
         */
        Intent intentFromMain = getIntent();
        this.artistList = intentFromMain.getParcelableArrayListExtra(MainActivity.ARTIST_LIST);
        this.artistNames = intentFromMain.getStringArrayListExtra("artist_names");

        new ArtistsActivity.GetArtistNewsOperation().execute("");

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
                openMainActivityIntent.putParcelableArrayListExtra(MainActivity.SAVED_LIST_ITEMS,listItems);
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
                openPlaylistActivityIntent.putStringArrayListExtra("artist_names",artistNames);
                startActivity(openPlaylistActivityIntent);
            }
        });
    }

    private void initializeListView(){
        artistView = (ListView) findViewById(R.id.artists_list);
        //create new instance of adapter class and set it to listView
        NewsItemAdapter adapter = new NewsItemAdapter(this, listItems);
        artistView.setAdapter(adapter);
        artistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = listItems.get(position).getLink();
                Intent intent = new Intent(getApplicationContext(), ArticleView.class);
                intent.putExtra(MainActivity.URL_TO_LOAD, url);
                startActivity(intent);
            }
        });
        adapter.notifyDataSetChanged();
    }

    // format the Artist to Google RSS format (i.e. Red Hot Chili Peppers
    // ==> red+hot+chili+peppers) and return the fully formatted Google
    // RSS query string
    private String getGoogleSearchQuery(String query) {
        query = query.toLowerCase();
        String[] words = query.split(" ");
        String result = "";

        int i;
        for (i = 0; i < words.length - 1; i++) {
            result += words[i] + "+";
        }
        result += words[i];

        String queryString = "https://news.google.com/news/feeds?pz=1&cf=all&ned="
                + LANGUAGE + "&hl=" + COUNTRY + "&q=" + result + "&output=rss";

        return queryString;
    }

    private class GetArtistNewsOperation extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... params){
            fetchNewsItems(artistNames);
            return "done";
        }

        @Override
        protected void onPostExecute(String result){
            /*
            get rid of the progress bar
             */
            findViewById(R.id.progressBar2).setVisibility(View.GONE);

            /*
            show list
            */
            findViewById(R.id.artists_list).setVisibility(View.VISIBLE);

            /*
            display the ListView of NewsItems using the NewsItemAdapter class
            */
            initializeListView();

        }

        /*
        fetch XML, parse it and store the NewsItems (Title, Link)
        in ArrayList<NewsItem> listItems
        */
        private void fetchNewsItems(ArrayList<String> artistList) {
            // first search http://pitchfork.com/rss/news/
            // and http://www.rollingstone.com/music/rss for any matches
            // with our tracked artists.


            for(String artist : artistList){
                finalUrl = getGoogleSearchQuery(artist);
                xmlHandler = new HandleXML(finalUrl, artist, false);
                xmlHandler.fetchXML();
                while(xmlHandler.parsingComplete);

                // add top top story to list to be displayed in News Feed
                googleNewsItems.addAll(xmlHandler.getNewsItems());
            }

//            for (String artist : artistList) {
//                finalUrl = getGoogleSearchQuery(artist);
//                xmlHandler = new HandleXML(finalUrl, artist, false);
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        xmlHandler.fetchXML();
//                        while (xmlHandler.parsingComplete) ;
//                        googleNewsItems.addAll(xmlHandler.getNewsItems());
//                    }
//                });
//                thread.start();
//            }


            // Algorithm to select from News Lists
            // Add selected new items to the listItems to display

            for(NewsItem newsItem : googleNewsItems){
                if(newsItem.getTitle().length() > 0){
                    listItems.add(newsItem);
                }
            }
        }
    }
}
