package com.example.erasyl.coap;

/**
 * Created by Erasyl on 17.03.2017.
 */

public class CoAPDetail {
    int id;
    String art;
    String narush;
    String fine1;
    String fine2;

    public CoAPDetail() {
    }

    public CoAPDetail(int id, String art, String narush, String fine1, String fine2) {
        this.id = id;
        this.art = art;
        this.narush = narush;
        this.fine1 = fine1;
        this.fine2 = fine2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getNarush() {
        return narush;
    }

    public void setNarush(String narush) {
        this.narush = narush;
    }

    public String getFine1() {
        return fine1;
    }

    public void setFine1(String fine1) {
        this.fine1 = fine1;
    }

    public String getFine2() {
        return fine2;
    }

    public void setFine2(String fine2) {
        this.fine2 = fine2;
    }
}
