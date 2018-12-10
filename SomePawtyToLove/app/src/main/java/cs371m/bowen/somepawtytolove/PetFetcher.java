package cs371m.bowen.somepawtytolove;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

@SuppressWarnings("FieldCanBeLocal")
public class PetFetcher {
    // Key
    private final String MY_KEY = "57b5bace4f4f4f114e806f0a13bb422a";

    // Api methods
    enum Methods{
        BREEDS_ID,
        SINGLE_PET_ID,
        RANDOM_PET_ID,
        FIND_PETS_ID,
        SINGLE_SHELTER_ID,
        FIND_SHELTERS_ID,
        SHELTER_PETS_ID,
        BREED_SHELTERS_ID
    }

    private final String BREEDS = "breed.list";
    private final String FIND_PETS = "pet.find";
    private final String FIND_SHELTERS = "shelter.find";

    // Url variables
    private Uri.Builder builder;
    private final String SCHEME = "https";
    private final String AUTHORITY = "api.petfinder.com";
    private final String KEY = "key";
    private final String FORMAT = "format";
    private final String JSON = "json";
    private final String ANIMAL = "animal";
    private final String BREED = "breed";
    private final String SEX = "sex";
    private final String LOCATION = "location";
    private final String OUTPUT = "output";
    private final String FULL = "full";
    private final String AGE = "age";
    private final String OFFSET = "offset";

    private final String TAG = "Fetch";

    public void getBreeds(String species, PetJson.IPetJson callback){
        if (species == null){
            Log.i(TAG, "No type of animal was given.");
            return;
        }
        createBaseUrl();
        builder.appendPath(BREEDS)
                .appendQueryParameter(KEY, MY_KEY)
                .appendQueryParameter(ANIMAL, species.toLowerCase())
                .appendQueryParameter(FORMAT, JSON);
        fetchJSON(Methods.BREEDS_ID, callback);
    }


    public void findPets(String species, String breed, String sex, String location, String age,
                         PetJson.IPetJson callback){
        if (location == null){
            Log.i(TAG, "Waiting for location to find pets.");
            return;
        }
        createBaseUrl();
        builder.appendPath(FIND_PETS)
                .appendQueryParameter(KEY, MY_KEY);

        if (MainActivity.lastOffset.equals("0")){
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
        } else{
            builder.appendQueryParameter(LOCATION, location);
            builder.appendQueryParameter(OFFSET, MainActivity.lastOffset);
        }



        builder.appendQueryParameter(OUTPUT, FULL)
                .appendQueryParameter(FORMAT, JSON);
        fetchJSON(Methods.FIND_PETS_ID, callback);
    }

    public void findShelters(String location, PetJson.IPetJson callback){
        if (location == null){
            Log.i(TAG, "No location was given to find shelters.");
            return;
        }
        createBaseUrl();
        builder.appendPath(FIND_SHELTERS)
                .appendQueryParameter(KEY, MY_KEY)
                .appendQueryParameter(LOCATION, location)
                .appendQueryParameter(FORMAT, JSON)
                .appendQueryParameter("count", "5");
        fetchJSON(Methods.FIND_SHELTERS_ID, callback);
    }

    private void fetchJSON(final Methods functionID, final PetJson.IPetJson callback){
        final String url = builder.build().toString();
        Log.d("uri", url);
        Request request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Request", "Responded ");
                switch (functionID){
                    case BREEDS_ID:
                        callback.fetchBreedList(PetJson.jsonToBreed(response));break;
                    case FIND_PETS_ID:
                        callback.fetchPetList(PetJson.jsonToPetList(response));break;
                    case FIND_SHELTERS_ID:
                        callback.fetchShelterList(PetJson.jsonToShelterList(response));break;
                }
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
