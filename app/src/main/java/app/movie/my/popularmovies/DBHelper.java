package app.movie.my.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "FavoriteDatabase";
    private static final int DB_VER = 1;

    private static final String CREATE_FAV_MOV_TABLE = "CREATE TABLE " + MovieContract.FAV_MOVIE_TABLE_NAME + " (" + MovieContract.ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ MovieContract.MOV_ID_COLUMN + " VARCHAR UNIQUE, " +
            MovieContract.POSTER_LINK_COLUMN + " VARCHAR, "+MovieContract.RELEASE_DATE_COLUMN+" VARCHAR, "+MovieContract.VOTE_AVG_COLUMN+" VARCHAR, "+
    MovieContract.TITLE_COLUMN+" VARCHAR, "+MovieContract.OVERVIEW_COLUMN+" VARCHAR);";

    private static final String CREATE_TRAILERS_TABLE = "CREATE TABLE " + MovieContract.TRAILER_TABLE_NAME + " (" + MovieContract.ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "+MovieContract.MOV_ID_COLUMN + " VARCHAR, " +
            MovieContract.VIDEO_NAME_COLUMN + " VARCHAR, "+MovieContract.VIDEO_KEY_COLUMN+" VARCHAR);";

    private static final String CREATE_REVIEWS_TABLE = "CREATE TABLE " + MovieContract.REVIEWS_TABLE_NAME + " (" + MovieContract.ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "+MovieContract.MOV_ID_COLUMN + " VARCHAR, " +
            MovieContract.COMMENT_AUTHOR_COLUMN + " VARCHAR, "+MovieContract.COMMENT_CONTENT_COLUMN+" VARCHAR);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAV_MOV_TABLE);
        db.execSQL(CREATE_REVIEWS_TABLE);
        db.execSQL(CREATE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.REVIEWS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.TRAILER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FAV_MOVIE_TABLE_NAME);
        onCreate(db);
    }

}
