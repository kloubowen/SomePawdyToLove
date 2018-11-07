package cs371m.bowen.somepawtytolove;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

//@SuppressWarnings("FieldCanBeLocal")
public class PetFetcher {
    // Key
    private final String MY_KEY = "57b5bace4f4f4f114e806f0a13bb422a";

    // Api methods
    private final String BREEDS = "breed.list";
    private final int BREEDS_ID = 1;
    private final String SINGLE_PET = "pet.get";
    private final int SINGLE_PET_ID = 2;
    private final String RANDOM_PET = "pet.getRandom";
    private final int RANDOM_PET_ID = 3;
    private final String FIND_PETS = "pet.find";
    private final int FIND_PETS_ID = 4;
    private final String SINGLE_SHELTER = "shelter.get";
    private final int SINGLE_SHELTER_ID = 5;
    private final String FIND_SHELTERS = "shelter.find";
    private final int FIND_SHELTERS_ID = 6;
    private final String SHELTER_PETS = "shelter.getPets";
    private final int SHELTER_PETS_ID = 7;
    private final String BREED_SHELTERS = "shelter.listByBreed";
    private final int BREED_SHELTERS_ID = 8;

    // Url variables
    private Uri.Builder builder;
    private final String SCHEME = "https";
    private final String AUTHORITY = "api.petfinder.com";
    private final String KEY = "key";
    private final String FORMAT = "format";
    private final String JSON = "json";
    private final String ID = "id";
    private final String ANIMAL = "animal";
    private final String BREED = "breed";
    private final String SEX = "sex";
    private final String LOCATION = "location";
    private final String OUTPUT = "output";
    private final String FULL = "full";
    private final String AGE = "age";

    private final String TAG = "Fetch";

    public void getBreeds(String species){
        if (species == null){
            Log.i(TAG, "No type of animal was given.");
            return;
        }
        createBaseUrl();
        builder.appendPath(BREEDS)
                .appendQueryParameter(KEY, MY_KEY)
                .appendQueryParameter(ANIMAL, species)
                .appendQueryParameter(FORMAT, JSON);
        fetchJSON(BREEDS_ID);
    }

    public void getPet(String petId){
        if (petId == null){
            Log.i(TAG, "No pet was specified.");
            return;
        }
        createBaseUrl();
        builder.appendPath(SINGLE_PET)
                .appendQueryParameter(KEY, MY_KEY)
                .appendQueryParameter(ID, petId)
                .appendQueryParameter(FORMAT, JSON);
        fetchJSON(SINGLE_PET_ID);
    }

    public void getRandomPet(String species, String breed, String sex, String location){
        if (location == null){
            Log.i(TAG, "No location was given to find pets.");
            return;
        }
        createBaseUrl();
        builder.appendPath(RANDOM_PET)
                .appendQueryParameter(KEY, MY_KEY);

        if (species != null){
            builder.appendQueryParameter(ANIMAL, species);
            if (breed != null){
                builder.appendQueryParameter(BREED, breed);
            }
        }

        if (sex != null){
            builder.appendQueryParameter(SEX, sex);
        }

        builder.appendQueryParameter(LOCATION, location)
                .appendQueryParameter(OUTPUT, FULL)
                .appendQueryParameter(FORMAT, JSON);
        fetchJSON(RANDOM_PET_ID);
    }

    public void findPets(String species, String breed, String sex, String location, String age){
        if (location == null){
            Log.i(TAG, "No location was given to find pets.");
            return;
        }
        createBaseUrl();
        builder.appendPath(FIND_PETS)
                .appendQueryParameter(KEY, MY_KEY);

        if (species != null){
            builder.appendQueryParameter(ANIMAL, species);
        }

        if (breed != null){
            builder.appendQueryParameter(BREED, breed);
        }

        if (sex != null){
            builder.appendQueryParameter(SEX, sex);
        }

        builder.appendQueryParameter(LOCATION, location);

        if (age != null){
            builder.appendQueryParameter(AGE, age);
        }

        builder.appendQueryParameter(OUTPUT, FULL)
                .appendQueryParameter(FORMAT, JSON);
        fetchJSON(FIND_PETS_ID);
    }

    public void getShelter(String shelterId){
        if (shelterId == null){
            Log.i(TAG, "No shelter was specified.");
            return;
        }
        createBaseUrl();
        builder.appendPath(SINGLE_SHELTER)
                .appendQueryParameter(KEY, MY_KEY)
                .appendQueryParameter(ID, shelterId)
                .appendQueryParameter(FORMAT, JSON);
        fetchJSON(SINGLE_SHELTER_ID);
    }

    public void findShelters(String location){
        if (location == null){
            Log.i(TAG, "No location was given to find shelters.");
            return;
        }
        createBaseUrl();
        builder.appendPath(FIND_SHELTERS)
                .appendQueryParameter(KEY, MY_KEY)
                .appendQueryParameter(LOCATION, location)
                .appendQueryParameter(FORMAT, JSON);
        fetchJSON(FIND_SHELTERS_ID);
    }

    public void getPetsFromShelter(String shelterId){
        if (shelterId == null){
            Log.i(TAG, "No id for the shelter was specified.");
            return;
        }
        createBaseUrl();
        builder.appendPath(SHELTER_PETS)
                .appendQueryParameter(KEY, MY_KEY)
                .appendQueryParameter(ID, shelterId)
                .appendQueryParameter(OUTPUT, FULL)
                .appendQueryParameter(FORMAT, JSON);
        fetchJSON(SHELTER_PETS_ID);
    }

    public void getSheltersWithBreed(String species, String breed){
        if (species == null || breed == null){
            Log.i(TAG, "The type of animal or breed of animal were not specified.");
            return;
        }
        createBaseUrl();
        builder.appendPath(BREED_SHELTERS)
                .appendQueryParameter(KEY, MY_KEY)
                .appendQueryParameter(ANIMAL, species)
                .appendQueryParameter(BREED, breed)
                .appendQueryParameter(FORMAT, JSON);
        fetchJSON(BREED_SHELTERS_ID);
    }

    private void fetchJSON(int functionID){
        final String url = builder.build().toString();
        Request request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Request", "Responded" + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Request", "Error " + error.getMessage() + " " + url);
            }
        });

        Net.getInstance().addToRequestQueue(request, MainActivity.AppName);
    }

    private void createBaseUrl(){
        builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY);
    }
}
