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
        class TaskPrint extends AsyncTask<Void, Void, Void> {
            private int count = 0;
            protected Void doInBackground(Void... params) {
                try { MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    count = MS_SQLSelect.ReadNotifyCount(
                            mssqlConnection, data.getCompany(), data.getUserLogin());
                    msc.disconnect();
                } catch (SQLException e) {
                    MS_SQLError.ErrorOnUIThread(context, intent, activity);
                } return null;
            }
            protected void onPostExecute(Void result) {
                if (count != 0){
                    notify_count.setText(String.valueOf(count));
                    ring_notify.setVisibility(View.VISIBLE);
                    notify_count.setVisibility(View.VISIBLE);
                }
            }
        }

        TaskPrint myTask = new TaskPrint(); myTask.execute();
    }
}
