package app.movie.my.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import app.movie.my.popularmovies.api.IEndPoints;
import app.movie.my.popularmovies.api.MoviesResults;
import app.movie.my.popularmovies.api.Reviews;
import app.movie.my.popularmovies.api.ReviewsResults;
import app.movie.my.popularmovies.api.Videos;
import app.movie.my.popularmovies.api.VideosResults;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MovieDetailFragment extends Fragment implements View.OnClickListener {

    private MoviesResults movieResult;
    protected Videos videos = null;
    private Reviews reviews = null;
    private boolean needSaving;
    private boolean gotImage;
    private android.support.v7.widget.ShareActionProvider shareActionProvider;

    private static SharedPreferences preferences;

    protected static final String VIDEO_PARCELABLE = "videoparcel";
    protected static final String REVIEW_PARCELABLE = "reviewparcel";


    public static MovieDetailFragment newInstance(Bundle bundle) {
        MovieDetailFragment myFragment = new MovieDetailFragment();
        if (MovieTiles.dualPane) {
            myFragment.setArguments(bundle);
        }
        myFragment.needSaving = false;
        myFragment.gotImage = false;
        return myFragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MoviesFragment.DETAIL_PARCELABLE, movieResult);
        if (videos != null) {
            outState.putParcelable(VIDEO_PARCELABLE, videos);
        }
        if (reviews != null) {
            outState.putParcelable(REVIEW_PARCELABLE, reviews);
        }
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        if (!MovieTiles.dualPane) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.movie_activity_title));
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            bundle = getArguments();
        }
        return fragmentView(bundle, savedInstanceState, inflater, container);
    }

    private View fragmentView(Bundle bundle, Bundle savedInstanceState, LayoutInflater inflater, ViewGroup container) {
        if (bundle == null) {
            return inflater.inflate(R.layout.select_movie, container, false);
        }
        movieResult = bundle.getParcelable(MoviesFragment.DETAIL_PARCELABLE);

        final View v = inflater.inflate(R.layout.movie_details_fragment, container, false);

        TextView title = (TextView) v.findViewById(R.id.title);
        final ImageView posterDetail = (ImageView) v.findViewById(R.id.posterDetail);
        TextView release = (TextView) v.findViewById(R.id.release);
        TextView vote = (TextView) v.findViewById(R.id.vote);
        TextView overview = (TextView) v.findViewById(R.id.overview);
        final ImageView fav = (ImageView) v.findViewById(R.id.fav);
        preferences = getActivity().getSharedPreferences(MoviesFragment.PREFS_NAME, getActivity().MODE_PRIVATE);

        final String sdPath = preferences.getString(movieResult.getId(), null);


        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sdPath == null) {
                    needSaving = true;
                    fav.setImageDrawable(getResources().getDrawable(R.drawable.fav_yellow));


                    //save to database
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.MOV_ID_COLUMN, movieResult.getId());
                    Log.v("jtest",""+gotImage);
                    if(gotImage) {
                        Bitmap bitmap = ((BitmapDrawable) posterDetail.getDrawable()).getBitmap();
                        saveBitmap(bitmap);
                    } else {
                        preferences.edit().putString(movieResult.getId(), "1").apply();
                    }
                    values.put(MovieContract.POSTER_LINK_COLUMN, movieResult.getPosterPath());
                    values.put(MovieContract.RELEASE_DATE_COLUMN, movieResult.getReleaseDate());
                    values.put(MovieContract.VOTE_AVG_COLUMN, movieResult.getVoteAverage());
                    values.put(MovieContract.TITLE_COLUMN, movieResult.getTitle());
                    values.put(MovieContract.OVERVIEW_COLUMN, movieResult.getOverview());
                    getActivity().getContentResolver().insert(Uri.parse(MovieContract.CONTENT_URI + "/" + MovieContract.FAV_MOVIE_TABLE_NAME), values);
                    getActivity().getContentResolver().notifyChange(Uri.parse(MovieContract.CONTENT_URI + "/" + MovieContract.FAV_MOVIE_TABLE_NAME), null);

                    if (videos != null) {
                        List<VideosResults> res = videos.getResults();
                        int size = res.size();
                        ContentValues[] valuesArray = new ContentValues[size];
                        for (int i = 0; i < size; i++) {
                            valuesArray[i] = new ContentValues();
                            valuesArray[i].put(MovieContract.MOV_ID_COLUMN, movieResult.getId());
                            valuesArray[i].put(MovieContract.VIDEO_NAME_COLUMN, res.get(i).getName());
                            valuesArray[i].put(MovieContract.VIDEO_KEY_COLUMN, res.get(i).getKey());
                        }
                        getActivity().getContentResolver().bulkInsert(Uri.parse(MovieContract.CONTENT_URI + "/" + MovieContract.TRAILER_TABLE_NAME), valuesArray);
                    }

                    if (reviews != null) {
                        List<ReviewsResults> res = reviews.getResults();
                        int size = res.size();
                        ContentValues[] valuesArray = new ContentValues[size];
                        for (int i = 0; i < size; i++) {
                            valuesArray[i] = new ContentValues();
                            valuesArray[i].put(MovieContract.MOV_ID_COLUMN, movieResult.getId());
                            valuesArray[i].put(MovieContract.COMMENT_AUTHOR_COLUMN, res.get(i).getAuthor());
                            valuesArray[i].put(MovieContract.COMMENT_CONTENT_COLUMN, res.get(i).getContent());
                        }
                        getActivity().getContentResolver().bulkInsert(Uri.parse(MovieContract.CONTENT_URI + "/" + MovieContract.REVIEWS_TABLE_NAME), valuesArray);

                    }

                } else {
                    needSaving = false;
                    File file = new File(sdPath);
                    file.delete();
                    fav.setImageDrawable(getResources().getDrawable(R.drawable.fav_white));
                    preferences.edit().remove(movieResult.getId()).apply();
                    getActivity().getContentResolver().delete(Uri.parse(MovieContract.CONTENT_URI + "/" + MovieContract.FAV_MOVIE_TABLE_NAME), MovieContract.MOV_ID_COLUMN + "=?", new String[]{movieResult.getId()});
                    getActivity().getContentResolver().delete(Uri.parse(MovieContract.CONTENT_URI + "/" + MovieContract.TRAILER_TABLE_NAME), MovieContract.MOV_ID_COLUMN + "=?", new String[]{movieResult.getId()});
                    getActivity().getContentResolver().delete(Uri.parse(MovieContract.CONTENT_URI + "/" + MovieContract.REVIEWS_TABLE_NAME), MovieContract.MOV_ID_COLUMN + "=?", new String[]{movieResult.getId()});
                    getActivity().getContentResolver().notifyChange(Uri.parse(MovieContract.CONTENT_URI + "/" + MovieContract.FAV_MOVIE_TABLE_NAME),null);
                }
            }
        });


        if (sdPath == null) {
            fav.setImageDrawable(getResources().getDrawable(R.drawable.fav_white));
            Glide.with(getActivity().getApplicationContext()).load(MoviesFragment.IMAGE_BASE_URL + movieResult.getPosterPath()).asBitmap().placeholder(R.drawable.placeholder_movies).listener(new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    gotImage = true;
                    Log.v("jtest", "true");
                    if(needSaving) {
                        saveBitmap(resource);
                    }
                    return false;
                }
            }).into(posterDetail);

        } else {
            fav.setImageDrawable(getResources().getDrawable(R.drawable.fav_yellow));

            File file = new File(sdPath);
            Glide.with(getActivity().getApplicationContext()).load(Uri.fromFile(file)).placeholder(R.drawable.placeholder_movies).into(posterDetail);

            //skip network operation
        }


        final LinearLayout detailHolder = (LinearLayout) v.findViewById(R.id.detailHolder);

        title.setText(movieResult.getTitle());
        release.setText(movieResult.getReleaseDate());
        vote.setText("Rating: " + movieResult.getVoteAverage());
        overview.setText(movieResult.getOverview());


        if (savedInstanceState != null) {
            loadFromParcelable(savedInstanceState, v);
        } else {
            if (sdPath != null) {
                loadFromParcelable(bundle, v);
            } else {
                RestAdapter adapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).
                        setEndpoint(MoviesFragment.BASE_URL).build();

                IEndPoints iEndPoints = adapter.create(IEndPoints.class);

                iEndPoints.getVideos(movieResult.getId(), getString(R.string.api_key), new Callback<Videos>() {
                    @Override
                    public void success(Videos videos, Response response) {
                        MovieDetailFragment.this.videos = videos;
                        addVideos(v);

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

                iEndPoints.getReviews(movieResult.getId(), getString(R.string.api_key), new Callback<Reviews>() {
                    @Override
                    public void success(Reviews reviews, Response response) {

                        MovieDetailFragment.this.reviews = reviews;
                        addReviews(v);

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        }

        return v;
    }

    private void saveBitmap(Bitmap bitmap) {
        File dataDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dataDir = new File(Environment.getExternalStorageDirectory(), "myappdata");
            if (!dataDir.isDirectory()) {
                dataDir.mkdirs();
            }
        }

        if (!dataDir.isDirectory()) {
            dataDir = getActivity().getFilesDir();
        }


        File imageFile = new File(dataDir, movieResult.getId() + ".jpg");

        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (outputStream != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            preferences.edit().putString(movieResult.getId(), imageFile.toString()).apply();
        }
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(videos!=null && videos.getResults().size()>0 && menu.findItem(R.id.shareAction)==null) {
            inflater.inflate(R.menu.share_menu, menu);

            // Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem(R.id.shareAction);

            // Fetch and store ShareActionProvider
            shareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Checkout the trailer for " + movieResult.getTitle() + ": http://www.youtube.com/watch?v="+videos.getResults().get(0).getKey());
                shareActionProvider.setShareIntent(shareIntent);

        }
        super.onCreateOptionsMenu(menu, inflater);

    }


    private void loadFromParcelable(Bundle savedInstanceState, View v) {

        videos = savedInstanceState.getParcelable(VIDEO_PARCELABLE);
        reviews = savedInstanceState.getParcelable(REVIEW_PARCELABLE);
        if (videos != null) {
            addVideos(v);
        }
        if (reviews != null) {
            addReviews(v);
        }

    }

    private void addReviews(View v) {
        LinearLayout reviewsHolder = (LinearLayout) v.findViewById(R.id.reviewsHolder);

        List<ReviewsResults> reviewsResults = reviews.getResults();
        int reviewCount = reviewsResults.size();

        if (reviewCount > 0) {

            for (int i = 0; i < reviewCount; i++) {
                LinearLayout reviewView;
                try {
                    reviewView = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.comment, reviewsHolder, false);
                } catch (NullPointerException npe) {
                    //request to load no more valid
                    return;
                }
                TextView author = (TextView) reviewView.findViewById(R.id.author);
                TextView review = (TextView) reviewView.findViewById(R.id.review);
                author.setText(reviewsResults.get(i).getAuthor());
                review.setText(Html.fromHtml(reviewsResults.get(i).getContent()));
                reviewsHolder.addView(reviewView);
            }

            v.findViewById(R.id.reviewsHeader).setVisibility(View.VISIBLE);
        }
    }

    private void addVideos(View v) {
        LinearLayout trailersHolder = (LinearLayout) v.findViewById(R.id.trailersHolder);

        List<VideosResults> videosResults = videos.getResults();
        int videoCount = videosResults.size();


        if (videoCount > 0) {
            int i;
            for (i = 0; i < videoCount; i++) {
                TextView videoName = null;
                try {
                    videoName = new TextView(getActivity());
                } catch (NullPointerException npe) {
                    //load request no more valid
                    return;
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                videoName.setGravity(Gravity.CENTER_VERTICAL);
                params.setMargins(0, 5, 0, 0);
                videoName.setTextColor(getResources().getColor(android.R.color.white));
                videoName.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                videoName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_arrow_white_48dp, 0, 0, 0);
                videoName.setLayoutParams(params);
                videoName.setText(videosResults.get(i).getName());
                trailersHolder.addView(videoName);
                videoName.setTag(videosResults.get(i).getKey());
                videoName.setOnClickListener(MovieDetailFragment.this);
            }

            v.findViewById(R.id.trailersHeader).setVisibility(View.VISIBLE);
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + v.getTag()));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + v.getTag()));
            startActivity(intent);
        }
    }
}
