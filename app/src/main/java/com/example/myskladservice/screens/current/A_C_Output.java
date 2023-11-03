package com.example.myskladservice.screens.current;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLInsert;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.database.MS_SQLUpdate;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.processing.tasker.TaskInterface;
import com.example.myskladservice.screens.table.A_T_Output;
import com.example.myskladservice.screens.table.A_T_Packing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class A_C_Output extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this);
        vibrator.vibrate(50);
        Intent intent = new Intent(A_C_Output.this, A_T_Output.class);
        startActivity(intent);
        finish();
    }

    private void enableAllImageButtons(ArrayList<View> View_s) {
        for (View view : View_s) {
            ImageView border = view.findViewById(R.id.cur_border);
            if (border != null) border.setAlpha(0f);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f3_output);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Intent two_btn_intent = new Intent(A_C_Output.this, A_C_Output.class);
        AppWorkData data = new AppWorkData(this);
        AppCompatActivity activity = this;
        Context context = this;

        PrintTask.PrintTaskCount(activity, context, two_btn_intent);

        AtomicInteger orderCount = new AtomicInteger(0);

        AppTableChecker checker = new AppTableChecker(this);
        LinearLayout TableView = findViewById(R.id.TableView);

        ArrayList<String> codes = new ArrayList<>();
        ArrayList<Integer> prod_ids = new ArrayList<>();
        ArrayList<View> View_s = new ArrayList<View>();

        AtomicInteger chesed_id = new AtomicInteger(0); chesed_id.set(1);
        AtomicInteger allCounter = new AtomicInteger(0);
        AtomicInteger allItemCounter = new AtomicInteger(0);

        ImageButton btn_print = findViewById(R.id.btn_print);
        ImageButton btn_confirm = findViewById(R.id.btn_confirm);
        TextView text_info = findViewById(R.id.text_info);
        TextView infostate = findViewById(R.id.infostate);
        btn_print.setEnabled(false); btn_confirm.setEnabled(false);
        btn_print.setAlpha(0.7f); btn_confirm.setAlpha(0.7f);

        btn_print.setOnClickListener(enter->{
            vibrator.vibrate(50);
            btn_print.setEnabled(false);  btn_print.setAlpha(0.7f);
            btn_confirm.setEnabled(true); btn_confirm.setAlpha(1f);
            String massage = "Друк видаткової документації";
            Toast.makeText(context, massage, Toast.LENGTH_LONG).show();
        });

        btn_confirm.setOnClickListener(enter->{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MS_SQLConnector msc = MS_SQLConnector.getConect();
                        Connection mssqlConnection = msc.connection;
                        ResultSet resultSet;

                        resultSet = MS_SQLSelect.HasCompanyEmail(mssqlConnection, data.getCompany());
                        resultSet.next(); int company = resultSet.getInt("id");
                        resultSet = MS_SQLSelect.HasUserLogin(mssqlConnection, data.getUserLogin(), company);
                        resultSet.next(); int user_id = resultSet.getInt("id");

                        MS_SQLUpdate.UPDOrdersArrive(mssqlConnection, checker.GetChecker(), 2, user_id);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "Видачу замовлень завершено", Toast.LENGTH_SHORT).show();
                                Intent intent= new Intent(A_C_Output.this, A_T_Output.class);
                                vibrator.vibrate(50);
                                startActivity(intent);
                                finish();
                            }
                        });
                        return;

                    } catch (SQLException e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                DialogsViewer.twoButtonDialog(
                                        context, new Intent(A_C_Output.this, A_C_Output.class), activity, "Помилка",
                                        "Невдале підключення до бази даних.\nПовторіть спробу або вийдіть:",
                                        "Вийти", "Повторити", 1
                                );
                            }
                        });
                        return;
                    }
                }
            }).start();
        });

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            private Handler mHandler;
            private Runnable mRunnable;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int id = view.getId();
                View temp = View_s.get(id-1);
                ImageView border = temp.findViewById(R.id.cur_border);
                TextView count = temp.findViewById(R.id.view_count);
                CheckBox checked = temp.findViewById(R.id.cur_state);
                String strCount = count.getText().toString().trim();
                int index = strCount.indexOf(" ");
                String result = strCount.substring(index + 1);

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mHandler = new Handler();
                        mRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (id == chesed_id.get() && !checker.GetPrivace() && !checked.isChecked()){
                                    vibrator.vibrate(50);
                                    allItemCounter.set(allItemCounter.get()+Integer.parseInt(result));
                                    text_info.setText(allItemCounter.get() + " / " + String.valueOf(orderCount.get()));
                                    checked.setChecked(true);
                                    allCounter.set(allCounter.get()+1);
                                    if (allCounter.get() == View_s.size()){
                                        btn_print.setEnabled(true);
                                        btn_print.setAlpha(1f);
                                    }
                                }
                            }
                        };
                        mHandler.postDelayed(mRunnable, 500);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler != null && mRunnable != null) {
                            mHandler.removeCallbacks(mRunnable);
                            chesed_id.set(id);
                            enableAllImageButtons(View_s);
                            border.setAlpha(1f);
                        }
                        break;
                }
                return true;
            }
        };

        ImageButton btn_back = findViewById(R.id.button_beck);
        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            onBackPressed();
        });



        class CheckingPrint extends AsyncTask<Void, Void, Void> {

            private TaskInterface listener; int OrderCode;

            public CheckingPrint(TaskInterface listener) {
                this.listener = listener;
            }

            protected Void doInBackground(Void... params) {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet;

                    resultSet = MS_SQLSelect.ReadOrderArrive(mssqlConnection, checker.GetChecker());
                    resultSet.next(); if(resultSet.getInt("state") != 0 ) checker.ChangePrivace(true);
                    OrderCode = resultSet.getInt("sum_count");
                    resultSet = MS_SQLSelect.ReadOrderArriveElements(mssqlConnection, checker.GetChecker());

                    int i = 1; while (resultSet.next()) {
                        View temp = getLayoutInflater().inflate(R.layout.template_cur_output, TableView, false);
                        TextView receiver = temp.findViewById(R.id.view_receiver);
                        TextView code = temp.findViewById(R.id.title_number);
                        TextView ttn = temp.findViewById(R.id.view_ttncode);
                        TextView count = temp.findViewById(R.id.view_count);
                        CheckBox checked = temp.findViewById(R.id.cur_state);
                        ImageButton button = temp.findViewById(R.id.button_checkcur);
                        ImageView border = temp.findViewById(R.id.cur_border);

                        ResultSet rProd = MS_SQLSelect.GetOrderArriveInfo(mssqlConnection, resultSet.getInt("orders_id"));
                        rProd.next();
                        receiver.setText(rProd.getString("adresser"));
                        prod_ids.add(rProd.getInt("code"));
                        codes.add(rProd.getString("ttn_code"));

                        code.setText(rProd.getString("code"));
                        ttn.setText(rProd.getString("ttn_code"));

                        border.setAlpha(0F); if (i == 1) border.setAlpha(1F);

                        button.setOnTouchListener(touchListener);

                        String countStr = String.valueOf(resultSet.getInt("count"));
                        if (!checker.GetPrivace()) checked.setChecked(false);
                        count.setText("x " + countStr);

                        button.setId(i); i++; View_s.add(temp);
                    }
                } catch (SQLException e) {
                    DialogsViewer.twoButtonDialog(
                            context, new Intent(A_C_Output.this, A_C_Output.class),
                            activity, "Помилка", "Невдале підключення до бази даних.\n" +
                                    "Повторіть спробу або вийдіть:", "Вийти", "Повторити", 1
                    );
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                text_info.setText("0 / " + String.valueOf(OrderCode));
                orderCount.set(OrderCode);
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
                    infostate.setText("Список відправлень до видачі порожній");
                } else {
                    Iterator<View> iterator = View_s.iterator();
                    while (iterator.hasNext()) {
                        View userView = iterator.next();
                        TableView.addView(userView);
                    }
                }
            }
        });

        checkingPrint.execute();
    }


}