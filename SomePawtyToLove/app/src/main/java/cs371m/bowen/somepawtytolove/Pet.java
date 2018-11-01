package cs371m.bowen.somepawtytolove;

import java.util.ArrayList;

public class Pet {
    private String name;
    private String breed;
    private String location;
    private String age;
    private ArrayList<String> picUrls;

    public Pet(String name, String breed, String location, String age){
        this.name = name;
        this.breed = breed;
        this.location = location;
        picUrls = new ArrayList<>();
    }

    public String getAge(){
        return age;
    }

    public void addPic(String uri){
        picUrls.add(uri);
    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public String getLocation() {
        return location;
    }

    public String getPic() {
        if(picUrls.isEmpty())
            return null;
        return picUrls.get(0);
    }
}
