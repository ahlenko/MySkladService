package com.example.myskladservice.screens.chaise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.screens.table.A_T_Packing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class A_S_Analitics extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ArrayList<String> UserNames = new ArrayList<>();
    ArrayList<Integer> UserId = new ArrayList<>();
    int empID = 0, CompanyID = 0, selBtn = 1;
    @Override public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this); Intent intent; vibrator.vibrate(50);
        if (data.getUserType()) intent = new Intent(A_S_Analitics.this, A_S_Menu.class);
        else intent = new Intent(A_S_Analitics.this, A_S_Menu_N.class);
        startActivity(intent); finish();
    }
    @Override protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.d8_analitics);

        Intent two_btn_intent = new Intent(A_S_Analitics.this, A_S_Analitics.class);
        AppWorkData data = new AppWorkData(this);
        AppCompatActivity activity = this;
        Context context = this;

        TextView enter_anal_type = findViewById(R.id.enter_statistiks);
        PrintTask.PrintTaskCount(activity, context, two_btn_intent);
        Spinner chose_enter = findViewById(R.id.select_statistiks);
        ImageButton btn_back = findViewById(R.id.button_beck);
        Spinner chose_employee = findViewById(R.id.select_of);
        TextView enter_employee = findViewById(R.id.enter_of);

        ArrayList<TextView> textShow = new ArrayList<>();
        textShow.add(findViewById(R.id.packing_count));
        textShow.add(findViewById(R.id.output_count));
        textShow.add(findViewById(R.id.input_count));
        textShow.add(findViewById(R.id.time_count));

        ArrayList<TextView> textBtn = new ArrayList<>();
        textBtn.add(findViewById(R.id.timeselect_button_text1));
        textBtn.add(findViewById(R.id.timeselect_button_text2));
        textBtn.add(findViewById(R.id.timeselect_button_text3));
        textBtn.add(findViewById(R.id.timeselect_button_text4));

        ArrayList<ImageButton> imageBtn = new ArrayList<>();
        imageBtn.add(findViewById(R.id.timeselect_button1));
        imageBtn.add(findViewById(R.id.timeselect_button2));
        imageBtn.add(findViewById(R.id.timeselect_button3));
        imageBtn.add(findViewById(R.id.timeselect_button4));
        imageBtn.get(1).setEnabled(false);

        for (int i = 0; i < imageBtn.size(); i++) { final int index = i;
            imageBtn.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 0; j < textBtn.size(); j++) {
                        textBtn.get(j).setTextColor(j == index ? getColor(R.color.fonts_color_blc) : getColor(R.color.grey));
                        imageBtn.get(j).setEnabled(j != index);
                    } selBtn = index; vibrator.vibrate(100);
                }
            });
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.analitics_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chose_enter.setAdapter(adapter);  chose_enter.setSelection(0);
        enter_anal_type.setText(adapter.getItem(0));
        chose_enter.setOnItemSelectedListener(this);

        class UserSearch extends AsyncTask<Void, Void, Void> {
            @Override protected Void doInBackground(Void... voids) {
                try{
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet = MS_SQLSelect.ReadEmployeeListAndManager
                            (mssqlConnection, data.getCompany());
                    while (resultSet.next()) {CompanyID = resultSet.getInt("company_id");
                        String nameUser = resultSet.getString("surname") +
                                " " + resultSet.getString("name") + " " +
                                resultSet.getString("lastname");
                        nameUser = nameUser.length() > 23 ? nameUser.substring(0, 23) + "..." : nameUser;
                        UserNames.add(nameUser);
                        UserId.add(resultSet.getInt("id"));
                    }
                } catch (SQLException e){
                    MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                } return null;
            }

            protected void onPostExecute(Void result) {
                if (UserNames.isEmpty()){ enter_employee.setText("...");
                    chose_employee.setEnabled(false); } else {
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context,
                            android.R.layout.simple_spinner_item, UserNames);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chose_employee.setAdapter(adapter2); chose_employee.setSelection(0);
                    enter_employee.setText(adapter2.getItem(0));
                    chose_employee.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) context);
                }
            }
        } UserSearch myTask = new UserSearch(); myTask.execute();

        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50); onBackPressed();
        });
    }

    @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        TextView enter_anal_type = findViewById(R.id.enter_statistiks);
        TextView enter_employee = findViewById(R.id.enter_of);
        String text = adapterView.getItemAtPosition(i).toString();

        if (adapterView.getId() == R.id.select_statistiks) enter_anal_type.setText(text);
        else {enter_employee.setText(text); empID = i;}

        // TODO StatisticAbout
    }

    @Override public void onNothingSelected(AdapterView<?> adapterView) {}
}