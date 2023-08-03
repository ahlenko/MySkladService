package com.example.myskladservice.screens.table;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.TaskInterface;
import com.example.myskladservice.screens.chaise.A_S_Menu;
import com.example.myskladservice.screens.chaise.A_S_Menu_N;
import com.example.myskladservice.screens.current.A_C_Checking;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

public class A_T_Checking extends AppCompatActivity {
    private ArrayList<Integer> ID_s = new ArrayList<Integer>();
    @Override
    public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this);
        vibrator.vibrate(50);
        Intent intent;
        if (data.getUserType()) intent = new Intent(A_T_Checking.this, A_S_Menu.class);
        else intent = new Intent(A_T_Checking.this, A_S_Menu_N.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d7_checking_table);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        AppTableChecker checker = new AppTableChecker(this);
        AppWorkData data = new AppWorkData(this);

        AppCompatActivity activity = this;
        Context context = this;


        ArrayList<View> View_s = new ArrayList<View>();

        LinearLayout TableView = findViewById(R.id.TableView);
        TextView text_info = findViewById(R.id.text_info);
        TextView infostate = findViewById(R.id.infostate);

        LocalDate currentDate = null;

        String[] month = getResources().getStringArray(R.array.month);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
            String dateS = month[currentDate.getMonthValue()-1] + " " + currentDate.getYear();
            text_info.setText(dateS);
        }

        AtomicReference<LocalDate> newDate = new AtomicReference<>(currentDate);

        class CheckingPrint extends AsyncTask<Void, Void, Void> {

            private TaskInterface listener;

            public CheckingPrint(TaskInterface listener) {
                this.listener = listener;
            }

            protected Void doInBackground(Void... params) {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet;

                    resultSet = MS_SQLSelect.CompanyManager(mssqlConnection, data.getCompany());
                    resultSet.next(); int company = resultSet.getInt("id");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        resultSet = MS_SQLSelect.ReadChecking(mssqlConnection, company, newDate.get());
                    }

                    int iter = 0;
                    while (resultSet.next()) {
                        View temp = getLayoutInflater().inflate(R.layout.template_view_checking, TableView, false);
                        TextView date = temp.findViewById(R.id.view_date);
                        TextView performer = temp.findViewById(R.id.view_performer);
                        TextView count = temp.findViewById(R.id.view_count);
                        TextView state = temp.findViewById(R.id.view_state);
                        ImageView state_indicator = temp.findViewById(R.id.state_indicator);
                        ImageButton button = temp.findViewById(R.id.button_checkingselect);

                        Date dateFromDB = resultSet.getDate("date");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                        String date_S = dateFormat.format(dateFromDB);
                        performer.setText(MS_SQLSelect.ReadUserName(mssqlConnection, resultSet.getInt("performer_id")));
                        int count_c = resultSet.getInt("sum_count");
                        if (count_c == 0) count.setText("___");
                        else count.setText(String.valueOf(count_c));

                        switch (resultSet.getInt("state")){
                            case 0:
                                state_indicator.setImageResource(R.drawable.ellipse_gray);
                                state.setText("Доручено");
                                break;
                            case 1:
                                state_indicator.setImageResource(R.drawable.ellipse_yellow);
                                state.setText("В роботі");
                                button.setEnabled(false);
                                break;
                            case 2:
                                state_indicator.setImageResource(R.drawable.ellipse_red);
                                state.setText("Не завершено");
                                break;
                            case 3:
                                state_indicator.setImageResource(R.drawable.ellipse_green);
                                state.setText("Облік завершено"); temp.setAlpha(0.7f);
                                break;
                        }

                        ID_s.add(resultSet.getInt("id"));
                        date.setText(date_S);
                        button.setId(iter);
                        View_s.add(temp);
                        iter++;
                    }
                } catch (SQLException e) {
                    DialogsViewer.twoButtonDialog(
                            context, new Intent(A_T_Checking.this, A_T_Checking.class),
                            activity, "Помилка", "Невдале підключення до бази даних.\n" +
                                    "Повторіть спробу або вийдіть:", "Вийти", "Повторити", 1
                    );
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (listener != null) {
                    listener.onTaskComplete();
                }
            }
        }

        CheckingPrint checkingPrint = new CheckingPrint(new TaskInterface() {
            @Override
            public void onTaskComplete() {
                TableView.removeAllViews();
                if (View_s.isEmpty()) {
                    infostate.setText("Дані про проведення обліку відсутні");
                } else{
                    Iterator<View> iterator = View_s.iterator();
                    while (iterator.hasNext()) {
                        View userView = iterator.next();
                        TableView.addView(userView);
                    }
                }

            }
        });

        checkingPrint.execute();

        ImageButton button_next = findViewById(R.id.button_next);
        button_next.setEnabled(false); button_next.setAlpha(0.4f);
        LocalDate finalCurrentDate = currentDate;
        button_next.setOnClickListener (enter -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                newDate.set(newDate.get().plusMonths(1));}
            if (finalCurrentDate.equals(newDate.get())) {
                button_next.setEnabled(false); button_next.setAlpha(0.4f);
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String dateS = month[newDate.get().getMonthValue()-1] + " " + newDate.get().getYear();
                text_info.setText(dateS);
            } vibrator.vibrate(50);
            View_s.clear(); ID_s.clear(); infostate.setText("");
            CheckingPrint checkingPrint2 = new CheckingPrint(new TaskInterface() {
                @Override
                public void onTaskComplete() {
                    TableView.removeAllViews();
                    if (View_s.isEmpty()) {
                        infostate.setText("Дані про проведення обліку відсутні");
                    } else{
                        Iterator<View> iterator = View_s.iterator();
                        while (iterator.hasNext()) {
                            View userView = iterator.next();
                            TableView.addView(userView);
                        }
                    }

                }
            });

            checkingPrint2.execute();
        });

        ImageButton button_prew = findViewById(R.id.button_prew);
        button_prew.setOnClickListener (enter -> {
            button_next.setEnabled(true); button_next.setAlpha(1f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                newDate.set(newDate.get().minusMonths(1));}
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String dateS = month[newDate.get().getMonthValue()-1] + " " + newDate.get().getYear();
                text_info.setText(dateS);
            } vibrator.vibrate(50);
            View_s.clear(); ID_s.clear(); infostate.setText("");
            CheckingPrint checkingPrint2 = new CheckingPrint(new TaskInterface() {
                @Override
                public void onTaskComplete() {
                    TableView.removeAllViews();
                    if (View_s.isEmpty()) {
                        infostate.setText("Дані про проведення обліку відсутні");
                    } else{
                        Iterator<View> iterator = View_s.iterator();
                        while (iterator.hasNext()) {
                            View userView = iterator.next();
                            TableView.addView(userView);
                        }
                    }

                }
            });

            checkingPrint2.execute();
        });

        ImageButton button_newchecking = findViewById(R.id.button_newchecking);
        button_newchecking.setOnClickListener (enter -> {
            checker.ChangePrivace(true);
            vibrator.vibrate(50);
            Intent intent = new Intent(this, A_C_Checking.class);
            startActivity(intent);
            finish();
        });

        ImageButton btn_back = findViewById(R.id.button_beck);
        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            onBackPressed();
        });
    }

    public void Button(View view) {
        AppTableChecker check = new AppTableChecker(this);
        check.ChangeChecker(ID_s.get(view.getId()));
        check.ChangePrivace(false);
        Intent intent = new Intent(this, A_C_Checking.class);
        startActivity(intent);
        finish();
    }
}