package cs371m.bowen.somepawtytolove;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("ClickableViewAccessibility")
public class MainActivity extends AppCompatActivity implements PetJson.IPetJson, Settings.ISettings {
    final int MAPS_ACTIVITY = 1;
    final int SETTINGS_ACTIVITY = 2;
    final int SAVED_ACTIVITY = 3;
    final int SIGN_IN = 4;
    public static ArrayList<Pet> savedPets;
    public static String AppName = "SomePawdyToLove";
    public static String lastOffset = "0";
    private ArrayList<Pet> fetchedPets;
    private PetFetcher petFetcher;
    private HashMap<String, String> mySettings;
    private Net net;
    private Pet currentPet;
    private int nextPetIndex;
    private FloatingActionButton rejectButton, saveButton;
    private FirebaseAuth mAuth;
    private Firebase firebase;
    private FirebaseUser currentUser;

    private ImageView pic;
    private float x1, x2, y1, y2;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5;
    public static Location mLastKnownLocation;
    public static String cityState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebase = Firebase.getInstance();
        firebase.init();

        if(currentUser==null){
            updateUser();
        }
        else{
            getLocationPermission();
            Log.i("authcheck", "current user: "+ currentUser.getEmail());
        }

        //Set up listeners
        rejectButton = findViewById(R.id.floatingReject);
        saveButton = findViewById(R.id.floatingSave);
        pic = findViewById(R.id.profileImage);
        pic.setOnTouchListener(new Swipe());

        TextView textView = (TextView) findViewById(R.id.descriptionTxt);
        textView.setMovementMethod(new ScrollingMovementMethod());

        savedPets = new ArrayList<>();
        petFetcher = new PetFetcher();

        Net.init(getApplicationContext());
        net = Net.getInstance();
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
            getDeviceLocation();
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
        getDeviceLocation();
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
                            try {
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude(), 1);
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                if (city == null || state == null){
                                    throw new IOException();
                                }
                                cityState = city + ", " + state;
                                //todo: remove this line
                                cityState = "Austin, Texas";
                                initMySettings();
                            } catch (IOException e) {
                                Log.e("error", "getting address");
                            }
                        }
                    }
                });
            } else
                Log.i("map", "permission not granted");
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void initMySettings(){
        firebase.getSettings(this);
    }

    public void updateUser() {
        Log.i("authcheck", "update user called");
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(), SIGN_IN);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void savePet(View view) {
        //Toast toast = Toast.makeText(this, "saving pets not yet implemented", Toast.LENGTH_SHORT);
        //toast.show();
//        petFetcher.getBreeds("cat", this);
//        petFetcher.getRandomPet("dog", "boxer", null, "78705", this);
//        petFetcher.getShelter("CA790", this);
        savedPets.add(currentPet);
        firebase.savePet(currentPet);
        loadNewPet(view);

        //todo: save pet
        //todo: load new pet
        //todo: updatePetUI(pet);
    }

    public void loadNewPet(View view) {
       // Toast toast = Toast.makeText(this, "rejecting pets not yet implemented", Toast.LENGTH_SHORT);
        //toast.show();
        disableButtons();
        if (fetchedPets == null || nextPetIndex == fetchedPets.size()){
            String species = mySettings.get("Species");
            if (species != null){
                species = species.toLowerCase();
            }
            String breed = mySettings.get("Breed");
            String sex = mySettings.get("Sex");
            String age = mySettings.get("Age");
            petFetcher.findPets(species, breed, sex, cityState, age, this);
        } else{
            currentPet = fetchedPets.get(nextPetIndex);
            updatePetUI();
        }
    }

    protected void updatePetUI(Pet pet) {
        currentPet = pet;
        updatePetUI();
    }

    protected void updatePetUI() {
        TextView name = findViewById(R.id.nameTxt);
        setTxtOr(name, currentPet.getName(), "No Name");
        TextView type = findViewById(R.id.speciesTxt);
        setTxtOr(type, currentPet.getBreed(), "Breed Unknown");
        TextView location = findViewById(R.id.locationTxt);
        setTxtOr(location, currentPet.getLocation(), "Location Unknown");
        TextView age = findViewById(R.id.ageTxt);
        setTxtOr(age, currentPet.getAge(), "Age Unknown");
        TextView bio = findViewById(R.id.descriptionTxt);
        bio.setMovementMethod(new ScrollingMovementMethod());
        setTxtOr(bio, currentPet.getDescription(), "");
        net.glideFetch(currentPet.getPic(), pic);
        nextPetIndex++;
        enableButtons();
    }

    protected void signOut(){
        //maybe clear current ui
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUser();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //todo: load settings
                Intent settingsIntent = new Intent(this, Settings.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("CurrentSettings", mySettings);
                settingsIntent.putExtras(bundle);
                startActivityForResult(settingsIntent, SETTINGS_ACTIVITY);
                return true;
            case R.id.action_map:
                Intent i = new Intent(this, MapsActivity.class);
                //i.putExtra("loop_setting", loop);
                //todo: add extras?
                startActivityForResult(i, MAPS_ACTIVITY);
                return true;
            case R.id.action_saved:
                //todo: load saved
                Intent savedIntent = new Intent(this, SavedPets.class);
                startActivity(savedIntent);
                return true;
            case R.id.action_sign_out:
                signOut();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_ACTIVITY){
            if (resultCode == RESULT_OK){
                HashMap<String, String> newSettings =
                        (HashMap<String, String>) data.getSerializableExtra("UpdatedSettings");
                Log.i("Settings", newSettings.toString());
                if (!newSettings.equals(mySettings)){
                    mySettings = newSettings;
                    lastOffset = "0";
                    fetchedPets = null;
                    nextPetIndex = 0;
                    rejectButton.performClick();
                }
            }
        } else if(requestCode == SIGN_IN){
            if (resultCode == RESULT_OK){
                initMySettings();
                getLocationPermission();
            } else {
                updateUser();
            }
        }
    }

    @Override
    public void fetchPet(Pet pet) {
        if(pet==null) {
            String species = mySettings.get("Species");
            if (species != null){
                species = species.toLowerCase();
            }
            String breed = mySettings.get("Breed");
            String sex = mySettings.get("Sex");
            petFetcher.getRandomPet(species, breed, sex, "78705", this);
        } else{
            updatePetUI(pet);
        }
    }

    @Override
    public void fetchPetList(ArrayList<Pet> pets) {
        if (pets == null){
            Toast.makeText(this, "We couldn't find any pets. Please change your settings",
                    Toast.LENGTH_SHORT).show();
        } else if(pets.isEmpty()){
            petFetcher.findPets(null, null, null, cityState, null, this);
        } else{
            nextPetIndex = 0;
            fetchedPets = pets;
            currentPet = fetchedPets.get(nextPetIndex);
            updatePetUI();
        }
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

    }

    @Override
    public void loadSettings(HashMap<String, String> userSettings) {
        mySettings = userSettings;
        rejectButton.performClick();
    }

    private void setTxtOr(TextView view, String txt, String alternate) {
        if(txt!=null)
            view.setText(txt);
        else
            view.setText(alternate);
        view.scrollTo(0, 0);
    }

    private void disableButtons(){
        rejectButton.setClickable(false);
        saveButton.setClickable(false);
    }

    private void enableButtons(){
        rejectButton.setClickable(true);
        saveButton.setClickable(true);
    }

    public void changePic(){
        String url = currentPet.getPic();
        net.glideFetch(url, pic);
    }

    private class Swipe implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    y1 = event.getY();
                    return true;
                case MotionEvent.ACTION_UP:
                    x2 = event.getX();
                    y2 = event.getY();
                    if (x1 == x2 && y1 == y2) {
                        changePic();
                    } else if (x1 > x2) {
                        // Swipe Left
                        rejectButton.performClick();
                    } else if (x2 > x1) {
                        // Swipe Right
                        saveButton.performClick();
                    }
                    return true;
            }
            return false;
        }
    }
}
