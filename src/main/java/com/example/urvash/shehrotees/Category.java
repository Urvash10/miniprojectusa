package com.example.urvash.shehrotees;

import com.google.firebase.database.Query;

/**
 * Created by urvash on 22/03/18.
 */

public class Category {
    private String Name;
    private String Image;

    public Category() {

    }

    public Category(String name, String image) {
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

}
