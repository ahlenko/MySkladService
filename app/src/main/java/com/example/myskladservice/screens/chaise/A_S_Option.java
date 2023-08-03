package com.example.myskladservice.screens.chaise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myskladservice.AM_Login;
import com.example.myskladservice.R;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.exception.ConfirmableException;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.screens.register.A_R_User;
import com.example.myskladservice.screens.table.A_T_Packing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class A_S_Option extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    @Override
    public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this);
        vibrator.vibrate(50);
        Intent intent;
        if (data.getUserType()) intent = new Intent(A_S_Option.this, A_S_Menu.class);
        else intent = new Intent(A_S_Option.this, A_S_Menu_N.class);
        startActivity(intent);
        finish();
    }
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d9_option);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        AppWorkData data = new AppWorkData(this);
        Spinner chose_lang = findViewById(R.id.selector_lang);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chose_lang.setAdapter(adapter);
        chose_lang.setSelection(data.getLang().equals("ua") ? 0 : 1);
        chose_lang.setOnItemSelectedListener(this);

        Spinner chose_enter = findViewById(R.id.selector_security);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.enter_type, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chose_enter.setAdapter(adapter2);
        chose_enter.setSelection(data.getEnterType());
        chose_enter.setOnItemSelectedListener(this);

        AppCompatActivity activity = this;
        Context context = this;

        ImageView ring_notify = findViewById(R.id.ring_notify);
        TextView notify_count = findViewById(R.id.notify_count);
        class TaskPrint extends AsyncTask<Void, Void, Void> {
            private int count = 0;
            protected Void doInBackground(Void... params) {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet;
                    resultSet = MS_SQLSelect.CompanyManager(mssqlConnection, data.getCompany());
                    resultSet.next(); int company = resultSet.getInt("id");
                    resultSet = MS_SQLSelect.HasUserLogin(mssqlConnection, data.getUserLogin(), resultSet.getInt("id"));
                    resultSet.next(); int performer = resultSet.getInt("id");
                    resultSet = MS_SQLSelect.ReadTaskPrintedPR(mssqlConnection, company, performer);
                    while (resultSet.next()) count++;
                } catch (SQLException e) {
                    DialogsViewer.twoButtonDialog(
                            context, new Intent(A_S_Option.this, A_S_Option.class),
                            activity, "Помилка", "Невдале підключення до бази даних.\n" +
                                    "Повторіть спробу або вийдіть:", "Вийти", "Повторити", 1
                    );
                }
                return null;
            }
            protected void onPostExecute(Void result) {
                if (count == 0){
                    ring_notify.setVisibility(View.INVISIBLE);
                    notify_count.setText("");
                } else notify_count.setText(String.valueOf(count));
            }
        }

        TaskPrint myTask = new TaskPrint();
        myTask.execute();

        Switch swith = findViewById(R.id.switch_notify);
        swith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.ChangeNotify(isChecked);
                data.SaveData();
            }
        });

        swith.setChecked(data.getNotify());

        ImageButton btn_back = findViewById(R.id.button_beck);
        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            onBackPressed();
        });

        ImageButton btn_renew_login = findViewById(R.id.button_changelogin);
        btn_renew_login.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            DialogsViewer dw = new DialogsViewer();
            dw.changeUserDataDialog(this, this, "Зміна логіну", "Підтвердити", "Відмінити", "Старий Email", "Новий Email", 2);
        });

        ImageButton btn_renew_phone = findViewById(R.id.button_changephone);
        btn_renew_phone.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            DialogsViewer dw = new DialogsViewer();
            dw.changeUserDataDialog(this, this, "Зміна номеру", "Підтвердити", "Відмінити", "Старий номер телефону", "Новий номер телефону", 1);
        });

        ImageButton btn_renew_pass = findViewById(R.id.button_changepass);
        btn_renew_pass.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            DialogsViewer dw = new DialogsViewer();
            dw.changeUserDataDialog(this, this, "Зміна паролю", "Підтвердити", "Відмінити", "Старий пароль", "Новий пароль", 0);
        });

        ImageButton btn_support = findViewById(R.id.button_support);
        btn_support.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            String url = "https://t.me/ahlenko";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        ImageButton btn_exit = findViewById(R.id.button_exit);
        btn_exit.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            DialogsViewer.twoButtonDialog(
                    this,  new Intent(A_S_Option.this, AM_Login.class), this, "Підтвердження",
                    "Ви дійсно хочете здійснити вихід з аккаунту",
                    "Так", "Ні", 8
            );
        });
    }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            AppWorkData data = new AppWorkData(this);
            String text = adapterView.getItemAtPosition(i).toString();

            TextView textlang = findViewById(R.id.view_lang);
            TextView textsec = findViewById(R.id.viev_security);
            if (adapterView.getId() == R.id.selector_lang){
                textlang.setText(text);
                if (i == 0) data.ChangeLanguage("ua");
                else        data.ChangeLanguage("an");
                data.SaveData();
            }
            else {
                textsec.setText(text);
                data.ChangeEnterType(i);
                data.SaveData();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
}