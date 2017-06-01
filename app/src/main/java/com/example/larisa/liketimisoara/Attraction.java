package com.example.larisa.liketimisoara;

import java.io.Serializable;

/**
 * Model for attraction.
 */

public class Attraction implements Serializable {

    private int id;
    private String name;
    private AttractionType type;
    private String info;
    private int imageResourceId;
    private boolean isTop10;
    private double longitude;
    private double latitude;

    public Attraction(){}

    public Attraction(int id, String name, AttractionType type) {
        this.id= id;
        this.name = name;
        this.type = type;
    }

    public Attraction(int id, String name, AttractionType type, String info,
                      int imageResourceId, boolean isTop10, double longitude,
                      double latitude) {

        this(id, name, type);
        this.info = info;
        this.imageResourceId = imageResourceId;
        this.isTop10 = isTop10;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttractionType getType() {
        return type;
    }

    public void setType(AttractionType type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public boolean isTop10() {
        return isTop10;
    }

    public void setTop10(boolean top10) {
        isTop10 = top10;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
