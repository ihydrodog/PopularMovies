package com.hydro.sunshine.popularmovieswithfragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {


    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Button favoriteButton = (Button) view.findViewById(R.id.favoriteButton);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton toggleButton = (ToggleButton)v;

                boolean checked = toggleButton.isChecked();

                if( m_movie != null) {
                    m_movie.favorite = checked ? 1 : 0;
                    if (checked)
                        MainActivity.movieDBManager.insert(m_movie);
                    else
                        MainActivity.movieDBManager.delete(m_movie);
                }
            }
        });

        return view;
    }

    public class MovieReview {

        public MovieReview(JSONObject obj) {
            try {
                id = obj.getString("id");
                author = obj.getString("author");
                content = obj.getString("content");
                url = obj.getString("url");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String id;
        String author;
        String content;
        String url;

    }

    public class MovieVideo {

        public MovieVideo(JSONObject obj) {
            try {
                id = obj.getString("id");
                iso_639_1 = obj.getString("iso_639_1");
                key = obj.getString("key");
                name = obj.getString("name");
                site = obj.getString("site");
                size = obj.getInt("size");
                type = obj.getString("type");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String id;
        String iso_639_1;
        String key;
        String name;
        String site;
        int size;
        String type;
    }


    class MovieVideoAdapter extends ArrayAdapter<MovieVideo> {

        public MovieVideoAdapter( Context context, int viewResourceId, List<MovieVideo> list) {
            super( context, viewResourceId, list);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            View v = convertView;
            if( v == null) {
                LayoutInflater vi = LayoutInflater.from( getContext());

                v = vi.inflate( R.layout.grid_video, parent, false);
            }

            MovieVideo m = getItem(position);
            if( m != null) {
                TextView text = (TextView)v.findViewById( R.id.videoLink);
                text.setText(m.name);
            }

            return v;
        }
    }

    class MovieReviewAdapter extends ArrayAdapter<MovieReview> {

        public MovieReviewAdapter( Context context, int viewResourceId, List<MovieReview> list) {
            super( context, viewResourceId, list);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            View v = convertView;
            if( v == null) {
                LayoutInflater vi = LayoutInflater.from( getContext());

                v = vi.inflate( R.layout.grid_review, parent, false);
            }

            MovieReview m = getItem(position);
            if( m != null) {
                TextView text = (TextView)v.findViewById( R.id.textAuthor);

                Log.d( m.author, m.content);
                text.setText(m.author);

                TextView content = (TextView)v.findViewById( R.id.textContent);
                content.setText(m.content);
            }

            return v;
        }
    }


    Movie m_movie;
    public void update( Movie movie) {
        m_movie = movie;

        TextView title = (TextView)getView().findViewById( R.id.detail_title);
        TextView releaseDate = (TextView)getView().findViewById( R.id.detail_release_date);
        TextView averageRating = (TextView)getView().findViewById( R.id.detail_average_rating);
        TextView overview = (TextView)getView().findViewById( R.id.detail_overview);
        ImageView poster = (ImageView)getView().findViewById( R.id.detail_poster);
        ToggleButton favoriteButton = (ToggleButton)getView().findViewById( R.id.favoriteButton);


        Picasso.with(getContext()).load( movie.getFullPosterPath()).into(poster);

        title.setText(movie.original_title);
        releaseDate.setText(movie.release_date);
        averageRating.setText(movie.vote_average);
        overview.setText(movie.overview);

        favoriteButton.setChecked(movie.favorite != 0);


        Uri.Builder builderForVideos = new Uri.Builder();
        builderForVideos.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath( movie.id)
                .appendPath( "videos")
                .appendQueryParameter("api_key", MainActivity.APIKEY);

        FetchURL fetchVideos = new FetchURL( new FetchURL.Callback() {

            @Override
            public void callback(String response) {
                JSONObject resultJson = null;
                try {
                    if( response == null )
                        return;

                    resultJson = new JSONObject(response);

                    JSONArray results = resultJson.getJSONArray("results");

                    List<MovieVideo> movies = new ArrayList<>();

                    for( int i = 0; i < results.length(); i++) {

                        JSONObject movieJson = results.getJSONObject( i);

                        MovieVideo movie = new MovieVideo(movieJson);

                        movies.add( movie);
                    }

                    ListView listView = (ListView) getView().findViewById(R.id.gridVideo);

                    listView.setAdapter(new MovieVideoAdapter(getActivity(), R.layout.grid_video, movies));

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            MovieVideo movie = (MovieVideo) parent.getAdapter().getItem(position);

                                                            Log.d( movie.site, movie.key);

                                                            if( movie.site.equals( "YouTube")) {
                                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + movie.key)));


                                                            }


                                                        }
                                                    }
                    );

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        fetchVideos.execute(builderForVideos.toString());

        Uri.Builder builderForReview = new Uri.Builder();
        builderForReview.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath( movie.id)
                .appendPath("reviews")
                .appendQueryParameter("api_key", MainActivity.APIKEY);

        FetchURL fetchReviews =  new FetchURL( new FetchURL.Callback() {

            @Override
            public void callback(String response) {
                JSONObject resultJson = null;
                try {
                    if( response == null )
                        return;

                    resultJson = new JSONObject(response);

                    JSONArray results = resultJson.getJSONArray("results");

                    List<MovieReview> movies = new ArrayList<>();

                    for( int i = 0; i < results.length(); i++) {

                        JSONObject movieJson = results.getJSONObject( i);

                        MovieReview movie = new MovieReview(movieJson);

                        movies.add( movie);
                    }

                    ListView listView = (ListView) getView().findViewById(R.id.gridReview);

                    listView.setAdapter(new MovieReviewAdapter( getActivity(), R.layout.grid_review, movies));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        fetchReviews.execute(builderForReview.toString());

    }


}
