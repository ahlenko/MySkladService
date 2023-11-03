package com.example.myskladservice.screens.current;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.example.myskladservice.AM_Login;
import com.example.myskladservice.R;
import com.example.myskladservice.processing.checkers.InputChecker;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.database.MS_SQLUpdate;
import com.example.myskladservice.processing.datastruct.UserData;
import com.example.myskladservice.processing.datastruct.Worktime;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.screens.register.A_R_User;
import com.example.myskladservice.screens.table.A_T_Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.regex.Pattern;

public class A_C_User extends AppCompatActivity {
    @Override
    public void onBackPressed() {}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_userview);
        AppWorkData data = new AppWorkData(this);
        AppTableChecker check = new AppTableChecker(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        Intent two_btn_intent = new Intent(A_C_User.this, A_C_User.class);
        AppCompatActivity activity = this;
        Context context = this;

        PrintTask.PrintTaskCount(activity, context, two_btn_intent);

        TextView infostate = findViewById(R.id.infostate);
        TextView textsurname = findViewById(R.id.textsurname);
        TextView textname = findViewById(R.id.textname);
        TextView textlastname = findViewById(R.id.textlastname);
        TextView texttel = findViewById(R.id.texttel4);
        TextView textworkstate = findViewById(R.id.textworkstate);
        TextView textworkplace = findViewById(R.id.textworkplace);
        TextView textlogin = findViewById(R.id.textlogin4);
        TextView textpassword = findViewById(R.id.textpassword4);
        TextView workdaystitle = findViewById(R.id.workdaystitle);
        TextView noworkdaystitle = findViewById(R.id.noworkdaystitle);
        TextView fullacess = findViewById(R.id.title_fullacess);

        EditText inputsurname = findViewById(R.id.inputsurname);
        EditText inputname = findViewById(R.id.inputname);
        EditText inputlastname = findViewById(R.id.inputlastname);
        EditText inputworkstate = findViewById(R.id.inputworkstate);
        EditText inputworkplace = findViewById(R.id.inputworkplace);
        EditText timeStartW_enter = findViewById(R.id.timeStartW_enter);
        EditText timeEndW_enter = findViewById(R.id.timeEndW_enter);
        EditText timeStartN_enter = findViewById(R.id.timeStartN_enter);
        EditText timeEndN_enter = findViewById(R.id.timeEndN_enter);
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
        CheckBox useracess = findViewById(R.id.select_fullacess);

        ConstraintLayout button_main = findViewById(R.id.constButton);
        ConstraintLayout button_add = findViewById(R.id.constButton2);

        button_add.setEnabled(false);
        button_add.setVisibility(View.INVISIBLE);

        class UserPrint extends AsyncTask<Void, Void, Void> {

            protected Void doInBackground(Void... params) {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    MS_SQLSelect.ReadUser(mssqlConnection, check.GetChecker());
                } catch (SQLException e) {
                    DialogsViewer.twoButtonDialog(
                            context,  new Intent(A_C_User.this, A_C_User.class),
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

                useracess.setChecked(UserData.fullaccess);
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

                if(Objects.equals(data.getUserLogin(), UserData.login)){
                    if (!check.GetPrivace()){
                        day1_checker.setEnabled(false);
                        day2_checker.setEnabled(false);
                        day3_checker.setEnabled(false);
                        day4_checker.setEnabled(false);
                        day5_checker.setEnabled(false);
                        day6_checker.setEnabled(false);
                        day7_checker.setEnabled(false);

                        timeStartW_enter.setEnabled(false);
                        timeEndW_enter.setEnabled(false);
                        timeStartN_enter.setEnabled(false);
                        timeEndN_enter.setEnabled(false);

                        inputworkstate.setEnabled(false);
                        inputworkplace.setEnabled(false);

                        button_add.setEnabled(true);
                        button_add.setVisibility(View.VISIBLE);

                        button_main.setEnabled(false);
                        button_main.setVisibility(View.INVISIBLE);
                    }
                    useracess.setClickable(false);
                    useracess.setAlpha(0);
                    fullacess.setAlpha(0);
                }
            }
        }

        UserPrint userPrint = new UserPrint();
        userPrint.execute();



        ImageButton button_adduser = findViewById(R.id.button_renew);
        button_adduser.setOnClickListener (enter -> {
            textlastname.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textname.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textsurname.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textworkstate.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textworkplace.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            texttel.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textlogin.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textpassword.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            workdaystitle.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            noworkdaystitle.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            infostate.setText("");

            String Sinputsurname = inputsurname.getText().toString().trim();
            String Sinputname = inputname.getText().toString().trim();
            String Sinputlastname = inputlastname.getText().toString().trim();
            String Sinputworkstate = inputworkstate.getText().toString().trim();
            String Sinputworkplace = inputworkplace.getText().toString().trim();
            String StimeStartW_enter = timeStartW_enter.getText().toString().trim();
            String StimeEndW_enter = timeEndW_enter.getText().toString().trim();
            String StimeStartN_enter = timeStartN_enter.getText().toString().trim();
            String StimeEndN_enter = timeEndN_enter.getText().toString().trim();
            String Sinputtel = inputtel.getText().toString().trim();
            String Sinputlogin = inputlogin.getText().toString().trim();
            String Sinputpassword = inputpassword.getText().toString().trim();

            boolean day1 = day1_checker.isChecked();
            boolean day2 = day2_checker.isChecked();
            boolean day3 = day3_checker.isChecked();
            boolean day4 = day4_checker.isChecked();
            boolean day5 = day5_checker.isChecked();
            boolean day6 = day6_checker.isChecked();
            boolean day7 = day7_checker.isChecked();
            boolean acess = useracess.isChecked();

            int enter_err = 0;

            if (InputChecker.isNotCSize(Sinputsurname, textsurname, 25, this)) enter_err++;
            if (InputChecker.isNotCSize(Sinputname, textname, 25, this)) enter_err++;
            if (InputChecker.isNotCSize(Sinputlastname, textlastname, 25, this)) enter_err++;
            if (InputChecker.isNotCSize(Sinputworkstate, textworkstate, 25, this)) enter_err++;
            if (InputChecker.isNotCSize(Sinputworkplace, textworkplace, 25, this)) enter_err++;
            if (InputChecker.isNotTime(StimeStartW_enter, workdaystitle, this)) enter_err++;
            if (InputChecker.isNotTime(StimeEndW_enter, workdaystitle, this)) enter_err++;
            if (InputChecker.isNotTime(StimeStartN_enter, noworkdaystitle, this)) enter_err++;
            if (InputChecker.isNotTime(StimeEndN_enter, noworkdaystitle, this)) enter_err++;
            if (InputChecker.isNotPhone(Sinputtel, texttel, this)) enter_err++;
            if (InputChecker.isNotEmail(Sinputlogin, textlogin, 35, this)) enter_err++;
            if (InputChecker.isNotPassword(Sinputpassword, textpassword, this)) enter_err++;

            if (enter_err == 0) {
                StimeStartW_enter += ":00";
                StimeEndW_enter += ":00";
                StimeStartN_enter += ":00";
                StimeEndN_enter += ":00";
                String finalStimeStartW_enter = StimeStartW_enter;
                String finalStimeEndW_enter = StimeEndW_enter;
                String finalStimeStartN_enter = StimeStartN_enter;
                String finalStimeEndN_enter = StimeEndN_enter;
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            MS_SQLConnector msc = MS_SQLConnector.getConect();
                            Connection mssqlConnection = msc.connection;
                            ResultSet resultSet;

                            resultSet = MS_SQLSelect.SelectUserTime(mssqlConnection,
                                    check.GetChecker()); resultSet.next();
                            MS_SQLUpdate.UPDWorkTime(mssqlConnection,
                                    finalStimeStartW_enter, finalStimeEndW_enter,
                                    finalStimeStartN_enter, finalStimeEndN_enter,
                                    false, resultSet.getInt("worktime_id")
                            );
                            MS_SQLUpdate.UPDWorkDays(mssqlConnection,
                                    day1, day2, day3,day4,day5,day6,day7,
                                    false, resultSet.getInt("workdays_id")
                            );
                            MS_SQLUpdate.UPDUser(mssqlConnection,
                                    Sinputsurname, Sinputname, Sinputlastname,
                                    Sinputworkstate, Sinputworkplace, acess,
                                    check.GetChecker()
                            );

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    vibrator.vibrate(50);
                                    Intent intent = new Intent(A_C_User.this, A_T_Users.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            return;
                        } catch (SQLException e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    DialogsViewer.twoButtonDialog(
                                            context,  two_btn_intent, activity, "Помилка",
                                            "Невдале підключення до бази даних.\nПовторіть спробу або вийдіть:",
                                            "Вийти", "Повторити", 1
                                    );
                                }
                            });
                            return;
                        }
                    }
                }).start();
            } else infostate.setText("* Деякі поля заповнено некоректно");
        });

        ImageButton button_adduser2 = findViewById(R.id.button_renew2);
        button_adduser2.setOnClickListener (enter ->{
            button_adduser.performClick();
        });

        ImageButton btn_del = findViewById(R.id.button_delete);
        btn_del.setOnClickListener( enter -> {
            if (check.GetPrivace()) {
                DialogsViewer.twoButtonDialog(
                        context,  new Intent(A_C_User.this, A_T_Users.class), activity,
                        "Підтвердження", "Видалення Вас як власника призведе до видалення компанії " +
                                "та її співробітників.\n" + " Ви можете виконати передачу права власності " +
                                "іншому співробітникові:", "Видалити", "Передати", 5
                );
            } else {
                DialogsViewer.twoButtonDialog(
                        context,  new Intent(A_C_User.this, A_T_Users.class), activity,
                        "Підтвердження", "Ви дійсно впевнені що хочете видалити даного співробітника?",
                        "Так", "Ні", 4
                );
            }
        });

        ImageButton btn_back = findViewById(R.id.button_beck);
        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            Intent intent = new Intent(A_C_User.this, A_T_Users.class);
            startActivity(intent);
            finish();
        });
    }
}