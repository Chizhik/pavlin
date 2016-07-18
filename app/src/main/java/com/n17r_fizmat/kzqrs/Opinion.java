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
    private String username;
    private Bitmap profilePic;
    private String firstWord, secondWord, thirdWord;
    private ParseUser sender, receiver;

    public Opinion(String username, Bitmap bm, String f, String s, String t) {
        this.username = username;
        this.profilePic = bm;
        this.firstWord = f;
        this.secondWord = s;
        this.thirdWord = t;
    }

    public void setUsername(String um) {
        this.username = um;
    }

    public String getUsername() {
        return this.username;
    }

    public void setProfilePic(Bitmap bm) {
        this.profilePic = bm;
    }

    public Bitmap getProfilePic() {
        return this.profilePic;
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
}
