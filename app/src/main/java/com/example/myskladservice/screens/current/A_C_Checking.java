package com.example.myskladservice.screens.current;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
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
import com.example.myskladservice.processing.database.MS_SQLDelete;
import com.example.myskladservice.processing.database.MS_SQLInsert;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.database.MS_SQLUpdate;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.processing.tasker.TaskInterface;
import com.example.myskladservice.screens.chaise.A_S_Analitics;
import com.example.myskladservice.screens.chaise.A_S_Login;
import com.example.myskladservice.screens.chaise.A_S_Menu;
import com.example.myskladservice.screens.chaise.A_S_Menu_N;
import com.example.myskladservice.screens.register.A_R_Company;
import com.example.myskladservice.screens.table.A_T_Checking;
import com.example.myskladservice.screens.table.A_T_Input;
import com.example.myskladservice.screens.table.A_T_Output;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class A_C_Checking extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this);
        vibrator.vibrate(50);
        Intent intent = new Intent(A_C_Checking.this, A_T_Checking.class);
        startActivity(intent);
        finish();
    }

    private void enableAllImageButtons(ArrayList<View> View_s) {
        for (View view : View_s) {
            ImageButton btn = view.findViewById(R.id.button_checkcur);
            view.setAlpha(0.4f); if (btn != null) {
                btn.setEnabled(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f7_checking);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Intent two_btn_intent = new Intent(A_C_Checking.this, A_C_Checking.class);
        AppWorkData data = new AppWorkData(this);
        AppCompatActivity activity = this;
        Context context = this;

        ImageButton btn_back = findViewById(R.id.button_beck);
        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            onBackPressed();
        });

        PrintTask.PrintTaskCount(activity, context, two_btn_intent);

        AppTableChecker checker = new AppTableChecker(this);

        LinearLayout TableView = findViewById(R.id.TableView);
        TextView infostate = findViewById(R.id.infostate);

        ImageButton nextItem = findViewById(R.id.button_nextitem);
        ImageButton minus = findViewById(R.id.button_minus);
        ImageButton plus = findViewById(R.id.button_plus);
        ImageButton confirm = findViewById(R.id.button_confirm);
        confirm.setEnabled(false); confirm.setAlpha(0.7F);
        minus.setEnabled(false); minus.setAlpha(0.7F);

        ArrayList<View> View_s = new ArrayList<View>();
        AtomicInteger chesed_id = new AtomicInteger(); chesed_id.set(1);
        TextView text_info = findViewById(R.id.text_info);

        AtomicInteger allCounter = new AtomicInteger(0);
        ArrayList<String> codes = new ArrayList<>();

        minus.setOnClickListener(enter->{
            View temp = View_s.get(chesed_id.get()-1);
            TextView count = temp.findViewById(R.id.view_countorig);
            TextView count_fact = temp.findViewById(R.id.view_countfact);
            int val_count;
            allCounter.set(allCounter.get()-1);
            if (!count_fact.getText().toString().trim().equals("--")){
                val_count = Integer.parseInt(count_fact.getText().toString());
                if (val_count == 1){
                    minus.setEnabled(false); minus.setAlpha(0.7f);
                    text_info.setText("-- / " + count.getText());
                    count_fact.setText("--");
                } else {
                    val_count--;
                    text_info.setText(String.valueOf(val_count) + " / " + count.getText());
                    count_fact.setText(String.valueOf(val_count));
                }
            }
        });

        plus.setOnClickListener(enter->{
            View temp = View_s.get(chesed_id.get()-1);
            TextView count = temp.findViewById(R.id.view_countorig);
            TextView count_fact = temp.findViewById(R.id.view_countfact);
            allCounter.set(allCounter.get()+5);
            if (!count_fact.getText().toString().trim().equals("--")){
                int val_count = Integer.parseInt(count_fact.getText().toString());
                val_count+=5;
                text_info.setText(String.valueOf(val_count) + " / " + count.getText());
                count_fact.setText(String.valueOf(val_count));
            } else {
                minus.setEnabled(true); minus.setAlpha(1f);
                text_info.setText("5 / " + count.getText());
                count_fact.setText("5");
            }
        });

        AtomicInteger allItemsChecked = new AtomicInteger(0);

        ArrayList<Integer> prod_ids = new ArrayList<>();

        nextItem.setOnClickListener(enter->{
            View temp = View_s.get(chesed_id.get()-1);
            CheckBox checked = temp.findViewById(R.id.cur_state);
            TextView count = temp.findViewById(R.id.view_countorig);
            TextView count_fact = temp.findViewById(R.id.view_countfact);
            checked.setChecked(true);
            if (count.getText().toString().trim() != count_fact.getText().toString().trim()){
                count_fact.setTextColor(getResources().getColor(R.color.red_note));
            } else count_fact.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            allItemsChecked.set(allItemsChecked.get()+1);
            if (allItemsChecked.get() == View_s.size()){
                confirm.setEnabled(true); confirm.setAlpha(1F);
            }
        });

        View.OnClickListener tableLister = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                enableAllImageButtons(View_s); chesed_id.set(id);
                View temp = View_s.get(id-1);
                temp.setAlpha(1f);
                TextView count = temp.findViewById(R.id.view_countorig);
                TextView count_fact = temp.findViewById(R.id.view_countfact);
                text_info.setText(count_fact.getText() + " / " + count.getText());
            }
        };

        confirm.setOnClickListener(enter->{
            new Thread(new Runnable() {
                public void run() {
                    try {
                        MS_SQLConnector msc = MS_SQLConnector.getConect();
                        Connection mssqlConnection = msc.connection;
                        ResultSet resultSet;

                        resultSet = MS_SQLSelect.HasCompanyEmail(mssqlConnection, data.getCompany());
                        resultSet.next(); int company = resultSet.getInt("id");
                        resultSet = MS_SQLSelect.HasUserLogin(mssqlConnection, data.getUserLogin(), company);
                        resultSet.next(); int user_id = resultSet.getInt("id");

                        int checking_id = MS_SQLInsert.AddChecking(mssqlConnection, company, user_id, 4, allCounter.get());
                        if (checking_id == -1) throw new SQLException();

                        int i = 0; for (View temp: View_s){
                            TextView count = temp.findViewById(R.id.view_countorig);
                            TextView count_fact = temp.findViewById(R.id.view_countfact);
                            int ATCount = Integer.parseInt(count.getText().toString()); int ATCount_fact;
                            if (count_fact.getText().toString() == "--"){ATCount_fact = 0;}
                            else { ATCount_fact = Integer.parseInt(count_fact.getText().toString());}
                            if (MS_SQLInsert.AddCheckingInfo(mssqlConnection, company, checking_id, prod_ids.get(i), ATCount_fact, ATCount) == -1){
                                MS_SQLDelete.DelCheckingInfo(mssqlConnection, checking_id);
                                MS_SQLDelete.DelChecking(mssqlConnection, checking_id);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(context, "Виникла помилка створення запису", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                throw  new SQLException();
                            }
                            MS_SQLUpdate.UPDPosition(mssqlConnection, ATCount_fact, prod_ids.get(i)); i++;
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "Облік товарів завершено", Toast.LENGTH_SHORT).show();
                                Intent intent= new Intent(A_C_Checking.this, A_T_Checking.class);
                                vibrator.vibrate(50);
                                startActivity(intent);
                                finish();
                            }
                        });
                        return;

                    } catch (SQLException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                DialogsViewer.twoButtonDialog(
                                        context, new Intent(A_C_Checking.this, A_C_Checking.class), activity, "Помилка",
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

        class CheckingPrint extends AsyncTask<Void, Void, Void> {

            private TaskInterface listener; int countInt;

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

                    resultSet = MS_SQLSelect.ReadProducts(mssqlConnection, company); int i = 1;


                    while (resultSet.next()) {
                        View temp = getLayoutInflater().inflate(R.layout.template_cur_checking, TableView, false);
                        ImageView image = temp.findViewById(R.id.photo_img);
                        TextView name = temp.findViewById(R.id.view_name);
                        TextView code = temp.findViewById(R.id.view_code);
                        TextView count = temp.findViewById(R.id.view_countorig);
                        ImageButton button = temp.findViewById(R.id.button_checkcur);

                        temp.setAlpha(0.4F); if (i==1) {
                            temp.setAlpha(1F); //button.setEnabled(false);
                            countInt = resultSet.getInt("count");
                        }

                        prod_ids.add(resultSet.getInt("id"));
                        codes.add(resultSet.getString("code"));

                        button.setId(i); i++;
                        button.setOnClickListener(tableLister);
                        name.setText(resultSet.getString("name"));
                        code.setText(resultSet.getString("code"));
                        count.setText(resultSet.getString("count"));

                        byte[] imageBytes = resultSet.getBytes("image");
                        boolean isEmpty = true;
                        for (byte b : imageBytes) {
                            if (b != 0) {
                                isEmpty = false;
                                break;
                            }
                        }
                        if (!isEmpty) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            image.setImageBitmap(bitmap);
                        }
                        View_s.add(temp);
                    }
                } catch (SQLException e) {
                    DialogsViewer.twoButtonDialog(
                            context, new Intent(A_C_Checking.this, A_C_Checking.class),
                            activity, "Помилка", "Невдале підключення до бази даних.\n" +
                                    "Повторіть спробу або вийдіть:", "Вийти", "Повторити", 1
                    );
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                text_info.setText("-- / " + countInt);
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
                    infostate.setText("Список товарів для обліку порожній");
                    plus.setAlpha(0.7f); plus.setEnabled(false);
                    nextItem.setAlpha(0.7f); nextItem.setEnabled(false);
                } else {
                    Iterator<View> iterator = View_s.iterator();
                    while (iterator.hasNext()) {
                        View userView = iterator.next();
                        TableView.addView(userView);
                    }
                }
            }
        });



        class CheckingPrintCh extends AsyncTask<Void, Void, Void> {

            private TaskInterface listener; int countInt;

            int countFact;

            public CheckingPrintCh(TaskInterface listener) {
                this.listener = listener;
            }

            protected Void doInBackground(Void... params) {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;

                    ResultSet resultSet = MS_SQLSelect.ReadCheckingInfo(mssqlConnection, checker.GetChecker());
                    ResultSet resultSetCheckers; int i = 1;

                    while (resultSet.next()) {
                        View temp = getLayoutInflater().inflate(R.layout.template_cur_checking, TableView, false);
                        resultSetCheckers = MS_SQLSelect.ReadProduct(mssqlConnection, resultSet.getInt("product_id"), 1);
                        resultSetCheckers.next();
                        ImageView image = temp.findViewById(R.id.photo_img);
                        TextView name = temp.findViewById(R.id.view_name);
                        TextView code = temp.findViewById(R.id.view_code);
                        TextView count = temp.findViewById(R.id.view_countorig);
                        TextView count_fact = temp.findViewById(R.id.view_countfact);
                        ImageButton button = temp.findViewById(R.id.button_checkcur);

                        temp.setAlpha(0.4F); if (i==1) {
                            temp.setAlpha(1F); //button.setEnabled(false);
                            countInt = resultSet.getInt("count");
                            countFact = resultSet.getInt("count_old");
                        }

                        prod_ids.add(resultSet.getInt("product_id"));
                        codes.add(resultSetCheckers.getString("code"));

                        button.setId(i); i++;
                        button.setOnClickListener(tableLister);
                        name.setText(resultSetCheckers.getString("name"));
                        code.setText(resultSetCheckers.getString("code"));
                        count.setText(resultSet.getString("count"));
                        count_fact.setText(resultSet.getString("count_old"));

                        CheckBox checked = temp.findViewById(R.id.cur_state);
                        checked.setChecked(true);

                        byte[] imageBytes = resultSetCheckers.getBytes("image");
                        boolean isEmpty = true;
                        for (byte b : imageBytes) {
                            if (b != 0) {
                                isEmpty = false;
                                break;
                            }
                        }
                        if (!isEmpty) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            image.setImageBitmap(bitmap);
                        }
                        View_s.add(temp);
                    }
                } catch (SQLException e) {
                    DialogsViewer.twoButtonDialog(
                            context, new Intent(A_C_Checking.this, A_C_Checking.class),
                            activity, "Помилка", "Невдале підключення до бази даних.\n" +
                                    "Повторіть спробу або вийдіть:", "Вийти", "Повторити", 1
                    );
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                text_info.setText("-- / " + countInt);
                if (listener != null) {
                    listener.onTaskComplete();
                }
            }
        }

        CheckingPrintCh checkingPrintCh = new CheckingPrintCh(new TaskInterface() {
            @Override
            public void onTaskComplete() {
                TableView.removeAllViews();
                if (View_s.isEmpty()) {
                    infostate.setText("Список товарів для обліку порожній");
                    plus.setAlpha(0.7f); plus.setEnabled(false);
                    nextItem.setAlpha(0.7f); nextItem.setEnabled(false);
                } else {
                    Iterator<View> iterator = View_s.iterator();
                    while (iterator.hasNext()) {
                        View userView = iterator.next();
                        TableView.addView(userView);
                    }
                }
            }
        });

        if (checker.GetPrivace()) checkingPrint.execute();
        else {
            plus.setEnabled(false); plus.setAlpha(0.7f);
            nextItem.setEnabled(false); nextItem.setAlpha(0.7f);
            checkingPrintCh.execute();
        }
    }
}