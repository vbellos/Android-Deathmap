package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.UUID;


public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    LocationManager locationManager;

    TextView locTXT;
    TextView speedTXT;
    TextView accelTXT;
    Float loc_speed;
    Float loc_acc;
    Long loc_time;

    double loc_lat,loc_long;


    MapView mapView;
    GoogleMap gmap;

    Marker userlocmarker;

    Switch spunitSwitch,accunitSwitch;

    userData userdata = new userData();

    AccelMarkerDAO accelMarkerDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelMarkerDAO=new AccelMarkerDAO();


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        spunitSwitch = findViewById(R.id.speedunit);
        accunitSwitch = findViewById(R.id.accunit);
        locTXT = findViewById(R.id.locTXT);
        speedTXT = findViewById(R.id.speedTXT);
        accelTXT = findViewById(R.id.accTXT);
        mapView = findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        CheckPermissions();


        loadUserData();

        loadSwitches();

        initValues();



    }

    public void loadUserData()
        {
            SharedPreferences mPrefs = getSharedPreferences("userdata", 0);
            userdata.setUserID(mPrefs.getString("UID", UUID.randomUUID().toString()));
            userdata.setSpUnit(mPrefs.getString("spUnit", "km"));
            userdata.setAccUnit(mPrefs.getString("accUnit", "g1"));
            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putString("UID", userdata.getUserID()).commit();
            mEditor.putString("spUnit", userdata.getSpUnit()).commit();
            mEditor.putString("accUnit", userdata.getAccUnit()).commit();
            mEditor.apply();

        }

        public void loadSwitches()
        {
            if (userdata.getSpUnit().contains("km")){spunitSwitch.setChecked(true);}else{spunitSwitch.setChecked(false);}
            if(userdata.getAccUnit().contains("g")){accunitSwitch.setChecked(true);}else{accunitSwitch.setChecked(false);}
        }



    public void CheckPermissions()
    {
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {openPermCheck();}
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, MainActivity.this);

        }
    }

    public void openPermCheck()
    {
        Intent checkperm = new Intent(this,CheckPermissions.class);
        startActivityForResult(checkperm, 1);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                CheckPermissions();
                break;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        loc_lat=location.getLatitude();
        loc_long=location.getLongitude();

        if(loc_speed == null || loc_speed==0){loc_speed=location.getSpeed();}
        Float prev_speed = loc_speed;
        loc_speed=location.getSpeed();

        if(loc_time==null || loc_speed==prev_speed ){loc_time = location.getTime();}
        Long prev_time = loc_time;
        loc_time = location.getTime();

        loc_acc = getAccel(prev_speed,loc_speed,prev_time,loc_time,0.1);

        updateTXT();

        if(gmap!=null){SetUserLocationMarker(location);}


        
        UnitConv uc = new UnitConv(prev_speed,loc_acc);


        if(uc.getAccG()<= (float) -0.5)
        {
            AccelMarker accelMarker = new AccelMarker(userdata.getUserID(),uc.getAccG(),loc_lat,loc_long,loc_time,uc.getSpeedKMH());
            accelMarkerDAO.addAccelMarker(accelMarker);
        }

    }
    public void initValues()
    {
        loc_lat=0;
        loc_long=0;
        loc_speed= Float.valueOf(0);
        loc_acc = Float.valueOf(0);
        updateTXT();
    }

    public void UnitChanged(View iew)
    {
        updateTXT();
        SharedPreferences mPrefs = getSharedPreferences("userdata", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();

        if(spunitSwitch.isChecked()){userdata.setSpUnit("km");}else{userdata.setSpUnit("m/s");}
        if(accunitSwitch.isChecked()){userdata.setAccUnit("g");}else{userdata.setAccUnit("m/s2");}

        mEditor.putString("spUnit", userdata.getSpUnit()).commit();
        mEditor.putString("accUnit", userdata.getAccUnit()).commit();
        mEditor.apply();

    }

    public void updateTXT()
    {

        UnitConv uc = new UnitConv(loc_speed,loc_acc);
        String speed,acc;

        Switch sp = findViewById(R.id.speedunit);
        Switch ac = findViewById(R.id.accunit);
        if (!sp.isChecked()){speed = String.valueOf(uc.getSpeedMS())+"m/s";}else{speed= String.valueOf(uc.getSpeedKMH()+"km/h");}
        if(!ac.isChecked()){acc = String.valueOf(uc.getAccMS2()+"m/s^2");}else{acc = String.valueOf(uc.getAccG()+"G");}

        locTXT.setText(String.valueOf(loc_lat)+"\n"+String.valueOf(loc_long));
        speedTXT.setText(speed);
        accelTXT.setText(acc);
    }

    public Float getAccel(float v1, float v2, long t1,long t2,double sensitivity)
    {
        if(t1!=t2) {
            float dt = Float.valueOf((t2 - t1) / 1000);
            float dv = v2 - v1;
            float acc= dv / dt;
            if(Math.abs(Math.abs(loc_acc)-Math.abs(acc)) >= sensitivity){return acc;}
            else{return loc_acc;}
        }
        else{return Float.valueOf(0);}
    }


    public void viewMaps(View view)
    {
        Intent viewMap = new Intent(this,MapsActivity.class);
        startActivity(viewMap);
    }

    public void StopUpdates(View view)
    {
        locationManager.removeUpdates(MainActivity.this);
        initValues();
    }

    public void SetUserLocationMarker(Location location)
    {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userlocmarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.redcar));

            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            userlocmarker = gmap.addMarker(markerOptions);
            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else  {
            //use the previously created marker
            userlocmarker.setPosition(latLng);
            userlocmarker.setRotation(location.getBearing());

            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
    }


    @Override
    public void onProviderEnabled(@NonNull String provider) {
        System.out.println("GPS ENABLED");
        //Toast.makeText(MainActivity.this,"onProviderEnabled",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        System.out.println("GPS DISABLED");
        Toast.makeText(MainActivity.this,"onProviderDisabled",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        System.out.println("DEBUG 4");
        Toast.makeText(MainActivity.this,"onStatusChanged",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmap = googleMap;

        gmap.setMinZoomPreference(12);


    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outperse) {
        super.onSaveInstanceState(outState,outperse);
        mapView.onSaveInstanceState(outState);

    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}