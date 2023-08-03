package com.example.myskladservice.screens.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myskladservice.AM_Login;
import com.example.myskladservice.R;
import com.example.myskladservice.processing.checkers.InputChecker;
import com.example.myskladservice.processing.database.MS_SQLDelete;
import com.example.myskladservice.processing.database.MS_SQLInsert;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.exception.SmallException;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.database.MS_SQLConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

public class A_R_Company extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        DialogsViewer.twoButtonDialog(
                this,  new Intent(A_R_Company.this, AM_Login.class), this, "Відміна",
                "Ви дійсно хочете згорнути процес реестрації?",
                "Так", "Ні", 7
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b1_reg_company);
        AppWorkData data = new AppWorkData(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        Intent two_btn_intent = new Intent(A_R_Company.this, A_R_Company.class);
        AppCompatActivity activity = this;
        Context context = this;

        TextView infostate = findViewById(R.id.infostate);
        TextView textname = findViewById(R.id.textname);
        TextView textadress = findViewById(R.id.textadress);
        TextView textspec = findViewById(R.id.textspec);
        TextView textemail = findViewById(R.id.textemail_2);
        TextView texttel = findViewById(R.id.texttel_2);
        TextView textcode = findViewById(R.id.textcode_2);
        TextView workdaystitle = findViewById(R.id.workdaystitle);
        TextView noworkdaystitle = findViewById(R.id.noworkdaystitle);

        EditText inputcompanyname = findViewById(R.id.inputcompanyname);
        EditText inputadres = findViewById(R.id.inputadres);
        EditText inputspec = findViewById(R.id.inputspec);
        EditText timeStartW_enter = findViewById(R.id.timeStartW_enter);
        EditText timeEndW_enter = findViewById(R.id.timeEndW_enter);
        EditText timeStartN_enter = findViewById(R.id.timeStartN_enter);
        EditText timeEndN_enter = findViewById(R.id.timeEndN_enter);
        EditText inputemail_2 = findViewById(R.id.inputemail_2);
        EditText inputtel_2 = findViewById(R.id.inputtel_2);
        EditText inputcode_2 = findViewById(R.id.inputcode_2);

        CheckBox day1_checker = findViewById(R.id.day1_checker);
        CheckBox day2_checker = findViewById(R.id.day2_checker);
        CheckBox day3_checker = findViewById(R.id.day3_checker);
        CheckBox day4_checker = findViewById(R.id.day4_checker);
        CheckBox day5_checker = findViewById(R.id.day5_checker);
        CheckBox day6_checker = findViewById(R.id.day6_checker);
        CheckBox day7_checker = findViewById(R.id.day7_checker);

        ImageButton next_reg_btn = findViewById(R.id.button_continuereg);
        next_reg_btn.setOnClickListener(enter -> {
            textname.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textadress.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textspec.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textemail.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            texttel.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textcode.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            workdaystitle.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            noworkdaystitle.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            infostate.setText("");

            String Sinputcompanyname = inputcompanyname.getText().toString().trim();
            String Sinputadres = inputadres.getText().toString().trim();
            String Sinputspec = inputspec.getText().toString().trim();
            String StimeStartW_enter = timeStartW_enter.getText().toString().trim();
            String StimeEndW_enter = timeEndW_enter.getText().toString().trim();
            String StimeStartN_enter = timeStartN_enter.getText().toString().trim();
            String StimeEndN_enter = timeEndN_enter.getText().toString().trim();
            String Sinputemail = inputemail_2.getText().toString().trim();
            String Sinputtel = inputtel_2.getText().toString().trim();
            String Sinputcode = inputcode_2.getText().toString().trim();

            boolean day1 = day1_checker.isChecked();
            boolean day2 = day2_checker.isChecked();
            boolean day3 = day3_checker.isChecked();
            boolean day4 = day4_checker.isChecked();
            boolean day5 = day5_checker.isChecked();
            boolean day6 = day6_checker.isChecked();
            boolean day7 = day7_checker.isChecked();

            int enter_err = 0;

            if (InputChecker.isNotCSize(Sinputcompanyname, textname, 25, this)) enter_err++;
            if (InputChecker.isNotCSize(Sinputadres, textadress, 80, this)) enter_err++;
            if (InputChecker.isNotCSize(Sinputspec, textspec, 40, this)) enter_err++;
            if (InputChecker.isNotTime(StimeStartW_enter, workdaystitle, this)) enter_err++;
            if (InputChecker.isNotTime(StimeEndW_enter, workdaystitle, this)) enter_err++;
            if (InputChecker.isNotTime(StimeStartN_enter, noworkdaystitle, this)) enter_err++;
            if (InputChecker.isNotTime(StimeEndN_enter, noworkdaystitle, this)) enter_err++;
            if (InputChecker.isNotEmail(Sinputemail, textemail, 35, this)) enter_err++;
            if (InputChecker.isNotPhone(Sinputtel, texttel, this)) enter_err++;
            if (InputChecker.isNotCSize(Sinputcode, textcode, 8, this)) enter_err++;

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

                            int enter_err = 0;
                            resultSet = MS_SQLSelect.HasCompanyEmail(mssqlConnection, Sinputemail);
                            if (resultSet.next()){
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        infostate.setText("* Деякі дані вже використовуються");
                                        textemail.setTextColor(getResources().getColor(R.color.red_note));
                                    }
                                }); enter_err++;
                            }

                            resultSet = MS_SQLSelect.HasCompanyPhoneNumber(mssqlConnection, Sinputtel);
                            if (resultSet.next()){
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        infostate.setText("* Деякі дані вже використовуються");
                                        texttel.setTextColor(getResources().getColor(R.color.red_note));
                                    }
                                }); enter_err++;
                            }

                            resultSet = MS_SQLSelect.HasCompanyRegNumber(mssqlConnection, Sinputcode);
                            if (resultSet.next()){
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        infostate.setText("* Деякі дані вже використовуються");
                                        textcode.setTextColor(getResources().getColor(R.color.red_note));
                                    }
                                }); enter_err++;
                            }

                            if (enter_err == 0) {
                                int workTimeID = MS_SQLInsert.AddWorkTime(mssqlConnection,
                                        finalStimeStartW_enter, finalStimeEndW_enter,
                                        finalStimeStartN_enter,finalStimeEndN_enter, true
                                ); if (workTimeID == -1)
                                    throw new SmallException(0, "* Помилка виконання запиту");

                                int workDaysID = MS_SQLInsert.AddWorkDays(mssqlConnection,
                                        day1, day2, day3, day4, day5, day6, day7, true
                                ); if (workDaysID == -1) {
                                    MS_SQLDelete.DelWorkTimeCO(mssqlConnection, workTimeID, true);
                                    throw new SmallException(0, "* Помилка виконання запиту");
                                };

                                int CompanyID = MS_SQLInsert.AddCompany(mssqlConnection,
                                        Sinputcompanyname, Sinputadres, Sinputspec, Sinputemail,
                                        Sinputtel, Sinputcode, workDaysID, workTimeID
                                ); if (CompanyID == -1) {
                                    MS_SQLDelete.DelWorkTimeCO(mssqlConnection, workTimeID, true);
                                    MS_SQLDelete.DelWorkDaysCO(mssqlConnection, workTimeID, true);
                                    throw new SmallException(0, "* Помилка виконання запиту");
                                };

                                data.ChangeCompany(Sinputemail); data.SaveData();

                                vibrator.vibrate(50);
                                Intent intent = new Intent(A_R_Company.this, A_R_User.class);
                                startActivity(intent);
                                finish();
                            }
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
                        } catch (SmallException e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    infostate.setText(e.getErrorMessage());
                                }
                            });
                            return;
                        }
                    }
                }).start();
            } else infostate.setText("* Деякі поля заповнено некоректно");
            return;
        });
    }
}