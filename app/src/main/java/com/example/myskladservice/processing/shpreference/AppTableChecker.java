package com.example.myskladservice.processing.shpreference;

import android.content.Context;
import android.content.SharedPreferences;

public class AppTableChecker {
    private Context context;
    private int check_number;
    private boolean dascreator;

    public AppTableChecker(Context context) {
        this.context = context;
        WriteData();
    }

    public int GetChecker(){return check_number;}
    public boolean GetPrivace(){return dascreator;}

    public void ChangeChecker(int check){
        this.check_number = check;
        SaveData();
    }

    public void ChangePrivace(boolean dascreator){
        this.dascreator = dascreator;
        SaveData();
    }

    public void WriteData(){
        SharedPreferences prefs = context.getSharedPreferences("TableChecker", Context.MODE_PRIVATE);
        check_number = prefs.getInt("check_number", 0);
        dascreator = prefs.getBoolean("dascreator", false);
    }

    private void SaveData(){
        SharedPreferences prefs = context.getSharedPreferences("TableChecker", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("check_number", check_number);
        editor.putBoolean("dascreator", dascreator);
        editor.apply();
    }
}
