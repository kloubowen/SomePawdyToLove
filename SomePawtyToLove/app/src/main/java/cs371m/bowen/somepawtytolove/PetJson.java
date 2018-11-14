package cs371m.bowen.somepawtytolove;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PetJson {
    public interface IPetJson{
        void fetchPet(Pet pet);
        void fetchPetList(ArrayList<Pet> pets);
        void fetchBreedList(ArrayList<String> breeds);
        void fetchShelter(Object shelter);
        void fetchShelterList(Object shelters);
    }

    public static Pet jsonToPet(JSONObject jO){
        Log.d("JSON", jO.toString() );
        try {
            JSONObject finder = jO.getJSONObject("petfinder");
                JSONObject petObj = finder.getJSONObject("pet");
                String status = petObj.getJSONObject("status").getString("$t");
                Log.d("status", status);
                if(!status.equals("A"))
                    return null;
                String age = petObj.getJSONObject("age").getString("$t");
                String name = petObj.getJSONObject("name").getString("$t");
                Log.d("age", age);
                Log.d("name", name);
                //todo: handle breeds as list
                JSONObject breeds = petObj.getJSONObject("breeds");
                JSONArray breedArray = breeds.getJSONArray("breed");
                String breed = "";
                for(int x = 0; x < breedArray.length(); x++) {
                    JSONObject b = breedArray.getJSONObject(x);
                    breed+=b.getString("$t");
                    if(x != breedArray.length()-1)
                        breed+=", ";
                }
                JSONObject contact = petObj.getJSONObject("contact");
                String zip = contact.getJSONObject("zip").getString("$t");
                String city = contact.getJSONObject("city").getString("$t");
                String state = contact.getJSONObject("state").getString("$t");
                Pet mPet = new Pet(name, breed, zip, age);
                mPet.setCityState(city, state);

                mPet.setDescription(petObj.getJSONObject("description").getString("$t"));
                JSONArray photos = petObj.getJSONObject("media").getJSONObject("photos").getJSONArray("photo");
                for(int x = 0; x < photos.length(); x++) {
                    JSONObject photo = photos.getJSONObject(x);
                    if(photo.getString("@size").equals("x")||photo.getString("@size").equals("pn")) {
                        String uri = photo.getString("$t");
                        mPet.addPic(uri);
                    }
                }
                Log.i("pet", "parsed new pet");
            return mPet;
        } catch (JSONException error) {
            Log.d("Error", "JSON parsing");
        }



        // This is NOT what you should return
        return null;
    }

    public static ArrayList<Pet> jsonToPetList(JSONObject jo){
        return null;
    }

    // Create shelter class and switch Object to Shelter
    public static Shelter jsonToShelter(JSONObject jo){
        return null;
    }

    public static ArrayList<Object> jsonToShelterList(JSONObject jo){
        return null;
    }

    public static ArrayList<String> jsonToBreed(JSONObject jO){
        ArrayList<String> breedList = new ArrayList<>();
        try{
            JSONObject breeds = jO.getJSONObject("petfinder").getJSONObject("breeds");
            JSONArray breedArray = breeds.getJSONArray("breed");
            for(int x = 0; x<breedArray.length(); x++){
                String breed = breedArray.getJSONObject(x).getString("$t");
                breedList.add(breed);
            }
        } catch (JSONException error) {
            Log.d("Error", "JSON parsing");
        }
        return breedList;
    }
}
