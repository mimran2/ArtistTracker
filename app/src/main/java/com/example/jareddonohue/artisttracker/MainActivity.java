package com.example.jareddonohue.artisttracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity {

    EditText query;
    Button fetchButton;
    private String COUNTRY = "us";
    private String LANGUAGE = "en";
    private String QUERY = "";
    private String finalUrl;
    private HandleXML obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        query = (EditText) findViewById(R.id.query);


        QUERY = query.getText().toString();
        finalUrl = getSearchQuery(QUERY);
        obj = new HandleXML(finalUrl);
        obj.fetchXML();
        while (obj.parsingComplete) ;

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
}
