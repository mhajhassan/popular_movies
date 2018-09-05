package com.nalovma.popularmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nalovma.popularmovies.adapter.ReviewAdapter;
import com.nalovma.popularmovies.databinding.FragmentReviewsBinding;

import java.util.Objects;

public class ReviewsFragment extends Fragment {

    private FragmentReviewsBinding mBinding;

    private ReviewAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reviews, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.rvReviews.setLayoutManager(layoutManager);
        mBinding.rvReviews.setHasFixedSize(false);
        mAdapter = new ReviewAdapter();
        mBinding.rvReviews.setAdapter(mAdapter);

        SharedViewModel sharedViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(SharedViewModel.class);
        sharedViewModel.getMovieReviewData().observe(this, movieReviews ->
                mAdapter.setMovieReviewList(movieReviews));
    }
}
