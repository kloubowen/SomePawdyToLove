package cs371m.bowen.somepawtytolove;

import org.json.JSONObject;

import java.util.ArrayList;

public class PetJson {
    public interface IPetJson{
        void fetchPet(Pet pet);
        void fetchPetList(ArrayList<Pet> pets);
        void fetchBreedList(ArrayList<String> breeds);
        void fetchShelter(Object shelter);
        void fetchShelterList(Object shelters);
    }

    public static Pet jsonToPet(JSONObject jo){
        return null;
    }

    public static ArrayList<Pet> jsonToPetList(JSONObject jo){
        return null;
    }

    // Create shelter class and switch Object to Shelter
    public static Object jsonToShelter(JSONObject jo){
        return null;
    }

    public static ArrayList<Object> jsonToShelterList(JSONObject jo){
        return null;
    }

    public static ArrayList<String> jsonToBreed(JSONObject jo){
        return null;
    }
}
