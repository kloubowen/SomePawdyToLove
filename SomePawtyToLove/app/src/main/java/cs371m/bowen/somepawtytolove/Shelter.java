package cs371m.bowen.somepawtytolove;

import android.location.Location;

public class Shelter {
    private String address;
    private String id;
    private String name;
    private Location mapLocation;

    public Shelter(String name, String address, String id) {
        this.name = name;
        this.address = address;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public Location getMapLocation() {
        return mapLocation;
    }
}
