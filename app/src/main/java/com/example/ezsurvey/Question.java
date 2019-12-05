package com.example.ezsurvey;

public class Question {
    private String name;
    private String type;
    private String reply;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Question() {
        name = "";
        type = "common";
        reply = "";
    }

    public Question(String name, String type, String reply, String date) {
        this.name = name;
        this.type = type;
        this.reply = reply;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
