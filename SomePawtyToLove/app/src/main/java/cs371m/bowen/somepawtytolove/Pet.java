package cs371m.bowen.somepawtytolove;

import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class Pet {
    private String name;
    private String breed;
    private String address;
    private String zip;
    private String city;
    private String state;
    private String age;
    private String description;
    private String email;
    PetID id;
    private ArrayList<String> picUrls;
    private int picIndex;

    public class PetID{
        String id;
    }

    public Pet(String name, String breed, String zip, String age){
        this.name = name;
        this.breed = breed;
        this.zip = zip;
        picUrls = new ArrayList<>();
        this.age = age;
        picIndex = -2;
        id = new PetID();
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

    public String getAdd() {return address;}

    public String getLocation() {
        return city + ", " + state + " " + zip;
    }

    public void setID(String id) {this.id.id=id;}

    public void setStreet(String street) {this.address = street;}

    public String getAddress() {return address+ " "+getLocation();}

    public String getPic() {
        if(picUrls.isEmpty())
            return null;
        picIndex+=2;
        if (picIndex >= picUrls.size()){
            picIndex = 0;
        }
        return picUrls.get(picIndex);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setCityState(String city, String state) {
        this.city = city;
        this.state = state;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }
}
