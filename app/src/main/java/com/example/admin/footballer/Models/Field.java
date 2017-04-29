package com.example.admin.footballer.Models;

import com.google.firebase.database.Exclude;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Field {
    public String cityName;
    public String fieldName;
    public String fieldOwner;
    public String locationName;
    public double latitude;
    public double longitude;
    public List<String> photoUrls;
    public int price;
    public Field() {

    }

    public Field(String fieldName, String cityName, String locationName, String fieldOwner,
                 double lat, double lon, List<String> photoUrls, int price) {
        this.fieldName = fieldName;
        this.cityName = cityName;
        this.locationName = locationName;
        this.fieldOwner = fieldOwner;
        this.latitude = lat;
        this.longitude = lon;
        this.photoUrls = photoUrls;
        if(photoUrls == null)
            this.photoUrls = new ArrayList<String>();
        this.price = price;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fieldName", fieldName);
        result.put("cityName", cityName);
        result.put("locationName", locationName);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("fieldOwner", fieldOwner);
        result.put("photoUrls", photoUrls);
        result.put("price", price);

        return result;
    }
}
