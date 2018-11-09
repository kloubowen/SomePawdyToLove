package cs371m.bowen.somepawtytolove;

import java.util.ArrayList;

public class Pet {
    private String status;
    private String name;
    private String animal;
    private String breed;
    private String zip;
    private String city;
    private String state;
    private String age;
    private String sex;
    private String description;
    private String email;
    private String id;
    private String shelterid;
    private ArrayList<String> picUrls;

    public Pet(String name, String breed, String zip, String age){
        this.name = name;
        this.breed = breed;
        this.zip = zip;
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
        return city + ", " + state + " " + zip;
    }

    public String getPic() {
        if(picUrls.isEmpty())
            return null;
        return picUrls.get(0);
    }
}
