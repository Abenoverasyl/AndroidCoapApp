package com.example.erasyl.coap;

/**
 * Created by Erasyl on 21.04.2017.
 */

public class NotificationForUser {

    private String id;
    private String title;
    private String message;
    private String url;

    public NotificationForUser() {
    }

    public NotificationForUser(String id, String title, String message, String url) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
