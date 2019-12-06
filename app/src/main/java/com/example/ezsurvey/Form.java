package com.example.ezsurvey;

import java.util.ArrayList;

public class Form {
    private String name;
    private ArrayList<Question> questions;
    private long noOfResponses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public long getNoOfResponses() {
        return noOfResponses;
    }

    public void setNoOfResponses(long noOfResponses) {
        this.noOfResponses = noOfResponses;
    }

    public Form(String name, ArrayList<Question> questions, long noOfResponses) {
        this.name = name;
        this.questions = questions;
        this.noOfResponses = noOfResponses;
    }

    public Form() {
        name = "";
        questions = new ArrayList<>();
        noOfResponses = 0;
    }
}
