package com.example.myapplication;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AccelMarkerDAO {

    private DatabaseReference databaseReference;
    public AccelMarkerDAO()
    {
        FirebaseDatabase db =  FirebaseDatabase.getInstance();
        databaseReference = db.getReference(AccelMarker.class.getSimpleName());
    }
    public Task<Void> addAccelMarker(AccelMarker am)
    {
          return databaseReference.push().setValue(am);
    }
}
