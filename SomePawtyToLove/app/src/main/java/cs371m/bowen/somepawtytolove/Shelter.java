package cs371m.bowen.somepawtytolove;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class Shelter {
    private String address;
    private String id;
    private String name;
    private LatLng mapLocation;

    public Shelter(String name, String address, String id) {
        this.name = name;
        this.address = address;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public LatLng getMapLocation() {
        return mapLocation;
    }

    public void setLatLng(String lat, String lng) {
        float la = Float.parseFloat(lat);
        float lo = Float.parseFloat(lng);
        mapLocation = new LatLng(la, lo);
    }

    public String getAddress(){
        return address;
    }
}
