package com.example.jareddonohue.artisttracker;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HandleXML {
    private String title = "title";
    private String link = "link";
    private String urlString = null;
    public ArrayList<NewsItem> newsItems;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;

    public HandleXML(String url){

        this.urlString = url;
        this.newsItems = new ArrayList<>(20);
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
                            title = text;
                        }

                        else if(name.equals("link")){
                            link = text;

                        }

                        else if(name.equals("item")){
                            currNewsItem = new NewsItem(title, link);
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