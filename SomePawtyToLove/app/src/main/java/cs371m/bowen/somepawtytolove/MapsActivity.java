package cs371m.bowen.somepawtytolove;

import android.app.FragmentManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cs371m.bowen.somepawtytolove.MainActivity.cityState;
import static cs371m.bowen.somepawtytolove.MainActivity.mLastKnownLocation;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, PetJson.IPetJson, GoogleMap.OnInfoWindowClickListener {

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
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
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

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        showPets();
        mMap.setOnInfoWindowClickListener(this);

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (marker.getTag() == null){
            return;
        }
        Pet pet = (Pet)marker.getTag();
        Log.i("maps", "registered marker click" + marker.getTag().toString());
        FragmentManager fm = getFragmentManager();
        Bundle b = new Bundle();

        b.putString("name", pet.getName());
        b.putString("email", pet.getEmail());
        b.putString("picURL", pet.getPic());
        b.putString("description", pet.getDescription());
        b.putString("breed", pet.getBreed());
        b.putString("loc", pet.getLocation());
        b.putString("age", pet.getAge());

        PetFragment myFragment = new PetFragment();
        myFragment.setArguments(b);
        fm.beginTransaction().replace(R.id.frame, myFragment).addToBackStack(null).commit();
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
            mo.title(pet.getName());
            Marker m = mMap.addMarker(mo);
            m.setTag(pet);
        } catch(IOException e) {
            Log.e("maps", "could not parse location");
        }
    }

    @Override
    public void fetchPetList(ArrayList<Pet> pets) {
    }

    @Override
    public void fetchBreedList(ArrayList<String> breeds) {
        // Intentionally left blank. Only Setting should deal with breeds.
    }

    @Override
    public void fetchShelterList(ArrayList<Shelter> shelters) {
        if(mMap==null)
            return;
        for(Shelter s : shelters) {
            //Log.d("found", s.getName());
            MarkerOptions mo = new MarkerOptions();
            try {
                Log.e("maps", "shelter location: "+s.getAddress());
                List<Address> addr = geo.getFromLocationName(s.getAddress(), 1);
                if(addr.isEmpty())
                    return;
                Address a = addr.get(0);
                LatLng pos = new LatLng(a.getLatitude(), a.getLongitude());
                mo.position(pos);
                mo.title(s.getName());
                mMap.addMarker(mo);
            } catch(IOException e) {
                Log.e("maps", "could not parse location");
            }
        }
        if(!shelters.isEmpty())
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shelters.get(0).getMapLocation(), DEFAULT_ZOOM));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    public void showPets() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db.getPetsCallback(currentUser.getUid(), this);
    }
}
