package com.example.diegoalvarez.duoauthentication;

/**
 * Created by harmanthind on 4/2/18.
 */

public class UserInput {

    public String appName;
    public String userName;
    public String password;

    public UserInput() {

    }

    public UserInput(String appName, String userName, String password) {
        this.appName = appName;
        this.userName = userName;
        this.password = password;
    }
}
