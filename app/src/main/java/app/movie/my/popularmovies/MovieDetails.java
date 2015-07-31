package app.movie.my.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class MovieDetails extends AppCompatActivity {

    private MovieDetailFragment movieDetailFragment;
    private static final String SAVE_MOVIE_DETAIL_FRAGMENT = "savedmoviedetailfragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_details);
        if(savedInstanceState!=null) {
            movieDetailFragment = (MovieDetailFragment) getSupportFragmentManager().getFragment(savedInstanceState,SAVE_MOVIE_DETAIL_FRAGMENT);
        } else {
            movieDetailFragment = new MovieDetailFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.movieDetails, movieDetailFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        FragmentManager fm = getSupportFragmentManager();
        fm.putFragment(outState,SAVE_MOVIE_DETAIL_FRAGMENT,movieDetailFragment);
        super.onSaveInstanceState(outState);
    }
}
