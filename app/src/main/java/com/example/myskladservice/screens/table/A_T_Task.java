package com.example.myskladservice.screens.table;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.datastruct.UserData;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppCreateOr;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.processing.tasker.TaskInterface;
import com.example.myskladservice.screens.chaise.A_S_Menu;
import com.example.myskladservice.screens.chaise.A_S_Menu_N;
import com.example.myskladservice.screens.current.A_C_User;
import com.example.myskladservice.screens.insert.A_I_AddWork;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class A_T_Task extends AppCompatActivity {
    @Override public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this); Intent intent; vibrator.vibrate(50);
        if (data.getUserType()) intent = new Intent(A_T_Task.this, A_S_Menu.class);
        else intent = new Intent(A_T_Task.this, A_S_Menu_N.class);
        startActivity(intent); finish();
    }
    @Override protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.d5_task_table);

        Intent two_btn_intent = new Intent(A_T_Task.this, A_T_Task.class);
        AppTableChecker checker = new AppTableChecker(this);
        AppCreateOr create = new AppCreateOr(this);
        AppWorkData data = new AppWorkData(this);
        AppCompatActivity activity = this;
        Context context = this;

        ImageButton btn_task = findViewById(R.id.button_createtask);
        LinearLayout container = findViewById(R.id.TableView);
        ImageButton btn_back = findViewById(R.id.button_beck);
        TextView infostate = findViewById(R.id.infostate);
        Deque<View> Tasks = new ArrayDeque<>();

        PrintTask.PrintTaskCount(activity, context, two_btn_intent);

        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet = MS_SQLSelect.ReadTaskPrinted(
                            mssqlConnection, data.getCompany(), data.getUserLogin(), "Adr");
                    while (resultSet.next()){
                        View temp = getLayoutInflater().inflate(R.layout.template_view_task,
                                container, false);
                        TextView performerName = temp.findViewById(R.id.view_performer);
                        ImageButton button = temp.findViewById(R.id.button_taskselect);
                        TextView taskType = temp.findViewById(R.id.view_tasktype);
                        TextView taskTime = temp.findViewById(R.id.view_tasktime);

                        performerName.setText(UserData.getUserName(resultSet));
                        taskType.setText(resultSet.getString("type"));
                        String time = resultSet.getString("starttime").substring(
                                0, resultSet.getString("starttime").length() - 11);
                        time += " / " + resultSet.getString("endtime") + " хв.";
                        taskTime.setText(time); button.setId(resultSet.getInt("id"));
                        Tasks.add(temp);
                    }
                } catch (SQLException e) {
                    MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (Tasks.isEmpty()) infostate.setText(R.string.not_have_task);
                        for (View userView : Tasks) { container.addView(userView); }
                    }
                });
            }
        }).start();

        btn_task.setOnClickListener (enter -> {
            create.ChangeCreate(true);checker.ChangePrivace(true);
            Intent intent = new Intent(this, A_I_AddWork.class);
            vibrator.vibrate(50); startActivity(intent); finish();
        });


        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50); onBackPressed();
        });
    }

    public void Button (View view){
        AppTableChecker check = new AppTableChecker(this); check.ChangePrivace(true);
        AppCreateOr create = new AppCreateOr(this); create.ChangeCreate(false);
        Intent intent = new Intent(this, A_I_AddWork.class);
        check.ChangeChecker(view.getId()); startActivity(intent); finish();
    }
}