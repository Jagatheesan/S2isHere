package app.movie.my.popularmovies;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class FavoriteProvider extends ContentProvider {

    protected static DBHelper dbHelper;


    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.FAV_MOVIE_TABLE_NAME, 0);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.TRAILER_TABLE_NAME, 1);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.REVIEWS_TABLE_NAME, 2);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cur = null;
        switch (uriMatcher.match(uri)) {
            case 0: cur = db.query(MovieContract.FAV_MOVIE_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                cur.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case 1:cur = db.query(MovieContract.TRAILER_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case 2:cur = db.query(MovieContract.REVIEWS_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                break;
        }
        return cur;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long res = -1000;

        switch (uriMatcher.match(uri)) {
            case 0:
                res = db.insertWithOnConflict(MovieContract.FAV_MOVIE_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                break;
            case 1:
                res = db.insertWithOnConflict(MovieContract.TRAILER_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                break;
            case 2:
                res = db.insertWithOnConflict(MovieContract.REVIEWS_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI", null);
        }

        if (res == -1) {
            return null;
        } else {
            return uri;
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int del = -1000;
        switch (uriMatcher.match(uri))
        {
            case 0: del = db.delete(MovieContract.FAV_MOVIE_TABLE_NAME,selection,selectionArgs);
                break;
            case 1: del = db.delete(MovieContract.TRAILER_TABLE_NAME,selection,selectionArgs);
                break;
            case 2: del = db.delete(MovieContract.REVIEWS_TABLE_NAME,selection,selectionArgs);
                break;
            default:
                del = 0;
        }
        return del;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numInserted = 0;
        long newID = -1;

        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        sqlDB.beginTransaction();
        try {
            for (ContentValues cv : values) {
                newID = -1;
                switch (uriMatcher.match(uri)) {
                    case 1:
                        newID = sqlDB.insertOrThrow(MovieContract.TRAILER_TABLE_NAME, null, cv);
                        break;
                    case 2:
                        newID = sqlDB.insertOrThrow(MovieContract.REVIEWS_TABLE_NAME, null, cv);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid URI", null);
                }
                if (newID <= 0) {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            }
            sqlDB.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
            numInserted = values.length;
        } finally {
            sqlDB.endTransaction();
        }
        return numInserted;
    }
}
