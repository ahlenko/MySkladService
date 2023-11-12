package com.example.myskladservice.screens.insert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.checkers.InputChecker;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLInsert;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.exception.SmallException;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.PrintTask;
import com.example.myskladservice.screens.table.A_T_Table;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class A_I_AddPosition extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private boolean request_code; byte[] imageBytes = null;
    private static final int PICK_IMAGE_REQUEST = 67;
    @Override public void onBackPressed() {}

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.f6_newposition);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Intent two_btn_intent = new Intent(A_I_AddPosition.this, A_I_AddPosition.class);
        AppWorkData data = new AppWorkData(this);
        AppCompatActivity activity = this;
        Context context = this;

        TextView ch_counter = findViewById(R.id.viewcountchars);
        TextView provider = findViewById(R.id.title_provider);
        TextView count = findViewById(R.id.title_startcount);
        TextView comment = findViewById(R.id.title_comment);
        TextView infostate = findViewById(R.id.infostate);
        TextView code = findViewById(R.id.title_barcode);
        TextView group = findViewById(R.id.title_group);
        TextView name = findViewById(R.id.title_name);
        TextView sm1 = findViewById(R.id.text_cm1);
        TextView sm2 = findViewById(R.id.text_cm2);
        TextView sm3 = findViewById(R.id.text_cm3);
        TextView kg = findViewById(R.id.text_kg1);

        EditText e_provider = findViewById(R.id.enter_provider);
        EditText e_count = findViewById(R.id.enter_startcount);
        EditText e_comment = findViewById(R.id.entercomment);
        EditText e_code = findViewById(R.id.input_barcode);
        EditText e_group = findViewById(R.id.input_group);
        EditText e_name = findViewById(R.id.input_name);
        EditText e_kg = findViewById(R.id.enter_weihgt);
        EditText e_sm1 = findViewById(R.id.enter_size1);
        EditText e_sm2 = findViewById(R.id.enter_size2);
        EditText e_sm3 = findViewById(R.id.enter_size3);

        ImageButton createPos = findViewById(R.id.button_addposition);
        ImageButton barcode = findViewById(R.id.button_scanbarcode);
        ImageButton addphoto = findViewById(R.id.button_addphoto);
        ImageButton btn_back = findViewById(R.id.button_beck);

        PrintTask.PrintTaskCount(activity, context, two_btn_intent);

        e_comment.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = charSequence.length(); String str = length + " / 90";
                if (length <= 90) ch_counter.setTextColor(getResources().getColor(R.color.grey));
                else ch_counter.setTextColor(getResources().getColor(R.color.red_note));
                ch_counter.setText(str); }

            @Override public void afterTextChanged(Editable editable) {}
        });

        createPos.setOnClickListener(enter -> {
            provider.setTextColor(getColor(R.color.fonts_color_blc));
            comment.setTextColor(getColor(R.color.fonts_color_blc));
            group.setTextColor(getColor(R.color.fonts_color_blc));
            count.setTextColor(getColor(R.color.fonts_color_blc));
            name.setTextColor(getColor(R.color.fonts_color_blc));
            code.setTextColor(getColor(R.color.fonts_color_blc));
            infostate.setText(""); int enter_err = 0;
            sm1.setTextColor(getColor(R.color.grey));
            sm2.setTextColor(getColor(R.color.grey));
            sm3.setTextColor(getColor(R.color.grey));
            kg.setTextColor(getColor(R.color.grey));

            String s_provider = e_provider.getText().toString().trim();
            String s_comment = e_comment.getText().toString().trim();
            String s_group = e_group.getText().toString().trim();
            String s_count = e_count.getText().toString().trim();
            String s_code = e_code.getText().toString().trim();
            String s_name = e_name.getText().toString().trim();
            String s_sm1 = e_sm1.getText().toString().trim();
            String s_sm2 = e_sm2.getText().toString().trim();
            String s_sm3 = e_sm3.getText().toString().trim();
            String s_kg = e_kg.getText().toString().trim();

            if(InputChecker.isNotCSize(s_provider, provider, 45, this)) enter_err++;
            if(InputChecker.isNotMaxSize(s_comment, comment, 90, this)) enter_err++;
            if(InputChecker.isNotCSize(s_group, group, 30, this)) enter_err++;
            if(InputChecker.isNotCSize(s_code, code, 30, this)) enter_err++;
            if(InputChecker.isNotCSize(s_name, name, 60, this)) enter_err++;
            if(InputChecker.isNotInt(s_count, count, this)) enter_err++;
            if(InputChecker.isNotFloat(s_kg, kg, this)) enter_err++;
            if(InputChecker.isNotInt(s_sm1, sm1, this)) enter_err++;
            if(InputChecker.isNotInt(s_sm2, sm2, this)) enter_err++;
            if(InputChecker.isNotInt(s_sm3, sm3, this)) enter_err++;

            if (enter_err == 0){
                new Thread(new Runnable() {
                    public void run() {
                        boolean bark = false; try {
                            MS_SQLConnector msc = MS_SQLConnector.getConect();
                            Connection mssqlConnection = msc.connection;
                            ResultSet resultSet = MS_SQLSelect.UsedBarcode(mssqlConnection,
                                    s_code, data.getCompany()); resultSet.next();
                            if (resultSet.getString("code") != null) bark = true;
                            if (bark) throw new SmallException(0, getString(R.string.pol_is_using));

                            int PositionID = MS_SQLInsert.AddPosition(mssqlConnection,
                                    resultSet.getInt("id"), imageBytes, s_name, s_group, s_code,
                                    Float.parseFloat(s_kg),   Integer.parseInt(s_sm1),
                                    Integer.parseInt(s_sm2),  Integer.parseInt(s_sm3),
                                    Integer.parseInt(s_count), s_provider, s_comment);
                            if (PositionID == -1) throw new SmallException(0, getString(R.string.pol_sql_error));
                            runOnUiThread(new Runnable() {
                                public void run() { vibrator.vibrate(50);
                                    Intent intent = new Intent(A_I_AddPosition.this, A_T_Table.class);
                                    Toast.makeText(context, R.string.position_added, Toast.LENGTH_SHORT).show();
                                    startActivity(intent); finish();
                                }
                            });
                        } catch (SQLException e) {
                            MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                        } catch (SmallException e){
                            boolean finalBark = bark; runOnUiThread(new Runnable() {
                                public void run() { infostate.setText(e.getErrorMessage());
                                    if (finalBark) e_code.setTextColor(getResources().getColor(R.color.red_note));
                                }
                            });
                        }
                    }
                }).start();
            } else infostate.setText(R.string.pol_is_incorect);
        });

        addphoto.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View view) { Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*"); startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        btn_back.setOnClickListener (enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(A_I_AddPosition.this, A_T_Table.class);
            startActivity(intent); finish();
        });

        barcode.setOnClickListener(enter->{
            if (checkCameraPermission()) { request_code = true;
                DialogsViewer.QRDialog(this, this, e_code, request_code, vibrator);
            } else requestCameraPermission();
        });
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                request_code = true;
            else Toast.makeText(this, getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        ImageButton addphoto = findViewById(R.id.button_addphoto);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try { InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                addphoto.setImageBitmap(scaled); ByteArrayOutputStream outputStream
                        = new ByteArrayOutputStream(); scaled.compress(Bitmap.CompressFormat.WEBP,
                        100, outputStream); imageBytes = outputStream.toByteArray();
            } catch (FileNotFoundException e) {e.printStackTrace();}
        }
    }
}