package com.example.jareddonohue.artisttracker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private String COUNTRY = "us";
    private String LANGUAGE = "en";
    private final static String pitchforkFeed   = "http://pitchfork.com/rss/news/";
    private final static String rollingstoneFee = "http://www.rollingstone.com/music/rss";

    private ArrayList<Artist> artistList;
    private HashSet<Artist> artistsInList;
    private ArrayList<String> artistNames;

    public final static String URL_TO_LOAD = ""; // used for Intent
    final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public final static String ARTIST_LIST = "com.example.jareddonohue.artisttracker.ARTIST_LIST";

    private String finalUrl;
    private HandleXML xmlHandler;
    NewsItemAdapter adapter;
    ArrayList<NewsItem> listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermissions();
        fetchArtistList();
        artistNames = getArtistNames();

        new GetNewsOperation().execute("");

        /*
        action listener for Playlist button in top nav bar
        right now it only opens the PlaylistActivity and does not pass any data
         */
        Button openPlaylistActivityBtn = (Button) findViewById(R.id.mainPlaylistsBtn);
        openPlaylistActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //action for button goes here
                Intent openPlaylistActivityIntent = new Intent(MainActivity.this, PlaylistActivity.class);
                openPlaylistActivityIntent.putParcelableArrayListExtra(ARTIST_LIST,artistList);
                startActivity(openPlaylistActivityIntent);
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
                Intent openArtistActivityIntent = new Intent(MainActivity.this, ArtistsActivity.class);
                openArtistActivityIntent.putParcelableArrayListExtra(ARTIST_LIST,artistList);
                startActivity(openArtistActivityIntent);
            }
        });
    }

    private class GetNewsOperation extends AsyncTask<String, String, String> {

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
            findViewById(R.id.progressBar).setVisibility(View.GONE);

            /*
            show list
            */
            findViewById(R.id.all_artists_news_list).setVisibility(View.VISIBLE);

            /*
            display the ListView of NewsItems using the NewsItemAdapter class
            */
            initializeListView();
        }

        /*
        fetch XML, parse it and store the NewsItems (Title, Link)
        in ArrayList<NewsItem> listItems
        */
        private void fetchNewsItems(ArrayList<String> artistList){
            // first search http://pitchfork.com/rss/news/
            // and http://www.rollingstone.com/music/rss for any matches
            // with our tracked artists.

            for(String artist : artistList){
                finalUrl = getGoogleSearchQuery(artist);
                xmlHandler = new HandleXML(finalUrl, artist);
                xmlHandler.fetchXML();
                while(xmlHandler.parsingComplete);

                // add top top story to list to be displayed in News Feed
                listItems.add(xmlHandler.getNewsItems().get(0));
            }
        }

        /*
        grab the artists we want to fetch news on and
        store them in ArrayList<String> artistList
        */
//        private void loadTrackedArtists(){
//            artistList = new ArrayList<String>();
//        }
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

    private void initializeListView(){
        adapter = new NewsItemAdapter(this, listItems);
        ListView listView = (ListView) findViewById(R.id.all_artists_news_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = getLinkToUrl(listItems.get(position).getLink());
                Intent intent = new Intent(getApplicationContext(), ArticleView.class);
                intent.putExtra(URL_TO_LOAD, url);
                startActivity(intent);
            }
        });
        adapter.notifyDataSetChanged();
    }

    // parse RSS <link> for the actual url
    private String getLinkToUrl(String url){
        String[] getUrl = url.split("url=");
        String link = getUrl[1];
        return link;
    }

    private ArrayList<String> getArtistNames(){
        ArrayList<String> artistNames = new ArrayList<String>();
        for(Artist a : artistList){
            artistNames.add(a.getArtistName());
        }
        return artistNames;
    }

    private void fetchArtistList() {
        artistsInList = new HashSet<>();
        artistList = new ArrayList<>();
        //retrieve song info
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = android.provider.MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, null);
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

        //sort the songs alphabetically
        Collections.sort(artistList, new Comparator<Artist>() {
            public int compare(Artist a, Artist b) {
                return a.getArtistName().compareTo(b.getArtistName());
            }
        });
    }

    private void getPermissions(){
        //ask for permissions at runtime
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(), "Need permission to read storage.", Toast.LENGTH_LONG).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
        //end of runtime permissions code block
    }
}
