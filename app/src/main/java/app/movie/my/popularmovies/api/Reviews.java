package app.movie.my.popularmovies.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reviews implements Parcelable {

    private List<ReviewsResults> results = new ArrayList<ReviewsResults>();

    public Reviews(Parcel in) {
        in.readTypedList(results,ReviewsResults.CREATOR);
    }

    public Reviews() {

    }

    /**
     *
     * @return
     * The results
     */
    public List<ReviewsResults> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setResults(List<ReviewsResults> results) {
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

    public static final Parcelable.Creator<Reviews> CREATOR = new Parcelable.Creator<Reviews>() {
        public Reviews createFromParcel(Parcel in) {
            return new Reviews(in);
        }

        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

}