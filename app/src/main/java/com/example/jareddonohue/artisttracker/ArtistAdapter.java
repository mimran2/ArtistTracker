package com.example.jareddonohue.artisttracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jareddonohue on 12/2/16.
 */

public class ArtistAdapter extends BaseAdapter {
    private ArrayList<Artist> artists;
    private LayoutInflater artistInf;

    public ArtistAdapter(Context c, ArrayList<Artist> theArtists){
        artists=theArtists;
        artistInf=LayoutInflater.from(c);
    }
    @Override
    public int getCount() {
        return artists.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout artistLay = (LinearLayout)artistInf.inflate
                (R.layout.artist, parent, false);
        //get title and artist views
        TextView artistNameView = (TextView)artistLay.findViewById(R.id.artist_name);
        TextView artistInfoView = (TextView)artistLay.findViewById(R.id.artist_info);
        //get song using position
        Artist currArtist = artists.get(position);
        //get title and artist strings
        artistNameView.setText(currArtist.getArtistName());
        artistInfoView.setText("Albums: " + currArtist.getNumAlbums() + ", Tracks: " + currArtist.getNumSongs());
        //set position as tag
        artistLay.setTag(position);
        return artistLay;
    }
}
