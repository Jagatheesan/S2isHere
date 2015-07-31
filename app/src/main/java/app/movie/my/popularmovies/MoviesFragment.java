package app.movie.my.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.movie.my.popularmovies.api.IEndPoints;
import app.movie.my.popularmovies.api.Movies;
import app.movie.my.popularmovies.api.MoviesResults;
import app.movie.my.popularmovies.api.Reviews;
import app.movie.my.popularmovies.api.ReviewsResults;
import app.movie.my.popularmovies.api.Videos;
import app.movie.my.popularmovies.api.VideosResults;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    protected GridView moviesGrid;
    private TextView emptyView;
    private String sortSelected;
    private SharedPreferences preferences;
    private FavCursorAdapter favCursorAdapter;
    private Bundle bundle = null;

    public static final String BASE_URL = "http://api.themoviedb.org/3";
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";

    private static final String POPULAR_PARAM = "popularity.desc";
    private static final String RATINGS_PARAM = "vote_average.desc";
    protected static final String FAVORITE_PARAM = "favParams";

    protected static final String PREFS_NAME = "jpref";
    private static final String SORT_PREF = "sort_pref";

    public static final String MOVIE_PARCELABLE = "movieparcelable";
    public static final String DETAIL_PARCELABLE = "moviedetailparcelable";

    private Movies movieParcelable;
    private IEndPoints iEndPoints;

    public static MoviesFragment newInstance(boolean dualPane) {
        MoviesFragment myFragment = new MoviesFragment();
        return myFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.movies_grid_view_fragment, container, false);

        preferences = getActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE);
        sortSelected = preferences.getString(SORT_PREF, POPULAR_PARAM);


        moviesGrid = (GridView) v.findViewById(R.id.moviesGrid);
        emptyView = (TextView) v.findViewById(R.id.emptyView);
        moviesGrid.setEmptyView(emptyView);


        if (sortSelected.equalsIgnoreCase(POPULAR_PARAM)) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.popularity));
        } else if(sortSelected.equalsIgnoreCase(RATINGS_PARAM)){
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.rating));
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Favorite Movies");
            loadFavoriteMovies();
            emptyView.setText(R.string.no_favs_yet);
            return v;
        }

        if (savedInstanceState != null) {
                setUpAdapter(((Movies) savedInstanceState.getParcelable(MOVIE_PARCELABLE)), true);
        } else {
            RestAdapter adapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).
                    setEndpoint(BASE_URL).build();

            iEndPoints = adapter.create(IEndPoints.class);
            loadMovies();
            if (MovieTiles.dualPane) {
                MovieDetailFragment newMovieDetailFragment = MovieDetailFragment.newInstance(savedInstanceState);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.landscapeDetailsFragment, newMovieDetailFragment).commit();
            }
        }

        return v;
    }

    private void loadMovies() {
        if (!sortSelected.equalsIgnoreCase(FAVORITE_PARAM)) {
            if(iEndPoints==null){
                RestAdapter adapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).
                        setEndpoint(BASE_URL).build();

                iEndPoints = adapter.create(IEndPoints.class);
            }
            iEndPoints.getMovies(sortSelected, getString(R.string.api_key), new Callback<Movies>() {
                @Override
                public void success(Movies movies, Response response) {
                    setUpAdapter(movies, false);
                    emptyView.setText(R.string.loading_movies);
                }

                @Override
                public void failure(RetrofitError error) {
                    emptyView.setText(error.getMessage());
                }
            });
        } else {
            loadFavoriteMovies();
        }
    }

    private void loadFavoriteMovies() {
        favCursorAdapter = new FavCursorAdapter(getActivity(), null, 0);
        moviesGrid.setAdapter(favCursorAdapter);
        getLoaderManager().initLoader(1, null, this);
        moviesGrid.setOnItemClickListener(this);
    }

    private void setUpAdapter(Movies movies, boolean loadDetails) {
        movieParcelable = movies;
        if (movieParcelable != null) {
            moviesGrid.setAdapter(new MoviesGridAdapter(movies.getResults(), getActivity()));
            moviesGrid.setOnItemClickListener(this);
            if (loadDetails && MovieTiles.lastSelectedMovie > -1 && MovieTiles.dualPane) {
                bundle = new Bundle();
                bundle.putParcelable(DETAIL_PARCELABLE, movieParcelable.getResults().get(MovieTiles.lastSelectedMovie));
                MovieDetailFragment newMovieDetailFragment = MovieDetailFragment.newInstance(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.landscapeDetailsFragment, newMovieDetailFragment).commit();

            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(!sortSelected.equalsIgnoreCase(FAVORITE_PARAM)) {
            outState.putParcelable(MOVIE_PARCELABLE, movieParcelable);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popular:
                if (!sortSelected.equalsIgnoreCase(POPULAR_PARAM)) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.popularity));
                    sortSelected = POPULAR_PARAM;
                    preferences.edit().putString(SORT_PREF, sortSelected).apply();
                    moviesGrid.setAdapter(null);
                    emptyView.setText(getString(R.string.loading_movies));
                    loadMovies();
                    if (MovieTiles.dualPane) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.landscapeDetailsFragment, new MovieDetailFragment()).commit();
                    }
                }
                break;
            case R.id.rated:
                if (!sortSelected.equalsIgnoreCase(RATINGS_PARAM)) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.rating));
                    sortSelected = RATINGS_PARAM;
                    preferences.edit().putString(SORT_PREF, sortSelected).apply();
                    moviesGrid.setAdapter(null);
                    emptyView.setText(getString(R.string.loading_movies));
                    loadMovies();
                    if (MovieTiles.dualPane) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.landscapeDetailsFragment, MovieDetailFragment.newInstance(null)).commit();
                    }
                }
                break;
            case R.id.favOption:
                if (!sortSelected.equalsIgnoreCase(FAVORITE_PARAM)) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.fav_string));
                    sortSelected = FAVORITE_PARAM;
                    preferences.edit().putString(SORT_PREF, sortSelected).apply();
                    moviesGrid.setAdapter(null);
                    emptyView.setText(getString(R.string.no_favs_yet));
                    loadMovies();
                    if (MovieTiles.dualPane) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.landscapeDetailsFragment, MovieDetailFragment.newInstance(null)).commit();
                    }
                }
                break;
            case R.id.refresh:
                loadMovies();
                if (MovieTiles.dualPane) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.landscapeDetailsFragment, MovieDetailFragment.newInstance(null)).commit();
                }
                break;
        }

        return true;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.sort_menu, menu);
    }





    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MovieTiles.lastSelectedMovie = position;
        MoviesResults moviesResults;
        bundle = new Bundle();
        if(parent.getAdapter() instanceof MoviesGridAdapter) {
            moviesResults = movieParcelable.getResults().get(position);
        } else {
            moviesResults = favCursorAdapter.getResultParcelable(position);
        }

        bundle.putParcelable(DETAIL_PARCELABLE, moviesResults);


        SharedPreferences preferences = getActivity().getSharedPreferences(MoviesFragment.PREFS_NAME, getActivity().MODE_PRIVATE);

        String sdPath = preferences.getString(moviesResults.getId(), null);


        if (sdPath != null) {

            //Trailers
            Videos videos = new Videos();
            Cursor cur = getActivity().getContentResolver().query(Uri.parse(MovieContract.CONTENT_URI + "/" + MovieContract.TRAILER_TABLE_NAME), null, null, null, null);
            List<VideosResults> res = new ArrayList<>();
            if (cur.moveToFirst()) {
                do {
                    VideosResults videosResults = new VideosResults();
                    int i = 2;
                    videosResults.setName(cur.getString(i++));
                    videosResults.setKey(cur.getString(i++));
                    res.add(videosResults);
                } while (cur.moveToNext());
                videos.setResults(res);
            }
            cur.close();

            //Reviews
            Reviews reviews = new Reviews();
            cur = getActivity().getContentResolver().query(Uri.parse(MovieContract.CONTENT_URI + "/" + MovieContract.REVIEWS_TABLE_NAME), null, null, null, null);
            List<ReviewsResults> res2 = new ArrayList<>();
            if (cur.moveToFirst()) {
                do {
                    ReviewsResults reviewsResults = new ReviewsResults();
                    int i = 2;
                    reviewsResults.setAuthor(cur.getString(i++));
                    reviewsResults.setContent(cur.getString(i++));
                    res2.add(reviewsResults);
                } while (cur.moveToNext());
                reviews.setResults(res2);
            }
            cur.close();

            //putting parcelable
            bundle.putParcelable(MovieDetailFragment.VIDEO_PARCELABLE, videos);
            bundle.putParcelable(MovieDetailFragment.REVIEW_PARCELABLE, reviews);
        }


        if (!MovieTiles.dualPane) {
            Intent intent = new Intent(getActivity(), MovieDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            MovieDetailFragment newMovieDetailFragment = MovieDetailFragment.newInstance(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.landscapeDetailsFragment, newMovieDetailFragment).commit();
        }
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new android.support.v4.content.CursorLoader(getActivity(),Uri.parse(MovieContract.CONTENT_URI + "/" + MovieContract.FAV_MOVIE_TABLE_NAME),null,null,null,null);
    }



    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        favCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        favCursorAdapter.swapCursor(null);
    }
}
