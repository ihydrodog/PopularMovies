package com.hydro.sunshine.popularmovieswithfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.CustomOnClickListener {

    public static MovieDBManager movieDBManager;
    public static  String APIKEY="";

    public enum Sort {
        Popularity,
        Rating,
        Favorite
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movieDBManager = new MovieDBManager( this, "movie.db", null, 1);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if( id == R.id.shareVideo) {
            String shareBody = "Here is the share content body";
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.shareVideo)));
        } else {
            MainActivityFragment mainFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            Sort sortType = Sort.Favorite;
            switch (id) {
                case R.id.sort_by_favorite:
                    sortType = Sort.Favorite;
                    break;
                case R.id.sort_by_popularity:
                    sortType = Sort.Popularity;
                    break;
                case R.id.sort_by_rating:
                    sortType = Sort.Rating;
                    break;
            }

            item.setChecked(true);


            mainFragment.updateSortMode(sortType);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClicked( Movie movie) {
        DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_detail);

        if( detailFragment == null) {

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        } else {
            detailFragment.update( movie);
        }
    }

    @Override
    public ArrayList<Movie> getFavoriteMovies() {
        return movieDBManager.selectAll();
    }

}
