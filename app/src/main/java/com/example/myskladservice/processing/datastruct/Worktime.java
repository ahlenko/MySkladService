package com.example.myskladservice.processing.datastruct;

import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myskladservice.R;

import java.util.ArrayList;

public class Worktime {
    public static boolean mon, tue, wed, thu, fri, sat, san;
    public static String startw, endw, starth, endh;

    public static void SetData (AppCompatActivity activity, ArrayList<CheckBox> boxes){
        EditText timeStartW_enter = activity.findViewById(R.id.timeStartW_enter);
        EditText timeStartN_enter = activity.findViewById(R.id.timeStartN_enter);
        EditText timeEndW_enter = activity.findViewById(R.id.timeEndW_enter);
        EditText timeEndN_enter = activity.findViewById(R.id.timeEndN_enter);

        timeStartW_enter.setText(startw);
        timeStartN_enter.setText(starth);
        timeEndW_enter.setText(endw);
        timeEndN_enter.setText(endh);

        boxes.get(0).setChecked(mon);
        boxes.get(1).setChecked(tue);
        boxes.get(2).setChecked(wed);
        boxes.get(3).setChecked(thu);
        boxes.get(4).setChecked(fri);
        boxes.get(5).setChecked(sat);
        boxes.get(6).setChecked(san);
    }
}
