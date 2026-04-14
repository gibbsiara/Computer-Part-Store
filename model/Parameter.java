package com.example.pcbuilder.model;

public class Parameter {
    private String name;

    public Parameter(String name) {
        this.name = name;
    }

    public String getName() {return name;}

    @Override
    public String toString() {
        return getName();
    }
}
