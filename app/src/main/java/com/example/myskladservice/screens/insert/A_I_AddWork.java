package com.example.myskladservice.screens.insert;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.checkers.InputChecker;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLDelete;
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLInsert;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.database.MS_SQLUpdate;
import com.example.myskladservice.processing.datastruct.TaskData;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.exception.SmallException;
import com.example.myskladservice.processing.shpreference.AppCreateOr;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.screens.table.A_T_Task;
import com.example.myskladservice.screens.table.A_T_Users_N;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class A_I_AddWork extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int AdresserID = 0, CompanyID = 0, PerformerID = 0; String LoginPerf = "";
    ArrayList<String> UserNames = new ArrayList<>();
    ArrayList<Integer> UserId = new ArrayList<>();
    @Override public void onBackPressed() {}
    @Override protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.f5_addwork);

        Intent two_btn_intent = new Intent(A_I_AddWork.this, A_I_AddWork.class);
        AppTableChecker checker = new AppTableChecker(this);
        AppCreateOr create = new AppCreateOr(this);
        AppWorkData data = new AppWorkData(this);
        AppCompatActivity activity = this;
        Context context = this;

        PrintTask.PrintTaskCount(activity, context, two_btn_intent);

        ImageButton btn_back = findViewById(R.id.button_beck);

        TextView infostate = findViewById(R.id.infostate);
        TextView enter_tasktype = findViewById(R.id.enter_tasktype);
        TextView enter_performer = findViewById(R.id.enter_performer);
        TextView button_title = findViewById(R.id.button_title);
        TextView time_start = findViewById(R.id.title_starting_time);
        TextView time_release = findViewById(R.id.title_release_time);
        TextView title_comment = findViewById(R.id.title_comment);
        TextView title_performer = findViewById(R.id.title_performer);
        TextView ch_counter = findViewById(R.id.viewcountchars);

        ImageButton red_btn = findViewById(R.id.button_deletetask);
        ImageButton prp_btn = findViewById(R.id.button_createtask);

        EditText time_start_ent = findViewById(R.id.enter_starting_time);
        EditText time_release_ent = findViewById(R.id.enter_release_time);
        EditText comment = findViewById(R.id.entercomment);

        Spinner chose_enter = findViewById(R.id.select_tasktype);
        Spinner chose_performer = findViewById(R.id.select_performer);

        comment.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = charSequence.length(); String str = length + " / 240";
                if (length <= 240) ch_counter.setTextColor(getResources().getColor(R.color.grey));
                else ch_counter.setTextColor(getResources().getColor(R.color.red_note));
                ch_counter.setText(str);}
            @Override public void afterTextChanged(Editable editable) {}
        });

        if (create.GetCreate()){
            red_btn.setEnabled(false); red_btn.setVisibility(View.INVISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { LocalTime currentTime = LocalTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                time_start_ent.setText(currentTime.format(formatter));}

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.task_type, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            chose_enter.setAdapter(adapter);  chose_enter.setSelection(0);
            enter_tasktype.setText(adapter.getItem(0).toString());
            chose_enter.setOnItemSelectedListener(this);

            class UserSearch extends AsyncTask<Void, Void, Void> {
                @Override protected Void doInBackground(Void... voids) {
                    try{
                        MS_SQLConnector msc = MS_SQLConnector.getConect();
                        Connection mssqlConnection = msc.connection;
                        ResultSet resultSet = MS_SQLSelect.ReadEmployeeListAndManager
                                (mssqlConnection, data.getCompany());
                        while (resultSet.next()) {CompanyID = resultSet.getInt("company_id");
                            if (!Objects.equals(data.getUserLogin(), resultSet.getString("login"))) {
                                String nameUser = resultSet.getString("surname") +
                                        " " + resultSet.getString("name") + " " +
                                        resultSet.getString("lastname");
                                LoginPerf = resultSet.getString("login");
                                nameUser = nameUser.length() > 23 ? nameUser.substring(0, 23) + "..." : nameUser;
                                UserNames.add(nameUser);
                                UserId.add(resultSet.getInt("id"));
                            } else AdresserID = resultSet.getInt("id");
                        }
                    } catch (SQLException e){
                        MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                    } return null;
                }

                protected void onPostExecute(Void result) {
                    if (UserNames.isEmpty()){ enter_performer.setText("...");
                        chose_performer.setEnabled(false); } else {
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context,
                                android.R.layout.simple_spinner_item, UserNames);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        chose_performer.setAdapter(adapter2); chose_performer.setSelection(0);
                        enter_performer.setText(adapter2.getItem(0));
                        chose_performer.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) context);
                    }
                }
            } UserSearch myTask = new UserSearch(); myTask.execute();
        } else {
            time_release_ent.setEnabled(false); chose_performer.setEnabled(false);
            time_start_ent.setEnabled(false);   chose_enter.setEnabled(false);
            comment.setEnabled(false);

            class ReadTask extends AsyncTask<Void, Void, Void> {
                @Override protected Void doInBackground(Void... voids) {
                    try { MS_SQLConnector msc = MS_SQLConnector.getConect();
                        Connection mssqlConnection = msc.connection;
                        MS_SQLSelect.ReadTask(mssqlConnection, checker.GetChecker());
                    } catch (SQLException e) {
                        MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                    } return null;
                };
                protected void onPostExecute(Void result) { TaskData.SetData(activity);}
            } ReadTask myTask = new ReadTask(); myTask.execute();

            if (checker.GetPrivace()){ button_title.setText(R.string.ttl_btn_delete_task);
                prp_btn.setEnabled(false); prp_btn.setVisibility(View.INVISIBLE);
            } else{ button_title.setText(R.string.ttl_btn_close_task);
                red_btn.setEnabled(false); red_btn.setVisibility(View.INVISIBLE);
            }
        }

        prp_btn.setOnClickListener (enter -> {
            if (!checker.GetPrivace()) { vibrator.vibrate(50); finish();
            } else { title_performer.setTextColor(getResources().getColor(R.color.fonts_color_blc));
                title_comment.setTextColor(getResources().getColor(R.color.fonts_color_blc));
                time_release.setTextColor(getResources().getColor(R.color.fonts_color_blc));
                time_start.setTextColor(getResources().getColor(R.color.fonts_color_blc));

                String Sperformer = enter_performer.getText().toString().trim();
                String Sstart = time_start_ent.getText().toString().trim();
                String Send = time_release_ent.getText().toString().trim();
                String Stask = enter_tasktype.getText().toString().trim();
                String Scomment = comment.getText().toString().trim();

                infostate.setText(""); int enter_err = 0;

                if (InputChecker.isNotMinMaxInt(Send, time_release,10, 240, this)) enter_err++;
                if (InputChecker.isEquals(Sperformer, title_performer, "...", this)) enter_err++;
                if (InputChecker.isNotMaxSize(Scomment, title_comment, 240, this)) enter_err++;
                if (InputChecker.isNotTime(Sstart, time_start, this)) enter_err++;

                if (enter_err == 0) {
                    Sstart += ":00"; String finalSstart = Sstart;
                    new Thread(new Runnable() {
                        public void run() {
                            try { MS_SQLConnector msc = MS_SQLConnector.getConect();
                                Connection mssqlConnection = msc.connection;SimpleDateFormat
                                        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date currentDate = new Date(); String formattedDate =
                                        dateFormat.format(currentDate);
                                int TaskID = MS_SQLInsert.AddTask(mssqlConnection,
                                        AdresserID, UserId.get(PerformerID), CompanyID,
                                        formattedDate, finalSstart, Send, Stask, Scomment);
                                if (TaskID == -1) throw new SmallException(0, getString(R.string.pol_sql_error));

                                runOnUiThread(new Runnable() {
                                    public void run() { vibrator.vibrate(50);
                                        Intent intent = new Intent(A_I_AddWork.this, A_T_Task.class);
                                        Toast.makeText(context, R.string.ttl_btn_added, Toast.LENGTH_SHORT).show();
                                        startActivity(intent); finish();
                                    }
                                });
                            } catch (SQLException e) {
                                MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                            } catch (SmallException e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        infostate.setText(e.getErrorMessage());
                                    }
                                });
                            }
                        }
                    }).start();
                } else infostate.setText(R.string.pol_is_incorect);
            }
        });

        red_btn.setOnClickListener(enter -> {
            new Thread(new Runnable() {
                public void run() {
                    try { MS_SQLConnector msc = MS_SQLConnector.getConect();
                        Connection mssqlConnection = msc.connection;
                        MS_SQLDelete.DelTask(mssqlConnection, checker.GetChecker());
                        MS_SQLUpdate.UPDStatistics(mssqlConnection, data.getCompany(), LoginPerf,
                                1, 0, 0, 0);
                        runOnUiThread(new Runnable() {
                            public void run() { vibrator.vibrate(50);
                                Intent intent = new Intent(A_I_AddWork.this, A_T_Task.class);
                                Toast.makeText(context, R.string.ttl_btn_task_deleted, Toast.LENGTH_SHORT).show();
                                startActivity(intent); finish();
                            }
                        });
                    } catch (SQLException e){
                        MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                    }
                }
            }).start();
        });

        btn_back.setOnClickListener (enter -> { vibrator.vibrate(50);
            if (!checker.GetPrivace()) finish(); else{
                Intent intent = new Intent(A_I_AddWork.this, A_T_Task.class);
                startActivity(intent);finish();
            }
        });
    }

    @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        TextView enter_tasktype = findViewById(R.id.enter_tasktype);
        TextView enter_performer = findViewById(R.id.enter_performer);
        String text = adapterView.getItemAtPosition(i).toString();

        if (adapterView.getId() == R.id.select_tasktype) enter_tasktype.setText(text);
        else { enter_performer.setText(text); PerformerID = i; }
    }

    @Override public void onNothingSelected(AdapterView<?> adapterView) {}
}