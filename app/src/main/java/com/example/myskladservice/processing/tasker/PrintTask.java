package com.example.myskladservice.processing.tasker;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.shpreference.AppWorkData;

import java.sql.Connection;
import java.sql.SQLException;

public class PrintTask {
    public static void PrintTaskCount(AppCompatActivity activity, Context context, Intent intent){
        TextView notify_count = activity.findViewById(R.id.notify_count);
        ImageView ring_notify = activity.findViewById(R.id.ring_notify);
        AppWorkData data = new AppWorkData(context);

        new Thread(new Runnable() {
            @Override public void run() {
                int count = 0; try { MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    count = MS_SQLSelect.ReadNotifyCount(
                            mssqlConnection, data.getCompany(), data.getUserLogin());
                } catch (SQLException e) {
                    MS_SQLError.ErrorOnUIThread(context, intent, activity);
                } int finalCount = count; activity.runOnUiThread(new Runnable() {
                    @Override public void run() { if (finalCount != 0){
                            notify_count.setText(String.valueOf(finalCount));
                            ring_notify.setVisibility(View.VISIBLE);
                            notify_count.setVisibility(View.VISIBLE); }
                    }
                });
            }
        }).start();
    }
}
