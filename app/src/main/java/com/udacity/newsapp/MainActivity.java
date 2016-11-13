package com.udacity.newsapp;

import android.app.LoaderManager;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.udacity.newsapp.remote.NewsContent;
import com.udacity.newsapp.remote.NewsLoader;
import com.udacity.newsapp.ui.NewsRecyclerAdapter;
import com.udacity.newsapp.util.QueryUtils;

import java.util.List;


public class MainActivity extends AppCompatActivity
                          implements View.OnFocusChangeListener,
                                     SearchView.OnQueryTextListener,
                                     MenuItemCompat.OnActionExpandListener,
                                     LoaderManager.LoaderCallbacks<List<NewsContent.NewsItem>> {

    private static final String TAG = MainActivity.class.getSimpleName();
    //
    private static final int NEWS_LOADER_ID = 1;
    //
    private String mQuery;
    //
    private NewsRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        //
        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString("query");
        }
        //
        mAdapter = new NewsRecyclerAdapter(this, NewsContent.ITEMS);
        mRecyclerView = (RecyclerView) findViewById(R.id.item_list);
        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView, mAdapter);
        //
        if (QueryUtils.isInternetAccess(MainActivity.this)) {
            getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
        } else {
            Snackbar.make(mRecyclerView, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }


    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView, NewsRecyclerAdapter adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextFocusChangeListener(this);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFocusChange(View view, boolean b) {
        Log.i(TAG, "onFocusChange");
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        mQuery = query;
        Log.i(TAG, "onQueryTextSubmit | mQuery: " + mQuery);
        return false;
    }


    @Override
    public boolean onQueryTextChange(String searchQuery) {
        Log.i(TAG, "onQueryTextChange | searchQuery: " + searchQuery);
        assert searchQuery != null;
        if (QueryUtils.isInternetAccess(MainActivity.this)) {
            List<NewsContent.NewsItem> results = mAdapter.filter(searchQuery.trim(), NewsContent.ITEMS);
            Log.v("App", searchQuery + ", " + NewsContent.ITEMS.size() + ", " + results.size());
            mAdapter.animateTo(results);
            mRecyclerView.scrollToPosition(0);

            String newFilter = !TextUtils.isEmpty(searchQuery) ? searchQuery : null;
            Log.i(TAG, "newFilter: " + newFilter);
            if (mQuery == null && newFilter == null) {
                return true;
            }
            if (mQuery != null && mQuery.equals(newFilter)) {
                return true;
            }
            mQuery = newFilter;
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
        else {
            Snackbar.make(mRecyclerView, "Por favor, ative a Internet para usufluir o máximo deste app", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        return true;
    }


    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }


    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }


    @Override
    public Loader<List<NewsContent.NewsItem>> onCreateLoader(int id, Bundle bundle) {
        Uri baseUri = Uri.parse(getString(R.string.BASE_NEWS_URL));
        Uri.Builder uriBuilder = baseUri.buildUpon();
        if (mQuery == null) {
            mQuery = "Startup";
        }
        Log.i(TAG, "onCreateLoader | mQuery: " + mQuery);
        uriBuilder.appendQueryParameter("q", mQuery);
        uriBuilder.appendQueryParameter("api-key", getResources().getString(R.string.key_guardianapis));
        uriBuilder.appendQueryParameter("page-size", "20");
        //
        return new NewsLoader(this, uriBuilder.toString());
    }


    @Override
    public void onLoadFinished(Loader<List<NewsContent.NewsItem>> loader, List<NewsContent.NewsItem> newsList) {
        Log.i(TAG, "onLoadFinished");
        switch (loader.getId()) {
            case NEWS_LOADER_ID:
                if (newsList != null && !newsList.isEmpty()) {
                    for (int i = 0; i < newsList.size(); i++) {
                        NewsContent.addItem(newsList.get(i));
                        mAdapter.addItem(i, NewsContent.ITEMS.get(i));
                    }
                    return;
                } else {
                    Snackbar.make(mRecyclerView, getString(R.string.no_news), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
        }
    }


    @Override
    public void onLoaderReset(Loader<List<NewsContent.NewsItem>> loader) {
        Log.i(TAG, "onLoaderReset");
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            mAdapter.removeItem(i);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: " + mQuery);
        outState.putString("query", mQuery);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState: " + mQuery);
        mQuery = savedInstanceState.getString("query");
    }



}
