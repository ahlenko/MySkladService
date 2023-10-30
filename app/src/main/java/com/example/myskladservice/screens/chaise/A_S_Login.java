package com.example.myskladservice.screens.chaise;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Vibrator;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myskladservice.AM_Login;
import com.example.myskladservice.R;
import com.example.myskladservice.processing.checkers.InputChecker;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.exception.ConfirmableException;
import com.example.myskladservice.processing.exception.SmallException;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.database.MS_SQLConnector;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class A_S_Login extends AppCompatActivity {
    @Override
    public void onBackPressed() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a2_login);
        AppWorkData data = new AppWorkData(this);
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        Intent two_btn_intent = new Intent(A_S_Login.this, A_S_Login.class);
        AppCompatActivity activity = this;
        Context context = this;

        TextView text_login = findViewById(R.id.TextLogin);
        TextView text_pass = findViewById(R.id.TextPassword);
        EditText edit_login = findViewById(R.id.EnterLogin);
        EditText edit_pass = findViewById(R.id.EnterPassword);
        ImageButton enter_btn = findViewById(R.id.button_enter);


        class EnterTask extends AsyncTask<Void, Void, Void> {
            private ConfirmableException exception;
            protected Void doInBackground(Void... params) {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet = MS_SQLSelect.IsCurrectLogin(
                            mssqlConnection, data.getCompany(), data.getUserLogin());

                    if (!resultSet.isBeforeFirst())
                        throw new ConfirmableException(3, getString(R.string.problem),
                                getString(R.string.company_deleted), getString(R.string.exit),
                                getString(R.string.btn_enter)
                        );

                    resultSet.next(); if (resultSet.getString("login") == null)
                        throw new ConfirmableException(2, getString(R.string.problem),
                                getString(R.string.employee_deleted), getString(R.string.second),
                                getString(R.string.enter)
                        );

                    boolean pass_correct = Objects.equals(data.getUserPass(), resultSet.getString("password"));
                    boolean full_access = resultSet.getBoolean("fullacess");

                    msc.disconnect(); if (data.getEnterType() != 0){
                        if (pass_correct){
                            data.ChangeUserType(full_access);
                            if (data.getEnterType() == 2){
                                 edit_pass.setText(data.getUserPass());
                                Intent intent; vibrator.vibrate(50);
                                if (data.getUserType())
                                    intent = new Intent(A_S_Login.this, A_S_Menu.class);
                                else intent = new Intent(A_S_Login.this, A_S_Menu_N.class);
                                startActivity(intent); finish();
                            } else
                                throw new ConfirmableException(4, "", "", "", "");
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.prob_passport_renew), Toast.LENGTH_SHORT).show();
                            data.ChangeEnterType(0);
                        }
                    }
                    throw new ConfirmableException(0, "", "", "", "");
                } catch (SQLException e) {
                    exception = new ConfirmableException(1, getString(R.string.problem),
                            getString(R.string.non_connected), getString(R.string.exit),
                            getString(R.string.repeate));
                } catch (ConfirmableException e) {
                    exception = e;
                } return null;
            }

            protected void onPostExecute(Void result) {
                switch (exception.getErrorType()) {
                    case 0:
                        edit_login.setText(data.getUserLogin()); break;
                    case 1:
                        DialogsViewer.twoButtonDialog(
                                context,  new Intent(A_S_Login.this, A_S_Login.class)
                                , activity, exception.getErrorTitle(), exception.getErrorMessage(),
                                exception.getErrorNegative(), exception.getErrorPositive(),
                                exception.getErrorType()
                        ); break;
                    case 2: case 3:
                        DialogsViewer.twoButtonDialog(
                                context,  new Intent(A_S_Login.this, AM_Login.class),
                                activity, exception.getErrorTitle(), exception.getErrorMessage(),
                                exception.getErrorNegative(), exception.getErrorPositive(),
                                exception.getErrorType()
                        ); break;
                    case 4:
                        edit_login.setText(data.getUserLogin());
                        FingerprintManager fingerprintManager = (FingerprintManager)
                            getSystemService(Context.FINGERPRINT_SERVICE);
                        if (!fingerprintManager.isHardwareDetected() || !fingerprintManager.hasEnrolledFingerprints()) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.prob_scanner), Toast.LENGTH_SHORT).show();
                            data.ChangeEnterType(0);
                        } else {
                            FingerprintManager.AuthenticationCallback authenticationCallback =
                                    new FingerprintManager.AuthenticationCallback() {
                                @Override
                                public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                                    edit_pass.setText(data.getUserPass());
                                    Intent intent; vibrator.vibrate(50);
                                    if (data.getUserType())
                                        intent = new Intent(A_S_Login.this, A_S_Menu.class);
                                    else intent = new Intent(A_S_Login.this, A_S_Menu_N.class);
                                    startActivity(intent); finish();
                                }

                                @Override
                                public void onAuthenticationFailed() {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.prob_scanned),
                                            Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onAuthenticationError(int errorCode, CharSequence errString) {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.prob_scanned),
                                            Toast.LENGTH_SHORT).show();
                                }
                            };
                            try {
                                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                                keyStore.load(null);
                                if (!keyStore.containsAlias("my_key_alias")) {
                                    KeyGenerator keyGenerator =
                                            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                                                    "AndroidKeyStore");
                                    KeyGenParameterSpec keyGenParameterSpec =
                                            new KeyGenParameterSpec.Builder("my_key_alias",
                                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                                            .build();
                                    keyGenerator.init(keyGenParameterSpec);
                                    keyGenerator.generateKey();
                                }
                                SecretKey key = (SecretKey) keyStore.getKey("my_key_alias", null);
                                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                                cipher.init(Cipher.ENCRYPT_MODE, key);
                                FingerprintManager.CryptoObject cryptoObject =
                                        new FingerprintManager.CryptoObject(cipher);
                                fingerprintManager.authenticate(cryptoObject, new CancellationSignal(),
                                        0, authenticationCallback, null);
                            } catch (NoSuchPaddingException | NoSuchAlgorithmException |
                                     InvalidKeyException | UnrecoverableKeyException |
                                     CertificateException | KeyStoreException | IOException |
                                     InvalidAlgorithmParameterException | NoSuchProviderException e) {
                                Toast.makeText(getApplicationContext(), getString(R.string.prob_biom),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } break;
                }
            }
        }

        EnterTask enterTask = new EnterTask(); enterTask.execute();


        enter_btn.setOnClickListener(enter -> {
            text_login.setText(R.string.log_text);
            text_login.setTextColor(getColor(R.color.fonts_color_blc));
            text_pass.setText(R.string.pass_text);
            text_pass.setTextColor(getColor(R.color.fonts_color_blc));

            String login = edit_login.getText().toString().trim();
            String pass = edit_pass.getText().toString().trim();

            int enter_err = 0;

            if(InputChecker.isNotEmail(login, text_login, 35,  this)) {
                text_login.setText(R.string.non_format_login); enter_err++; }
            if(InputChecker.isNotPassword(pass, text_pass,  this)) {
                text_pass.setText(R.string.non_current_password); enter_err++; }

            if (enter_err == 0) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            MS_SQLConnector msc = MS_SQLConnector.getConect();
                            Connection mssqlConnection = msc.connection;
                            ResultSet resultSet = MS_SQLSelect.IsCurrectLogin(
                                    mssqlConnection, data.getCompany(), login);

                            resultSet.next(); if (resultSet.getString("login") == null)
                                throw new SmallException(0, getString(R.string.non_current_login));

                            if (!resultSet.getString("password").equals(pass))
                                throw new SmallException(1, getString(R.string.non_current_password));
                            data.Enter(resultSet.getBoolean("fullacess"), login, pass);

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Intent intent; vibrator.vibrate(50);
                                    if (data.getUserType())
                                         intent = new Intent(A_S_Login.this, A_S_Menu.class);
                                    else intent = new Intent(A_S_Login.this, A_S_Menu_N.class);
                                    startActivity(intent); finish();
                                }
                            }); return;
                        } catch (SQLException e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    DialogsViewer.twoButtonDialog(
                                            context,  two_btn_intent, activity, getString(R.string.problem),
                                            getString(R.string.non_connected), getString(R.string.exit),
                                            getString(R.string.repeate), 1
                                    );
                                }
                            }); return;
                        } catch (SmallException e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    switch (e.getErrorCode()) {
                                        case 0:
                                            text_login.setText(e.getErrorMessage());
                                            text_login.setTextColor(getResources().getColor(R.color.red_note));
                                            break;
                                        case 1:
                                            text_pass.setText(e.getErrorMessage());
                                            text_pass.setTextColor(getResources().getColor(R.color.red_note));
                                            break;
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}