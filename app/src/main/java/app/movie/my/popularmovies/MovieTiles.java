package app.movie.my.popularmovies;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/*

This Source code is made available for Udacity Course Evaluation purpose only. Please, don't share the code.

Websites referred to create this project: stackoverflow.com, developer.android.com

 */

public class MovieTiles extends AppCompatActivity {

    protected static boolean dualPane;
    protected static int lastSelectedMovie = -1;
    private MoviesFragment moviesFragment;
    protected MovieDetailFragment movieDetailFragment;

    private static final String SAVE_MOVIE_FRAGMENT = "savemovie";
    private static final String SAVE_MOVIE_DETAIL_FRAGMENT = "savemoviedetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_grid_view);

        if(findViewById(R.id.landscapeDetailsFragment)!=null) {
            dualPane = true;
        } else {
            dualPane = false;
        }

        if(savedInstanceState==null) {
            moviesFragment = MoviesFragment.newInstance(false);
            getSupportFragmentManager().beginTransaction().add(R.id.moviesFragment, moviesFragment).commit();
            if(dualPane) {
                movieDetailFragment = MovieDetailFragment.newInstance(null);
                getSupportFragmentManager().beginTransaction().add(R.id.landscapeDetailsFragment, movieDetailFragment).commit();
            }
        } else {
            moviesFragment = (MoviesFragment) getSupportFragmentManager().getFragment(savedInstanceState,SAVE_MOVIE_FRAGMENT);
            if(dualPane) {
                movieDetailFragment = (MovieDetailFragment) getSupportFragmentManager().getFragment(savedInstanceState,SAVE_MOVIE_DETAIL_FRAGMENT);
                if(movieDetailFragment==null) {
                    movieDetailFragment = MovieDetailFragment.newInstance(null);
                    getSupportFragmentManager().beginTransaction().add(R.id.landscapeDetailsFragment, movieDetailFragment).commit();
                }
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fm = getSupportFragmentManager();
        fm.putFragment(outState,SAVE_MOVIE_FRAGMENT,moviesFragment);
        if(dualPane && movieDetailFragment!=null && movieDetailFragment.isAdded()) {
            fm.putFragment(outState, SAVE_MOVIE_DETAIL_FRAGMENT, movieDetailFragment);
        }
    }

}
