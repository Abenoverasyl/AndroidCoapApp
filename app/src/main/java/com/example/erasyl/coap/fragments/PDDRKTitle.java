package com.example.erasyl.coap.fragments;

/**
 * Created by Erasyl on 16.03.2017.
 */

public class PDDRKTitle {
    int id;
    String title;

    public PDDRKTitle() {
    }

    public PDDRKTitle(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
