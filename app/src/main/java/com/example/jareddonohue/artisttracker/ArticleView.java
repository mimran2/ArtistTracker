package com.example.jareddonohue.artisttracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Created by jareddonohue on 11/21/16.
 */

public class ArticleView extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);
        WebView webview = new WebView(this);
        setContentView(webview);
        Intent intent = getIntent();
        String url = intent.getStringExtra(MainActivity.URL_TO_LOAD);
        webview.loadUrl(url);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
