package com.example.myskladservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myskladservice.processing.checkers.InputChecker;
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.exception.SmallException;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.screens.chaise.A_S_Login;
import com.example.myskladservice.screens.chaise.A_S_Menu;
import com.example.myskladservice.screens.chaise.A_S_Menu_N;
import com.example.myskladservice.screens.register.A_R_Company;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AM_Login extends AppCompatActivity {
    @Override
    public void onBackPressed() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.a1_login_first);
        AppWorkData data = new AppWorkData(this);
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        Intent two_btn_intent = new Intent(AM_Login.this, AM_Login.class);
        AppCompatActivity activity = this;
        Context context = this;

        if (data.getStateClear()) {
            TextView text_email = findViewById(R.id.TextEmail);
            TextView text_login = findViewById(R.id.TextLogin);
            TextView text_pass = findViewById(R.id.TextPassword);
            EditText edit_email = findViewById(R.id.EnterEmail);
            EditText edit_login = findViewById(R.id.EnterLogin);
            EditText edit_pass = findViewById(R.id.EnterPassword);
            ImageButton enter_btn = findViewById(R.id.button_enter);

            enter_btn.setOnClickListener(enter -> {
                text_email.setText(R.string.email_text);
                text_email.setTextColor(getColor(R.color.fonts_color_blc));
                text_login.setText(R.string.log_text);
                text_login.setTextColor(getColor(R.color.fonts_color_blc));
                text_pass.setText(R.string.pass_text);
                text_pass.setTextColor(getColor(R.color.fonts_color_blc));

                String email = edit_email.getText().toString().trim();
                String login = edit_login.getText().toString().trim();
                String pass = edit_pass.getText().toString().trim();

                int enter_err = 0;

                if (InputChecker.isNotEmail(email, text_email, 35, this)) {
                    text_email.setText(R.string.non_format_email); enter_err++; }
                if (InputChecker.isNotEmail(login, text_login, 35, this)) {
                    text_login.setText(R.string.non_format_login); enter_err++; }
                if (InputChecker.isNotPassword(pass, text_pass, this)) {
                    text_pass.setText(R.string.non_current_password); enter_err++; }

                if (enter_err == 0) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                MS_SQLConnector msc = MS_SQLConnector.getConect();
                                Connection mssqlConnection = msc.connection;
                                ResultSet resultSet = MS_SQLSelect.IsCorrectLoginOP(
                                        mssqlConnection, email, login);

                                if (!resultSet.isBeforeFirst())
                                    throw new SmallException(0, String.valueOf(R.string.non_reg_email));

                                resultSet.next(); if (resultSet.getString("login") == null)
                                    throw new SmallException(1, getString(R.string.non_current_login));

                                if (!resultSet.getString("password").equals(pass))
                                    throw new SmallException(2, getString(R.string.non_current_password));
                                data.FirstEnter(resultSet.getBoolean("fullacess"), email, login, pass);

                                msc.disconnect();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Intent intent; vibrator.vibrate(50);
                                        if (data.getUserType())
                                            intent = new Intent(AM_Login.this, A_S_Menu.class);
                                        else intent = new Intent(AM_Login.this, A_S_Menu_N.class);
                                        startActivity(intent); finish();
                                    }
                                }); return;
                            } catch (SQLException e) {
                                MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                            } catch (SmallException e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        switch (e.getErrorCode()) {
                                            case 0:
                                                text_email.setText(e.getErrorMessage());
                                                text_email.setTextColor(getResources().getColor(R.color.red_note));
                                                break;
                                            case 1:
                                                text_login.setText(e.getErrorMessage());
                                                text_login.setTextColor(getResources().getColor(R.color.red_note));
                                                break;
                                            case 2:
                                                text_pass.setText(e.getErrorMessage());
                                                text_pass.setTextColor(getResources().getColor(R.color.red_note));
                                                break;
                                        }
                                    }
                                });
                            }
                        }
                    }).start();
                } return;
            });

            Button reg_btn = findViewById(R.id.RegBTN);
            reg_btn.setOnClickListener(enter -> {
                vibrator.vibrate(50);
                Intent intent = new Intent(this, A_R_Company.class);
                startActivity(intent); finish();
            });

        } else {
            Intent intent = new Intent(this, A_S_Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent); finish();
        }
    }
}