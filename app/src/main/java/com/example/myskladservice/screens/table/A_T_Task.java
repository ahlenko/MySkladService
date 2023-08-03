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
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppCreateOr;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.TaskInterface;
import com.example.myskladservice.screens.chaise.A_S_Menu;
import com.example.myskladservice.screens.chaise.A_S_Menu_N;
import com.example.myskladservice.screens.current.A_C_User;
import com.example.myskladservice.screens.insert.A_I_AddWork;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class A_T_Task extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this);
        vibrator.vibrate(50);
        Intent intent;
        if (data.getUserType()) intent = new Intent(A_T_Task.this, A_S_Menu.class);
        else intent = new Intent(A_T_Task.this, A_S_Menu_N.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d5_task_table);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        AppWorkData data = new AppWorkData(this);
        AppCreateOr create = new AppCreateOr(this);
        AppTableChecker checker = new AppTableChecker(this);

        AppCompatActivity activity = this;
        Context context = this;

        ImageView ring_notify = findViewById(R.id.ring_notify);
        TextView notify_count = findViewById(R.id.notify_count);
        class TaskPrintR extends AsyncTask<Void, Void, Void> {
            private int count = 0;
            protected Void doInBackground(Void... params) {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet;
                    resultSet = MS_SQLSelect.CompanyManager(mssqlConnection, data.getCompany());
                    resultSet.next(); int company = resultSet.getInt("id");
                    resultSet = MS_SQLSelect.HasUserLogin(mssqlConnection, data.getUserLogin(), resultSet.getInt("id"));
                    resultSet.next(); int performer = resultSet.getInt("id");
                    resultSet = MS_SQLSelect.ReadTaskPrintedPR(mssqlConnection, company, performer);
                    while (resultSet.next()) count++;
                } catch (SQLException e) {
                    DialogsViewer.twoButtonDialog(
                            context, new Intent(A_T_Task.this, A_T_Task.class),
                            activity, "Помилка", "Невдале підключення до бази даних.\n" +
                                    "Повторіть спробу або вийдіть:", "Вийти", "Повторити", 1
                    );
                }
                return null;
            }
            protected void onPostExecute(Void result) {
                if (count == 0){
                    ring_notify.setVisibility(View.INVISIBLE);
                    notify_count.setText("");
                } else notify_count.setText(String.valueOf(count));
            }
        }

        TaskPrintR myTask = new TaskPrintR();
        myTask.execute();

        TextView infostate = findViewById(R.id.infostate);

        LinearLayout container = findViewById(R.id.TableView);
        Deque<View> Tasks = new ArrayDeque<>();

        class TaskPrint extends AsyncTask<Void, Void, Void> {

            private TaskInterface listener;

            public TaskPrint(TaskInterface listener) {
                this.listener = listener;
            }

            protected Void doInBackground(Void... params){
                try{
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet;
                    resultSet = MS_SQLSelect.CompanyManager(mssqlConnection, data.getCompany());
                    resultSet.next(); int company = resultSet.getInt("id");
                    resultSet = MS_SQLSelect.HasUserLogin(mssqlConnection, data.getUserLogin(), resultSet.getInt("id"));
                    resultSet.next(); int adresser = resultSet.getInt("id");
                    resultSet = MS_SQLSelect.ReadTaskPrinted(mssqlConnection, company, adresser);
                    while (resultSet.next()){
                        View temp = getLayoutInflater().inflate(R.layout.template_view_task, container, false);
                        TextView performerName = temp.findViewById(R.id.view_performer);
                        TextView taskType = temp.findViewById(R.id.view_tasktype);
                        TextView taskTime = temp.findViewById(R.id.view_tasktime);
                        ImageButton button = temp.findViewById(R.id.button_taskselect);

                        performerName.setText(MS_SQLSelect.ReadUserName(mssqlConnection, resultSet.getInt("performer_id")));
                        taskType.setText(resultSet.getString("type"));
                        String time = resultSet.getString("starttime").substring(0, resultSet.getString("starttime").length() - 11);
                        time += " / " + resultSet.getString("endtime") + " хв.";
                        taskTime.setText(time);
                        button.setId(resultSet.getInt("id"));
                        Tasks.add(temp);
                    }

                } catch (SQLException e){
                    DialogsViewer.twoButtonDialog(
                            context,  new Intent(A_T_Task.this, A_T_Task.class),
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

        TaskPrint userPrint = new TaskPrint(new TaskInterface() {
            @Override
            public void onTaskComplete() {
                if (Tasks.isEmpty()) infostate.setText("Вами ще не було доручено завдань");
                Iterator<View> iterator = Tasks.iterator();
                while (iterator.hasNext()) {
                    View userView = iterator.next();
                    container.addView(userView);
                }
            }
        });

        userPrint.execute();

        ImageButton btn_task = findViewById(R.id.button_createtask);
        btn_task.setOnClickListener (enter -> {
            create.ChangeCreate(true);
            checker.ChangePrivace(true);
            vibrator.vibrate(50);
            Intent intent = new Intent(this, A_I_AddWork.class);
            startActivity(intent);
            finish();
        });

        ImageButton btn_back = findViewById(R.id.button_beck);
        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            onBackPressed();
        });
    }

    public void Button (View view){
        AppTableChecker check = new AppTableChecker(this);
        check.ChangePrivace(true);
        AppCreateOr create = new AppCreateOr(this);
        create.ChangeCreate(false);
        Intent intent = new Intent(this, A_I_AddWork.class);
        check.ChangeChecker(view.getId());
        startActivity(intent);
        finish();
    }
}