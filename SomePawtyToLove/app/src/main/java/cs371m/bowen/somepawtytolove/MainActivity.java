package cs371m.bowen.somepawtytolove;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PetJson.IPetJson {
    final int MAPS_ACTIVITY = 1;
    final int SETTINGS_ACTIVITY = 2;
    final int SAVED_ACTIVITY = 3;
    public static ArrayList<Pet> savedPets;
    public static String AppName = "SomePawdyToLove";
    private PetFetcher petFetcher;
    private HashMap<String, String> mySettings;
    private Net net;
    private Pet currentPet;
    private FloatingActionButton rejectButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up listeners
        rejectButton = findViewById(R.id.floatingReject);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rejectPet();
            }
        });

        saveButton = findViewById(R.id.floatingSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                savePet();
            }
        });

        TextView textView = (TextView) findViewById(R.id.descriptionTxt);
        textView.setMovementMethod(new ScrollingMovementMethod());

        savedPets = new ArrayList<>();
        petFetcher = new PetFetcher();
        initMySettings();

        Net.init(getApplicationContext());
        net = Net.getInstance();
        petFetcher.getRandomPet(null, null, null, "78705", this);
    }

    private void initMySettings(){
        mySettings = new HashMap<>();
        mySettings.put("Age", null);
        mySettings.put("Species", null);
        mySettings.put("Breed", null);
        mySettings.put("Sex", null);
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
//        petFetcher.findPets(species, breed, sex, "Austin, Texas", null, this);
//        petFetcher.getShelter("CA790", this);
        disableButtons();

        //petFetcher.getRandomPet("dog", "boxer", null, "78705", this);

        savedPets.add(currentPet);

        String species = mySettings.get("Species");
        if (species != null){
            species = species.toLowerCase();
        }
        String breed = mySettings.get("Breed");
        String sex = mySettings.get("Sex");
        petFetcher.getRandomPet(species, breed, sex, "78705", this);

        //todo: save pet
        //todo: load new pet
        //todo: updatePetUI(pet);
    }

    protected void rejectPet() {
       // Toast toast = Toast.makeText(this, "rejecting pets not yet implemented", Toast.LENGTH_SHORT);
        //toast.show();
        String species = mySettings.get("Species");
        if (species != null){
            species = species.toLowerCase();
            Log.i("reject", species);
        }
        String breed = mySettings.get("Breed");
        String sex = mySettings.get("Sex");
        petFetcher.getRandomPet(species, breed, sex, "78705", this);
        //todo: load new pet
        //todo: updatePetUI(pet);
    }

    protected void updatePetUI(Pet pet) {
        currentPet = pet;
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
        ImageView pic = findViewById(R.id.profileImage);

        net.glideFetch(currentPet.getPic(), pic);
        enableButtons();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_ACTIVITY){
            if (resultCode == RESULT_OK){
                mySettings = (HashMap<String, String>) data.getExtras()
                        .getSerializable("UpdatedSettings");
                Log.i("Settings", mySettings.toString());
            }
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
    public void fetchShelter(Shelter shelter) {

    }

    @Override
    public void fetchShelterList(ArrayList<Shelter> shelters) {

    }

    private void setTxtOr(TextView view, String txt, String alternate) {
        if(txt!=null)
            view.setText(txt);
        else
            view.setText(alternate);
    }

    private void disableButtons(){
        rejectButton.setClickable(false);
        saveButton.setClickable(false);
    }

    private void enableButtons(){
        rejectButton.setClickable(true);
        saveButton.setClickable(true);
    }

    public void changePic(View view){
        ImageView image = view.findViewById(R.id.profileImage);
        String url = currentPet.getPic();
        net.glideFetch(url, image);
    }
}
