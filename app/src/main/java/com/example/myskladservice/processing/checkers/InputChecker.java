package com.example.myskladservice.processing.checkers;

import android.util.Patterns;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myskladservice.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.regex.Pattern;

public class InputChecker {
    public static boolean isNotEmail (String str, TextView text, int max, AppCompatActivity a){
        if (str.isEmpty() || str.length() > max){
            text.setTextColor(a.getResources().getColor(R.color.red_note));
            return true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(str).matches()){
            text.setTextColor(a.getResources().getColor(R.color.red_note));
            return true;
        } return false;
    }

    public static boolean isNotCSize (String str, TextView text, int max, AppCompatActivity a){
        if (str.isEmpty() || str.length() > max){
            text.setTextColor(a.getResources().getColor(R.color.red_note));
            return true;
        } return false;
    }

    public static boolean isNotMaxSize (String str, TextView text, int max, AppCompatActivity a){
        if (str.length() > max){
            text.setTextColor(a.getResources().getColor(R.color.red_note));
            return true;
        } return false;
    }

    public static boolean isNotTime(String str, TextView text, AppCompatActivity a) {
        if (str.length() == 5){
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            format.setLenient(false);
            try {
                format.parse(str);
            } catch (ParseException e) {
                text.setTextColor(a.getResources().getColor(R.color.red_note));
                return true;
            }
        } else {
            SimpleDateFormat format = new SimpleDateFormat("H:mm");
            format.setLenient(false);
            try {
                format.parse(str);
            } catch (ParseException e) {
                text.setTextColor(a.getResources().getColor(R.color.red_note));
                return true;
            }
        } return false;
    }

    public static boolean isNotPhone (String str, TextView text, AppCompatActivity a){
        Pattern pattern = Pattern.compile("^0\\d{2}-\\d{3}-\\d{4}$");
        if (str.isEmpty() || !pattern.matcher(str).matches()) {
            text.setTextColor(a.getResources().getColor(R.color.red_note));
            return true;
        } return false;
    }

    public static boolean isNotPassword (String str, TextView text, AppCompatActivity a){
        if (str.isEmpty() || str.length() > 20 || !str.matches("^[a-zA-Z0-9]+$")) {
            text.setTextColor(a.getResources().getColor(R.color.red_note));
            return true;
        } return false;
    }

    public static boolean isNotInt (String str, TextView text, AppCompatActivity a){
        try {
            Integer.parseInt(str);
            return false;
        } catch (NumberFormatException e) {
            text.setTextColor(a.getResources().getColor(R.color.red_note));
            return true;
        }
    }

    public static boolean isNotFloat (String str, TextView text, AppCompatActivity a){
        if (str.isEmpty()) {
            text.setTextColor(a.getResources().getColor(R.color.red_note));
            return true;
        } else if (!str.contains(".")) str += ".0";

        try {
            Float.parseFloat(str);
            return false;
        } catch (NumberFormatException e) {
            text.setTextColor(a.getResources().getColor(R.color.red_note));
            return true;
        }
    }
}
