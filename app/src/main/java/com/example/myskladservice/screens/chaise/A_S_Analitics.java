package com.example.myskladservice.screens.chaise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.screens.table.A_T_Packing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class A_S_Analitics extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this);
        Intent intent; vibrator.vibrate(50);
        if (data.getUserType()) intent = new Intent(A_S_Analitics.this, A_S_Menu.class);
        else intent = new Intent(A_S_Analitics.this, A_S_Menu_N.class);
        startActivity(intent); finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.d8_analitics);

        Intent two_btn_intent = new Intent(A_S_Analitics.this, A_S_Analitics.class);
        AppCompatActivity activity = this;
        Context context = this;

        ImageButton btn_back = findViewById(R.id.button_beck);

        PrintTask.PrintTaskCount(activity, context, two_btn_intent);

        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50); onBackPressed();
        });
    }
}