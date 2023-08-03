package com.example.myskladservice.screens.table;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.tasker.TaskInterface;
import com.example.myskladservice.screens.chaise.A_S_Login;
import com.example.myskladservice.screens.chaise.A_S_Menu;
import com.example.myskladservice.screens.chaise.A_S_Menu_N;
import com.example.myskladservice.screens.current.A_C_UserView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class A_T_Users_N extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this);
        vibrator.vibrate(50);
        Intent intent;
        if (data.getUserType()) intent = new Intent(A_T_Users_N.this, A_S_Menu.class);
        else intent = new Intent(A_T_Users_N.this, A_S_Menu_N.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d12_users_non);
        AppWorkData data = new AppWorkData(this);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        AppCompatActivity activity = this;
        Context context = this;

        ImageView ring_notify = findViewById(R.id.ring_notify);
        TextView notify_count = findViewById(R.id.notify_count);
        class TaskPrint extends AsyncTask<Void, Void, Void> {
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
                            context, new Intent(A_T_Users_N.this, A_T_Users_N.class),
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

        TaskPrint myTaskRing = new TaskPrint();
        myTaskRing.execute();

        LinearLayout container = findViewById(R.id.TableView);
        Deque<View> Users = new ArrayDeque<>();

        class UserPrint extends AsyncTask<Void, Void, Void> {
            private TaskInterface listener;

            public UserPrint(TaskInterface listener) {
                this.listener = listener;
            }

            protected Void doInBackground(Void... params){
                try{
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet;

                    resultSet = MS_SQLSelect.CompanyManager(mssqlConnection, data.getCompany()); resultSet.next();
                    resultSet = MS_SQLSelect.SelectUser(mssqlConnection, resultSet.getInt("id"));

                    while (resultSet.next()){
                        View temp = getLayoutInflater().inflate(R.layout.template_view_user, container, false);
                        TextView name = temp.findViewById(R.id.view_workername);
                        TextView workstate = temp.findViewById(R.id.view_workstate);
                        TextView wplace = temp.findViewById(R.id.view_wplace);
                        ImageView activity = temp.findViewById(R.id.activity_indicator);
                        ImageView uimage = temp.findViewById(R.id.user_image);
                        ImageButton button = temp.findViewById(R.id.button_userselect);

                        button.setId(resultSet.getInt("id"));
                        String nameUser = resultSet.getString("surname") + " " +
                                          resultSet.getString("name").charAt(0) + ". " +
                                          resultSet.getString("lastname").charAt(0) + ".";
                        name.setText(nameUser);

                        workstate.setText(resultSet.getString("workpost"));
                        wplace.setText(resultSet.getString("workplace"));

                        if (!resultSet.getBoolean("onwork")) activity.setImageResource(R.drawable.ellipse_red);
                        if (resultSet.getBoolean("fullacess")) uimage.setImageResource(R.drawable.ico_manager);
                        Users.add(temp);
                    }
                } catch (SQLException e){
                    DialogsViewer.twoButtonDialog(
                            context,  new Intent(A_T_Users_N.this, A_T_Users_N.class),
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

        UserPrint userPrint = new UserPrint(new TaskInterface() {
            @Override
            public void onTaskComplete() {
                Iterator<View> iterator = Users.iterator();
                while (iterator.hasNext()) {
                    View userView = iterator.next();
                    container.addView(userView);
                }
            }
        });

        userPrint.execute();

        ImageButton btn_back = findViewById(R.id.button_beck);
        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            onBackPressed();
        });
    }

    public void Button (View view){
        AppTableChecker check = new AppTableChecker(this);
        check.ChangeChecker(view.getId());
        Intent intent = new Intent(this, A_C_UserView.class);
        startActivity(intent);
        finish();
    }
}