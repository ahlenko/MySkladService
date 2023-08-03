package com.example.myskladservice.processing.shpreference;

import android.content.Context;
import android.content.SharedPreferences;

public class AppWorkData {
    private Context context;
    private int enter_type;
    // 0 - введення паролю;
    // 1 - біометрія;
    // 2 - вхід без паролю

    private boolean notify;
    // Сповіщення on/off
    private boolean user_admin;
    // Розширений доступ on/off
    private boolean first_login;
    // Перший вхід on/off

    private String lang;
    // Мова: ua - укр.; an - англ.;
    private String user_login;
    // Логін входу користувача;
    private String user_pass;
    // Пароль входу користувача;
    private String company;
    // Ідентифікатор компанії

    public AppWorkData(Context context) {
        this.context = context;
        WriteData();
    }

    public int getEnterType() {return enter_type;}
    public boolean getStateClear(){return first_login;}
    public boolean getNotify() {return notify;}
    public boolean getUserType() {return user_admin;}
    public String getLang() {return lang;}
    public String getUserLogin() {return user_login;}
    public String getUserPass() {return user_pass;}
    public String getCompany() {return company;}

    public void ChangeEnterType(int type){this.enter_type = type;}

    public void ChangeNotify(boolean notify){
        this.notify = notify;
    }

    public void ChangeUserType(boolean user){
        this.user_admin = user;
    }

    public void ChangeFirstEnter(boolean enter){
        this.first_login = enter;
    }

    public void ChangeLanguage(String lang){this.lang = lang;}

    public void ChangeLogin(String login){
        this.user_login = login;
    }

    public void ChangePassword(String pass){
        this.user_pass = pass;

    }

    public void ChangeCompany(String email){
        this.company = email;
    }

    public void FirstEnter(boolean usertype, String email, String login, String pass){
        ChangeUserType(usertype);
        ChangeFirstEnter(false);
        ChangeCompany(email);
        ChangeLogin(login);
        ChangePassword(pass);
        SaveData();
    }

    public void Enter(boolean usertype, String login, String pass){
        ChangeUserType(usertype);
        ChangeLogin(login);
        ChangePassword(pass);
        SaveData();
    }

    public void RegUser(String login, String pass){
        ChangeFirstEnter(false);
        ChangeLogin(login);
        ChangePassword(pass);
        SaveData();
    }

    public void SaveData(){
        SharedPreferences prefs = context.getSharedPreferences("DatSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("enter_type", enter_type);
        editor.putBoolean("notify",notify);
        editor.putBoolean("user_admin", user_admin);
        editor.putBoolean("first_login", first_login);
        editor.putString("lang", lang);
        editor.putString("user_login", user_login);
        editor.putString("user_pass", user_pass);
        editor.putString("company", company);
        editor.apply();
    }

    public void WriteData(){
        SharedPreferences prefs = context.getSharedPreferences("DatSettings", Context.MODE_PRIVATE);
        enter_type = prefs.getInt("enter_type", 0);
        notify = prefs.getBoolean("notify", true);
        user_admin = prefs.getBoolean("user_admin", true);
        first_login = prefs.getBoolean("first_login", true);
        lang = prefs.getString("lang", "ua");
        user_login = prefs.getString("user_login", "");
        user_pass = prefs.getString("user_pass", "");
        company = prefs.getString("company", "");
    }

    public void ClearData(){
        SharedPreferences prefs = context.getSharedPreferences("DatSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("enter_type", 0);
        editor.putBoolean("notify",true);
        editor.putBoolean("user_admin", true);
        editor.putBoolean("first_login", true);
        editor.putString("lang", "ua");
        editor.putString("user_login", "");
        editor.putString("user_pass", "");
        editor.putString("company", "");
        editor.apply();
    }
}

