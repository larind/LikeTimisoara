package com.example.larisa.liketimisoara;

/**
 * Created by Larisa on 2/20/2017.
 */

public class DataModel {
    String name;
    int id_;
    int image;
    AttractionType type;

    public DataModel(String name, int id_, int image, AttractionType type) {
        this.name = name;
        this.id_ = id_;
        this.image = image;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public int getId() {
        return id_;
    }

    public AttractionType getType() {
        return type;
    }
}
