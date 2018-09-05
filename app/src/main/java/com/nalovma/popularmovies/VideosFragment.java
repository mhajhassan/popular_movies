package com.nalovma.popularmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nalovma.popularmovies.adapter.VideoAdapter;
import com.nalovma.popularmovies.databinding.FragmentVideosBinding;
import com.nalovma.popularmovies.model.MovieVideo;
import com.nalovma.popularmovies.utils.Constants;

import java.util.Objects;

public class VideosFragment extends Fragment implements VideoAdapter.ItemClickListener {

    private FragmentVideosBinding mBinding;
    private VideoAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_videos, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.rvVideos.setLayoutManager(layoutManager);
        mBinding.rvVideos.setHasFixedSize(false);
        mAdapter = new VideoAdapter(this);
        mBinding.rvVideos.setAdapter(mAdapter);

        SharedViewModel sharedViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(SharedViewModel.class);
        sharedViewModel.getMovieVideoData().observe(this, videoList -> {
            mAdapter.setMovieVideoList(videoList);
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        MovieVideo movieVideo = mAdapter.getMovieVideoItem(position);
        watchYoutubeVideo(view.getContext(), String.valueOf(movieVideo.getKey()));
    }


    // method to open youtube link
    public static void watchYoutubeVideo(Context context, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_APP_URL + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_BASE_URL + id));


        if (isAppInstalled("com.google.android.youtube", context)) {
            try {
                appIntent.setClassName("com.google.android.youtube", "com.google.android.youtube.WatchActivity");
                context.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                context.startActivity(webIntent);
            }

        } else {
            try {
                context.startActivity(webIntent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(context, "Youtube Not installed", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //check if an app is installed
    public static boolean isAppInstalled(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }
}
