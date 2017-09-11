package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.data.FavoriteContract.FavoriteEntry;

public class FavoriteProvider extends ContentProvider {

    private FavoriteDbHelper mFavoriteDbHelper;

    /**
     * URI matcher code for the content URI for the favorites table
     */
    private static final int FAVORITES = 100;
    /**
     * URI matcher code for the content URI for a single favorite (movie)
     */
    private static final int FAVORITE_ID = 101;

    @Override
    public boolean onCreate() {
        mFavoriteDbHelper = new FavoriteDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        // Get a readable SQLite database
        SQLiteDatabase db = mFavoriteDbHelper.getReadableDatabase();
        // Create a uri matcher
        final int match = sUriMatcher.match(uri);
        switch (match) {
            // query entire favorites table
            case FAVORITES:
                cursor = db.query(FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            // query a single row of favorites table
            case FAVORITE_ID:
                selection = FavoriteEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
        }
        // Set notification uri on cursor
        // If the data at URI changes, we will update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        // Return the whole product table or a row of favorite (movie)
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return insertFavorite(uri, values);
        }
        return null;
    }

    private Uri insertFavorite(Uri uri, ContentValues values) {

        Integer movie_id = values.getAsInteger(FavoriteEntry.COLUMN_MOVIE_ID);
        if(movie_id == null) {
            throw new IllegalArgumentException("My favorite movies must have movie id.");
        }

        // Get SQLite database for inserting data
        SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();

        // An id with the type long is returned
        // from inserting data into SQLite database
        long id = db.insert(FavoriteEntry.TABLE_NAME, null, values);

        // Create log if failed to insert a row of favorite (movie)
        // If failed to insert, then the id will be -1
        if (id == -1) {
            return null;
        }
        // Notify all listener that the data has been changed for favorites uri
        // content://com.example.android.popularmovies/favorites/favorites
        getContext().getContentResolver().notifyChange(uri,null);
        // Return content uri with the id of the newly added row at the end
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            // If uri matches favorites uri, then delete the whole table
            case FAVORITES:
                return deleteFavorite(uri, selection, selectionArgs);
            // If uri matches a single favorite (movie), then delete a single row of product
            case FAVORITE_ID:
                selection = FavoriteEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                //selection = FavoriteEntry.COLUMN_MOVIE_ID + "=?";
//                selectionArgs = new String[] {};
                return deleteFavorite(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Delete is not supported for uri: " + uri);
        }
    }

    private int deleteFavorite(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();
        int rowDeleted = db.delete(FavoriteEntry.TABLE_NAME, selection, selectionArgs);
        // If more than zero rows are deleted, then notify listener all listeners
        // that the data has been changed for product uri
        if(rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        // Return the number of rows deleted
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return updateTable(uri, values, selection, selectionArgs);
            case FAVORITE_ID:
                selection = FavoriteEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateTable(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot update table for uri: " + uri);
        }
    }

    private int updateTable(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();
        // Update favorites table
        int rowsAffected = db.update(FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
        // Notify changes so the ui in MainActivity will be updated automatically
        getContext().getContentResolver().notifyChange(uri, null);
        // Return number of rows affected
        return rowsAffected;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return FavoriteEntry.CONTENT_LIST_TYPE;
            case FAVORITE_ID:
                return FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // The content uri is: content:// com.example.android.popularmovies.favorites/favorites
        sUriMatcher.addURI(FavoriteContract.CONTENT_AUTHORITY, FavoriteContract.PATH_FAVORITES, FAVORITES);
        // The content uri is: content:// com.example.android.popularmovies.favorites/favorites/#
        sUriMatcher.addURI(FavoriteContract.CONTENT_AUTHORITY, FavoriteContract.PATH_FAVORITES + "/#", FAVORITE_ID);
    }
}
