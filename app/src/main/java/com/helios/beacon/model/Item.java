package com.helios.beacon.model;

/**
 * Created by nhantran on 11/12/14.
 */
public class Item {

    private int id;
    private String name;
    private double price;
    private String logoUrl;
    private String description;
    private String status;

    public Item(){};

    public Item(int id, String name, Double price, String logoUrl, String description, String status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.logoUrl = logoUrl;
        this.description = description;
        this.status = status;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
