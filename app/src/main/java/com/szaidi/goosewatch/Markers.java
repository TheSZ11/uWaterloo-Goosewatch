package com.szaidi.goosewatch;

public class Markers {
    Double longitude;
    Double latitude;
    String location;

    public Markers(){
        this.longitude = 0.0000;
        this.latitude = 0.0000;
        this.location = "";
    }

    public Markers(Double longitude, Double latitude, String location){
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
    }

    public Double getLongitude(){
        return longitude;
    }

    public Double getLatitude(){
        return latitude;
    }

    public String getLocation(){
        return location;
    }
}
