package com.example.myskladservice.screens.chaise;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.example.myskladservice.screens.table.A_T_Table_N;
import com.example.myskladservice.screens.table.A_T_Task;
import com.example.myskladservice.screens.table.A_T_Users_N;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class A_S_Menu_N extends AppCompatActivity {
    @Override public void onBackPressed() {}

    @Override protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.c2_main_menu_non);

        Intent two_btn_intent = new Intent(A_S_Menu_N.this, A_S_Menu_N.class);
        AppWorkData data = new AppWorkData(this);
        Deque<View> Tasks = new ArrayDeque<>();
        AppCompatActivity activity = this;
        Context context = this;

        ImageButton settings_btn = findViewById(R.id.btn_settings);
        TextView notify_count = findViewById(R.id.notify_count);
        ImageButton users_btn = findViewById(R.id.button_users);
        TextView work_state_t = findViewById(R.id.work_state_t);
        ImageView ring_notify = findViewById(R.id.ring_notify);
        ImageButton output_btn = findViewById(R.id.btn_output);
        ImageButton zbirka_btn = findViewById(R.id.btn_zbirka);
        ImageButton work_btn = findViewById(R.id.button_work);
        ImageButton rest_btn = findViewById(R.id.button_rest);
        LinearLayout container = findViewById(R.id.TableView);
        ImageButton table_btn = findViewById(R.id.btn_table);
        ImageButton check_btn = findViewById(R.id.btn_check);
        ImageButton input_btn = findViewById(R.id.btn_input);
        TextView infostate = findViewById(R.id.infostate);

        class UserAccess_Tasker extends AsyncTask<Void, Void, Void> {
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
                    work_state_t.setText(R.string.btn_complite_work);
                    rest_btn.setVisibility(View.INVISIBLE); rest_btn.setEnabled(false);
                    work_btn.setVisibility(View.VISIBLE); work_btn.setEnabled(true);
                } else {
                    work_state_t.setText(R.string.btn_start_work);
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
                        Iterator<View> iterator = Tasks.iterator(); int msg_count = 0;
                        while (iterator.hasNext()) {
                            View userView = iterator.next(); msg_count++;
                            container.addView(userView);
                        } if (msg_count == 0){
                            ring_notify.setVisibility(View.INVISIBLE);
                            notify_count.setText("");
                        } else notify_count.setText(String.valueOf(msg_count));
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
                    }catch (SQLException e){
                        MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                    }
                }
            }).start();
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    work_state_t.setText(R.string.btn_start_work);
                    work_btn.setVisibility(View.INVISIBLE); work_btn.setEnabled(false);
                    rest_btn.setVisibility(View.VISIBLE); rest_btn.setEnabled(true);
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
                    }catch (SQLException e){
                        MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                    }
                }
            }).start();
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    work_state_t.setText(R.string.btn_complite_work);
                    rest_btn.setVisibility(View.INVISIBLE); rest_btn.setEnabled(false);
                    work_btn.setVisibility(View.VISIBLE); work_btn.setEnabled(true);
                }
            }, 100);

        });

        users_btn.setOnClickListener (enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Users_N.class);
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

        check_btn.setOnClickListener (enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Checking.class);
            startActivity(intent); finish();
        });

        table_btn.setOnClickListener (enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Table_N.class);
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