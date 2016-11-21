package com.example.jareddonohue.artisttracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jareddonohue on 11/21/16.
 */

public class NewsItemAdapter extends ArrayAdapter<NewsItem> {

        public NewsItemAdapter(Context context, ArrayList<NewsItem> newsItems) {
            super(context, 0, newsItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            NewsItem item = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_object, parent, false);
            }
            // Lookup view for data population
            TextView tvArtist = (TextView) convertView.findViewById(R.id.artist_name);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.article_title);
            // Populate the data into the template view using the data object
            tvArtist.setText(item.getArtist());
            tvTitle.setText(item.getTitle());
            // Return the completed view to render on screen
            return convertView;
        }
    }
