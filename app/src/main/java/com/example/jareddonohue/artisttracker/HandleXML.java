package com.example.jareddonohue.artisttracker;


import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HandleXML extends AsyncTask<URL, Integer, Long>{
    private String title = "title";
    private String link = "link";
    private String urlString = null;
    public ArrayList<NewsItem> newsItems;
    public String artist;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    private boolean isGoogleNews = false;

    public HandleXML(String url, String artist){

        this.urlString = url;
        this.newsItems = new ArrayList<>(20);
        this.artist = artist;
        if(!(artist.equals("Rolling Stone") || artist.equals("Pitchfork"))){
            isGoogleNews = true;
        }
    }


    @Override
    protected Long doInBackground(URL... urls) {
        return 1L;
    }

    protected void onPreExecute(Long result) {

    }

    protected void onPostExecute(Long result) {

    }

    public ArrayList<NewsItem> getNewsItems(){
        return this.newsItems;
    }

    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text=null;

        try {
            event = myParser.getEventType();
            NewsItem currNewsItem;

            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                        if(name.equals("title")){
                            if(isGoogleNews) {
                                if (text.matches(".*" + artist.toLowerCase() + ".*")) {
                                    title = text;
                                }else{
                                    title = "";
                                }
                            }
                            title = text;
                        }

                        else if(name.equals("link")){
                            // parse RSS <link> for the actual url if Google News
                            if(isGoogleNews){
                                // This is not one of the Title blocks of the RSS feed, accept
                                if(!title.endsWith("Google News")) {
                                    String[] getUrl = text.split("url=");
                                    link = getUrl[1];
                                }
                            }else {
                                link = text;
                            }
                        }

                        else if(name.equals("item")){
                            currNewsItem = new NewsItem(title, link, artist);
                            this.newsItems.add(currNewsItem);
                            title = "";
                            link  = "";
                        }

                        else{
                        }

                        break;
                }

                event = myParser.next();
            }

            parsingComplete = false;
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n\n" + newsItems.size() + "\n\n");

    }

    public void fetchXML(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    parseXMLAndStoreIt(myparser);
                    stream.close();
                }

                catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        thread.start();
    }
}