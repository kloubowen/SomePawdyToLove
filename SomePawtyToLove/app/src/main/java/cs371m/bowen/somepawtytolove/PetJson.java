package cs371m.bowen.somepawtytolove;

import org.json.JSONObject;

public class PetJson {
    public interface IPetJson{
        void fetchSinglePet(Pet pet);
    }

    public static Pet jsonToPet(JSONObject jo){
        return null;
    }
}
