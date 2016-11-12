package com.udacity.newsapp.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DummyContent {

    public static final List<DummyItem> ITEMS = new ArrayList<>();
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<>();


    public static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }


    public static class DummyItem
    {
        public final String id;
        public final String title;
        public final String date;
        public final String section;
        public final String details;

        public DummyItem(String id, String title, String date, String section, String details)
        {
            this.id      = id;
            this.title   = title;
            this.date    = date;
            this.section = section;
            this.details = details;
        }

        @Override
        public String toString()
        {
            return title;
        }
    }



}
