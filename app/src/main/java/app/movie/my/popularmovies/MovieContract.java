package app.movie.my.popularmovies;


public class MovieContract {
    protected static final String AUTHORITY = "app.movie.my.popularmovies.provider";
    protected static final String CONTENT_URI = "content://" + AUTHORITY;

    protected static final String FAV_MOVIE_TABLE_NAME = "fav_mov_table";
    protected static final String TRAILER_TABLE_NAME = "trailer_mov_table";
    protected static final String REVIEWS_TABLE_NAME = "reviews_mov_table";

    protected static final String ID_COLUMN = "_id";
    protected static final String MOV_ID_COLUMN = "mov_id";
    protected static final String TITLE_COLUMN = "title";
    protected static final String OVERVIEW_COLUMN = "overview";
    protected static final String POSTER_LINK_COLUMN = "posterlink";
    protected static final String RELEASE_DATE_COLUMN = "release_date";
    protected static final String VOTE_AVG_COLUMN = "vote_avg";
    protected static final String SDCARD_IMAGE_EXISTS = "image_sd";
    protected static final String COMMENT_AUTHOR_COLUMN= "comment_author";
    protected static final String COMMENT_CONTENT_COLUMN = "comment_column";
    protected static final String VIDEO_NAME_COLUMN = "video_name";
    protected static final String VIDEO_KEY_COLUMN = "video_key";
}
