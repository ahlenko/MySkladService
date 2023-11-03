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
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.processing.tasker.TaskInterface;
import com.example.myskladservice.screens.table.A_T_Checking;
import com.example.myskladservice.screens.table.A_T_Packing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class A_C_Packing extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(A_C_Packing.this, A_T_Packing.class);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50); startActivity(intent); finish();
    }

    private void enableAllImageButtons(ArrayList<View> View_s) {
        for (View view : View_s) {
            ImageView border = view.findViewById(R.id.cur_border);
            if (border != null) border.setAlpha(0f);
        }
    }

    private static String generateRandomNumber(int digits) {
        StringBuilder stringBuilder = new StringBuilder(digits);
        Random random = new Random(); for (int i = 0; i < digits; i++) {
            int digit = random.nextInt(10); stringBuilder.append(digit);
        } return stringBuilder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.f2_packing);

        AtomicInteger chesed_id = new AtomicInteger(0); chesed_id.set(1);
        AtomicInteger allItemCounter = new AtomicInteger(0);
        AtomicReference<String> TTNCode = new AtomicReference<>();
        AtomicInteger allCounter = new AtomicInteger(0);

        Intent two_btn_intent = new Intent(A_C_Packing.this, A_C_Packing.class);
        AppTableChecker checker = new AppTableChecker(this);
        LinearLayout TableView = findViewById(R.id.TableView);
        AppWorkData data = new AppWorkData(this);
        ArrayList<Integer> prod_ids = new ArrayList<>();
        ArrayList<View> View_s = new ArrayList<View>();
        ArrayList<String> codes = new ArrayList<>();
        AppCompatActivity activity = this;
        Context context = this;

        ImageButton btn_confirm = findViewById(R.id.btn_confirm);
        ImageButton btn_print = findViewById(R.id.btn_nextitem);
        ImageButton btn_back = findViewById(R.id.button_beck);
        TextView text_info = findViewById(R.id.text_info);
        TextView infostate = findViewById(R.id.infostate);

        btn_print.setEnabled(false); btn_confirm.setEnabled(false);
        btn_print.setAlpha(0.7f); btn_confirm.setAlpha(0.7f);

        PrintTask.PrintTaskCount(activity, context, two_btn_intent);

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            private Handler mHandler; private Runnable mRunnable;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int id = view.getId();   View temp = View_s.get(id-1);
                ImageView border = temp.findViewById(R.id.cur_border);
                CheckBox checked = temp.findViewById(R.id.cur_state);
                TextView count = temp.findViewById(R.id.view_count);
                String strCount = count.getText().toString().trim();
                int index = strCount.indexOf("/"); String result =
                        strCount.substring(index + 1);

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mHandler = new Handler();
                        mRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (id == chesed_id.get() && !checker.GetPrivace() && !checked.isChecked()){
                                    String vie = getString(R.string.delimer_s)  +  " " +  result +  "/"  +  result;
                                    count.setText(vie); checked.setChecked(true); vibrator.vibrate(50);
                                    allItemCounter.set(allItemCounter.get()+Integer.parseInt(result));
                                    allCounter.set(allCounter.get()+1);
                                    if (allCounter.get() == View_s.size()) {
                                        btn_print.setEnabled(true); btn_print.setAlpha(1f); }
                                }
                            }
                        }; mHandler.postDelayed(mRunnable, 500); break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler != null && mRunnable != null) {
                            mHandler.removeCallbacks(mRunnable);
                            border.setAlpha(1f); chesed_id.set(id);
                            enableAllImageButtons(View_s);
                        } break;
                } return true;
            }
        };

        class CheckingPrint extends AsyncTask<Void, Void, Void> {

            private TaskInterface listener; int OrderCode;

            public CheckingPrint(TaskInterface listener) {
                this.listener = listener;
            }

            protected Void doInBackground(Void... params) {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet = MS_SQLSelect.ReadOrderDetails(
                            mssqlConnection, checker.GetChecker());
                    resultSet.next(); OrderCode = resultSet.getInt("code");
                    if(resultSet.getInt("state") != 0 ) checker.ChangePrivace(true);
                    resultSet.previous(); int i = 1; while (resultSet.next()) {
                        View temp = getLayoutInflater().inflate(R.layout.template_cur_packing,
                                TableView, false);
                        ImageButton button = temp.findViewById(R.id.button_checkcur);
                        ImageView border = temp.findViewById(R.id.cur_border);
                        CheckBox checked = temp.findViewById(R.id.cur_state);
                        TextView count = temp.findViewById(R.id.view_count);
                        TextView name = temp.findViewById(R.id.view_name);

                        name.setText(resultSet.getString("name"));
                        prod_ids.add(resultSet.getInt("id"));
                        codes.add(resultSet.getString("code"));

                        border.setAlpha(0F); if (i == 1) border.setAlpha(1F);

                        button.setOnTouchListener(touchListener);

                        String countStr = String.valueOf(resultSet.getInt("count"));
                        if (checker.GetPrivace()) {
                            count.setText("x " + countStr + "/" + countStr);
                        } else {count.setText("x 0/" + countStr); checked.setChecked(false);};

                        button.setId(i); i++; View_s.add(temp);
                    }
                } catch (SQLException e) {
                    MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                } return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                text_info.setText(String.valueOf(OrderCode));
                if (listener != null) listener.onTaskComplete();
            }
        }

        CheckingPrint checkingPrint = new CheckingPrint(new TaskInterface() {
            @Override
            public void onTaskComplete() { TableView.removeAllViews();
                for (View userView : View_s) TableView.addView(userView); }
        }); checkingPrint.execute();

        btn_print.setOnClickListener(enter->{ vibrator.vibrate(50);
            btn_print.setEnabled(false);  btn_print.setAlpha(0.7f);
            btn_confirm.setEnabled(true); btn_confirm.setAlpha(1f);
            TTNCode.set(generateRandomNumber(14));
            String massage = "Друк ТТН для замовлення: " +
                    text_info.getText().toString() +
                    "\n Привласнено код: " + TTNCode.get();
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

                        int arrive_id;
                        ResultSet resp = MS_SQLSelect.ArriverAtThatDay(mssqlConnection, company);
                        if (resp.next() && resp.getInt("state") == 0){
                            arrive_id = resp.getInt("id");
                            MS_SQLUpdate.AddArriver(mssqlConnection, arrive_id, resp.getInt("sum_count") + 1);
                        } else{
                            arrive_id = MS_SQLInsert.NewArriverAtThatDay(mssqlConnection, company);
                        }

                        MS_SQLUpdate.UPDPackingInfo(mssqlConnection, checker.GetChecker(), user_id, TTNCode.get(), 2, allItemCounter);
                        int i = 0; for(View temp : View_s){
                            TextView count = temp.findViewById(R.id.view_count);
                            String strCount = count.getText().toString().trim();
                            int index = strCount.indexOf("/");
                            String result = strCount.substring(index + 1);

                            int count_value = MS_SQLSelect.GetProdCountById(mssqlConnection, prod_ids.get(i));
                            MS_SQLUpdate.UPDProdCountById(mssqlConnection, prod_ids.get(i), count_value - Integer.parseInt(result));

                            MS_SQLInsert.NewArriveOrder(mssqlConnection, arrive_id, company, checker.GetChecker(), 1);
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "Складання замовлення завершено", Toast.LENGTH_SHORT).show();
                                Intent intent= new Intent(A_C_Packing.this, A_T_Packing.class);
                                vibrator.vibrate(50); startActivity(intent); finish();
                            }
                        }); return;
                    } catch (SQLException e){
                        MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                    }
                }
            }).start();
        });

        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50); onBackPressed();
        });
    }
}