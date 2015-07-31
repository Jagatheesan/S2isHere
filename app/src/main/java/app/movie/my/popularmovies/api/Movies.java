package app.movie.my.popularmovies.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Movies implements Parcelable {

    private List<MoviesResults> results = new ArrayList<MoviesResults>();

    public Movies(Parcel in) {
        in.readTypedList(results,MoviesResults.CREATOR);
    }

    public Movies() {

    }

    /**
     * @return The results
     */
    public List<MoviesResults> getResults() {
        return results;
    }

    /**
     * @param results The results
     */
    public void setResults(List<MoviesResults> results) {
        this.results = results;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(results);
    }

    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };
}
