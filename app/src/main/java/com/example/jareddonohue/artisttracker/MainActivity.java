package com.example.jareddonohue.artisttracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

        /*
        grab the artists we want to fetch news on and
        store them in ArrayList<String> artistList
        */
        loadTrackedArtists();

        /*
        fetch XML, parse it and store the NewsItems (Title, Link)
        in ArrayList<NewsItem> listItems
         */
        fetchNewsItems(artistList);

        /*
        display the ListView of NewsItems using the NewsItemAdapter class
         */
        initializeListView();
    }

    private void loadTrackedArtists(){
        artistList = new ArrayList<String>();
        artistList.add("The Growlers");
        artistList.add("STRFKR");
        artistList.add("Kanye West");
        artistList.add("Funkadelic");
        artistList.add("Led Zeppelin");
        artistList.add("Red Hot Chili Peppers");
    }

    private void fetchNewsItems(ArrayList<String> artistList){
        // first search http://pitchfork.com/rss/news/
        // and http://www.rollingstone.com/music/rss for any matches
        // to our tracked artists.

        for(String artist : artistList){
            finalUrl = getGoogleSearchQuery(artist);
            xmlHandler = new HandleXML(finalUrl, artist);
            xmlHandler.fetchXML();
            while(xmlHandler.parsingComplete);
            listItems.add(xmlHandler.getNewsItems().get(0));
            listItems.add(xmlHandler.getNewsItems().get(1));
        }

    }

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

    private String getLinkToUrl(String url){
        String[] getUrl = url.split("url=");
        String link = getUrl[1];
        return link;
    }
}
