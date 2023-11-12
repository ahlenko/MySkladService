package com.example.myskladservice.processing.dialogs;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.checkers.InputChecker;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLDelete;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.database.MS_SQLUpdate;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChoiseProcessor {
    public static ListenerProcessor defaultInitialize(){
        ListenerProcessor listener = new ListenerProcessor() {
            @Override public void onPositiveButtonClick() {}
            @Override public void onNegativeButtonClick() {}
        }; return listener;
    }
    public static ListenerProcessor databaseMessage(Intent intent, AppCompatActivity activity){
 /*1*/      ListenerProcessor listener = new ListenerProcessor() {
            @Override public void onPositiveButtonClick() {
                activity.startActivity(intent); activity.finish(); }
            @Override public void onNegativeButtonClick() {
                activity.finish();
            }
        }; return listener;
    }

    public static ListenerProcessor incorectEmail(Intent intent, AppCompatActivity activity, Context context){
 /*3*/  ListenerProcessor listener = new ListenerProcessor() {
            AppWorkData data = new AppWorkData(context);
            @Override public void onPositiveButtonClick() { data.ClearData();
                activity.startActivity(intent); activity.finish();}
            @Override public void onNegativeButtonClick() {
                data.ClearData(); activity.finish();}
        }; return listener;
    }

    public static ListenerProcessor incorectLogin(Intent intent, AppCompatActivity activity, Context context){
 /*2*/  ListenerProcessor listener = new ListenerProcessor() {
            AppWorkData data = new AppWorkData(context);
            @Override public void onPositiveButtonClick() {}
            @Override public void onNegativeButtonClick() { data.ClearData();
                activity.startActivity(intent); activity.finish();
            }
        }; return listener;
    }

    public static ListenerProcessor deleteConfirm(Intent intent, AppCompatActivity activity, Context context){
 /*4*/  ListenerProcessor listener = new ListenerProcessor() {
            AppTableChecker checker = new AppTableChecker(context);
            @Override public void onPositiveButtonClick() {}
            @Override public void onNegativeButtonClick() {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            MS_SQLConnector msc = MS_SQLConnector.getConect();
                            Connection mssqlConnection = msc.connection;
                            MS_SQLDelete.DelUserByID(mssqlConnection, checker.GetChecker());
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(context, R.string.employee_del_suc, Toast.LENGTH_SHORT).show();
                                    activity.startActivity(intent); activity.finish();
                                }
                            });
                        } catch (SQLException e) {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(context, R.string.employee_del_err, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        }; return listener;
    }

    public static ListenerProcessor deleteMainConfirm(Intent intent, AppCompatActivity activity, Context context){
 /*5*/  ListenerProcessor listener = new ListenerProcessor() {
            @Override public void onPositiveButtonClick() {}
            @Override public void onNegativeButtonClick() {}
        }; return listener;
    }

    public static ListenerProcessor deleteRegCompany(Intent intent, AppCompatActivity activity, Context context){
 /*6*/  ListenerProcessor listener = new ListenerProcessor() {
            final AppWorkData data = new AppWorkData(context);
            @Override public void onPositiveButtonClick() {}
            @Override public void onNegativeButtonClick() {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            MS_SQLConnector msc = MS_SQLConnector.getConect();
                            Connection mssqlConnection = msc.connection;
                            MS_SQLDelete.DelRegCompany(mssqlConnection, data.getCompany());
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    activity.startActivity(intent); activity.finish();
                                }
                            });
                        } catch (SQLException e) {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(context, R.string.company_del_err, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        }; return listener;
    }

    public static ListenerProcessor oncanselreg(Intent intent, AppCompatActivity activity, Context context){
 /*7*/  ListenerProcessor listener = new ListenerProcessor() {
            @Override public void onPositiveButtonClick() {}
            @Override public void onNegativeButtonClick() {
                activity.startActivity(intent); activity.finish();}
        }; return listener;
    }
    public static ListenerProcessor onExit(Intent intent, AppCompatActivity activity, Context context){
 /*8*/  ListenerProcessor listener = new ListenerProcessor() {
            final AppWorkData data = new AppWorkData(context);
            @Override public void onPositiveButtonClick() {}
            @Override public void onNegativeButtonClick() { data.ClearData();
                activity.startActivity(intent); activity.finish();}
        }; return listener;
    }

    public static ListenerProcessor dataUpdate(AppCompatActivity activity, Context context, int type, String upd){
 /*9*/  ListenerProcessor listener = new ListenerProcessor() {
            final AppWorkData data = new AppWorkData(context);
            @Override public void onPositiveButtonClick() {}
            @Override public void onNegativeButtonClick() {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            MS_SQLConnector msc = MS_SQLConnector.getConect();
                            Connection mssqlConnection = msc.connection;
                            ResultSet resultSet = MS_SQLSelect.IsCorrectLoginOP( mssqlConnection,
                                    data.getCompany(), data.getUserLogin());resultSet.next();
                            int iduser = resultSet.getInt("id"); switch (type){
                                case 0: data.ChangePassword(upd);
                                    MS_SQLUpdate.UPDUserPassword(mssqlConnection, upd, iduser);
                                    data.SaveData(); break;
                                case 1: MS_SQLUpdate.UPDUserPhone(mssqlConnection, upd, iduser); break;
                                case 2: data.ChangeLogin(upd);
                                    MS_SQLUpdate.UPDUserLogin(mssqlConnection, upd, iduser);
                                    data.SaveData(); break;
                            }
                        } catch (SQLException e) {
                            Toast.makeText(context, R.string.database_err, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
        }; return listener;
    }

    public static ListenerProcessor deletePosition(Intent intent, AppCompatActivity activity, Context context) {
        ListenerProcessor listener = new ListenerProcessor() {
            final AppTableChecker check = new AppTableChecker(context);
            @Override public void onPositiveButtonClick() {}
            @Override public void onNegativeButtonClick() {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            MS_SQLConnector msc = MS_SQLConnector.getConect();
                            Connection mssqlConnection = msc.connection;
                            MS_SQLDelete.DelPos(mssqlConnection, check.GetChecker());
                            activity.runOnUiThread(new Runnable() {
                                public void run() { activity.startActivity(intent); activity.finish();}
                            });
                        } catch (SQLException e) {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(context, R.string.position_del_err,
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        }; return listener;
    }
}
