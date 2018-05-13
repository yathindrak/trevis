package com.trevis.trevis;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    static boolean status;
    LocationManager locationManager;
    Button btn;
    static int p=0;

    LocationService myService;

    int PERMISSIONS_REQUEST = 10;


    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myService = binder.getService();
            status = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status=false;
        }
    };

    @Override
    protected void onDestroy() {
        if(status==true){
            unbindService();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkGps();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }


        if (status == false)
            bindService();


        btn = (Button)findViewById(R.id.button);
        //Request Permission
        checkPermissions();

        /*
        * This should be triggered when the user is in a trouble*/
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("How to close alertdialog programmatically");
                builder.setMessage("5 second dialog will close automatically");
                builder.setCancelable(false);

                final AlertDialog closedialog= builder.create();


                closedialog.setButton(AlertDialog.BUTTON_POSITIVE, "Proceed", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        //Send data to the backend
                        System.out.println("Lat : " +LocationService.mCurrentLocation.getLatitude());
                        System.out.println("Long : " +LocationService.mCurrentLocation.getLongitude());

                        //Pass user id and location

                    } });

                closedialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        System.out.println("Clickedd");

                    } });
                closedialog.show();

                final Timer timer2 = new Timer();
                timer2.schedule(new TimerTask() {
                    public void run() {
                        closedialog.dismiss();
                        timer2.cancel(); //this will cancel the timer of the system
                    }
                }, 5000);
            }
        });


    }

    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED    ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED    ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED    ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CONTROL_LOCATION_UPDATES,
                    Manifest.permission.INSTALL_LOCATION_PROVIDER,
                    Manifest.permission.ACCESS_NETWORK_STATE,

            }, PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Not GRANTED", Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }
    }

    @Override
    public void onBackPressed() {
        if(status==false){
            super.onBackPressed();
        }
        else{
            moveTaskToBack(true);
        }
    }

    void bindService() {
        if (status == true)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        bindService(i, sc, BIND_AUTO_CREATE);
        status = true;
    }

    void unbindService() {
        if (status == false)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        unbindService(sc);
        status = false;
    }

    //This method leads you to the alert dialog box.
    void checkGps() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }
    }

    //This method configures the Alert Dialog box.
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
