package app.movie.my.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import app.movie.my.popularmovies.api.MoviesResults;


public class FavCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private Context context;
    private SharedPreferences preferences;

    public FavCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        preferences = context.getSharedPreferences(MoviesFragment.PREFS_NAME, context.MODE_PRIVATE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.grid_element, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cur) {

        String id = cur.getString(1);
        String posterPath = cur.getString(2);
        String sdPath = preferences.getString(id, null);
        if (sdPath != null && !sdPath.equalsIgnoreCase("1")) {
            File file = new File(sdPath);
            Glide.with(context).load(Uri.fromFile(file)).placeholder(R.drawable.placeholder_movies).into((ImageView) view);
        } else {
            Glide.with(context).load(posterPath).placeholder(R.drawable.placeholder_movies).into((ImageView) view);
        }
    }

    public MoviesResults getResultParcelable(int position) {
        Cursor cur = getCursor();
        MoviesResults moviesResults = new MoviesResults();
        cur.moveToPosition(position);
        if (cur != null) {
            int i = 1;
            String id = cur.getString(i++);
            moviesResults.setId(id);
            String sdPath = preferences.getString(id, null);
            if (sdPath != null && !sdPath.equalsIgnoreCase("1")) {
                moviesResults.setSdImage(preferences.getString(id, null));
            } else {
                moviesResults.setSdImage("");
            }
            moviesResults.setPosterPath(cur.getString(i++));
            moviesResults.setReleaseDate(cur.getString(i++));
            moviesResults.setVoteAverage(cur.getString(i++));
            moviesResults.setTitle(cur.getString(i++));
            moviesResults.setOverview(cur.getString(i++));
        }
        return moviesResults;
    }
}
