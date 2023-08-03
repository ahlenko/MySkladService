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
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.database.MS_SQLUpdate;
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
    @Override
    public void onBackPressed() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c2_main_menu_non);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        AppWorkData data = new AppWorkData(this);

        AppCompatActivity activity = this;
        Context context = this;

        ImageButton work_btn = findViewById(R.id.button_work);
        ImageButton rest_btn = findViewById(R.id.button_rest);

        ImageView ring_notify = findViewById(R.id.ring_notify);
        TextView notify_count = findViewById(R.id.notify_count);

        AppCreateOr create = new AppCreateOr(this);
        AppTableChecker checker = new AppTableChecker(this);
        TextView infostate = findViewById(R.id.infostate);
        LinearLayout container = findViewById(R.id.TableView);
        Deque<View> Tasks = new ArrayDeque<>();

        class TaskPrint extends AsyncTask<Void, Void, Void> {

            private TaskInterface listener;
            public TaskPrint(TaskInterface listener) {
                this.listener = listener;
            }

            protected Void doInBackground(Void... params) {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet;
                    resultSet = MS_SQLSelect.CompanyManager(mssqlConnection, data.getCompany());
                    resultSet.next();
                    int company = resultSet.getInt("id");
                    resultSet = MS_SQLSelect.HasUserLogin(mssqlConnection, data.getUserLogin(), resultSet.getInt("id"));
                    resultSet.next();
                    int performer = resultSet.getInt("id");
                    resultSet = MS_SQLSelect.ReadTaskPrintedPR(mssqlConnection, company, performer);
                    while (resultSet.next()) {
                        View temp = getLayoutInflater().inflate(R.layout.template_view_task_pre_n, container, false);
                        TextView taskType = temp.findViewById(R.id.title_receivetime);
                        TextView taskTime = temp.findViewById(R.id.view_receivetime);
                        TextView adresserName = temp.findViewById(R.id.view_adresser);
                        TextView complitetime = temp.findViewById(R.id.view_complitetime);
                        ImageButton button = temp.findViewById(R.id.button_taskselect);
                        taskType.setText(resultSet.getString("type"));
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            LocalTime currentTime = LocalTime.now();
                            LocalTime startTime = LocalTime.parse(resultSet.getString("starttime").substring(0, resultSet.getString("starttime").length() - 11));
                            long minutesBetween = startTime.until(currentTime, ChronoUnit.MINUTES);
                            String time = minutesBetween + " хв. тому";
                            taskTime.setText(time);
                        }
                        adresserName.setText(MS_SQLSelect.ReadUserName(mssqlConnection, resultSet.getInt("adresser_id")));
                        String time = resultSet.getInt("endtime") + " (хв)";
                        complitetime.setText(time);
                        button.setId(resultSet.getInt("id"));
                        Tasks.add(temp);
                    }
                } catch (SQLException e) {
                    DialogsViewer.twoButtonDialog(
                            context, new Intent(A_S_Menu_N.this, A_S_Menu_N.class),
                            activity, "Помилка", "Невдале підключення до бази даних.\n" +
                                    "Повторіть спробу або вийдіть:", "Вийти", "Повторити", 1
                    );
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (listener != null) {
                    listener.onTaskComplete();
                }
            }
        }

        TaskPrint taskPrint = new TaskPrint(new TaskInterface() {
            @Override
            public void onTaskComplete() {
                if (Tasks.isEmpty()) infostate.setText("Вам ще не було доручено завдань");
                Iterator<View> iterator = Tasks.iterator();
                int msg_count = 0;
                while (iterator.hasNext()) {
                    View userView = iterator.next();
                    msg_count ++;
                    container.addView(userView);
                }
                if (msg_count == 0){
                    ring_notify.setVisibility(View.INVISIBLE);
                    notify_count.setText("");
                } else notify_count.setText(String.valueOf(msg_count));
            }
        });

        taskPrint.execute();

        work_btn.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        MS_SQLConnector msc = MS_SQLConnector.getConect();
                        Connection mssqlConnection = msc.connection;
                        ResultSet resultSet;

                        resultSet = MS_SQLSelect.HasCompanyEmail(
                                mssqlConnection, data.getCompany());
                        resultSet.next();
                        resultSet = MS_SQLSelect.UserATWork(
                                mssqlConnection, data.getUserLogin(),
                                resultSet.getInt("id"));
                        resultSet.next();
                        MS_SQLUpdate.UPDUserATWork(
                                mssqlConnection, false,
                                resultSet.getInt("id"));
                    }catch (SQLException ignored){}
                }
            }).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
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
                        ResultSet resultSet;

                        resultSet = MS_SQLSelect.HasCompanyEmail(
                                mssqlConnection, data.getCompany());
                        resultSet.next();
                        resultSet = MS_SQLSelect.UserATWork(
                                mssqlConnection, data.getUserLogin(),
                                resultSet.getInt("id"));
                        resultSet.next();
                        MS_SQLUpdate.UPDUserATWork(
                                mssqlConnection, true,
                                resultSet.getInt("id"));
                    }catch (SQLException e){
                        int a = 5;
                    }
                }
            }).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rest_btn.setVisibility(View.INVISIBLE);
                    rest_btn.setEnabled(false);
                    work_btn.setVisibility(View.VISIBLE);
                    work_btn.setEnabled(true);
                }
            }, 100);

        });

        class UserAccess_Tasker extends AsyncTask<Void, Void, Void> {
            boolean onWork;
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet;
                    resultSet = MS_SQLSelect.HasCompanyEmail(
                            mssqlConnection, data.getCompany());
                    resultSet.next();
                    resultSet = MS_SQLSelect.UserATWork(
                            mssqlConnection, data.getUserLogin(),
                            resultSet.getInt("id"));
                    resultSet.next();
                    onWork = resultSet.getBoolean("onwork");
                } catch (SQLException e) {
                    DialogsViewer.twoButtonDialog(
                            context,  new Intent(A_S_Menu_N.this, A_S_Menu_N.class),
                            activity, "Помилка", "Невдале підключення до бази даних.\n" +
                                    "Повторіть спробу або вийдіть:", "Вийти", "Повторити", 1
                    );
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                if (onWork){
                    rest_btn.setVisibility(View.INVISIBLE);
                    rest_btn.setEnabled(false);
                    work_btn.setVisibility(View.VISIBLE);
                    work_btn.setEnabled(true);
                } else {
                    work_btn.setVisibility(View.INVISIBLE);
                    work_btn.setEnabled(false);
                    rest_btn.setVisibility(View.VISIBLE);
                    rest_btn.setEnabled(true);
                }
            }
        }

        UserAccess_Tasker userAccess_tasker = new UserAccess_Tasker();
        userAccess_tasker.execute();

        ImageButton users_btn = findViewById(R.id.button_users);
        users_btn.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Users_N.class);
            startActivity(intent);
            finish();
        });

        ImageButton zbirka_btn = findViewById(R.id.btn_zbirka);
        zbirka_btn.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Packing.class);
            startActivity(intent);
            finish();
        });

        ImageButton output_btn = findViewById(R.id.btn_output);
        output_btn.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Output.class);
            startActivity(intent);
            finish();
        });

        ImageButton input_btn = findViewById(R.id.btn_input);
        input_btn.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Input.class);
            startActivity(intent);
            finish();
        });

        ImageButton check_btn = findViewById(R.id.btn_check);
        check_btn.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Checking.class);
            startActivity(intent);
            finish();
        });

        ImageButton table_btn = findViewById(R.id.btn_table);
        table_btn.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            Intent intent = new Intent(this, A_T_Table_N.class);
            startActivity(intent);
            finish();
        });

        ImageButton settings_btn = findViewById(R.id.btn_settings);
        settings_btn.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            Intent intent = new Intent(this, A_S_Option.class);
            startActivity(intent);
            finish();
        });
    }

    public void Button (View view){
        AppTableChecker check = new AppTableChecker(this);
        check.ChangePrivace(false);
        AppCreateOr create = new AppCreateOr(this);
        create.ChangeCreate(false);
        Intent intent = new Intent(this, A_I_AddWork.class);
        check.ChangeChecker(view.getId());
        startActivity(intent);
    }
}