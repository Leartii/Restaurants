package com.example.restaurants.Model;

public class Menu_Items {
    private String price, name , image, parent_key;

    public Menu_Items() {
    }

    public Menu_Items(String price, String name, String image, String parent_key) {
        this.price = price;
        this.name = name;
        this.image = image;
        this.parent_key = parent_key;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getParent_key() {
        return parent_key;
    }

    public void setParent_key(String parent_key) {
        this.parent_key = parent_key;
    }
}
