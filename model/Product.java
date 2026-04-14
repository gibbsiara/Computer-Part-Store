package com.example.pcbuilder.model;

public class Product {
    private int id;
    private String name;
    private double price;
    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String toString() {
        return id + " | " + name + " | " + price + "PLN";
    }
}
