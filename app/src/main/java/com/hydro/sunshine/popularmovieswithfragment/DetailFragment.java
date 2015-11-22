package com.hydro.sunshine.popularmovieswithfragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


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
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    public void update( Movie movie) {
        TextView title = (TextView)getView().findViewById( R.id.detail_title);
        TextView releaseDate = (TextView)getView().findViewById( R.id.detail_release_date);
        TextView averageRating = (TextView)getView().findViewById( R.id.detail_average_rating);
        TextView overview = (TextView)getView().findViewById( R.id.detail_overview);
        ImageView poster = (ImageView)getView().findViewById( R.id.detail_poster);


        Picasso.with(getContext()).load( movie.getFullPosterPath()).into( poster);

        title.setText( movie.original_title);
        releaseDate.setText( movie.release_date);
        averageRating.setText( movie.vote_average);
        overview.setText( movie.overview);
    }


}
