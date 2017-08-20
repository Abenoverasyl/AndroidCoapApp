package com.example.erasyl.coap;

/**
 * Created by Erasyl on 28.03.2017.
 */

public class Car {
    private String id;
    private String image;
    private String date;
    private String gn;
    private String sum;

    public Car() {
    }

    public Car(String id, String image, String date, String gn, String sum) {
        this.id = id;
        this.image = image;
        this.date = date;
        this.gn = gn;
        this.sum = sum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGn() {
        return gn;
    }

    public void setGn(String gn) {
        this.gn = gn;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
}
