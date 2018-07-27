package com.nalovma.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.nalovma.popularmovies.utils.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    final static String POPULAR_MOVIE = "popular";
    final static String TOP_RATED_MOVIE = "top_rated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URL url = NetworkUtils.createUrl(POPULAR_MOVIE);
        URL url2 = NetworkUtils.createImageUrl("nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg ");

        Log.d("url1",url.toString());
        Log.d("url2",url2.toString());


    }
}
