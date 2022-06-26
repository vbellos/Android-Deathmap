package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    MapView mapView;
    GoogleMap gmap;
    String userID;
    DatabaseReference mref;


    List<AccelMarker> accelMarkerList = new ArrayList<AccelMarker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapView = findViewById(R.id.mapView2);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);


        SharedPreferences mPrefs = getSharedPreferences("userdata", 0);
        userID =mPrefs.getString("UID", UUID.randomUUID().toString());

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        mref = db.getReference("AccelMarker");
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> keys = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(ds!=null) {
                        AccelMarker accelMarker;
                        accelMarker = ds.getValue(AccelMarker.class);

                        if (!accelMarkerList.contains(accelMarker)) {
                            accelMarkerList.add(accelMarker);
                            addMarker(accelMarker);
                        }
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, error.getMessage()); //Don't ignore errors!

            }
        });



    }




    public void goBack(View view)
    {
        finish();
    }


    public void addMarker(AccelMarker accelMarker)
    {
        LatLng latLng = new LatLng(accelMarker.getLoc_lat(),accelMarker.getLoc_long());
        Date date=new Date(accelMarker.getTime());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(String.valueOf(accelMarker.getLoc_lat())+" , "+String.valueOf(accelMarker.getLoc_long()));
        markerOptions.snippet("Acceleration: "+accelMarker.getAcc() + "\n Speed: "+accelMarker.getSpeed() + "\n Time: "+date);
        if(accelMarker.getUser().contains(userID)){
            if(accelMarker.getAcc() <= -0.7){markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.brake_hard_user));}
            else{markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.brake_med_user));}
        }
        else{
            if(accelMarker.getAcc() <= -0.7){markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.brake_hard));}
            else{markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.brake_med));}
        }


        Marker AccMarker = gmap.addMarker(markerOptions);
        //AccMarker.showInfoWindow();
        gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmap = googleMap;

        gmap.setMinZoomPreference(12);
        LatLng ny = new LatLng(37.93494486, 23.74380609);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));

        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(this);
        gmap.setInfoWindowAdapter(adapter);


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