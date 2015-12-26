package com.hydro.sunshine.popularmovieswithfragment;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie implements Parcelable {

    public Movie( JSONObject obj) {
        try {
            original_title = obj.getString("original_title");
            poster_path = obj.getString("poster_path");
            release_date = obj.getString("release_date");
            vote_average = obj.getString("vote_average");
            overview = obj.getString("overview");
            id = obj.getString("id");

        } catch ( JSONException e) {
            e.printStackTrace();
        }
    }

//    public Movie( String title, String path) {
//
//        original_title = title;
//        poster_path = path;
//
//    }

    public Movie( Parcel src) {

        original_title = src.readString();
        poster_path = src.readString();
        release_date = src.readString();
        vote_average = src.readString();
        overview = src.readString();
        id = src.readString();
        favorite = src.readInt();

    }

    public String original_title;
    public String poster_path;
    public String release_date;
    public String vote_average;
    public String overview;
    public String id;

    public int favorite;

    public Movie(String test, String s) {
        original_title =test;
        poster_path = s;
    }

    public Movie( Cursor c) {
        id = c.getString(1);
        original_title = c.getString(2);
        poster_path = c.getString(3);
        release_date = c.getString(4);
        vote_average = c.getString(5);
        overview = c.getString(6);
        favorite = c.getInt(7);
    }

    public String getFullPosterPath() {
        String baseUrl = "http://image.tmdb.org/t/p/w185";
        return baseUrl + poster_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( original_title);
        dest.writeString( poster_path);
        dest.writeString( release_date);
        dest.writeString( vote_average);
        dest.writeString( overview);
        dest.writeString( id);
        dest.writeInt( favorite);
    }

    public static final Creator<Movie> CREATOR
            = new Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


}
