package com.example.myskladservice.processing.shpreference;

import android.content.Context;
import android.content.SharedPreferences;

public class AppCreateOr {
    private Context context;
    private boolean isCreate;

    public AppCreateOr(Context context) {
        this.context = context;
        WriteData();
    }

    public boolean GetCreate(){return isCreate;}

    public void ChangeCreate(boolean dascreate){
        this.isCreate = dascreate;
        SaveData();
    }

    public void WriteData(){
        SharedPreferences prefs = context.getSharedPreferences("Creation", Context.MODE_PRIVATE);
        isCreate = prefs.getBoolean("dascreator", true);
    }

    private void SaveData(){
        SharedPreferences prefs = context.getSharedPreferences("Creation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("dascreator", isCreate);
        editor.apply();
    }
}
