package com.example.jareddonohue.artisttracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String COUNTRY = "us";
    private String LANGUAGE = "en";
    private final static String pitchforkFeed   = "http://pitchfork.com/rss/news/";
    private final static String rollingstoneFee = "http://www.rollingstone.com/music/rss";
    private ArrayList<String> artistList;
    public final static String URL_TO_LOAD = ""; // used for Intent
    private String finalUrl;
    private HandleXML xmlHandler;
    NewsItemAdapter adapter;
    ArrayList<NewsItem> listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Intent openPlaylistActivityIntent = new Intent(MainActivity.this, ArtistsActivity.class);
                startActivity(openPlaylistActivityIntent);
            }
        });
    }

    private class GetNewsOperation extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... params){
            loadTrackedArtists();
            fetchNewsItems(artistList);
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

                // add top two stories to list to be displayed in News Feed
                listItems.add(xmlHandler.getNewsItems().get(0));
                listItems.add(xmlHandler.getNewsItems().get(1));
            }
        }

        /*
        grab the artists we want to fetch news on and
        store them in ArrayList<String> artistList
        */
        private void loadTrackedArtists(){
            artistList = new ArrayList<String>();
            artistList.add("The Growlers");
            artistList.add("STRFKR");
            artistList.add("Kanye West");
            artistList.add("Funkadelic");
            artistList.add("Led Zeppelin");
            artistList.add("Red Hot Chili Peppers");
        }
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
}
