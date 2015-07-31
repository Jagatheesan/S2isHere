package app.movie.my.popularmovies.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class VideosResults implements Parcelable{

    private String key;
    private String name;

    public VideosResults(Parcel in) {
        key = in.readString();
        name = in.readString();
    }

    public VideosResults() {

    }


    /**
     *
     * @return
     * The key
     */
    public String getKey() {
        return key;
    }

    /**
     *
     * @param key
     * The key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<VideosResults> CREATOR = new Parcelable.Creator<VideosResults>() {
        public VideosResults createFromParcel(Parcel in) {
            return new VideosResults(in);
        }

        public VideosResults[] newArray(int size) {
            return new VideosResults[size];
        }
    };
}