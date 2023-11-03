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
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.datastruct.UserData;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.processing.tasker.TaskInterface;
import com.example.myskladservice.screens.chaise.A_S_Analitics;
import com.example.myskladservice.screens.chaise.A_S_Login;
import com.example.myskladservice.screens.chaise.A_S_Menu;
import com.example.myskladservice.screens.chaise.A_S_Menu_N;
import com.example.myskladservice.screens.current.A_C_UserView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class A_T_Users_N extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this);
        Intent intent; vibrator.vibrate(50);
        if (data.getUserType()) intent = new Intent(A_T_Users_N.this, A_S_Menu.class);
        else intent = new Intent(A_T_Users_N.this, A_S_Menu_N.class);
        startActivity(intent); finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.d12_users_non);
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        Intent two_btn_intent = new Intent(A_T_Users_N.this, A_T_Users_N.class);
        AppWorkData data = new AppWorkData(this);
        AppCompatActivity activity = this;
        Context context = this;

        LinearLayout container = findViewById(R.id.TableView);
        ImageButton btn_back = findViewById(R.id.button_beck);
        Deque<View> Users = new ArrayDeque<>();

        PrintTask.PrintTaskCount(activity, context, two_btn_intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet = MS_SQLSelect.ReadEmployeeListAndManager(
                            mssqlConnection, data.getCompany());

                    while (resultSet.next()){
                        View temp = getLayoutInflater().inflate(R.layout.template_view_user,
                                container, false);
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

                        if (!resultSet.getBoolean("onwork"))
                            activity.setImageResource(R.drawable.ellipse_red);
                        if (resultSet.getBoolean("fullacess"))
                            uimage.setImageResource(R.drawable.ico_manager);
                        Users.add(temp);
                    } msc.disconnect();
                } catch (SQLException e) {
                    MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        for (View userView : Users) container.addView(userView);
                    }
                });
            }
        }).start();

        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50); onBackPressed();
        });
    }

    public void Button (View view){
        AppTableChecker check = new AppTableChecker(this);
        check.ChangeChecker(view.getId());Intent intent = new Intent(
                this, A_C_UserView.class);
        startActivity(intent); finish();
    }
}