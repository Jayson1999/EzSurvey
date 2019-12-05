package com.example.ezsurvey;

import java.util.ArrayList;

public class Form {
    private String name;
    private ArrayList<Question> questions;

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

    public Form(String name, ArrayList<Question> questions) {
        this.name = name;
        this.questions = questions;
    }

    public Form() {
        name = "";
        questions = new ArrayList<>();
    }
}
