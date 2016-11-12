package com.udacity.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.udacity.newsapp.remote.NewsContent;

import java.util.List;

/**
 * Created by geovani on 09/11/16.
 */

public class NewsLoader extends AsyncTaskLoader<List<NewsContent.NewsItem>> {

    private String mURL;


    public NewsLoader(Context context, String url) {
        super(context);
        mURL = url;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public List<NewsContent.NewsItem> loadInBackground() {
        if (mURL == null) {
            return null;
        }
//        NewsContent.NewsItem.ITEMS.addAll(QueryUtils.fetchNewsData(mURL));
//        return NewsContent.NewsItem.ITEMS;
        return QueryUtils.fetchNewsData(mURL);
    }


}
