package com.n17r_fizmat.kzqrs;

import android.graphics.Bitmap;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by Alisher on 7/7/2016.
 */

public class Opinion {
    private String firstWord, secondWord, thirdWord, date;
    private User sender, receiver;

    public Opinion(User sender, User receiver, String f, String s, String t, String d) {
        this.sender = sender;
        this.receiver = receiver;
        this.firstWord = f;
        this.secondWord = s;
        this.thirdWord = t;
        this.date = d;
    }


    public void setFirstWord(String s) {
        this.firstWord = s;
    }

    public String getFirstWord() {
        return this.firstWord;
    }

    public void setSecondWord(String s) {
        this.secondWord = s;
    }

    public String getSecondWord() {
        return this.secondWord;
    }

    public void setThirdWord(String s) {
        this.thirdWord = s;
    }

    public String getThirdWord() {
        return this.thirdWord;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
