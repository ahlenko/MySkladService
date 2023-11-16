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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.datastruct.UserData;
import com.example.myskladservice.processing.datastruct.Worktime;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.screens.table.A_T_Users;
import com.example.myskladservice.screens.table.A_T_Users_N;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

public class A_C_UserView extends AppCompatActivity {
    @Override public void onBackPressed() {}

    @Override protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.f1_userread);
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        Intent two_btn_intent = new Intent(A_C_UserView.this, A_C_UserView.class);
        AppTableChecker check = new AppTableChecker(this);
        ArrayList<CheckBox> checkboxes = new ArrayList<>();
        ArrayList<TextView> textViews = new ArrayList<>();
        AppWorkData data = new AppWorkData(this);
        AppCompatActivity activity = this;
        Context context = this;

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
                    if (isChecked) textViews.get(index).setTextColor(getColor(R.color.fonts_color_wht));
                    else textViews.get(index).setTextColor(getColor(R.color.akcent_purple));}
            }); i++;
        }

        new Thread(new Runnable() {
            @Override public void run() {
                try { MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    MS_SQLSelect.ReadUser(mssqlConnection,
                            check.GetChecker());
                } catch (SQLException e) {
                    MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                } runOnUiThread(new Runnable() {@Override public void run()
                    {UserData.SetData(activity, checkboxes, false);}
                });
            }
        }).start();

        ImageButton button_adduser = findViewById(R.id.button_returnview);
        button_adduser.setOnClickListener(enter -> { vibrator.vibrate(50); Intent intent;
            if (data.getUserType()) intent = new Intent(A_C_UserView.this, A_T_Users.class);
            else intent = new Intent(A_C_UserView.this, A_T_Users_N.class);
            startActivity(intent); finish();
        });

        ImageButton btn_back = findViewById(R.id.button_beck);
        btn_back.setOnClickListener(enter -> { vibrator.vibrate(50); Intent intent;
            if (data.getUserType()) intent = new Intent(A_C_UserView.this, A_T_Users.class);
            else intent = new Intent(A_C_UserView.this, A_T_Users_N.class);
            startActivity(intent); finish();
        });
    }
}