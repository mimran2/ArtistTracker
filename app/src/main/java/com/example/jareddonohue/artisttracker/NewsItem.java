package com.example.jareddonohue.artisttracker;

/**
 * Created by jareddonohue on 11/17/16.
 */

public class NewsItem {
    private String title;
    private String link;

    NewsItem(String title, String link){
        this.title = title;
        this.link  = link;
    }

    NewsItem(){
        this("","");
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewsItem newsItem = (NewsItem) o;

        if (title != null ? !title.equals(newsItem.title) : newsItem.title != null) return false;
        return link != null ? link.equals(newsItem.link) : newsItem.link == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        return result;
    }
}
