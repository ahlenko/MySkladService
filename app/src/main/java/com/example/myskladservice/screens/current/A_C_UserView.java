package com.example.myskladservice.screens.current;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.datastruct.UserData;
import com.example.myskladservice.processing.datastruct.Worktime;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.screens.table.A_T_Users;
import com.example.myskladservice.screens.table.A_T_Users_N;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class A_C_UserView extends AppCompatActivity {
    @Override
    public void onBackPressed() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_userread);

        AppWorkData data = new AppWorkData(this);
        AppTableChecker check = new AppTableChecker(this);
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
                            context, new Intent(A_C_UserView.this, A_C_UserView.class),
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

        TaskPrint myTask = new TaskPrint();
        myTask.execute();

        TextView inputsurname = findViewById(R.id.inputsurname);
        TextView inputname = findViewById(R.id.inputname);
        TextView inputlastname = findViewById(R.id.inputlastname);
        TextView inputworkstate = findViewById(R.id.inputworkstate);
        TextView inputworkplace = findViewById(R.id.inputworkplace);
        TextView timeStartW_enter = findViewById(R.id.timeStartW_enter);
        TextView timeEndW_enter = findViewById(R.id.timeEndW_enter);
        TextView timeStartN_enter = findViewById(R.id.timeStartN_enter);
        TextView timeEndN_enter = findViewById(R.id.timeEndN_enter);
        TextView inputtel = findViewById(R.id.inputtel4);
        TextView inputlogin = findViewById(R.id.inputlogin4);
        TextView inputpassword = findViewById(R.id.inputpassword4);

        CheckBox day1_checker = findViewById(R.id.day1_checker);
        CheckBox day2_checker = findViewById(R.id.day2_checker);
        CheckBox day3_checker = findViewById(R.id.day3_checker);
        CheckBox day4_checker = findViewById(R.id.day4_checker);
        CheckBox day5_checker = findViewById(R.id.day5_checker);
        CheckBox day6_checker = findViewById(R.id.day6_checker);
        CheckBox day7_checker = findViewById(R.id.day7_checker);

        class UserPrint extends AsyncTask<Void, Void, Void> {

            protected Void doInBackground(Void... params) {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    MS_SQLSelect.ReadUser(mssqlConnection, check.GetChecker());
                } catch (SQLException e) {
                    DialogsViewer.twoButtonDialog(
                            context,  new Intent(A_C_UserView.this, A_C_UserView.class),
                            activity, "Помилка", "Невдале підключення до бази даних.\n" +
                                    "Повторіть спробу або вийдіть:", "Вийти", "Повторити", 1
                    );
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                inputsurname.setText(UserData.surname);
                inputname.setText(UserData.name);
                inputlastname.setText(UserData.lastname);
                inputworkstate.setText(UserData.workpost);
                inputworkplace.setText(UserData.workplace);
                inputtel.setText(UserData.phnumber);
                inputlogin.setText(UserData.login);
                inputpassword.setText(UserData.password);

                day1_checker.setChecked(Worktime.mon);
                day2_checker.setChecked(Worktime.tue);
                day3_checker.setChecked(Worktime.wed);
                day4_checker.setChecked(Worktime.thu);
                day5_checker.setChecked(Worktime.fri);
                day6_checker.setChecked(Worktime.sat);
                day7_checker.setChecked(Worktime.san);

                timeStartW_enter.setText(Worktime.startw);
                timeEndW_enter.setText(Worktime.endw);
                timeStartN_enter.setText(Worktime.starth);
                timeEndN_enter.setText(Worktime.endh);
            }
        }

        UserPrint userPrint = new UserPrint();
        userPrint.execute();

        ImageButton button_adduser = findViewById(R.id.button_returnview);
        button_adduser.setOnClickListener(enter -> {
            vibrator.vibrate(50);
            Intent intent;
            if (data.getUserType()) intent = new Intent(A_C_UserView.this, A_T_Users.class);
            else intent = new Intent(A_C_UserView.this, A_T_Users_N.class);
            startActivity(intent);
            finish();
        });

        ImageButton btn_back = findViewById(R.id.button_beck);
        btn_back.setOnClickListener(enter -> {
            vibrator.vibrate(50);
            Intent intent;
            if (data.getUserType()) intent = new Intent(A_C_UserView.this, A_T_Users.class);
            else intent = new Intent(A_C_UserView.this, A_T_Users_N.class);
            startActivity(intent);
            finish();
        });
    }
}