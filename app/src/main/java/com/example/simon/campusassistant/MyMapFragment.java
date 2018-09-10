package com.example.simon.campusassistant;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

/**
 * Created by Jianzhe Hu.
 */

public class MyMapFragment extends Fragment implements OnMapReadyCallback {

    static GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Marker a;
    static Marker[] busstop;
    static int busstopnum;
    static Marker[] studentcenter;
    static int studentcenternum;
    static Marker[] classroom;
    static int classroomnum;
    static Marker[] library;
    static int librarynum;
    static Marker[] food;
    static int foodnum;
    static Marker[] sport;
    static int sportnum;
    static Marker[] yourlocation;
    static int yourlocationnum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        final String[] select_qualification = {
                "Select Category", "Bus Stop", "Student Center", "Classroom", "Library", "Food",
                "Sport", "Your Locations"};
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);

        ArrayList<StateVO> listVOs = new ArrayList<>();

        for (int i = 0; i < select_qualification.length; i++) {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(select_qualification[i]);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }
        MyAdapter2 myAdapter = new MyAdapter2(getActivity(), 0,
                listVOs);
        spinner.setAdapter(myAdapter);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng mLocation = new LatLng(location.getLatitude(),location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation,15));
                        }
                    }
                });
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                }
            };
        };
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (a!=null) {
                    a.remove();
                    a = null;
                }
                else
                    a =mMap.addMarker(new MarkerOptions().position(latLng).title("new").draggable(true));
            }
        });
        final SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase("/data/data/"+getActivity().getPackageName()+"/databases/mapDB.db", null);
        Cursor cursor = database.rawQuery("select * from busstop", null);
        busstopnum=cursor.getCount();
        busstop=new Marker[busstopnum];
        for (int i=0;i<cursor.getCount();i++){
            cursor.moveToFirst();
            cursor.move(i);
            busstop[i]=mMap.addMarker(new MarkerOptions().position(new LatLng(cursor.getDouble(2),cursor.getDouble(3))).title(cursor.getString(1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)));
            busstop[i].setVisible(false);
        }
        cursor = database.rawQuery("select * from studentcenter", null);
        studentcenternum=cursor.getCount();
        studentcenter=new Marker[studentcenternum];
        for (int i=0;i<cursor.getCount();i++){
            cursor.moveToFirst();
            cursor.move(i);
            studentcenter[i]=mMap.addMarker(new MarkerOptions().position(new LatLng(cursor.getDouble(2),cursor.getDouble(3))).title(cursor.getString(1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.studentcenter)));
            studentcenter[i].setVisible(false);
        }
        cursor = database.rawQuery("select * from classroom", null);
        classroomnum=cursor.getCount();
        classroom=new Marker[classroomnum];
        for (int i=0;i<cursor.getCount();i++){
            cursor.moveToFirst();
            cursor.move(i);
            classroom[i]=mMap.addMarker(new MarkerOptions().position(new LatLng(cursor.getDouble(2),cursor.getDouble(3))).title(cursor.getString(1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.classroom)));
            classroom[i].setVisible(false);
        }
        cursor = database.rawQuery("select * from library", null);
        librarynum=cursor.getCount();
        library=new Marker[librarynum];
        for (int i=0;i<cursor.getCount();i++){
            cursor.moveToFirst();
            cursor.move(i);
            library[i]=mMap.addMarker(new MarkerOptions().position(new LatLng(cursor.getDouble(2),cursor.getDouble(3))).title(cursor.getString(1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.library)));
            library[i].setVisible(false);
        }
        cursor = database.rawQuery("select * from food", null);
        foodnum=cursor.getCount();
        food=new Marker[foodnum];
        for (int i=0;i<cursor.getCount();i++){
            cursor.moveToFirst();
            cursor.move(i);
            food[i]=mMap.addMarker(new MarkerOptions().position(new LatLng(cursor.getDouble(2),cursor.getDouble(3))).title(cursor.getString(1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.food)));
            food[i].setVisible(false);
        }
        cursor = database.rawQuery("select * from sport", null);
        sportnum=cursor.getCount();
        sport=new Marker[sportnum];
        for (int i=0;i<cursor.getCount();i++){
            cursor.moveToFirst();
            cursor.move(i);
            sport[i]=mMap.addMarker(new MarkerOptions().position(new LatLng(cursor.getDouble(2),cursor.getDouble(3))).title(cursor.getString(1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.sport)));
            sport[i].setVisible(false);
        }

        database.execSQL("CREATE TABLE IF NOT EXISTS yourlocation (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, latitude DOUBLE, longitude DOUBLE)");
        cursor = database.rawQuery("select * from yourlocation", null);
        yourlocationnum=cursor.getCount();
        yourlocation=new Marker[yourlocationnum];
        for (int i=0;i<cursor.getCount();i++){
            cursor.moveToFirst();
            cursor.move(i);
            yourlocation[i]=mMap.addMarker(new MarkerOptions().position(new LatLng(cursor.getDouble(2),cursor.getDouble(3))).title(cursor.getString(1)));
            yourlocation[i].setVisible(false);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (a!=null)
                {
                    if (marker.getId().equals(a.getId()))
                    {
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View dialog = inflater.inflate(R.layout.dialog,(ViewGroup) getActivity().findViewById(R.id.dialog));
                        final EditText editText = (EditText) dialog.findViewById(R.id.et);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Enter name of the location");
                        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ContentValues cv = new ContentValues();
                                cv.put("name",editText.getText().toString());
                                cv.put("latitude", marker.getPosition().latitude);
                                cv.put("longitude", marker.getPosition().longitude);
                                database.insert("yourlocation", null, cv);
                                String name="Empty";
                                if (!editText.getText().toString().isEmpty())
                                    name=editText.getText().toString();
                                mMap.addMarker(new MarkerOptions().position(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude)).title(name));
                                a.remove();
                                a=null;
                            }
                        });
                        builder.setView(dialog);
                        builder.setIcon(R.mipmap.ic_launcher);
                        builder.show();
                    }
                }
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.getTitle();
            }
        });
    }



    static public void addmarker(){
        LatLng sydney = new LatLng(-34, 151);
        Marker b = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        b.setVisible(false);
        b.setVisible(true);
    }

    static public void addbusstop(){
       for (int i=0;i<busstopnum;i++)
           busstop[i].setVisible(true);
    }
    static public void removebusstop(){
        for (int i=0;i<busstopnum;i++)
            busstop[i].setVisible(false);
    }
    static public void addcenter(){
        for (int i=0;i<studentcenternum;i++)
            studentcenter[i].setVisible(true);
    }
    static public void removecenter(){
        for (int i=0;i<studentcenternum;i++)
            studentcenter[i].setVisible(false);
    }
    static public void addclassroom(){
        for (int i=0;i<classroomnum;i++)
            classroom[i].setVisible(true);
    }
    static public void removeclassroom(){
        for (int i=0;i<classroomnum;i++)
            classroom[i].setVisible(false);
    }
    static public void addlibrary(){
        for (int i=0;i<librarynum;i++)
            library[i].setVisible(true);
    }
    static public void removelibrary(){
        for (int i=0;i<librarynum;i++)
            library[i].setVisible(false);
    }
    static public void addfood(){
        for (int i=0;i<foodnum;i++)
            food[i].setVisible(true);
    }
    static public void removefood(){
        for (int i=0;i<foodnum;i++)
            food[i].setVisible(false);
    }
    static public void addsport(){
        for (int i=0;i<sportnum;i++)
            sport[i].setVisible(true);
    }
    static public void removesport(){
        for (int i=0;i<sportnum;i++)
            sport[i].setVisible(false);
    }
    static public void addyours(){
        for (int i=0;i<yourlocationnum;i++)
            yourlocation[i].setVisible(true);
    }
    static public void removeyours(){
        for (int i=0;i<yourlocationnum;i++)
            yourlocation[i].setVisible(false);
    }
}