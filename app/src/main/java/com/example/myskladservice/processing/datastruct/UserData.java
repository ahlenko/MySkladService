package com.example.myskladservice.processing.datastruct;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserData {
    public static String surname;
    public static String name;
    public static String lastname;
    public static String workpost;
    public static String workplace;
    public static String phnumber;
    public static String login;
    public static String password;
    public static boolean fullaccess;

    public static String getUserName(ResultSet resultSet) throws SQLException {
        surname = resultSet.getString("surname");
        name = resultSet.getString("name");
        lastname = resultSet.getString("lastname");
        if (surname != null && name != null && lastname != null)
            return surname + " " + name.charAt(0) + ". " + lastname.charAt(0) + ".";
        else return "_____________";
    }
}
