package com.company.roomie.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {

    public static String prefName = "USER_SESSION";
    public static String sessionName = "IS_LOGGED_IN";
    public static String userEmail = "USER_EMAIL";
    public static String userPassword = "USER_PASSWORD";
    public static String userName = "USER_NAME";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public UserSession(Context context) {
        this.context = context;
    }

    public void createUserSession(){
        preferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean(sessionName,true);
        editor.apply();
    }

    public void deleteUserSession(){
        preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean(sessionName,false);
        editor.apply();
    }

    public boolean checkUserSession(){
        preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return  preferences.getBoolean(sessionName,false);
    }

    public void setUserEmail(String email){
        preferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(userEmail,email);
        editor.apply();
    }

    public String getUserEmail(){
        preferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        return preferences.getString(userEmail,null);
    }
    public void setUserPassword(String password){
        preferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(userPassword,password);
        editor.apply();
    }

    public String getUserPassword(){
        preferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        return preferences.getString(userPassword,null);
    }

    public void setUserName(String name){
        preferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(getUserEmail(),name);
        editor.apply();
    }

    public String getUserName(){
        preferences = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        return preferences.getString(getUserEmail(),null);
    }

}
