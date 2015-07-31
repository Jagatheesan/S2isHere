package app.movie.my.popularmovies.api;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface IEndPoints {

    @GET("/discover/movie")
    public void getMovies(@Query("sort_by") String sort, @Query("api_key") String apiKey, Callback<Movies> response);

    @GET("/movie/{id}/reviews")
    public void getReviews(@Path("id") String id, @Query("api_key") String apiKey, Callback<Reviews> response);

    @GET("/movie/{id}/videos")
    public void getVideos(@Path("id") String id, @Query("api_key") String apiKey, Callback<Videos> response);

}
