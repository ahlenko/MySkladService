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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myskladservice.AM_Login;
import com.example.myskladservice.R;
import com.example.myskladservice.processing.checkers.InputChecker;
import com.example.myskladservice.processing.database.MS_SQLDelete;
import com.example.myskladservice.processing.database.MS_SQLError;
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
import java.util.ArrayList;
import java.util.regex.Pattern;

public class A_R_Company extends AppCompatActivity {
    @Override public void onBackPressed() {
        DialogsViewer.twoButtonDialog(
                this,  new Intent(A_R_Company.this, AM_Login.class), this,
                getString(R.string.cancel), getString(R.string.confirm_cancel_reg), getString(R.string.dialog_confirm), getString(R.string.dialog_discard), 7
        );
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.b1_reg_company);
        Vibrator vibrator = (Vibrator)  getSystemService(Context.VIBRATOR_SERVICE);
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        Intent two_btn_intent = new Intent(A_R_Company.this, A_R_Company.class);
        ArrayList<CheckBox> checkboxes = new ArrayList<>();
        ArrayList<TextView> textViews = new ArrayList<>();
        AppWorkData data = new AppWorkData(this);
        AppCompatActivity activity = this;
        Context context = this;

        ImageButton next_reg_btn = findViewById(R.id.button_continuereg);
        EditText inputcompanyname = findViewById(R.id.inputcompanyname);
        EditText timeStartW_enter = findViewById(R.id.timeStartW_enter);
        EditText timeStartN_enter = findViewById(R.id.timeStartN_enter);
        EditText timeEndW_enter = findViewById(R.id.timeEndW_enter);
        EditText timeEndN_enter = findViewById(R.id.timeEndN_enter);
        EditText inputemail_2 = findViewById(R.id.inputemail_2);
        EditText inputcode_2 = findViewById(R.id.inputcode_2);
        EditText inputtel_2 = findViewById(R.id.inputtel_2);
        EditText inputadres = findViewById(R.id.inputadres);
        EditText inputspec = findViewById(R.id.inputspec);

        TextView noworkdaystitle = findViewById(R.id.noworkdaystitle);
        TextView workdaystitle = findViewById(R.id.workdaystitle);
        TextView textadress = findViewById(R.id.textadress);
        TextView textemail = findViewById(R.id.textemail_2);
        TextView infostate = findViewById(R.id.infostate);
        TextView textcode = findViewById(R.id.textcode_2);
        TextView textspec = findViewById(R.id.textspec);
        TextView texttel = findViewById(R.id.texttel_2);
        TextView textname = findViewById(R.id.textname);

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

        int i = 0; for (CheckBox ch : checkboxes) { final int index = i;
            ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) textViews.get(index).setTextColor(getColor(R.color.fonts_color_blc));
                    else textViews.get(index).setTextColor(getColor(R.color.akcent_purple));}
            }); i++;
        }

        next_reg_btn.setOnClickListener(enter -> { infostate.setText("");
            noworkdaystitle.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            workdaystitle.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textadress.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textemail.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textname.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textspec.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            textcode.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            texttel.setTextColor(getResources().getColor(R.color.fonts_color_blc));

            String Sinputcompanyname = inputcompanyname.getText().toString().trim();
            String StimeStartW_enter = timeStartW_enter.getText().toString().trim();
            String StimeStartN_enter = timeStartN_enter.getText().toString().trim();
            String StimeEndW_enter = timeEndW_enter.getText().toString().trim();
            String StimeEndN_enter = timeEndN_enter.getText().toString().trim();
            String Sinputemail = inputemail_2.getText().toString().trim();
            String Sinputcode = inputcode_2.getText().toString().trim();
            String Sinputadres = inputadres.getText().toString().trim();
            String Sinputspec = inputspec.getText().toString().trim();
            String Sinputtel = inputtel_2.getText().toString().trim();

            ArrayList<Boolean> days = new ArrayList<>(); int enter_err = 0;
            for (CheckBox day_ch : checkboxes) days.add(day_ch.isChecked());

            if (InputChecker.isNotCSize(Sinputcompanyname, textname, 25, this)) enter_err++;
            if (InputChecker.isNotTime(StimeStartN_enter, noworkdaystitle, this)) enter_err++;
            if (InputChecker.isNotCSize(Sinputadres, textadress, 80, this)) enter_err++;
            if (InputChecker.isNotTime(StimeEndN_enter, noworkdaystitle, this)) enter_err++;
            if (InputChecker.isNotEmail(Sinputemail, textemail, 35, this)) enter_err++;
            if (InputChecker.isNotTime(StimeStartW_enter, workdaystitle, this)) enter_err++;
            if (InputChecker.isNotCSize(Sinputspec, textspec, 40, this)) enter_err++;
            if (InputChecker.isNotTime(StimeEndW_enter, workdaystitle, this)) enter_err++;
            if (InputChecker.isNotCSize(Sinputcode, textcode, 8, this)) enter_err++;
            if (InputChecker.isNotPhone(Sinputtel, texttel, this)) enter_err++;

            if (enter_err == 0) {
                StimeStartW_enter += ":00"; String finalStimeStartW_enter = StimeStartW_enter;
                StimeStartN_enter += ":00"; String finalStimeStartN_enter = StimeStartN_enter;
                StimeEndW_enter += ":00"; String finalStimeEndW_enter = StimeEndW_enter;
                StimeEndN_enter += ":00"; String finalStimeEndN_enter = StimeEndN_enter;
                new Thread(new Runnable() {
                    public void run() {
                        boolean code = false, tel = false, email = false; try {
                            MS_SQLConnector msc = MS_SQLConnector.getConect();
                            Connection mssqlConnection = msc.connection;
                            ResultSet resultSet = MS_SQLSelect.UsedCompanyData(mssqlConnection,
                                    Sinputtel, Integer.parseInt(Sinputcode), Sinputemail);

                            if (resultSet.getString("email") != null) email = true;
                            if (resultSet.getString("phnumber") != null) tel = true;
                            if (resultSet.getString("reg_number") != null) code = true;
                            if (tel || email || code) throw new SmallException(0, getString(R.string.pol_is_using));

                            int workTimeID = MS_SQLInsert.AddWorkTime(mssqlConnection,
                                    finalStimeStartW_enter, finalStimeEndW_enter,
                                    finalStimeStartN_enter,finalStimeEndN_enter, true
                            ); if (workTimeID == -1){
                                throw new SmallException(0, getString(R.string.pol_sql_error));
                            } int workDaysID = MS_SQLInsert.AddWorkDays(mssqlConnection,
                                    days.get(0), days.get(1), days.get(2), days.get(3),
                                    days.get(4), days.get(5), days.get(6), true
                            ); if (workDaysID == -1) {
                                MS_SQLDelete.DelWorkTimeCO(mssqlConnection, workTimeID, true);
                                throw new SmallException(0, getString(R.string.pol_sql_error));
                            }; int CompanyID = MS_SQLInsert.AddCompany(mssqlConnection,
                                    Sinputcompanyname, Sinputadres, Sinputspec, Sinputemail,
                                    Sinputtel, Sinputcode, workDaysID, workTimeID
                            ); if (CompanyID == -1) {
                                MS_SQLDelete.DelWorkTimeCO(mssqlConnection, workTimeID, true);
                                MS_SQLDelete.DelWorkDaysCO(mssqlConnection, workTimeID, true);
                                throw new SmallException(0, getString(R.string.pol_sql_error));
                            }; data.ChangeCompany(Sinputemail); data.SaveData();
                            runOnUiThread(new Runnable() {
                                public void run() { vibrator.vibrate(50);
                                    Intent intent = new Intent(A_R_Company.this, A_R_User.class);
                                    startActivity(intent); finish();
                                }
                            });
                        } catch (SQLException e) {
                            MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                        } catch (SmallException e) {
                            boolean finalTel = tel, finalEmail = email, finalCode = code;
                            runOnUiThread(new Runnable() {
                                public void run() { infostate.setText(e.getErrorMessage());
                                    if(finalTel) texttel.setTextColor(getResources().getColor(R.color.red_note));
                                    if(finalEmail) textemail.setTextColor(getResources().getColor(R.color.red_note));
                                    if(finalCode) textcode.setTextColor(getResources().getColor(R.color.red_note));
                                }
                            });
                        }
                    }
                }).start();
            } else infostate.setText(R.string.pol_is_incorect);
        });
    }
}