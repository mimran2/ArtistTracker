package com.example.jareddonohue.artisttracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String COUNTRY = "us";
    private String LANGUAGE = "en";
    private String QUERY = "Red Hot Chili Peppers";
    private String linkOfUrl = "";
    public final static String URL_TO_LOAD = "";
    private String finalUrl;
    private HandleXML xmlHandler;
    NewsItemAdapter adapter;
    ArrayList<NewsItem> listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        finalUrl = getSearchQuery(QUERY);
        xmlHandler = new HandleXML(finalUrl, QUERY);
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
