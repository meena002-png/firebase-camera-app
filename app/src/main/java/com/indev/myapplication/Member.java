package com.indev.myapplication;

public class Member {

    public String name;
    public String link;
    public String date;

    public Member() {}

    public Member(String name, String link, String date) {
        this.name = name;
        this.link = link;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
