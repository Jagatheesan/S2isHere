package app.movie.my.popularmovies.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Videos implements Parcelable {

    private List<VideosResults> results = new ArrayList<VideosResults>();

    public Videos(Parcel in) {
        in.readTypedList(results,VideosResults.CREATOR);
    }

    public Videos() {

    }


    /**
     *
     * @return
     * The results
     */
    public List<VideosResults> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setResults(List<VideosResults> results) {
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

    public static final Parcelable.Creator<Videos> CREATOR = new Parcelable.Creator<Videos>() {
        public Videos createFromParcel(Parcel in) {
            return new Videos(in);
        }

        public Videos[] newArray(int size) {
            return new Videos[size];
        }
    };
}