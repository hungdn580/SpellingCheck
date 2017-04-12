package com.example.model;

import java.io.UnsupportedEncodingException;

/***
 * Created by hungdn58 on 08/11/2016.
 */
public class Suggest {
    private String error;
    private String suggest;
    private int position;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSuggest() {
        String newString = "";
        try {
            newString = new String(suggest.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return newString;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }
}
