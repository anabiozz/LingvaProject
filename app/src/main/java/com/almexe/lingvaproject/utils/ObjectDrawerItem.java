package com.almexe.lingvaproject.utils;

public class ObjectDrawerItem {

    public int icon;
    public String name;

    // Constructor.
    public ObjectDrawerItem(int icon, String name) {

        this.icon = icon;
        this.name = name;
    }

    public ObjectDrawerItem(String name) {
        this.name = name;
    }
}