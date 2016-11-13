package com.udacity.newsapp.remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NewsContent {

    public static final List<NewsItem> ITEMS = new ArrayList<>();
    public static final Map<String, NewsItem> ITEM_MAP = new HashMap<>();


    public static void addItem(NewsItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }


    public static class NewsItem
    {
        public final String id;
        public final String title;
        public final String date;
        public final String section;
        public final String site;

        public NewsItem(String id, String title, String date, String section, String site)
        {
            this.id      = id;
            this.title   = title;
            this.date    = date;
            this.section = section;
            this.site    = site;
        }

        @Override
        public String toString()
        {
            return title;
        }
    }



}
