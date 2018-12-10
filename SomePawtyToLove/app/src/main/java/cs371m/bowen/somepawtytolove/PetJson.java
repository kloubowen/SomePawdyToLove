package cs371m.bowen.somepawtytolove;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class PetJson {
    public interface IPetJson{
        void fetchPet(Pet pet);
        void fetchPetList(ArrayList<Pet> pets);
        void fetchBreedList(ArrayList<String> breeds);
        void fetchShelter(Shelter shelter);
        void fetchShelterList(ArrayList<Shelter> shelters);
    }

    public static Pet jsonToPet(JSONObject jO){
        Log.d("JSON", jO.toString() );
        try {
            JSONObject finder = jO.getJSONObject("petfinder");
            JSONObject petObj = finder.getJSONObject("pet");
            return parsePet(petObj);
        } catch (JSONException error) {
            Log.d("Error", "JSON parsing pet record");
        }

        // This is NOT what you should return
        return null;
    }

    public static ArrayList<Pet> jsonToPetList(JSONObject jo){
        Log.d("list json", jo.toString());
        ArrayList<Pet> petList = new ArrayList<>();
        try{
            JSONObject petfinder = jo.getJSONObject("petfinder");
            JSONArray pets = petfinder.getJSONObject("pets").getJSONArray("pet");
            for(int x = 0; x<pets.length(); x++) {
                Pet p = parsePet(pets.getJSONObject(x));
                if(p!=null) {
                    Log.d("pets", p.getName());
                    petList.add(p);
                }
            }
            MainActivity.lastOffset = petfinder.getJSONObject("lastOffset").getString("$t");
            Log.i("Petlist", "Pets Parsed: " + petList.size());
            return petList;
        } catch (JSONException error) {
            Log.d("Error", "JSON parsing pet list");
        }
        return null;
    }

    private static Pet parsePet(JSONObject petObj){
        try {
            String status = petObj.getJSONObject("status").getString("$t");
            Log.d("status", status);
            if(!status.equals("A"))
                return null;
            String age = petObj.getJSONObject("age").getString("$t");
            String name = petObj.getJSONObject("name").getString("$t");
            String id = petObj.getJSONObject("id").getString("$t");
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
            String address = contact.getJSONObject("address1").getString("$t");
            String city = contact.getJSONObject("city").getString("$t");
            String state = contact.getJSONObject("state").getString("$t");
            String email = contact.getJSONObject("email").getString("$t");
            Pet mPet = new Pet(name, breed, zip, age);
            mPet.setCityState(city, state);
            mPet.setEmail(email);
            mPet.setID(id);
            mPet.setStreet(address);

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
            Log.d("Error", "JSON parsing pet record");
        }
        return null;
    }

    // Create shelter class and switch Object to Shelter
    public static Shelter jsonToShelter(JSONObject jO){
        Shelter shelter = null;
        try{
            String name = jO.getJSONObject("name").getString("$t");
            String street = jO.getJSONObject("address1").optString("$t");
            String city = jO.getJSONObject("city").getString("$t");
            String state = jO.getJSONObject("state").getString("$t");
            String zip = jO.getJSONObject("zip").getString("$t");
            String id = jO.getJSONObject("id").getString("$t");
            String address;
            if (street.isEmpty()){
                address = city + ", " + state + " " + zip;
            } else {
                address = street + ", " + city + ", " + state + " " + zip;
            }
            shelter = new Shelter(name, address, id);

            String lat = jO.getJSONObject("latitude").getString("$t");
            String lng = jO.getJSONObject("longitude").getString("$t");
            shelter.setLatLng(lat, lng);
        } catch (JSONException error) {
            Log.d("Error", "JSON parsing shelter" + error.toString());
        }
        return shelter;
    }

    public static ArrayList<Shelter> jsonToShelterList(JSONObject jO){
        Log.d("list json", jO.toString());
        ArrayList<Shelter> shelterList = new ArrayList<>();
        try{
            JSONArray shelters = jO.getJSONObject("petfinder").getJSONObject("shelters").getJSONArray("shelter");
            for(int x = 0; x<shelters.length(); x++) {
                Shelter s = jsonToShelter(shelters.getJSONObject(x));

                if(s!=null) {
                    Log.d("shelter", s.getName());
                    shelterList.add(s);
                }
            }
        } catch (JSONException error) {
            Log.d("Error", "JSON parsing shelter list");
        }
        return shelterList;
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
