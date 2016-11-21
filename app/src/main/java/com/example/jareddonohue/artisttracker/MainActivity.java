package com.example.jareddonohue.artisttracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String COUNTRY = "us";
    private String LANGUAGE = "en";
    private String QUERY = "Red Hot Chili Peppers";
    private String finalUrl;
    private HandleXML xmlHandler;
    ArrayAdapter<NewsItem> adapter;
    ArrayList<NewsItem> listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        finalUrl = getSearchQuery(QUERY);
        xmlHandler = new HandleXML(finalUrl);
        xmlHandler.fetchXML();
        while(xmlHandler.parsingComplete);
        listItems = xmlHandler.getNewsItems();

        initializeListView();
    }

    private String getSearchQuery(String query) {
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
        ListView lv = (ListView) findViewById(R.id.all_artists_news_list);

        adapter = new ArrayAdapter<NewsItem>(this, android.R.layout.simple_list_item_1,listItems) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                // Customize list view here

                return view;
            }
        };

        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
