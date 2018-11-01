package cs371m.bowen.somepawtytolove;

import java.util.ArrayList;

public class Pet {
    private String name;
    private String breed;
    private String location;
    private ArrayList<String> picUrls;

    public Pet(String name, String breed, String location){
        this.name = name;
        this.breed = breed;
        this.location = location;
    }
}
