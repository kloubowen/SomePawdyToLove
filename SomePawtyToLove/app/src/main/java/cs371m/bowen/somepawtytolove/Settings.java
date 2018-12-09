package cs371m.bowen.somepawtytolove;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Settings extends AppCompatActivity implements PetJson.IPetJson {
    private Spinner species, breeds;
    private RadioGroup sex, age;
    private PetFetcher petFetcher;
    private TextView message;
    private String[] speciesList;
    private String[] breedList;
    private HashMap<String, String> mySettings;
    private Firebase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        //
        db = Firebase.getInstance();
        mySettings = (HashMap<String, String>) getIntent().getExtras()
                .getSerializable("CurrentSettings");
        speciesList = getResources().getStringArray(R.array.animals);
        petFetcher = new PetFetcher();
        species = findViewById(R.id.species_spinner);
        breeds = findViewById(R.id.breed_spinner);
        sex = findViewById(R.id.sex_group);
        age = findViewById(R.id.age_group);
        message = findViewById(R.id.breed_message);

        ArrayAdapter<CharSequence> speciesAdapter = ArrayAdapter.createFromResource(this,
                R.array.animals, android.R.layout.simple_spinner_item);
        speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        species.setAdapter(speciesAdapter);
        species.setOnItemSelectedListener(new SpeciesListener());
        setSettings();
    }

    private void setSettings(){
        String myAge = mySettings.get("Age");
        if (myAge != null){
            switch (myAge){
                case "Baby": age.check(R.id.baby);break;
                case "Adult": age.check(R.id.adult);break;
                case "Senior": age.check(R.id.senior);break;
            }
        }

        String mySex = mySettings.get("Sex");
        if (mySex != null){
            switch (mySex){
                case "M": sex.check(R.id.male);break;
                case "F": sex.check(R.id.female);break;
            }
        }

        String mySpecies = mySettings.get("Species");
        if (mySpecies != null){
            int position = Arrays.binarySearch(speciesList, mySpecies);
            species.setSelection(position);
            breeds.setVisibility(View.VISIBLE);
            message.setVisibility(View.GONE);
        }
    }

    public void apply(View view){
        Intent result = new Intent();
        if (species.getSelectedItemPosition() != 0){
            mySettings.put("Species", speciesList[species.getSelectedItemPosition()]);
            if (breeds.getSelectedItemPosition() != 0){
                mySettings.put("Breed", breedList[breeds.getSelectedItemPosition()]);
            }
        }

        switch (sex.getCheckedRadioButtonId()){
            case R.id.male: mySettings.put("Sex", "M");break;
            case R.id.female: mySettings.put("Sex", "F");break;
            case R.id.both: mySettings.put("Sex", null);break;
        }

        switch (age.getCheckedRadioButtonId()){
            case R.id.baby: mySettings.put("Age", "Baby");break;
            case R.id.adult: mySettings.put("Age", "Adult");break;
            case R.id.senior: mySettings.put("Age", "Senior");break;
            case R.id.all_age: mySettings.put("Age", null);break;
        }
        db.saveSettings(mySettings);
        Bundle bundle = new Bundle();
        bundle.putSerializable("UpdatedSettings", mySettings);
        result.putExtras(bundle);
        setResult(RESULT_OK, result);
        Log.i("Settings", "Applied");
        finish();
    }

    public void cancel(View view){
        Intent result = new Intent();
        setResult(RESULT_CANCELED, result);
        finish();
    }

    @Override
    public void fetchPet(Pet pet) {

    }

    @Override
    public void fetchPetList(ArrayList<Pet> pets) {

    }

    @Override
    public void fetchBreedList(ArrayList<String> breeds) {
        breeds.add(0, "Please select a breed");
        breedList = breeds.toArray(new String[breeds.size()]);
        ArrayAdapter<String> breedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, breeds);
        breedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.breeds.setAdapter(breedAdapter);
        String myBreed = mySettings.get("Breed");
        if (myBreed != null){
            int position = Arrays.binarySearch(breedList, myBreed);
            if (position < 0){
                position = 0;
            }
            this.breeds.setSelection(position);
        }
        Log.i("Settings ", "Breeds set");
    }

    @Override
    public void fetchShelter(Shelter shelter) {

    }

    @Override
    public void fetchShelterList(ArrayList<Shelter> shelters) {

    }

    private class SpeciesListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0){
                breeds.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
            } else{
                breeds.setVisibility(View.VISIBLE);
                message.setVisibility(View.GONE);
                String selectedSpecies = speciesList[position].toLowerCase();
                petFetcher.getBreeds(selectedSpecies, Settings.this);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
