package com.example.jareddonohue.artisttracker;


import android.os.Parcel;
import android.os.Parcelable;

public class NewsItem implements Parcelable{
    private String artist;
    private String title;
    private String link;

    NewsItem(String title, String link, String artist){
        this.title = title;
        this.link  = link;
        this.artist = artist;
    }

    NewsItem(){
        this("","","");
    }

    protected NewsItem(Parcel in) {
        artist = in.readString();
        title = in.readString();
        link = in.readString();
    }

    public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getArtist() {
        return artist;
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewsItem newsItem = (NewsItem) o;

        if (artist != null ? !artist.equals(newsItem.artist) : newsItem.artist != null)
            return false;
        if (title != null ? !title.equals(newsItem.title) : newsItem.title != null) return false;
        return link != null ? link.equals(newsItem.link) : newsItem.link == null;

    }

    @Override
    public int hashCode() {
        int result = artist != null ? artist.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeString(link);
    }
}
