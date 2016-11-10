package com.udacity.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.udacity.newsapp.dummy.DummyContent;

import java.util.List;

/**
 * Created by geovani on 09/11/16.
 */

public class NewsLoader extends AsyncTaskLoader<List<DummyContent.DummyItem>> {

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
    public List<DummyContent.DummyItem> loadInBackground() {
        if (mURL == null) {
            return null;
        }
        return null;
    }


}
