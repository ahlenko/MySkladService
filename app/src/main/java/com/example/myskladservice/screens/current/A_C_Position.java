package com.example.myskladservice.screens.current;

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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.checkers.InputChecker;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLInsert;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.database.MS_SQLUpdate;
import com.example.myskladservice.processing.datastruct.PositionData;
import com.example.myskladservice.processing.datastruct.UserData;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.screens.insert.A_I_AddPosition;
import com.example.myskladservice.screens.table.A_T_Table;
import com.example.myskladservice.screens.table.A_T_Users;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class A_C_Position extends AppCompatActivity {
    @Override
    public void onBackPressed() {};
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int PICK_IMAGE_REQUEST = 67;
    private boolean request_code;
    byte[] imageBytes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String[] BarcodeOrig = {""};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.f6_positionview);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Intent two_btn_intent = new Intent(A_C_Position.this, A_C_Position.class);
        AppWorkData data = new AppWorkData(this);
        AppTableChecker check = new AppTableChecker(this);

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
                    resultSet.next();
                    int company = resultSet.getInt("id");
                    resultSet = MS_SQLSelect.HasUserLogin(mssqlConnection, data.getUserLogin(), resultSet.getInt("id"));
                    resultSet.next();
                    int performer = resultSet.getInt("id");
                    resultSet = MS_SQLSelect.ReadTaskPrintedPR(mssqlConnection, company, performer);
                    while (resultSet.next()) count++;
                } catch (SQLException e) {
                    DialogsViewer.twoButtonDialog(
                            context, new Intent(A_C_Position.this, A_C_Position.class),
                            activity, "Помилка", "Невдале підключення до бази даних.\n" +
                                    "Повторіть спробу або вийдіть:", "Вийти", "Повторити", 1
                    );
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                if (count == 0) {
                    ring_notify.setVisibility(View.INVISIBLE);
                    notify_count.setText("");
                } else notify_count.setText(String.valueOf(count));
            }
        }

        TaskPrint myTask = new TaskPrint();
        myTask.execute();

        ImageButton addphoto = findViewById(R.id.button_addphoto);
        TextView infostate = findViewById(R.id.infostate);

        TextView name = findViewById(R.id.title_name);
        TextView group = findViewById(R.id.title_group);
        TextView code = findViewById(R.id.title_barcode);
        TextView kg = findViewById(R.id.text_kg1);
        TextView sm1 = findViewById(R.id.text_cm1);
        TextView sm2 = findViewById(R.id.text_cm2);
        TextView sm3 = findViewById(R.id.text_cm3);
        TextView count = findViewById(R.id.title_startcount);
        TextView provider = findViewById(R.id.title_provider);
        TextView comment = findViewById(R.id.title_comment);
        TextView ch_counter = findViewById(R.id.viewcountchars);

        EditText e_name = findViewById(R.id.input_name);
        EditText e_group = findViewById(R.id.input_group);
        EditText e_code = findViewById(R.id.input_barcode);
        EditText e_kg = findViewById(R.id.enter_weihgt);
        EditText e_sm1 = findViewById(R.id.enter_size1);
        EditText e_sm2 = findViewById(R.id.enter_size2);
        EditText e_sm3 = findViewById(R.id.enter_size3);
        EditText e_count = findViewById(R.id.enter_startcount);
        EditText e_provider = findViewById(R.id.enter_provider);
        EditText e_comment = findViewById(R.id.entercomment);

        ImageButton barcode = findViewById(R.id.button_scanbarcode);

        class TaskWrite extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    MS_SQLSelect.ReadProduct(mssqlConnection, check.GetChecker());
                } catch (SQLException e) {
                    DialogsViewer.twoButtonDialog(
                            context, new Intent(A_C_Position.this, A_C_Position.class),
                            activity, "Помилка", "Невдале підключення до бази даних.\n" +
                                    "Повторіть спробу або вийдіть:", "Вийти", "Повторити", 1
                    );
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                byte[] imageBytesR = PositionData.image;
                boolean isEmpty = true;
                for (byte b : imageBytesR) {
                    if (b != 0) {
                        isEmpty = false;
                        break;
                    }
                }
                if (!isEmpty) {
                    imageBytes = imageBytesR;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytesR, 0, imageBytes.length);
                    addphoto.setImageBitmap(bitmap);
                }
                BarcodeOrig[0] = PositionData.code;
                e_name.setText(PositionData.name);
                e_group.setText(PositionData.group);
                e_code.setText(PositionData.code);
                e_kg.setText(String.valueOf(PositionData.weight));
                e_sm1.setText(String.valueOf(PositionData.size_1));
                e_sm2.setText(String.valueOf(PositionData.size_2));
                e_sm3.setText(String.valueOf(PositionData.size_3));
                e_count.setText(String.valueOf(PositionData.count));
                e_provider.setText(PositionData.provider);
                e_comment.setText(PositionData.comment);
                ch_counter.setText((PositionData.comment).length() + " / 90");
            }
        }

        TaskWrite myTaskWrite = new TaskWrite();
        myTaskWrite.execute();

        ImageButton btn_renew = findViewById(R.id.button_renew);
        btn_renew.setOnClickListener(enter -> {
            name.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            group.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            code.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            count.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            provider.setTextColor(getResources().getColor(R.color.fonts_color_blc));
            comment.setTextColor(getResources().getColor(R.color.fonts_color_blc));

            kg.setTextColor(getResources().getColor(R.color.grey));
            sm1.setTextColor(getResources().getColor(R.color.grey));
            sm2.setTextColor(getResources().getColor(R.color.grey));
            sm3.setTextColor(getResources().getColor(R.color.grey));

            String s_name = e_name.getText().toString().trim();
            String s_group = e_group.getText().toString().trim();
            String s_code = e_code.getText().toString().trim();
            String s_kg = e_kg.getText().toString().trim();
            String s_sm1 = e_sm1.getText().toString().trim();
            String s_sm2 = e_sm2.getText().toString().trim();
            String s_sm3 = e_sm3.getText().toString().trim();
            String s_count = e_count.getText().toString().trim();
            String s_provider = e_provider.getText().toString().trim();
            String s_comment = e_comment.getText().toString().trim();

            infostate.setText("");

            int enter_err = 0;

            if (InputChecker.isNotCSize(s_name, name, 60, this)) enter_err++;
            if (InputChecker.isNotCSize(s_group, group, 30, this)) enter_err++;
            if (InputChecker.isNotCSize(s_code, code, 30, this)) enter_err++;
            if (InputChecker.isNotCSize(s_provider, provider, 45, this)) enter_err++;
            if (InputChecker.isNotFloat(s_kg, kg, this)) enter_err++;
            if (InputChecker.isNotInt(s_sm1, sm1, this)) enter_err++;
            if (InputChecker.isNotInt(s_sm2, sm2, this)) enter_err++;
            if (InputChecker.isNotInt(s_sm3, sm3, this)) enter_err++;
            if (InputChecker.isNotInt(s_count, count, this)) enter_err++;
            if (s_comment.length() > 90) {
                comment.setTextColor(getResources().getColor(R.color.red_note));
                enter_err++;
            }
            if (enter_err == 0) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            MS_SQLConnector msc = MS_SQLConnector.getConect();
                            Connection mssqlConnection = msc.connection;
                            int company;
                            ResultSet resultSet;

                            resultSet = MS_SQLSelect.HasCompanyEmail(mssqlConnection, data.getCompany());
                            resultSet.next();
                            company = resultSet.getInt("id");
                            resultSet = MS_SQLSelect.HasBarcode(mssqlConnection, s_code, company);
                            if (resultSet.next()) {
                                if (!Objects.equals(resultSet.getString("code"), BarcodeOrig[0])) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            infostate.setText("* Деякі дані вже використовуються");
                                            e_code.setTextColor(getResources().getColor(R.color.red_note));
                                        }
                                    });
                                }
                            }
                            if (imageBytes == null) {
                                MS_SQLUpdate.UPDPosition(mssqlConnection,
                                        company, s_name, s_group, s_code, Float.parseFloat(s_kg),
                                        Integer.parseInt(s_sm1), Integer.parseInt(s_sm2),
                                        Integer.parseInt(s_sm3), Integer.parseInt(s_count),
                                        s_provider, s_comment, check.GetChecker()
                                );
                            } else {
                                MS_SQLUpdate.UPDPosition(mssqlConnection,
                                        company, imageBytes, s_name, s_group, s_code, Float.parseFloat(s_kg),
                                        Integer.parseInt(s_sm1), Integer.parseInt(s_sm2),
                                        Integer.parseInt(s_sm3), Integer.parseInt(s_count),
                                        s_provider, s_comment, check.GetChecker()
                                );
                            }
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    vibrator.vibrate(50);
                                    Intent intent;
                                    intent = new Intent(A_C_Position.this, A_T_Table.class);
                                    Toast.makeText(context, "Позицію оновлено", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            return;

                        } catch (SQLException e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    DialogsViewer.twoButtonDialog(
                                            context, two_btn_intent, activity, "Помилка",
                                            "Невдале підключення до бази даних.\nПовторіть спробу або вийдіть:",
                                            "Вийти", "Повторити", 1
                                    );
                                }
                            });
                            return;
                        }
                    }
                }).start();
            } else infostate.setText("* Деякі поля заповнено некоректно");
        });

        ImageButton btn_del = findViewById(R.id.button_delete);
        btn_del.setOnClickListener(enter -> {
            DialogsViewer.twoButtonDialog(
                    context,  new Intent(A_C_Position.this, A_T_Table.class), activity,
                    "Підтвердження", "Ви дійсно впевнені що хочете видалити дану товарну позицію?",
                    "Так", "Ні", 9
            );
        });

        addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        ImageButton btn_back = findViewById(R.id.button_beck);
        btn_back.setOnClickListener(enter -> {
            vibrator.vibrate(50);
            Intent intent = new Intent(A_C_Position.this, A_T_Table.class);
            startActivity(intent);
            finish();
        });

        barcode.setOnClickListener(enter->{
            if (checkCameraPermission()) {
                request_code = true;
                DialogsViewer.QRDialog(this, this, e_code, request_code, vibrator);
            } else {
                requestCameraPermission();
            }
        });
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                request_code = true;
            } else {
                Toast.makeText(this, "Доступ до камери не отримано", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageButton addphoto = findViewById(R.id.button_addphoto);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // отримання URI вибраного зображення
            Uri selectedImageUri = data.getData();
            try {
                // відкриття потоку з вибраним зображенням та його відображення в ImageButton
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                addphoto.setImageBitmap(scaled);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                scaled.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
                imageBytes = outputStream.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}