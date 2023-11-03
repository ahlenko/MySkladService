package com.example.myskladservice.processing.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.dialogs.DialogsViewer;

import java.net.ContentHandlerFactory;

public class MS_SQLError {
    public static void ErrorOnUIThread(Context context, Intent intent, AppCompatActivity activity){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                DialogsViewer.twoButtonDialog(
                        context,  intent, activity, activity.getString(R.string.problem),
                        activity.getString(R.string.non_connected), activity.getString(R.string.exit),
                        activity.getString(R.string.repeate), 1
                );
            }
        }); return;
    }
}
