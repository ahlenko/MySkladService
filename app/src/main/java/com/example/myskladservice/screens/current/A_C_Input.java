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
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLInsert;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.database.MS_SQLUpdate;
import com.example.myskladservice.processing.datastruct.UserData;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.processing.tasker.TaskInterface;
import com.example.myskladservice.screens.table.A_T_Input;
import com.example.myskladservice.screens.table.A_T_Packing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class A_C_Input extends AppCompatActivity {
    @Override public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Intent intent = new Intent(A_C_Input.this, A_T_Input.class);
        vibrator.vibrate(50); startActivity(intent); finish();
    }

    private void enableAllImageButtons(ArrayList<View> View_s) {
        for (View view : View_s) {
            ImageView border = view.findViewById(R.id.cur_border);
            if (border != null) border.setAlpha(0f);}
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.f4_input);

        Intent two_btn_intent = new Intent(A_C_Input.this, A_C_Input.class);
        AtomicInteger chesed_id = new AtomicInteger(0); chesed_id.set(1);
        AtomicInteger allItemCounter = new AtomicInteger(0);
        AppTableChecker checker = new AppTableChecker(this);
        AtomicInteger allCounter = new AtomicInteger(0);
        AtomicInteger orderCount = new AtomicInteger(0);
        AppWorkData data = new AppWorkData(this);
        ArrayList<Integer> prod_ids = new ArrayList<>();
        ArrayList<View> View_s = new ArrayList<View>();
        ArrayList<String> codes = new ArrayList<>();
        AppCompatActivity activity = this;
        Context context = this;

        ImageButton btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setEnabled(false);btn_confirm.setAlpha(0.7f);
        LinearLayout TableView = findViewById(R.id.TableView);
        ImageButton btn_back = findViewById(R.id.button_beck);
        ImageButton btn_print = findViewById(R.id.btn_print);
        btn_print.setEnabled(false);btn_print.setAlpha(0.7f);
        TextView text_info = findViewById(R.id.text_info);
        TextView infostate = findViewById(R.id.infostate);

        PrintTask.PrintTaskCount(activity, context, two_btn_intent);

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            private Handler mHandler; private Runnable mRunnable;
            @Override public boolean onTouch(View view, MotionEvent motionEvent) {
                int id = view.getId(); View temp = View_s.get(id-1);
                ImageView border = temp.findViewById(R.id.cur_border);
                CheckBox checked = temp.findViewById(R.id.cur_state);
                TextView count = temp.findViewById(R.id.view_count);
                String strCount = count.getText().toString().trim();
                int index = strCount.indexOf("/");
                String result = strCount.substring(index + 1);

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mHandler = new Handler(); mRunnable = new Runnable() {
                            @Override public void run() {
                                if (id == chesed_id.get() && !checker.GetPrivace() && !checked.isChecked()){
                                    allItemCounter.set(allItemCounter.get()+Integer.parseInt(result));
                                    String str = allItemCounter.get() + " / " + orderCount.get();
                                    text_info.setText(str); str = "x " + result + "/" + result;
                                    count.setText(str); checked.setChecked(true);
                                    allCounter.set(allCounter.get()+1);
                                    if (allCounter.get() == View_s.size()){
                                        btn_print.setEnabled(true); btn_print.setAlpha(1f);
                                    } vibrator.vibrate(50);
                                }
                            }
                        }; mHandler.postDelayed(mRunnable, 150); break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler != null && mRunnable != null) {
                            mHandler.removeCallbacks(mRunnable);
                            chesed_id.set(id); border.setAlpha(1f);
                            enableAllImageButtons(View_s);
                        } break;
                } return true;
            }
        };


        Thread scanner = new Thread(new Runnable() {
            @Override public void run() {

            }
        });

        new Thread(new Runnable() {
            @Override public void run() {
                int OrderCode = 0; try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet = MS_SQLSelect.ReadAllAddition(mssqlConnection, checker.GetChecker());;

                    int i = 1; while (resultSet.next()) {
                        if (i==1){ if(resultSet.getInt("state") != 0 ) checker.ChangePrivace(true);
                            OrderCode = resultSet.getInt("sum_count");
                        } View temp = getLayoutInflater().inflate(R.layout.template_cur_input,
                                TableView, false);
                        ImageButton button = temp.findViewById(R.id.button_checkcur);
                        ImageView border = temp.findViewById(R.id.cur_border);
                        CheckBox checked = temp.findViewById(R.id.cur_state);
                        border.setAlpha(0F); if (i == 1) border.setAlpha(1F);
                        TextView count = temp.findViewById(R.id.view_count);
                        TextView name = temp.findViewById(R.id.view_name);

                        name.setText(resultSet.getString("name"));
                        codes.add(resultSet.getString("code"));
                        prod_ids.add(resultSet.getInt("id"));

                        String countStr = String.valueOf(resultSet.getInt("count")); if (checker.GetPrivace()) {
                            border.setAlpha(0f);
                            String str = "x " + countStr + "/" + countStr; count.setText(str);
                        } else {String str = "x 0/" + countStr; count.setText(str); checked.setChecked(false);
                        }; button.setOnTouchListener(touchListener);
                        button.setId(i); i++; View_s.add(temp);
                    }
                } catch (SQLException e) {
                    MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                }
                int finalOrderCode = OrderCode; runOnUiThread(new Runnable() {
                    public void run() { TableView.removeAllViews();
                        String str = "0 / " + String.valueOf(finalOrderCode);
                        text_info.setText(str); orderCount.set(finalOrderCode);
                        if (View_s.isEmpty()) infostate.setText(R.string.input_list_empty);
                        else { for (View userView : View_s) TableView.addView(userView); scanner.start();}}

                });
            }
        }).start();

        btn_confirm.setOnClickListener(enter->{
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        MS_SQLConnector msc = MS_SQLConnector.getConect(); Connection mssqlConnection = msc.connection;
                        MS_SQLUpdate.UPDAdditionOrOrdersArriveInfo(mssqlConnection,  data.getCompany(),
                                data.getUserLogin(), 2, checker.GetChecker(), "Addition");

                        int i = 0; for(View temp : View_s){ TextView count = temp.findViewById(R.id.view_count);
                            String strCount = count.getText().toString().trim(); int index = strCount.indexOf("/");
                            String result = strCount.substring(index + 1);
                            MS_SQLUpdate.UPDPosition(mssqlConnection, prod_ids.get(i), Integer.parseInt(result), 1);
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, R.string.input_list_end, Toast.LENGTH_SHORT).show();
                                Intent intent= new Intent(A_C_Input.this, A_T_Input.class);
                                vibrator.vibrate(50); startActivity(intent); finish();
                            }
                        });
                    } catch (SQLException e){
                        MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                    }
                }
            }).start();
        });

        btn_print.setOnClickListener(enter->{ vibrator.vibrate(50);
            btn_print.setEnabled(false);  btn_print.setAlpha(0.7f);
            btn_confirm.setEnabled(true); btn_confirm.setAlpha(1f);
            String massage = getString(R.string.print_doc_about_input);
            Toast.makeText(context, massage, Toast.LENGTH_LONG).show();
        });

        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50); onBackPressed();
        });
    }
}