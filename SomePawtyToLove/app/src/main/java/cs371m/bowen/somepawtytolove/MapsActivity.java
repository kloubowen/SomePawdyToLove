package cs371m.bowen.somepawtytolove;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PetJson.IPetJson {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mDefaultLocation;
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5;
    final int DEFAULT_ZOOM = 10;
    private Location mLastKnownLocation;
    private PetFetcher petFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        petFetcher = new PetFetcher();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mDefaultLocation = new LatLng(-34, 151);

    }

    //code from documentation: https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //code from documentation: https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    public void updateLocationUI() {
        if (mMap == null) {
            Log.i("maps", "mMap is null");
            return;
        }
        try {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                getDeviceLocation();
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Log.i("maps", "location successfully found");
                            mLastKnownLocation = (Location)task.getResult();
                            Log.i("maps", mLastKnownLocation.toString());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            try {
                                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1);
//                                String cityName = addresses.get(0).getAddressLine(0);
//                                String stateName = addresses.get(0).getAddressLine(1);
//                                String countryName = addresses.get(0).getAddressLine(2);
                                //Log.d("location of person", cityName+", "+stateName);
                                petFetcher.findShelters("Austin, TX", MapsActivity.this);

                            } catch (IOException e) {
                                Log.e("error", "getting address");
                            }

                        } else {
                            Log.d("map", "Current location is null. Using defaults.");
                            Log.e("map", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            } else
                Log.i("map", "permission not granted");
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("maps", "onMapReady Called");
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
//        getDeviceLocation();

    }

    @Override
    public void fetchPet(Pet pet) {

    }

    @Override
    public void fetchPetList(ArrayList<Pet> pets) {
    }

    @Override
    public void fetchBreedList(ArrayList<String> breeds) {
        // Intentionally left blank. Only Setting should deal with breeds.
    }

    @Override
    public void fetchShelter(Shelter shelter) {

    }

    @Override
    public void fetchShelterList(ArrayList<Shelter> shelters) {
        if(mMap==null)
            return;
        for(Shelter s : shelters) {
            //Log.d("found", s.getName());
            MarkerOptions mo = new MarkerOptions();
            mo.position(s.getMapLocation());
            mo.title(s.getName());
            mMap.addMarker(mo);
        }
        if(!shelters.isEmpty())
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shelters.get(0).getMapLocation(), DEFAULT_ZOOM));
    }
}
