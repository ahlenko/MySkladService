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
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.exception.ConfirmableException;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.screens.register.A_R_User;
import com.example.myskladservice.screens.table.A_T_Packing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class A_S_Option extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    @Override public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this); Intent intent; vibrator.vibrate(50);
        if (data.getUserType()) intent = new Intent(A_S_Option.this, A_S_Menu.class);
        else intent = new Intent(A_S_Option.this, A_S_Menu_N.class);
        startActivity(intent); finish();
    }
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Override protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.d9_option);

        Intent two_btn_intent = new Intent(A_S_Option.this, A_S_Option.class);
        Spinner chose_enter = findViewById(R.id.selector_security);
        Spinner chose_lang = findViewById(R.id.selector_lang);
        AppWorkData data = new AppWorkData(this);
        AppCompatActivity activity = this;
        Context context = this;

        ImageButton btn_renew_phone = findViewById(R.id.button_changephone);
        ImageButton btn_renew_login = findViewById(R.id.button_changelogin);
        ImageButton btn_renew_pass = findViewById(R.id.button_changepass);
        ImageButton btn_support = findViewById(R.id.button_support);
        ImageButton btn_exit = findViewById(R.id.button_exit);
        ImageButton btn_back = findViewById(R.id.button_beck);
        Switch swith = findViewById(R.id.switch_notify);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.language, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chose_lang.setAdapter(adapter);
        chose_lang.setSelection(data.getLang().equals("ua") ? 0 : 1);
        chose_lang.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.enter_type, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chose_enter.setAdapter(adapter2);
        chose_enter.setSelection(data.getEnterType());
        chose_enter.setOnItemSelectedListener(this);

        PrintTask.PrintTaskCount(activity, context, two_btn_intent);

        swith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.ChangeNotify(isChecked); data.SaveData();}
        }); swith.setChecked(data.getNotify());

        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50); onBackPressed();
        });

        btn_renew_login.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            DialogsViewer dw = new DialogsViewer();
            dw.changeUserDataDialog(this, this, getString(R.string.ttl_newr_login),
                    getString(R.string.btn_confirm_scan), getString(R.string.cancel),
                    getString(R.string.old_new_login), getString(R.string.ttl_new_login), 2);
        });

        btn_renew_phone.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            DialogsViewer dw = new DialogsViewer();
            dw.changeUserDataDialog(this, this, getString(R.string.ttl_newr_phone),
                    getString(R.string.btn_confirm_scan), getString(R.string.cancel),
                    getString(R.string.old_new_phone), getString(R.string.ttl_new_phone), 1);
        });

        btn_renew_pass.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            DialogsViewer dw = new DialogsViewer();
            dw.changeUserDataDialog(this, this, getString(R.string.ttl_newr_pass),
                    getString(R.string.btn_confirm_scan), getString(R.string.cancel),
                    getString(R.string.old_new_pass), getString(R.string.ttl_new_pass), 0);
        });

        btn_support.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            String url = getString(R.string.link);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        btn_exit.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            DialogsViewer.twoButtonDialog(
                    this,  new Intent(A_S_Option.this, AM_Login.class),
                    this, getString(R.string.confirmation), getString(R.string.confirmation_exit),
                    getString(R.string.dialog_confirm), getString(R.string.dialog_discard), 8
            );
        });
    }

    @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        AppWorkData data = new AppWorkData(this);

        TextView textsec = findViewById(R.id.viev_security);
        TextView textlang = findViewById(R.id.view_lang);

        if (adapterView.getId() == R.id.selector_lang){
            textlang.setText(text);
            if (i == 0) data.ChangeLanguage("ua");
            else        data.ChangeLanguage("an");
            data.SaveData();
        } else {
            textsec.setText(text);
            data.ChangeEnterType(i);
            data.SaveData();
        }
    }

    @Override public void onNothingSelected(AdapterView<?> adapterView) {}
}