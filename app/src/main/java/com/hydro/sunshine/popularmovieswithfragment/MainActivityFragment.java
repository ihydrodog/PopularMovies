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






    public class FetchTask extends AsyncTask<Integer, Void, List<Movie>> {

        /* The date/time conversion code is going to be moved outside the asynctask later,
        * so for convenience we're breaking it out into its own method now.
        */


        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private List<Movie> getMoviesFromJson(String jsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject resultJson = new JSONObject(jsonStr);
            JSONArray results = resultJson.getJSONArray("results");

            List<Movie> movies = new ArrayList<>();

            for( int i = 0; i < results.length(); i++) {

                JSONObject movieJson = results.getJSONObject( i);

                Movie movie = new Movie(movieJson);

                movies.add( movie);
            }
            return movies;

        }

        @Override
        protected List<Movie> doInBackground(Integer... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            Integer sortMode = params[0];

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                int numDays = 7;
                String sortBy = "popularity.desc";
                if( sortMode == R.id.sort_by_rating)
                    sortBy = "vote_average.desc";
                else if( sortMode == R.id.sort_by_popularity) {
                }

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter( "sort_by", sortBy)
                        .appendQueryParameter("api_key", "APIKEY");


                URL url = new URL( builder.build().toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
                Log.v(LOG_TAG, jsonStr);
                try {
                    return getMoviesFromJson(jsonStr);
                } catch ( JSONException e) {

                    return null;
                }

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(List<Movie> results) {
            if( results != null) {


                m_arrayAdapter.clear();
                m_arrayAdapter.addAll(results);
            }
        }
    }

    class MovieAdapter extends ArrayAdapter<Movie> {

        public MovieAdapter( Context context, int viewResourceId, ArrayList<Movie> list) {
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


                Picasso.with(getContext()).load( m.getFullPosterPath()).into(iv);
            }

//            Log.d( LOG_TAG, m.original_title);

            return v;
        }
    }


    MovieAdapter m_arrayAdapter;


    private CustomOnClickListener customListener;
    public interface CustomOnClickListener{
        public void onClicked(Movie movie);

    }
    // Activity 로 데이터를 전달할 커스텀 리스너를 연결
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        customListener = (CustomOnClickListener)context;
    }

    public MainActivityFragment() {
    }
    GridView m_gridView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Activity activity = getActivity();

        m_gridView = (GridView) view.findViewById(R.id.gridView);

        ArrayList<Movie> list = new ArrayList<Movie>();

//             list.add( new Movie( "test", "/uXZYawqUsChGSj54wcuBtEdUJbh.jpg"));


        m_arrayAdapter = new MovieAdapter( getContext(), R.layout.grid_view_item, list);



        m_gridView.setAdapter(m_arrayAdapter);

        FetchTask fetchTask = new FetchTask();
        fetchTask.execute( R.id.sort_by_popularity);

        m_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Movie movie = m_arrayAdapter.getItem(position);

                                                customListener.onClicked( movie);
//                                                Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, movie.toString());
//
//                                                startActivity(detailIntent);
                                            }
                                        }
        );

        return view;
    }

    public void updateSortMode( int sortMode) {
        FetchTask fetchTask = new FetchTask();
        fetchTask.execute( sortMode);

    }
}
