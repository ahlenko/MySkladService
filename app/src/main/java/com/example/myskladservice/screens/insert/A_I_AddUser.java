package com.example.myskladservice.screens.insert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myskladservice.AM_Login;
import com.example.myskladservice.R;
import com.example.myskladservice.processing.checkers.InputChecker;
import com.example.myskladservice.processing.database.MS_SQLDelete;
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLInsert;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.database.MS_SQLUpdate;
import com.example.myskladservice.processing.datastruct.UserData;
import com.example.myskladservice.processing.datastruct.Worktime;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.exception.SmallException;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.screens.chaise.A_S_Menu;
import com.example.myskladservice.screens.register.A_R_User;
import com.example.myskladservice.screens.table.A_T_Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class A_I_AddUser extends AppCompatActivity {
    @Override public void onBackPressed() {}
    @Override protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.f1_adduser);
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        Intent two_btn_intent = new Intent(A_I_AddUser.this, A_I_AddUser.class);
        ArrayList<CheckBox> checkboxes = new ArrayList<>();
        ArrayList<TextView> textViews = new ArrayList<>();
        AppWorkData data = new AppWorkData(this);
        AppCompatActivity activity = this;
        Context context = this;

        ImageButton button_adduser = findViewById(R.id.button_adduser);
        TextView noworkdaystitle = findViewById(R.id.noworkdaystitle);
        TextView workdaystitle = findViewById(R.id.workdaystitle);
        TextView textworkstate = findViewById(R.id.textworkstate);
        TextView textworkplace = findViewById(R.id.textworkplace);
        TextView textpassword = findViewById(R.id.textpassword4);
        TextView textlastname = findViewById(R.id.textlastname);
        TextView textsurname = findViewById(R.id.textsurname);
        ImageButton btn_back = findViewById(R.id.button_beck);
        TextView textlogin = findViewById(R.id.textlogin4);
        TextView infostate = findViewById(R.id.infostate);
        TextView textname = findViewById(R.id.textname);
        TextView texttel = findViewById(R.id.texttel4);

        EditText timeStartW_enter = findViewById(R.id.timeStartW_enter);
        EditText timeStartN_enter = findViewById(R.id.timeStartN_enter);
        EditText timeEndW_enter = findViewById(R.id.timeEndW_enter);
        EditText timeEndN_enter = findViewById(R.id.timeEndN_enter);
        EditText inputworkstate = findViewById(R.id.inputworkstate);
        EditText inputworkplace = findViewById(R.id.inputworkplace);
        EditText inputpassword = findViewById(R.id.inputpassword4);
        EditText inputlastname = findViewById(R.id.inputlastname);
        CheckBox useracess = findViewById(R.id.select_fullacess);
        EditText inputsurname = findViewById(R.id.inputsurname);
        EditText inputlogin = findViewById(R.id.inputlogin4);
        EditText inputname = findViewById(R.id.inputname);
        EditText inputtel = findViewById(R.id.inputtel4);

        checkboxes.add(findViewById(R.id.day1_checker));
        checkboxes.add(findViewById(R.id.day2_checker));
        checkboxes.add(findViewById(R.id.day3_checker));
        checkboxes.add(findViewById(R.id.day4_checker));
        checkboxes.add(findViewById(R.id.day5_checker));
        checkboxes.add(findViewById(R.id.day6_checker));
        checkboxes.add(findViewById(R.id.day7_checker));

        textViews.add(findViewById(R.id.day1_text));
        textViews.add(findViewById(R.id.day2_text));
        textViews.add(findViewById(R.id.day3_text));
        textViews.add(findViewById(R.id.day4_text));
        textViews.add(findViewById(R.id.day5_text));
        textViews.add(findViewById(R.id.day6_text));
        textViews.add(findViewById(R.id.day7_text));

        PrintTask.PrintTaskCount(activity, context, two_btn_intent);

        int i = 0; for (CheckBox ch : checkboxes) { final int index = i;
            ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) textViews.get(index).setTextColor(getColor(R.color.fonts_color_blc));
                    else textViews.get(index).setTextColor(getColor(R.color.akcent_purple));}
            }); i++;
        }

        new Thread(new Runnable() {
            @Override public void run() {
                try { MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    MS_SQLSelect.WorkTimetable(mssqlConnection, data.getCompany(), -1);
                } catch (SQLException e) {
                    MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                } runOnUiThread(new Runnable() {@Override public void run()
                    {Worktime.SetData(activity, checkboxes);}
                });
            }
        }).start();

        button_adduser.setOnClickListener (enter -> {
            noworkdaystitle.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textworkstate.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textworkplace.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            workdaystitle.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textpassword.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textlastname.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textsurname.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textlogin.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textname.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            texttel.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            infostate.setText("");

            String StimeStartW_enter = timeStartW_enter.getText().toString().trim();
            String StimeStartN_enter = timeStartN_enter.getText().toString().trim();
            String StimeEndW_enter = timeEndW_enter.getText().toString().trim();
            String StimeEndN_enter = timeEndN_enter.getText().toString().trim();
            String Sinputworkstate = inputworkstate.getText().toString().trim();
            String Sinputworkplace = inputworkplace.getText().toString().trim();
            String Sinputlastname = inputlastname.getText().toString().trim();
            String Sinputpassword = inputpassword.getText().toString().trim();
            String Sinputsurname = inputsurname.getText().toString().trim();
            String Sinputlogin = inputlogin.getText().toString().trim();
            String Sinputname = inputname.getText().toString().trim();
            String Sinputtel = inputtel.getText().toString().trim();

            ArrayList <Boolean> days = new ArrayList<>();
            for (CheckBox day_ch : checkboxes) days.add(day_ch.isChecked());
            boolean acess = useracess.isChecked(); int enter_err = 0;

            if(InputChecker.isNotCSize(Sinputworkstate, textworkstate, 25, this)) enter_err++;
            if(InputChecker.isNotCSize(Sinputworkplace, textworkplace, 25, this)) enter_err++;
            if(InputChecker.isNotCSize(Sinputlastname, textlastname, 25, this)) enter_err++;
            if(InputChecker.isNotCSize(Sinputsurname, textsurname, 25, this)) enter_err++;
            if(InputChecker.isNotTime(StimeStartN_enter, noworkdaystitle,  this)) enter_err++;
            if(InputChecker.isNotTime(StimeEndN_enter, noworkdaystitle,  this)) enter_err++;
            if(InputChecker.isNotTime(StimeStartW_enter, workdaystitle,  this)) enter_err++;
            if(InputChecker.isNotEmail(Sinputlogin, textlogin, 35,  this)) enter_err++;
            if(InputChecker.isNotPassword(Sinputpassword, textpassword,  this)) enter_err++;
            if(InputChecker.isNotTime(StimeEndW_enter, workdaystitle,  this)) enter_err++;
            if(InputChecker.isNotCSize(Sinputname, textname, 25, this)) enter_err++;
            if(InputChecker.isNotPhone(Sinputtel, texttel,  this)) enter_err++;

            if (enter_err == 0) {
                StimeStartW_enter += ":00"; String finalStimeStartW_enter = StimeStartW_enter;
                StimeStartN_enter += ":00"; String finalStimeStartN_enter = StimeStartN_enter;
                StimeEndW_enter += ":00"; String finalStimeEndW_enter = StimeEndW_enter;
                StimeEndN_enter += ":00";  String finalStimeEndN_enter = StimeEndN_enter;

                new Thread(new Runnable() {
                    public void run() {
                        boolean ph_num = false, log = false; try {
                            MS_SQLConnector msc = MS_SQLConnector.getConect();
                            Connection mssqlConnection = msc.connection;
                            ResultSet resultSet = MS_SQLSelect.UsedEmployeeData(mssqlConnection,
                                    data.getCompany(), Sinputlogin,Sinputtel); resultSet.next();
                            if (resultSet.getString("phnumber") != null) ph_num = true;
                            if (resultSet.getString("login") != null) log = true;

                            if (ph_num || log) throw new SmallException(0, getString(R.string.pol_is_using));
                            int company = resultSet.getInt("id");

                            int workDaysID = MS_SQLInsert.AddWorkDays(mssqlConnection,
                                    days.get(0), days.get(1), days.get(2), days.get(3),
                                    days.get(4), days.get(5), days.get(6), false
                            ); if (workDaysID == -1) {
                                throw new SmallException(0, getString(R.string.pol_sql_error));
                            }; int workTimeID = MS_SQLInsert.AddWorkTime(mssqlConnection,
                                    workDaysID, finalStimeStartW_enter, finalStimeEndW_enter,
                                    finalStimeStartN_enter,finalStimeEndN_enter, false
                            ); if (workTimeID == -1){
                                MS_SQLDelete.DelWorkDaysCO(mssqlConnection, workDaysID, false);
                                throw new SmallException(0, getString(R.string.pol_sql_error));
                            } int ManagerID = MS_SQLInsert.AddUser(mssqlConnection,
                                    Sinputsurname, Sinputname, Sinputlastname, Sinputworkstate,
                                    Sinputworkplace, Sinputtel, Sinputlogin, Sinputpassword,
                                    acess, workDaysID, workTimeID, company
                            ); if (ManagerID == -1) {
                                MS_SQLDelete.DelWorkTimeCO(mssqlConnection, workDaysID, false);
                                throw new SmallException(0, getString(R.string.pol_sql_error));
                            };
                            runOnUiThread(new Runnable() {
                                public void run() {vibrator.vibrate(50);
                                    Intent intent = new Intent(A_I_AddUser.this, A_T_Users.class);
                                    Toast.makeText(context, getString(R.string.employee_added), Toast.LENGTH_SHORT).show();
                                    startActivity(intent); finish();
                                }
                            });
                        } catch (SQLException e) {
                            MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                        } catch (SmallException e) {
                            boolean finalLog = log, finalPh_num = ph_num; runOnUiThread(new Runnable() {
                                public void run() { infostate.setText(e.getErrorMessage());
                                    if(finalPh_num) texttel.setTextColor(getResources().getColor(R.color.red_note));
                                    if(finalLog) textlogin.setTextColor(getResources().getColor(R.color.red_note));
                                }
                            });
                        }
                    }
                }).start();
            } else infostate.setText(R.string.pol_is_incorect);
        });

        btn_back.setOnClickListener (enter -> {vibrator.vibrate(50);
            Intent intent = new Intent(A_I_AddUser.this, A_T_Users.class);
            startActivity(intent); finish();
        });
    }
}