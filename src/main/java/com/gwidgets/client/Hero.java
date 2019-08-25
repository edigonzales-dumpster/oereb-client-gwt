package com.gwidgets.client;

import gwt.material.design.client.base.SearchObject;

public class Hero extends SearchObject {

    private String name;
    private String description;
    private int power;

    public Hero() {}

    public Hero(String imgProfile, String name, String description, int power) {
        //super(imgProfile, name);
        this.name = name;
        this.description = description;
        this.power = power;
        setKeyword(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
