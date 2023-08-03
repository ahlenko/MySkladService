package com.example.myskladservice.processing.barcodescanner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.example.myskladservice.R;
import com.google.zxing.Result;

public class BC_Scan {
    private CodeScanner mCodeScanner;
    private boolean isFullScanner;
    private CodeScannerView codeScanner;
    private TextView scanned;
    private ImageView line;
    private CheckBox activate;
    private Context context;
    private AppCompatActivity activity;
    private ConstraintLayout upperView;
    private Vibrator vibrator;
    private boolean isCreated;

    public BC_Scan(CodeScannerView codeScanner, TextView scanned, ImageView line, CheckBox activate, Context context, AppCompatActivity activity, boolean perm, Vibrator vibrator, ConstraintLayout upperView) {
        this.codeScanner = codeScanner;
        this.scanned = scanned;
        this.line = line;
        this.activate = activate;
        this.context = context;
        this.activity = activity;
        this.vibrator = vibrator;
        this.upperView = upperView;

        this.isFullScanner = true;
        if (perm) {
            setupScanner();
            mCodeScanner.startPreview();
            isCreated = true;
        } else {
            isCreated = false;
        }
    };

    public BC_Scan(CodeScannerView codeScanner, TextView scanned, Context context, AppCompatActivity activity, boolean perm, Vibrator vibrator) {
        this.codeScanner = codeScanner;
        this.scanned = scanned;
        this.context = context;
        this.activity = activity;
        this.vibrator = vibrator;

        this.isFullScanner = false;
        if (perm) {
            setupScanner();
            mCodeScanner.startPreview();
            isCreated = true;
        } else {
            isCreated = false;
        }
    };

    public void Terminate() {
        mCodeScanner.releaseResources();
    }

    public void DeTerminate() {
        mCodeScanner.startPreview();
    }

    private void setupScanner() {
        mCodeScanner = new CodeScanner(context, codeScanner);
        mCodeScanner.setCamera(CodeScanner.CAMERA_BACK);
        mCodeScanner.setFormats(CodeScanner.ALL_FORMATS);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        mCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setFlashEnabled(false);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                if (codeScanner.isPressed()) {
                    vibrator.vibrate(50);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scanned.setText(result.getText());
                        }
                    });
                }
            }
        });

        codeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }
}

