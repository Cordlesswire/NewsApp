package com.udacity.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.udacity.newsapp.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

import static com.udacity.newsapp.dummy.DummyContent.ITEMS;


public class MainActivity extends AppCompatActivity
                          implements View.OnFocusChangeListener,
                                     SearchView.OnQueryTextListener,
                                     MenuItemCompat.OnActionExpandListener,
                                     LoaderManager.LoaderCallbacks<List<DummyContent.DummyItem>> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int BOOK_LOADER_ID = 1;
    //
    private String mQuery;
    //
    private NewsRecyclerAdapter mAdapter;
    private List<DummyContent.DummyItem> mNewses;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);

        mNewses = new ArrayList<>();
        mNewses.addAll(ITEMS);

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString("query");
        }

        mAdapter = new NewsRecyclerAdapter(this, mNewses);
        mRecyclerView = (RecyclerView) findViewById(R.id.item_list);
        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView, mAdapter);

        ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            Snackbar.make(mRecyclerView, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
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
        ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            mQuery = query;
            Log.i(TAG, "onQueryTextSubmit | mQuery: " + mQuery);
            getLoaderManager().restartLoader(BOOK_LOADER_ID, null, this);
        } else {
            Snackbar.make(mRecyclerView, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchQuery) {
        Log.i(TAG, "onQueryTextChange | searchQuery: " + searchQuery);
        assert searchQuery != null;
        assert mNewses != null;
        List<DummyContent.DummyItem> results = mAdapter.filter(searchQuery.trim(), mNewses);//(ArrayList<DummyContent.DummyItem>) DummyContent.ITEMS
        Log.v("App", searchQuery + ", " + mNewses.size() + ", " + results.size());
        mAdapter.animateTo(results);
//        mListView.invalidate();
        mRecyclerView.scrollToPosition(0);
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
    public Loader<List<DummyContent.DummyItem>> onCreateLoader(int id, Bundle bundle) {
        Uri baseUri = Uri.parse(getString(R.string.BASE_NEWS_URL));
        Uri.Builder uriBuilder = baseUri.buildUpon();
        if (mQuery == null) {
            mQuery = "Udacity";
        }
        Log.i(TAG, "onCreateLoader | mQuery: " + mQuery);
        // TODO Rever as duas linhas a seguir:
        uriBuilder.appendQueryParameter("q", mQuery);
        uriBuilder.appendQueryParameter("maxResults", "40");
        //
        Log.i(TAG, "onCreateLoader | uriBuilder.toString(): " + uriBuilder.toString());
        return new NewsLoader(this, uriBuilder.toString());
    }


    @Override
    public void onLoadFinished(Loader<List<DummyContent.DummyItem>> loader, List<DummyContent.DummyItem> newsList) {
        Log.i(TAG, "onLoadFinished");
        //
        if (newsList != null && !newsList.isEmpty()) {
//            mAdapter.addAll(mNewses); // ERROR
//            mAdapter = new NewsRecyclerAdapter(this, mNewses);
            for (int i = 0; i < mNewses.size(); i++) {
                mAdapter.addItem(i, mNewses.get(i));
            }
            mNewses.addAll(newsList);
        } else {
            Snackbar.make(mRecyclerView, getString(R.string.no_news), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<DummyContent.DummyItem>> loader) {
        Log.i(TAG, "onLoaderReset");
//        mAdapter.clear(); // ERROR
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
