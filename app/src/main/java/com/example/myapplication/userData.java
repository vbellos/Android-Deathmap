package com.example.myapplication;

import android.app.Application;

import java.util.UUID;

public class userData extends Application {

    public  static String userID;

    public static String getUserID() {
        return userID;
    }

    public  static void setUserID(String userID) {
        userData.userID = userID;
    }

    public static String getSpUnit() {
        return spUnit;
    }

    public static void setSpUnit(String spUnit) {
        userData.spUnit = spUnit;
    }

    public static String getAccUnit() {
        return accUnit;
    }

    public static  void setAccUnit(String accUnit) {
        userData.accUnit = accUnit;
    }

    public static String spUnit;
    public  static String accUnit;
}
