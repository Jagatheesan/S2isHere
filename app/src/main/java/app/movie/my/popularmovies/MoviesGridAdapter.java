package app.movie.my.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import app.movie.my.popularmovies.api.MoviesResults;

public class MoviesGridAdapter extends BaseAdapter {

    protected List<MoviesResults> movies;
    private Context context;

    MoviesGridAdapter(List<MoviesResults> movies, Context context){
        this.movies = movies;
        this.context = context;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_element,parent,false);
        }
        //convertView.setTag(position);
        Glide.with(context).load(movies.get(position).getPosterPath()).placeholder(R.drawable.placeholder_movies).into((ImageView) convertView);
        return convertView;
    }
}
