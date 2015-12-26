package com.hydro.sunshine.popularmovieswithfragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by hydro on 2015-12-13.
 */
public class MovieDBManager extends SQLiteOpenHelper {

    String tableName = "MOVIES";
    public MovieDBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format( "CREATE TABLE MOVIES ( _id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, original_title TEXT, poster_path TEXT, release_date TEXT, vote_average TEXT, overview TEXT, favorite INTEGER);");
        db.execSQL( query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert( Movie m) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contents = new ContentValues();
        contents.put( "id", m.id);
        contents.put( "original_title", m.original_title);
        contents.put( "poster_path", m.poster_path);
        contents.put( "release_date", m.release_date);
        contents.put( "vote_average", m.vote_average);
        contents.put( "overview", m.overview);
        contents.put("favorite", m.favorite);

        db.insert( tableName, null, contents );
        db.close();
    }


    public void delete( Movie m) {

        SQLiteDatabase db = getWritableDatabase();

        db.delete(tableName, "id=?", new String[]{m.id});
        db.close();
    }

    public ArrayList<Movie> select( Movie m) {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Movie> movies = new ArrayList<Movie>();


        Cursor results = db.query( tableName, null, "id=?", new String[]{m.id}, null, null, null, null);

        results.moveToFirst();
        while( !results.isAfterLast()) {
            Movie movie = new Movie( results);
            movies.add( movie);
            results.moveToNext();

        }

        return movies;

    }

    public ArrayList<Movie> selectAll() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from MOVIES";
        ArrayList<Movie> movies = new ArrayList<Movie>();

        Cursor results = db.query( tableName, null, null, null, null, null, null, null);
        results.moveToFirst();
        while( !results.isAfterLast()) {
            Movie movie = new Movie( results);
            movies.add( movie);
            results.moveToNext();

        }

        return movies;

    }
}
