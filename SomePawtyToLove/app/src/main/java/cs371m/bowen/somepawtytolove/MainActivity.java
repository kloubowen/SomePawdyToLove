package cs371m.bowen.somepawtytolove;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PetJson.IPetJson {
    final int MAPS_ACTIVITY = 1;
    final int SETTINGS_ACTIVITY = 2;
    final int SAVED_ACTIVITY = 3;
    public static ArrayList<Pet> savedPets;
    public static String AppName = "SomePawdyToLove";
    private PetFetcher petFetcher;
    private Net net;
    private Pet currentPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        net =Net.getInstance();

        //Set up listeners
        ImageButton rejectButton = findViewById(R.id.rejectButton);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rejectPet();
            }
        });

        ImageButton saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                savePet();
            }
        });

        savedPets = new ArrayList<>();
        petFetcher = new PetFetcher();
        Net.init(getApplicationContext());
        petFetcher.getRandomPet(null, null, null, "78705", this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    protected void savePet() {
        //Toast toast = Toast.makeText(this, "saving pets not yet implemented", Toast.LENGTH_SHORT);
        //toast.show();
//        petFetcher.getBreeds("cat", this);
//        petFetcher.getRandomPet(null, null, null, "Austin, TX", this);
//        petFetcher.findPets(null, null, null, "78705", null, this);
//        petFetcher.getShelter("CA790", this);
        petFetcher.getRandomPet(null, null, null, "78705", this);
        //todo: save pet
        //todo: load new pet
        //todo: updatePetUI(pet);
    }

    protected void rejectPet() {
       // Toast toast = Toast.makeText(this, "rejecting pets not yet implemented", Toast.LENGTH_SHORT);
        //toast.show();
        petFetcher.getRandomPet(null, null, null, "78705", this);
        //todo: load new pet
        //todo: updatePetUI(pet);
    }

    protected void updatePetUI(Pet pet) {
        currentPet = pet;
        TextView name = findViewById(R.id.nameTxt);
        name.setText(pet.getName());
        TextView type = findViewById(R.id.speciesTxt);
        type.setText(pet.getBreed());
        TextView location = findViewById(R.id.locationTxt);
        location.setText(pet.getLocation());
        TextView age = findViewById(R.id.ageTxt);
        age.setText(pet.getAge());
        ImageView pic = findViewById(R.id.profileImage);

        net.glideFetch(pet.getPic(), pic);

        //todo: load pic with glide like redfetch
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //todo: load settings
                return true;
            case R.id.action_map:
                Intent i = new Intent(this, MapsActivity.class);
                //i.putExtra("loop_setting", loop);
                //todo: add extras?
                startActivityForResult(i, MAPS_ACTIVITY);
                return true;
            case R.id.action_saved:
                //todo: load saved
                Intent saved = new Intent(this, SavedPets.class);
                startActivity(saved);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void fetchPet(Pet pet) {
        if(pet==null)
            petFetcher.getRandomPet(null, null, null, "78705", this);
        else
            updatePetUI(pet);
    }

    @Override
    public void fetchPetList(ArrayList<Pet> pets) {

    }

    @Override
    public void fetchBreedList(ArrayList<String> breeds) {
        // Intentionally left blank. Only Setting should deal with breeds.
    }

    @Override
    public void fetchShelter(Object shelter) {

    }

    @Override
    public void fetchShelterList(Object shelters) {

    }
}
