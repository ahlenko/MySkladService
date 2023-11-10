package com.example.myskladservice.processing.datastruct;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    public static void SetData (AppCompatActivity activity, ArrayList<CheckBox> boxes){
        EditText inputworkstate = activity.findViewById(R.id.inputworkstate);
        EditText inputworkplace = activity.findViewById(R.id.inputworkplace);
        TextView inputpassword = activity.findViewById(R.id.inputpassword4);
        EditText inputlastname = activity.findViewById(R.id.inputlastname);
        CheckBox useracess = activity.findViewById(R.id.select_fullacess);
        EditText inputsurname = activity.findViewById(R.id.inputsurname);
        TextView inputlogin = activity.findViewById(R.id.inputlogin4);
        EditText inputname = activity.findViewById(R.id.inputname);
        TextView inputtel = activity.findViewById(R.id.inputtel4);

        inputworkplace.setText(workplace);
        useracess.setChecked(fullaccess);
        inputworkstate.setText(workpost);
        inputlastname.setText(lastname);
        inputpassword.setText(password);
        inputsurname.setText(surname);
        inputtel.setText(phnumber);
        inputlogin.setText(login);
        inputname.setText(name);

        Worktime.SetData(activity, boxes);
    }
}
