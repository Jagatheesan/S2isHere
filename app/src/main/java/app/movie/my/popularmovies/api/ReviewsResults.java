package app.movie.my.popularmovies.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class ReviewsResults implements Parcelable{

    private String author;
    private String content;

    public ReviewsResults(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    public ReviewsResults() {

    }


    /**
     *
     * @return
     * The author
     */
    public String getAuthor() { if(author!=null)
        return author; else return "";
    }

    /**
     *
     * @param author
     * The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     *
     * @return
     * The content
     */
    public String getContent() {
        if(content!=null) return content; else return "";
    }

    /**
     *
     * @param content
     * The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(author);
        dest.writeString(content);
    }

    public static final Parcelable.Creator<ReviewsResults> CREATOR = new Parcelable.Creator<ReviewsResults>() {
        public ReviewsResults createFromParcel(Parcel in) {
            return new ReviewsResults(in);
        }

        public ReviewsResults[] newArray(int size) {
            return new ReviewsResults[size];
        }
    };
}
