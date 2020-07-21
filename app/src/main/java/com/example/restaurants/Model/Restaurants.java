package com.example.restaurants.Model;

public class Restaurants {
    private String name, imageLink, freeseats, distance;
    private Double latitude, longitude;

    public Restaurants() {
    }

    public Restaurants(String name, String imageLink, String freeseats, String distance, Double latitude, Double longitude) {
        this.name = name;
        this.imageLink = imageLink;
        this.freeseats = freeseats;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getFreeseats() {
        return freeseats;
    }

    public void setFreeseats(String freeseats) {
        this.freeseats = freeseats;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
