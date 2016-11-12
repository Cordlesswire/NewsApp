package com.udacity.newsapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.udacity.newsapp.MainActivity;
import com.udacity.newsapp.R;


public class ItemDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setupToolbar(toolbar);
        //
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        setupFabOnClick(fab);

        if (savedInstanceState == null) {
            createItemDeail();
        }
    }


    private void createItemDeail() {
        Bundle arguments = new Bundle();
        String extraByMain = getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID);
        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, extraByMain);
        //
        ItemDetailFragment fragment = new ItemDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().add(R.id.item_detail_container, fragment).commit();
    }


    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private void setupFabOnClick(FloatingActionButton fab) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
