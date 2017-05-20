package com.example.larisa.liketimisoara;

/**
 * Created by Larisa on 2/20/2017.
 */

public class DataModel {
    int nameId;
    int id_;
    int image;
    AttractionType type;

    public DataModel(int nameId, int id_, int image, AttractionType type) {
        this.nameId = nameId;
        this.id_ = id_;
        this.image = image;
        this.type = type;
    }

    public int getName() {
        return nameId;
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
