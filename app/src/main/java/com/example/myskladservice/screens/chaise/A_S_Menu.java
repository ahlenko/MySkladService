package com.example.myskladservice.screens.chaise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myskladservice.AM_Login;
import com.example.myskladservice.R;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.database.MS_SQLUpdate;
import com.example.myskladservice.processing.datastruct.UserData;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppCreateOr;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.TaskInterface;
import com.example.myskladservice.screens.insert.A_I_AddWork;
import com.example.myskladservice.screens.table.A_T_Checking;
import com.example.myskladservice.screens.table.A_T_Input;
import com.example.myskladservice.screens.table.A_T_Output;
import com.example.myskladservice.screens.table.A_T_Packing;
import com.example.myskladservice.screens.table.A_T_Table;
import com.example.myskladservice.screens.table.A_T_Task;
import com.example.myskladservice.screens.table.A_T_Users;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class A_S_Menu extends AppCompatActivity {
    @Override public void onBackPressed() {}

    @Override protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.c1_main_menu);

        Intent two_btn_intent = new Intent(A_S_Menu.this, A_S_Menu.class);
        AppWorkData data = new AppWorkData(this);
        Deque<View> Tasks = new ArrayDeque<>();
        AppCompatActivity activity = this;
        Context context = this;

        ImageButton taskmanager_btn = findViewById(R.id.btn_taskmanager);
        ImageButton analitics_btn = findViewById(R.id.btn_analitics);
        ImageButton settings_btn = findViewById(R.id.btn_settings);
        ImageButton users_btn = findViewById(R.id.button_users);
        ImageButton output_btn = findViewById(R.id.btn_output);
        ImageButton zbirka_btn = findViewById(R.id.btn_zbirka);
        ImageButton check_btn = findViewById(R.id.btn_check);
        ImageButton table_btn = findViewById(R.id.btn_table);
        ImageButton input_btn = findViewById(R.id.btn_input);
        LinearLayout container = findViewById(R.id.TableView);
        ImageButton work_btn = findViewById(R.id.button_work);
        ImageButton rest_btn = findViewById(R.id.button_rest);
        TextView infostate = findViewById(R.id.infostate);

        class UserAccess_Tasker extends AsyncTask<Void, Void, Void>{
            boolean onWork;
            @Override protected Void doInBackground(Void... voids) {
                try{
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    onWork = MS_SQLSelect.IsUserAtWork(mssqlConnection,
                            data.getCompany(), data.getUserLogin());
                    msc.disconnect();
                } catch (SQLException e) {
                    MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                } return null;
            }
            protected void onPostExecute(Void result) {
                if (onWork){
                    rest_btn.setVisibility(View.INVISIBLE); rest_btn.setEnabled(false);
                    work_btn.setVisibility(View.VISIBLE); work_btn.setEnabled(true);
                } else {
                    work_btn.setVisibility(View.INVISIBLE); work_btn.setEnabled(false);
                    rest_btn.setVisibility(View.VISIBLE); rest_btn.setEnabled(true);
                }
            }
        } UserAccess_Tasker userAccess_tasker = new UserAccess_Tasker(); userAccess_tasker.execute();

        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet = MS_SQLSelect.ReadTaskPrinted(
                            mssqlConnection, data.getCompany(), data.getUserLogin(), "Adr");
                    while (resultSet.next()) {
                        View temp = getLayoutInflater().inflate(R.layout.template_view_task_pre,
                                container, false);
                        TextView complitetime = temp.findViewById(R.id.view_complitetime);
                        ImageButton button = temp.findViewById(R.id.button_taskselect);
                        TextView taskType = temp.findViewById(R.id.title_receivetime);
                        TextView adresserName = temp.findViewById(R.id.view_adresser);
                        TextView taskTime = temp.findViewById(R.id.view_receivetime);
                        taskType.setText(resultSet.getString("type"));

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            LocalTime currentTime = LocalTime.now(); LocalTime startTime =
                                    LocalTime.parse(resultSet.getString("starttime").substring(0,
                                            resultSet.getString("starttime").length() - 11));
                            long minutesBetween = startTime.until(currentTime, ChronoUnit.MINUTES);
                            String time = minutesBetween + " " +
                                    getString(R.string.time_points_before);taskTime.setText(time); }

                        adresserName.setText(UserData.getUserName(resultSet));
                        String time = resultSet.getInt("endtime") + " " +
                                getString(R.string.time_points); complitetime.setText(time);
                        button.setId(resultSet.getInt("id")); Tasks.add(temp);
                    } msc.disconnect();
                } catch (SQLException e) {
                    MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (Tasks.isEmpty()) infostate.setText(R.string.not_have_task);
                        for (View userView : Tasks) container.addView(userView);
                    }
                });
            }
        }).start();

        work_btn.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        MS_SQLConnector msc = MS_SQLConnector.getConect();
                        Connection mssqlConnection = msc.connection;
                        MS_SQLUpdate.UPDUserATWork( mssqlConnection, false,
                                data.getCompany(), data.getUserLogin());
                        msc.disconnect();
                    } catch (SQLException e){
                        MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                    }
                }
            }).start();
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    work_btn.setVisibility(View.INVISIBLE);
                    work_btn.setEnabled(false);
                    rest_btn.setVisibility(View.VISIBLE);
                    rest_btn.setEnabled(true);
                }
            }, 100);
        });

        rest_btn.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        MS_SQLConnector msc = MS_SQLConnector.getConect();
                        Connection mssqlConnection = msc.connection;
                        MS_SQLUpdate.UPDUserATWork( mssqlConnection, true,
                                data.getCompany(), data.getUserLogin());
                        msc.disconnect();
                    } catch (SQLException e){
                        MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                    }
                }
            }).start();
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    rest_btn.setVisibility(View.INVISIBLE);
                    rest_btn.setEnabled(false);
                    work_btn.setVisibility(View.VISIBLE);
                    work_btn.setEnabled(true);
                }
            }, 100);
        });

        users_btn.setOnClickListener (enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Users.class);
            startActivity(intent); finish();
        });

        zbirka_btn.setOnClickListener (enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Packing.class);
            startActivity(intent); finish();
        });

        output_btn.setOnClickListener (enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Output.class);
            startActivity(intent); finish();
        });

        input_btn.setOnClickListener (enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Input.class);
            startActivity(intent); finish();
        });

        taskmanager_btn.setOnClickListener (enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Task.class);
            startActivity(intent); finish();
        });

        table_btn.setOnClickListener (enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Table.class);
            startActivity(intent); finish();
        });

        check_btn.setOnClickListener (enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Checking.class);
            startActivity(intent); finish();
        });


        analitics_btn.setOnClickListener (enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(this, A_S_Analitics.class);
            startActivity(intent); finish();
        });


        settings_btn.setOnClickListener (enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(this, A_S_Option.class);
            startActivity(intent); finish();
        });
    }
    public void Button (View view){
        AppTableChecker check = new AppTableChecker(this);
        check.ChangePrivace(false);
        AppCreateOr create = new AppCreateOr(this);
        create.ChangeCreate(false);
        Intent intent = new Intent(this, A_I_AddWork.class);
        check.ChangeChecker(view.getId()); startActivity(intent);
    }
}