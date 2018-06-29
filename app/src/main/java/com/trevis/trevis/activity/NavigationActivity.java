package com.trevis.trevis.activity;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

// classes needed to initialize map
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;

// classes needed to add location layer
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;

import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

// classes needed to add a marker
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

// classes to calculate a route
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.support.v7.widget.Toolbar;
import android.util.Log;

// classes needed to launch navigation UI
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.trevis.trevis.R;


public class NavigationActivity extends AppCompatActivity implements LocationEngineListener, PermissionsListener ,
        NavigationView.OnNavigationItemSelectedListener{


    private MapView mapView;

    static boolean status;
    private FirebaseAuth mAuth;

    // variables for adding location layer
    private MapboxMap map;
    //Built in permission manager of mapbox
    private PermissionsManager permissionsManager;
    //Creates the ui of the map
    private LocationLayerPlugin locationPlugin;
    //Can access location related stuff
    private LocationEngine locationEngine;
    //Stores the current location
    private Location originLocation;


    // variables for adding a marker
    private Marker destinationMarker;
    private LatLng originCoord;
    private LatLng destinationCoord;


    // variables for calculating and drawing a route
    private Point originPosition;
    private Point destinationPosition;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    //Provide runtime stylings for navigation
    private NavigationMapRoute navigationMapRoute;


    private Button button;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_navigation);
        mToolbar = (Toolbar) findViewById(R.id.people_toolbar);
        //setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Navigation");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);



        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {

                map = mapboxMap;
                enableLocationPlugin();

                originCoord = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());
                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        if (destinationMarker != null) {
                            mapboxMap.removeMarker(destinationMarker);
                        }
                        destinationCoord = point;
                        destinationMarker = mapboxMap.addMarker(new MarkerOptions()
                                .position(destinationCoord)
                        );


                        destinationPosition = Point.fromLngLat(destinationCoord.getLongitude(), destinationCoord.getLatitude());
                        originPosition = Point.fromLngLat(originCoord.getLongitude(), originCoord.getLatitude());
                        getRoute(originPosition, destinationPosition);

                        button.setEnabled(true);
                        button.setBackgroundResource(R.color.mapboxBlue);
                    }
                });


                button = findViewById(R.id.startButton);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Point origin = originPosition;
                        Point destination = destinationPosition;

                        Intent intent = new Intent(NavigationActivity.this, NavigationViewActivity.class);
                        intent.putExtra("origin", origin);  // pass your values and retrieve them in the other Activity using keyName
                        intent.putExtra("destination", destination);
                        startActivity(intent);

//                        boolean simulateRoute = true;
//                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
//                                .origin(origin)
//                                .destination(destination)
//                                .shouldSimulateRoute(simulateRoute)
//                                .build();
//
//                        // Call this method with Context from within an Activity
//                        NavigationLauncher.startNavigation(NavigationActivity.this, options);


                    }
                });


            }


        });


    }


    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        //Normally we gets the 1st route
                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if(status==false){
            super.onBackPressed();
        }
        else{
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trouble, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }

        if (item.getItemId() == R.id.main_logout_btn) {

            //mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_location) {
            // Go to location activity
            Intent startIntent = new Intent(NavigationActivity.this, NavigationActivity.class);
            startActivity(startIntent);
        } else if (id == R.id.nav_trouble) {

            Intent startIntent = new Intent(NavigationActivity.this, TroubleActivity.class);
            startActivity(startIntent);
            finish();

        } else if (id == R.id.nav_community) {
            Intent startIntent = new Intent(NavigationActivity.this, CommunityActivity.class);
            startActivity(startIntent);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of LOST location engine
            initializeLocationEngine();
            //initializeLocationLayer();

            locationPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
            locationPlugin.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    // Send to start if not logged in successfully
    private void sendToStart() {
        Intent startIntent = new Intent(NavigationActivity.this, LoginActivity.class);
        startActivity(startIntent);
        // When user go there we dont need to cme him back to this page. So finish that
        finish();
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationLayer() {
        locationPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationPlugin.setLocationLayerEnabled(true);
        locationPlugin.setCameraMode(CameraMode.TRACKING);
        locationPlugin.setRenderMode(RenderMode.NORMAL);
    }


    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //This calls when user denies the permissions for the first time
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    //This calls if permissions granted
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPosition(location);
            locationEngine.removeLocationEngineListener(this);
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


}
