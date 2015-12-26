package com.hydro.sunshine.popularmovieswithfragment;

import android.app.ActionBar;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        DetailFragment detailFragment;
        detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_detail);


        detailFragment.update((Movie) (getIntent().getExtras().getParcelable("movie")));

        getSupportActionBar().setDisplayHomeAsUpEnabled( true);

    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch ( item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask( this);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
