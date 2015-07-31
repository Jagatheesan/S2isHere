package app.movie.my.popularmovies.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import app.movie.my.popularmovies.MovieTiles;
import app.movie.my.popularmovies.MoviesFragment;

/**
 * Created by dell on 22-07-2015.
 */
public class MoviesResults implements Parcelable {

    private String id;
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("poster_path")
    private String posterPath;
    private String popularity;
    private String title;
    private String sdImage;
    @SerializedName("vote_average")
    private String voteAverage;

    public MoviesResults(Parcel in) {
        id = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        popularity = in.readString();
        title = in.readString();
        voteAverage = in.readString();
        sdImage=in.readString();
    }

    public MoviesResults(){
        sdImage="";
    }

    public String getSdImage() {
        return sdImage;
    }

    public void setSdImage(String sdImage) {
        this.sdImage = sdImage;
    }

    /**
     * @return The id
     */
    public String getId() { if(id!=null)
        return id; else return "";
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The overview
     */
    public String getOverview() {
        if (overview != null) return overview;
        else return "";
    }

    /**
     * @param overview The overview
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     * @return The releaseDate
     */
    public String getReleaseDate() {
        if (releaseDate != null) return releaseDate;
        return "";
    }

    /**
     * @param releaseDate The release_date
     */
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * @return The posterPath
     */
    public String getPosterPath() {
        if (posterPath != null)
            return MoviesFragment.IMAGE_BASE_URL+posterPath;
        else return "";
    }

    /**
     * @param posterPath The poster_path
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    /**
     * @return The popularity
     */
    public String getPopularity() { if(popularity!=null)
        return popularity; else return "";
    }

    /**
     * @param popularity The popularity
     */
    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    /**
     * @return The title
     */
    public String getTitle() { if(title!=null)
        return title; else return "";
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The voteAverage
     */
    public String getVoteAverage() { if(voteAverage!=null)
        return voteAverage; else return "";
    }

    /**
     * @param voteAverage The vote_average
     */
    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeString(popularity);
        dest.writeString(title);
        dest.writeString(voteAverage);
        dest.writeString(sdImage);
    }

    public static final Parcelable.Creator<MoviesResults> CREATOR = new Parcelable.Creator<MoviesResults>() {
        public MoviesResults createFromParcel(Parcel in) {
            return new MoviesResults(in);
        }

        public MoviesResults[] newArray(int size) {
            return new MoviesResults[size];
        }
    };
}

