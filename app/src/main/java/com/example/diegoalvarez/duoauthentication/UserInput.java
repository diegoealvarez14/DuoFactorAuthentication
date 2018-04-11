package com.example.diegoalvarez.duoauthentication;

/**
 * Created by harmanthind on 4/2/18.
 */

public class UserInput {

    public String appName;
    public String userName;
    public String password;
    public String id;
    public UserInput() {

    }

    public UserInput(String id, String appName, String userName, String password) {
        this.id = id;
        this.appName = appName;
        this.userName = userName;
        this.password = password;
    }

    public String getId() {
        return id;
    }
    public String getAppName()
    {
        return appName;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getPassword()
    {
        return password;
    }
}
