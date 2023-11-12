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
import com.example.myskladservice.processing.database.MS_SQLError;
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
    @Override public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Intent intent = new Intent(A_C_Checking.this, A_T_Checking.class);
        vibrator.vibrate(50); startActivity(intent); finish();
    }

    private void enableAllImageButtons(ArrayList<View> View_s) {
        for (View view : View_s) {
            ImageButton btn = view.findViewById(R.id.button_checkcur);
            view.setAlpha(0.4f); if (btn != null)  btn.setEnabled(true);}
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.f7_checking);

        Intent two_btn_intent = new Intent(A_C_Checking.this, A_C_Checking.class);
        AtomicInteger chesed_id = new AtomicInteger(); chesed_id.set(1);
        AtomicInteger allItemsChecked = new AtomicInteger(0);
        AppTableChecker checker = new AppTableChecker(this);
        AtomicInteger allCounter = new AtomicInteger(0);
        ArrayList<Integer> prod_ids = new ArrayList<>();
        AppWorkData data = new AppWorkData(this);
        ArrayList<String> codes = new ArrayList<>();
        ArrayList<View> View_s = new ArrayList<View>();
        AppCompatActivity activity = this;
        boolean dasChecked = false;
        Context context = this;

        ImageButton nextItem = findViewById(R.id.button_nextitem);
        ImageButton confirm = findViewById(R.id.button_confirm);
        LinearLayout TableView = findViewById(R.id.TableView);
        ImageButton btn_back = findViewById(R.id.button_beck);
        ImageButton minus = findViewById(R.id.button_minus);
        TextView infostate = findViewById(R.id.infostate);
        TextView text_info = findViewById(R.id.text_info);
        ImageButton plus = findViewById(R.id.button_plus);
        confirm.setEnabled(false); confirm.setAlpha(0.7F);
        minus.setEnabled(false); minus.setAlpha(0.7F);

        if (!checker.GetPrivace()){ plus.setEnabled(false);
            plus.setAlpha(0.7f); nextItem.setEnabled(false);
            nextItem.setAlpha(0.7f); dasChecked = true;}

        PrintTask.PrintTaskCount(activity, context, two_btn_intent);

        View.OnClickListener tableLister = new View.OnClickListener() {
            @Override public void onClick(View view) { int id = view.getId();
                enableAllImageButtons(View_s); chesed_id.set(id);
                View temp = View_s.get(id-1); temp.setAlpha(1f);
                TextView count = temp.findViewById(R.id.view_countorig);
                TextView count_fact = temp.findViewById(R.id.view_countfact);
                String str = count_fact.getText() + " / " + count.getText();
                text_info.setText(str);
            }
        };

        boolean finalDasChecked = dasChecked; new Thread(new Runnable() {
            @Override public void run() {
                int countInt = 0; try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet; if (finalDasChecked)
                         resultSet = MS_SQLSelect.ReadProducts(mssqlConnection, data.getCompany());
                    else resultSet = MS_SQLSelect.ReadCheckingInfo(mssqlConnection, checker.GetChecker());

                    int i = 1; while (resultSet.next()) {
                        View temp = getLayoutInflater().inflate(R.layout.template_cur_checking,
                                TableView, false);
                        TextView count_fact = temp.findViewById(R.id.view_countfact);
                        ImageButton button = temp.findViewById(R.id.button_checkcur);
                        TextView count = temp.findViewById(R.id.view_countorig);
                        ImageView image = temp.findViewById(R.id.photo_img);
                        TextView name = temp.findViewById(R.id.view_name);
                        TextView code = temp.findViewById(R.id.view_code);
                        temp.setAlpha(0.4F); if (i == 1) {
                            temp.setAlpha(1F); countInt = resultSet.getInt("count");
                        } button.setId(i); i++; button.setOnClickListener(tableLister);
                        codes.add(resultSet.getString("code"));
                        name.setText(resultSet.getString("name"));
                        code.setText(resultSet.getString("code"));
                        count.setText(resultSet.getString("count"));
                        prod_ids.add(resultSet.getInt("product_id"));
                        byte[] imageBytes = resultSet.getBytes("image");
                        if (finalDasChecked)
                            count_fact.setText(resultSet.getString("count_old"));
                        boolean isEmpty = true; for (byte b : imageBytes)
                            if (b != 0) { isEmpty = false; break;}
                        if (!isEmpty) { Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes,
                                0, imageBytes.length); image.setImageBitmap(bitmap); }
                        View_s.add(temp);
                    } int finalCountInt = countInt;
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            String str = "-- / " + finalCountInt; text_info.setText(str);
                            TableView.removeAllViews(); if (View_s.isEmpty()) {
                                infostate.setText(R.string.checking_list_empty);
                                plus.setAlpha(0.7f); plus.setEnabled(false);
                                nextItem.setAlpha(0.7f); nextItem.setEnabled(false);
                            } else for (View userView : View_s) TableView.addView(userView);
                        }
                    });
                } catch (SQLException e){
                    MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                }
            }
        }).start();

        minus.setOnClickListener(enter->{
            View temp = View_s.get(chesed_id.get()-1);
            TextView count = temp.findViewById(R.id.view_countorig);
            TextView count_fact = temp.findViewById(R.id.view_countfact);
            int val_count; allCounter.set(allCounter.get()-1);
            if (!count_fact.getText().toString().trim().equals("--")){
                val_count = Integer.parseInt(count_fact.getText().toString());
                if (val_count == 1){ String str = "-- / " + count.getText();
                    text_info.setText(str); count_fact.setText("--");
                    minus.setEnabled(false); minus.setAlpha(0.7f);
                } else { val_count--; String str = val_count + " / " + count.getText();
                    text_info.setText(str); count_fact.setText(String.valueOf(val_count));
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
                val_count+=5; String str = val_count + " / " + count.getText();
                text_info.setText(str); count_fact.setText(String.valueOf(val_count));
            } else { String str = "5 / " + count.getText();
                minus.setEnabled(true); minus.setAlpha(1f);
                text_info.setText(str); count_fact.setText("5"); }
        });

        nextItem.setOnClickListener(enter->{ View temp = View_s.get(chesed_id.get()-1);
            TextView count_fact = temp.findViewById(R.id.view_countfact);
            TextView count = temp.findViewById(R.id.view_countorig);
            CheckBox checked = temp.findViewById(R.id.cur_state); checked.setChecked(true);
            if (!count.getText().toString().trim().equals(count_fact.getText().toString().trim()))
                count_fact.setTextColor(getResources().getColor(R.color.red_note));
            else count_fact.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            allItemsChecked.set(allItemsChecked.get()+1);
            if (allItemsChecked.get() == View_s.size()) confirm.setEnabled(true); confirm.setAlpha(1F);
        });

        confirm.setOnClickListener(enter->{
            new Thread(new Runnable() {
                public void run() {
                    try {
                        MS_SQLConnector msc = MS_SQLConnector.getConect();
                        Connection mssqlConnection = msc.connection;

                        int checking_id = MS_SQLInsert.AddChecking(mssqlConnection,
                                data.getCompany(), data.getUserLogin(), 4, allCounter.get());
                        if (checking_id == -1) throw new SQLException();

                        int i = 0; for (View temp: View_s){
                            TextView count_fact = temp.findViewById(R.id.view_countfact);
                            TextView count = temp.findViewById(R.id.view_countorig);
                            int ATCount = Integer.parseInt(count.getText().toString()); int ATCount_fact;
                            if (count_fact.getText().toString().equals("--")) ATCount_fact = 0;
                            else ATCount_fact = Integer.parseInt(count_fact.getText().toString());
                            if (MS_SQLInsert.AddCheckingInfo(mssqlConnection, data.getCompany(), checking_id,
                                    prod_ids.get(i), ATCount_fact, ATCount) == -1){
                                MS_SQLDelete.DelChecking(mssqlConnection, checking_id); throw  new SQLException();
                            } MS_SQLUpdate.UPDPosition(mssqlConnection, ATCount_fact, prod_ids.get(i), 0); i++;
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, R.string.checking_list_end, Toast.LENGTH_SHORT).show();
                                Intent intent= new Intent(A_C_Checking.this, A_T_Checking.class);
                                vibrator.vibrate(50); startActivity(intent); finish();
                            }
                        });
                    } catch (SQLException e) {
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