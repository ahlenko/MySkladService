package com.example.myskladservice.processing.datastruct;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.myskladservice.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserData {
    public static boolean fullaccess; public static String workplace;
    public static String lastname; public static String workpost;
    public static String phnumber; public static String password;
    public static String surname; public static String login;

    public static String name;

    public static String getUserName(ResultSet resultSet) throws SQLException {
        lastname = resultSet.getString("lastname");
        surname = resultSet.getString("surname");
        name = resultSet.getString("name");
        if (surname != null && name != null && lastname != null)
            return surname + " " + name.charAt(0) + ". " + lastname.charAt(0) + ".";
        else return "_____________";
    }

    public static void SetData (AppCompatActivity activity, ArrayList<CheckBox> boxes, boolean acc){
        EditText inputworkstate = activity.findViewById(R.id.inputworkstate);
        EditText inputworkplace = activity.findViewById(R.id.inputworkplace);
        TextView inputpassword = activity.findViewById(R.id.inputpassword4);
        EditText inputlastname = activity.findViewById(R.id.inputlastname);
        EditText inputsurname = activity.findViewById(R.id.inputsurname);
        CheckBox useracess; if (acc) {useracess = activity.findViewById(
             R.id.select_fullacess); useracess.setChecked(fullaccess);}
        TextView inputlogin = activity.findViewById(R.id.inputlogin4);
        EditText inputname = activity.findViewById(R.id.inputname);
        TextView inputtel = activity.findViewById(R.id.inputtel4);

        inputworkplace.setText(workplace);
        inputworkstate.setText(workpost);
        inputlastname.setText(lastname);
        inputpassword.setText(password);
        inputsurname.setText(surname);
        inputtel.setText(phnumber);
        inputlogin.setText(login);
        inputname.setText(name);

        Worktime.SetData(activity, boxes);
    }

    public static void setAllFalse (AppCompatActivity activity, ArrayList<CheckBox> boxes){
        ConstraintLayout button_main = activity.findViewById(R.id.constButton);
        ConstraintLayout button_add = activity.findViewById(R.id.constButton2);
        EditText timeStartW_enter = activity.findViewById(R.id.timeStartW_enter);
        EditText timeStartN_enter = activity.findViewById(R.id.timeStartN_enter);
        EditText timeEndW_enter = activity.findViewById(R.id.timeEndW_enter);
        EditText timeEndN_enter = activity.findViewById(R.id.timeEndN_enter);
        EditText inputWorkState = activity.findViewById(R.id.inputworkstate);
        EditText inputWorkPlace = activity.findViewById(R.id.inputworkplace);

        button_main.setEnabled(false); button_main.setVisibility(View.INVISIBLE);
        button_add.setEnabled(true); button_add.setVisibility(View.VISIBLE);

        for (CheckBox box : boxes) box.setEnabled(false);

        timeStartW_enter.setEnabled(false);
        timeStartN_enter.setEnabled(false);
        timeEndW_enter.setEnabled(false);
        timeEndN_enter.setEnabled(false);
        inputWorkState.setEnabled(false);
        inputWorkPlace.setEnabled(false);
    }
}
