package com.example.model;

import java.util.ArrayList;

/***
 * Created by hungdn58 on 08/11/2016.
 */
public class Mistake {
    private String message;
    private String result;
    private ArrayList<Suggest> mistakes;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ArrayList<Suggest> getMistakes() {
        return mistakes;
    }

    public void setMistakes(ArrayList<Suggest> mistakes) {
        this.mistakes = mistakes;
    }
}
