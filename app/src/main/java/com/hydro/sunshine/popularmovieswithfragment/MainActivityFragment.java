package com.hydro.sunshine.popularmovieswithfragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    static String LOG_TAG = MainActivity.class.getSimpleName();



    class MovieAdapter extends ArrayAdapter<Movie> {

        public MovieAdapter( Context context, int viewResourceId, List<Movie> list) {
            super( context, viewResourceId, list);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            View v = convertView;
            if( v == null) {
                LayoutInflater vi = LayoutInflater.from( getContext());

                v = vi.inflate( R.layout.grid_view_item, parent, false);
            }

            Movie m = getItem(position);
            if( m != null) {
                ImageView iv = (ImageView)v.findViewById( R.id.movie_image);

                // configuration
                // http://api.themoviedb.org/3/configuration?api_key=APIKEY


                Picasso.with(getContext())
                        .load(m.getFullPosterPath())
                        .placeholder(android.R.drawable.ic_dialog_alert)
                        .error(android.R.drawable.ic_dialog_alert)
                        .into(iv);
            }

//            Log.d( LOG_TAG, m.original_title);

            return v;
        }
    }

    private CustomOnClickListener m_customListener;
    public interface CustomOnClickListener{
        public void onClicked(Movie movie);
        public List<Movie> getFavoriteMovies();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        m_customListener = (CustomOnClickListener)context;
    }

    public MainActivityFragment() {
    }

    static private String MOVIE_ARRAY = "movie_array";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_ARRAY, m_movieList);
    }

    ArrayList<Movie> m_movieList = new ArrayList<Movie>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if( savedInstanceState != null) {
            m_movieList = savedInstanceState.getParcelableArrayList( MOVIE_ARRAY);
        }


        View view = inflater.inflate(R.layout.fragment_main, container, false);

        updateSortMode(MainActivity.Sort.Popularity);



        return view;
    }

    void updateGridView( List<Movie> list) {
        GridView gridView = (GridView) getView().findViewById(R.id.gridView);

        MovieAdapter adapter = new MovieAdapter( getContext(), R.layout.grid_view_item, list);

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getAdapter().getItem(position);

                m_customListener.onClicked( movie);

            }
        });
    }

    public void updateSortMode( MainActivity.Sort sortMode) {
//        FetchTask fetchTask = new FetchTask( m_arrayAdapter);
//
//        m_arrayAdapter.clear();
//        if( sortMode == MainActivity.Sort.Favorite)
//            m_arrayAdapter.addAll(m_customListener.getFavoriteMovies());
//
//        fetchTask.execute( sortMode);

        if( sortMode == MainActivity.Sort.Favorite) {
            updateGridView(m_customListener.getFavoriteMovies());
        }
        else {
            String sortBy = "popularity.desc";
            if (sortMode == MainActivity.Sort.Rating)
                sortBy = "vote_average.desc";

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("sort_by", sortBy)
                    .appendQueryParameter("api_key", MainActivity.APIKEY);


            FetchURL fetch = new FetchURL(new FetchURL.Callback() {
                @Override
                public void callback(String response) {

                    try {

                        m_movieList.clear();

                        JSONObject resultJson = new JSONObject(response);
                        JSONArray results = resultJson.getJSONArray("results");


                        for (int i = 0; i < results.length(); i++) {

                            JSONObject movieJson = results.getJSONObject(i);

                            Movie movie = new Movie(movieJson);

                            if( MainActivity.movieDBManager.select( movie).size() > 0)
                                movie.favorite = 1;

                            m_movieList.add(movie);
                        }


                        updateGridView( m_movieList);

                    } catch ( JSONException e) {
                        return;
                    }
                }
            });
            fetch.execute(builder.toString());
        }



    }
}
