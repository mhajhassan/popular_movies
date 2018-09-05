package com.nalovma.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nalovma.popularmovies.R;
import com.nalovma.popularmovies.model.MovieVideo;
import com.nalovma.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<MovieVideo> movieVideoList;
    private ItemClickListener mClickListener;

    public VideoAdapter(ItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.video_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        MovieVideo movieVideo = movieVideoList.get(position);
        holder.videoName.setText(movieVideo.getName());
        holder.videoSize.setText(String.valueOf(movieVideo.getSize()));
        holder.videoType.setText(movieVideo.getType());
        String youtubeImageUrl = NetworkUtils.createYoutubeImageUrl(movieVideo.getKey());

        Picasso.get()
                .load(youtubeImageUrl)
                .placeholder(R.drawable.movie_image)
                .error(R.drawable.movie_image)
                .into(holder.videoImageView);

    }

    @Override
    public int getItemCount() {
        if (movieVideoList == null) {
            return 0;
        }
        return movieVideoList.size();
    }

    public MovieVideo getMovieVideoItem(int position) {
        return movieVideoList.get(position);
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView videoImageView;
        TextView videoName;
        TextView videoType;
        TextView videoSize;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoImageView = itemView.findViewById(R.id.iv_video_image);
            videoName = itemView.findViewById(R.id.tv_video_name);
            videoType = itemView.findViewById(R.id.tv_video_type);
            videoSize = itemView.findViewById(R.id.tv_video_size);
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

    public void setMovieVideoList(List<MovieVideo> movieVideoList) {
        this.movieVideoList = movieVideoList;
        notifyDataSetChanged();
    }
}
