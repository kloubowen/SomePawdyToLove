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

import static cs371m.bowen.somepawtytolove.MainActivity.cityState;
import static cs371m.bowen.somepawtytolove.MainActivity.mLastKnownLocation;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PetJson.IPetJson {

    private GoogleMap mMap;
    final int DEFAULT_ZOOM = 10;
    private PetFetcher petFetcher;
    private Firebase db;
    private Geocoder geo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        petFetcher = new PetFetcher();
        db = Firebase.getInstance();
        geo = new Geocoder(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void updateLocationUI() {
        if (mMap == null) {
            Log.i("maps", "mMap is null");
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            getDeviceLocation();
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        petFetcher.findShelters(cityState, MapsActivity.this);
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
        MarkerOptions mo = new MarkerOptions();
        try {
            Log.e("maps", "pet location: "+pet.getAddress());
            List<Address> addr = geo.getFromLocationName(pet.getAddress(), 1);
            if(addr.isEmpty())
                return;
            Address a = addr.get(0);
            LatLng pos = new LatLng(a.getLatitude(), a.getLongitude());
            mo.position(pos);
            mMap.addMarker(mo);
        } catch(IOException e) {
            Log.e("maps", "could not parse location");
        }
       // mo.position(s.getMapLocation());
        //mo.title(s.getName());

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

    public void showPets() {
        //db.getSavedPetsQuery();
        db.getPet("id", this);
    }
}
