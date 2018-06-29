package com.trevis.trevis.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.trevis.trevis.R;

public class NavigationViewActivity extends AppCompatActivity {

    NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_view);
        navigationView = findViewById(R.id.navigationView);
        navigationView.onCreate(savedInstanceState);
        navigationView.initialize((OnNavigationReadyCallback) this);

        String origin = getIntent().getExtras().getString("origin");
        String destination = getIntent().getExtras().getString("destination");

//        NavigationViewOptions options = NavigationViewOptions.builder()
//                .directionsRoute(DirectionsRoute route)
//                .shouldSimulateRoute(boolean simulateRoute)
//                .build();
    }

//    @Override
//    public void onNavigationReady() {
//
//    }

//    @Override
//    public void onNavigationFinished() {
//        finish();
//    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        // If the navigation view didn't need to do anything, call super
        if (!navigationView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigationView.onDestroy();
    }
}
