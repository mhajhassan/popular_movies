package com.nalovma.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nalovma.popularmovies.R;
import com.nalovma.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private ItemClickListener mClickListener;

    /**
     * Constructs a new {@link MovieAdapter}.
     */
    public MovieAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        String moviePoster = movie.getPosterPath();
        Picasso.get()
                .load(moviePoster)
                .placeholder(R.drawable.movie_image)
                .error(R.drawable.movie_image)
                .into(holder.movieImageView);

    }

    /*
     * return movie item by position
     * */
    public Movie getItem(int position) {
        return movieList.get(position);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    /**
     * Cache of the children views for a movie list.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView movieImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            movieImageView = itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }
}
