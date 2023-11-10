package com.example.myskladservice.processing.datastruct;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myskladservice.R;

import java.util.ArrayList;

public class TaskData {
    public static String pefrormer;
    public static String starttime;
    public static String comment;
    public static String endtime;

    public static String type;

    public static void SetData (AppCompatActivity activity){
        EditText time_release_ent = activity.findViewById(R.id.enter_release_time);
        EditText time_start_ent = activity.findViewById(R.id.enter_starting_time);
        TextView enter_performer = activity.findViewById(R.id.enter_performer);
        TextView enter_tasktype = activity.findViewById(R.id.enter_tasktype);
        EditText comments = activity.findViewById(R.id.entercomment);

        enter_performer.setText(pefrormer);
        time_start_ent.setText(starttime);
        time_release_ent.setText(endtime);
        enter_tasktype.setText(type);
        comments.setText(comment);
    }
}
