package com.example.myskladservice.screens.table;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.processing.tasker.TaskInterface;
import com.example.myskladservice.screens.chaise.A_S_Menu;
import com.example.myskladservice.screens.chaise.A_S_Menu_N;
import com.example.myskladservice.screens.current.A_C_Checking;
import com.example.myskladservice.screens.current.A_C_Packing;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class A_T_Packing extends AppCompatActivity {
    private ArrayList<Integer> ID_s = new ArrayList<Integer>();
    @Override public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this); Intent intent; vibrator.vibrate(50);
        if (data.getUserType()) intent = new Intent(A_T_Packing.this, A_S_Menu.class);
        else intent = new Intent(A_T_Packing.this, A_S_Menu_N.class);
        startActivity(intent); finish();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.d2_packing_table);

        Intent two_btn_intent = new Intent(A_T_Packing.this, A_T_Packing.class);
        String[] month = getResources().getStringArray(R.array.month2);
        AppWorkData data = new AppWorkData(this);
        ArrayList<View> View_s = new ArrayList<View>();
        AppCompatActivity activity = this;
        LocalDate currentDate = null;
        Context context = this;

        ImageButton button_next = findViewById(R.id.button_next);
        button_next.setEnabled(false); button_next.setAlpha(0.4f);
        ImageButton button_prew = findViewById(R.id.button_prew);
        LinearLayout TableView = findViewById(R.id.TableView);
        ImageButton btn_back = findViewById(R.id.button_beck);
        TextView text_info = findViewById(R.id.text_info);
        TextView infostate = findViewById(R.id.infostate);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
            String dateS = currentDate.getDayOfMonth() + " " +
                    month[currentDate.getMonthValue()-1] + " " +
                    currentDate.getYear();
            text_info.setText(dateS); }

        AtomicReference<LocalDate> newDate = new AtomicReference<>(currentDate);
        PrintTask.PrintTaskCount(activity, context, two_btn_intent);
        ExecutorService executor = Executors.newFixedThreadPool(1);

        Thread PackingPrint = new Thread(new Runnable() {
            @Override public void run() {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet = MS_SQLSelect.ReadTableInfo(mssqlConnection,
                            data.getCompany(), newDate.get(), "Orders");
                    int iter = 0; while (resultSet.next()) {
                        View temp = getLayoutInflater().inflate(R.layout.template_view_packing,
                                TableView, false);
                        ImageButton packBtn = temp.findViewById(R.id.button_packing);
                        ImageButton printBtn = temp.findViewById(R.id.button_print);
                        TextView ttncode = temp.findViewById(R.id.view_ttncode);
                        TextView number = temp.findViewById(R.id.view_number);
                        TextView count = temp.findViewById(R.id.view_count);
                        TextView state = temp.findViewById(R.id.view_state);

                        count.setText(String.valueOf(resultSet.getInt("sum_count")));
                        number.setText(String.valueOf(resultSet.getInt("code")));
                        ttncode.setText(resultSet.getString("ttn_code"));

                        switch (resultSet.getInt("state")){
                            case 0: state.setText(R.string.packing_state_0);
                                printBtn.setAlpha(0.4f); break;
                            case 1: state.setText(R.string.packing_state_1);
                                packBtn.setAlpha(0.4f);
                                printBtn.setAlpha(0.4f); break;
                            case 2: state.setText(R.string.packing_state_2);
                                temp.setAlpha(0.4f); break;
                        } ID_s.add(resultSet.getInt("id"));
                        packBtn.setId(iter); printBtn.setId(iter);
                        View_s.add(temp); iter++;
                    }
                } catch (SQLException e) {
                    MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (View_s.isEmpty()) infostate.setText(R.string.packing_not_search);
                        else for (View userView : View_s) TableView.addView(userView);
                    }
                });
            }
        }); executor.execute(PackingPrint);

        LocalDate finalCurrentDate = currentDate;
        button_next.setOnClickListener (enter -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                newDate.set(newDate.get().plusDays(1));
            if (finalCurrentDate.equals(newDate.get())){
                button_next.setEnabled(false); button_next.setAlpha(0.4f);}
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String dateS = newDate.get().getDayOfMonth() + " "
                        + month[newDate.get().getMonthValue()-1] +
                        " " + newDate.get().getYear();
                text_info.setText(dateS);
            } vibrator.vibrate(50); View_s.clear(); ID_s.clear();
            infostate.setText(""); executor.execute(PackingPrint);
        });

        button_prew.setOnClickListener (enter -> {
            button_next.setEnabled(true); button_next.setAlpha(1f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                newDate.set(newDate.get().minusDays(1));}
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String dateS = newDate.get().getDayOfMonth() + " "
                        + month[newDate.get().getMonthValue()-1] +
                        " " + newDate.get().getYear();
                text_info.setText(dateS);
            } vibrator.vibrate(50); View_s.clear(); ID_s.clear();
            infostate.setText(""); executor.execute(PackingPrint);
        });

        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50); onBackPressed();
        });
    }

    public void ButtonPack(View view) {
        AppTableChecker check = new AppTableChecker(this);
        check.ChangeChecker(ID_s.get(view.getId()));
        check.ChangePrivace(false);
        Intent intent = new Intent(this, A_C_Packing.class);
        startActivity(intent); finish();
    }

    public void ButtonPrint(View view) {
        AppTableChecker check = new AppTableChecker(this);
        check.ChangeChecker(ID_s.get(view.getId()));
        check.ChangePrivace(false);
    }
}