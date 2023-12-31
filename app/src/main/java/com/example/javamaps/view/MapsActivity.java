package com.example.javamaps.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.javamaps.R;
import com.example.javamaps.model.Place;
import com.example.javamaps.roomDB.PlaceDao;
import com.example.javamaps.roomDB.PlaceDatabase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.javamaps.databinding.ActivityMapsBinding;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ActivityResultLauncher<String> permissionLauncher;
    LocationManager locationManager;
    LocationListener locationListener;
    SharedPreferences shaderedPreferences;
    Boolean info;
    Marker lastLocationMarker;
    SQLiteDatabase database;
    Double longitude,latitude;
    String placename;
    PlaceDatabase db;
    PlaceDao placeDao;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerLauncher();

        Intent intent =getIntent();
        shaderedPreferences = this.getSharedPreferences("com.example.javamaps",MODE_PRIVATE);
        info = false;
        lastLocationMarker=null;

        db= Room.databaseBuilder(getApplicationContext(),PlaceDatabase.class,"Places")
               // .allowMainThreadQueries()
                .build();
        placeDao=db.placeDao();

        binding.saveButton.setEnabled(false);

        longitude=0.0;
        latitude=0.0;




    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

       //Casting

      locationManager =(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
      locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                info=shaderedPreferences.getBoolean("info",false);
                if (lastLocationMarker != null) {
                    lastLocationMarker.remove();
                }

              if(!info) {
                  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                  shaderedPreferences.edit().putBoolean("info",true).apply();
              }
            }

        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

                Snackbar.make(binding.getRoot(),"Permisson needed for maps",Snackbar.LENGTH_INDEFINITE).setAction("Give Permisson", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //request Permission
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).show();
            }else{
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }else{
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener);

            Location lastLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

            if(lastLocation!=null){
                LatLng lastUserLocation= new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
               lastLocationMarker= mMap.addMarker(new MarkerOptions().position(lastUserLocation).title("Last Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
            }

            mMap.setMyLocationEnabled(true);

        }


    }

    private void registerLauncher(){

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //permisson granted

                    if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener);


                        Location lastLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                        if(lastLocation!=null){
                            LatLng lastUserLocation= new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                           lastLocationMarker= mMap.addMarker(new MarkerOptions().position(lastUserLocation).title("Last Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                        }
                    }
                }else{
                    //Permissom denied
                    Toast.makeText(MapsActivity.this,"Permisson needed!",Toast.LENGTH_LONG).show();

                }


            }
        });

    }



    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));

        latitude=latLng.latitude;
        longitude=latLng.longitude;

        binding.saveButton.setEnabled(true);

    }


    public void Save(View view){

   Place place = new Place(binding.placeText.getText().toString(),latitude,longitude);

        compositeDisposable.add(placeDao.insert(place)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MapsActivity.this::handleResponse)
        );
    }

    private void handleResponse(){
        Intent intent = new Intent(MapsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void delete(View view){

       /* Place place = new Place();

        //placeDao.insert(place).subscribeOn(Schedulers.io().subscribe);

        compositeDisposable.add(placeDao.insert(place)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );*/
    }

   /* public void save(View view){

         placename = binding.placeText.getText().toString();




        try{
            database=this.openOrCreateDatabase("Place",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS place(id INTEGER PRIMARY KEY, placename VARCHAR, latitude DOUBLE,longitude DOUBLE)");

            String sqlString="INSERT INTO place(placename,latitude,longitude)VALUES(?,?,?)";
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
                      sqLiteStatement.bindString(1,placename);
                      sqLiteStatement.bindDouble(2,latitude);
                      sqLiteStatement.bindDouble(3,longitude);
                      sqLiteStatement.execute();

        }catch (Exception e){}

        Intent intent = new Intent(MapsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }*/


}