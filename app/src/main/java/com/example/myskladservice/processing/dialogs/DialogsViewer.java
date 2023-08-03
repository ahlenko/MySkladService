package com.example.myskladservice.processing.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.budiyev.android.codescanner.CodeScannerView;
import com.example.myskladservice.R;
import com.example.myskladservice.processing.barcodescanner.BC_Scan;
import com.example.myskladservice.processing.checkers.InputChecker;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.exception.SmallException;
import com.example.myskladservice.processing.shpreference.AppWorkData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DialogsViewer extends DialogFragment {

    public DialogsViewer(){};

    public static void twoButtonDialog(Context context, Intent intent, AppCompatActivity activity,
                                       String title, String massage, String neg_text, String pos_text, int type) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customLayout = inflater.inflate(R.layout.dialog_simple, null);
        ListenerProcessor listener = ChoiseProcessor.defaultInitialize();

        switch (type){
            case 1: listener = ChoiseProcessor.databaseMessage(intent, activity); break;
            case 2: listener = ChoiseProcessor.incorectLogin(intent, activity, context); break;
            case 3: listener = ChoiseProcessor.incorectEmail(intent, activity, context); break;
            case 4: listener = ChoiseProcessor.deleteConfirm(intent, activity, context); break;
            case 5: listener = ChoiseProcessor.deleteMainConfirm(intent, activity, context); break;
            case 6: listener = ChoiseProcessor.deleteRegCompany(intent, activity, context); break;
            case 7: listener = ChoiseProcessor.oncanselreg(intent, activity, context); break;
            case 8: listener = ChoiseProcessor.onExit(intent, activity, context); break;
            case 9: listener = ChoiseProcessor.deletePosition(intent, activity, context); break;
        }

        ListenerProcessor finalListener = listener;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        TextView T_title = customLayout.findViewById(R.id.title);
        TextView T_massage = customLayout.findViewById(R.id.message);
        TextView T_pos_text = customLayout.findViewById(R.id.pos_text);
        TextView T_neg_text = customLayout.findViewById(R.id.neg_text);

        ImageButton pos = customLayout.findViewById(R.id.positive_button);
        ImageButton neg = customLayout.findViewById(R.id.negative_button);

        T_title.setText((CharSequence) title);
        T_massage.setText((CharSequence) massage);
        T_pos_text.setText((CharSequence) pos_text);
        T_neg_text.setText((CharSequence) neg_text);

        if (type == 3){
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialog.dismiss();
                    finalListener.onPositiveButtonClick();
                }
            });
        }

        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.show();

        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finalListener.onPositiveButtonClick();
            }
        });

        neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finalListener.onNegativeButtonClick();
            }
        });
    }

    public void changeUserDataDialog(Context context, AppCompatActivity activity,
                                     String title, String neg_text, String pos_text,
                                     String Old, String New, int type) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customLayout = inflater.inflate(R.layout.dialog_changedata, null);

        AppWorkData data = new AppWorkData(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        TextView T_title = customLayout.findViewById(R.id.title);
        TextView T_pos_text = customLayout.findViewById(R.id.pos_text);
        TextView T_neg_text = customLayout.findViewById(R.id.neg_text);
        TextView textOld = customLayout.findViewById(R.id.textOld);
        TextView textNew = customLayout.findViewById(R.id.textNew);

        EditText enterOld = customLayout.findViewById(R.id.enterOld);
        EditText enterNew = customLayout.findViewById(R.id.enterNew);

        ImageButton pos = customLayout.findViewById(R.id.positive_button);
        ImageButton neg = customLayout.findViewById(R.id.negative_button);

        T_title.setText((CharSequence) title);
        T_pos_text.setText((CharSequence) pos_text);
        T_neg_text.setText((CharSequence) neg_text);
        textOld.setText((CharSequence) Old);
        textNew.setText((CharSequence) New);

        if(type != 0) enterOld.setEnabled(false);

        if(type == 1) {
            enterOld.setInputType(InputType.TYPE_CLASS_PHONE);
            enterNew.setInputType(InputType.TYPE_CLASS_PHONE);
            class PrintPhone extends AsyncTask<Void, Void, Void>{
                private String number;

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        MS_SQLConnector msc = MS_SQLConnector.getConect();
                        Connection mssqlConnection = msc.connection;
                        ResultSet resultSet;
                        resultSet = MS_SQLSelect.HasCompanyEmail(
                                mssqlConnection, data.getCompany()); resultSet.next();
                        resultSet = MS_SQLSelect.HasUserLogin(
                                mssqlConnection, data.getUserLogin(), resultSet.getInt("id"));
                        resultSet.next();
                        number = resultSet.getString("phnumber");
                    }catch (SQLException e){
                        number = "";
                    }
                    return null;
                }

                protected void onPostExecute(Void result){
                    if(number == ""){
                        textNew.setText("Помилка бази даних");
                        textNew.setTextColor(activity.getResources().getColor(R.color.red_note));
                    } else {
                        enterOld.setText(number);
                    }
                }
            }

            PrintPhone phoneTask = new PrintPhone();
            phoneTask.execute();
        }

        if(type == 2)  {
            enterOld.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            enterNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            enterOld.setText(data.getUserLogin());
        }

        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.show();

        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListenerProcessor listener = ChoiseProcessor.dataUpdate(activity, context, type, "");
                dialog.dismiss();
                listener.onPositiveButtonClick();
            }
        });

        neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String old = enterOld.getText().toString().trim();
                String renew = enterNew.getText().toString().trim();
                ListenerProcessor listener = ChoiseProcessor.dataUpdate(activity, context, type, renew);
                class CheckUserData extends AsyncTask<Void, Void, Void>{
                    private SmallException exception;
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            MS_SQLConnector msc = MS_SQLConnector.getConect();
                            Connection mssqlConnection = msc.connection;
                            ResultSet resultSet;
                            resultSet = MS_SQLSelect.HasCompanyEmail(
                                    mssqlConnection, data.getCompany()); resultSet.next();
                            int company = resultSet.getInt("id");
                            resultSet = MS_SQLSelect.HasUserLogin(
                                    mssqlConnection, data.getUserLogin(), company);
                            resultSet.next();
                            switch (type){
                                case 0: {
                                    if (old.equals(resultSet.getString("password")))
                                        if (!InputChecker.isNotPassword(renew, textNew, activity))
                                        {throw new SmallException(2, "Good");}
                                        else {throw new SmallException(1, "Некоректний пароль");}
                                    else {throw new SmallException(0, "Неправильний пароль");}
                                }
                                case 1:{
                                    resultSet = MS_SQLSelect.HasUserPhone(mssqlConnection, renew, company);
                                    if (resultSet.next())
                                        throw new SmallException(1, "Номер вже використовується");
                                    if (!InputChecker.isNotPhone(renew, textNew, activity))
                                        {throw new SmallException(2, "Good");}
                                    else
                                        {throw new SmallException(1, "Некоректний номер телефону");}
                                }
                                case 2:{
                                    resultSet = MS_SQLSelect.HasUserLogin(mssqlConnection, renew, company);
                                    if (resultSet.next())
                                        throw new SmallException(1, "Email вже використовується");
                                    if (!InputChecker.isNotEmail(renew, textNew, 35, activity))
                                        {throw new SmallException(2, "Good");}
                                    else
                                        {throw new SmallException(1, "Логін не відповідає формату Email");}
                                }
                            }

                        } catch (SQLException e){
                            exception = new SmallException(1, "Помилка бази даних");
                        } catch (SmallException e){
                            exception = e;
                        }
                        return null;

                    }

                    protected void onPostExecute(Void result){
                        switch (exception.getErrorCode()) {
                            case 0:
                                textOld.setText(exception.getErrorMessage());
                                textOld.setTextColor(activity.getResources().getColor(R.color.red_note));
                                break;
                            case 1:
                                textNew.setText(exception.getErrorMessage());
                                textNew.setTextColor(activity.getResources().getColor(R.color.red_note));
                                break;
                            case 2:
                                dialog.dismiss();
                                listener.onNegativeButtonClick();
                                break;
                        }
                    }
                }

                CheckUserData enterTask = new CheckUserData();
                enterTask.execute();
            }
        });
    }


    public static void QRDialog(Context context, AppCompatActivity activity, EditText code, boolean req, Vibrator vibrator) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customLayout = inflater.inflate(R.layout.dialog_barcode, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        CodeScannerView scannerView = customLayout.findViewById(R.id.scanner_view);
        TextView scanned = customLayout.findViewById(R.id.textScanned);
        ImageButton confirm = customLayout.findViewById(R.id.positive_button);

        BC_Scan scan = new BC_Scan(scannerView, scanned, context, activity, req, vibrator);

        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.show();

        confirm.setOnClickListener(enter->{
            if (scanned.getText().toString().trim().isEmpty() || scanned.getText().toString().trim().equals("Сканування не виконано")){
                scanned.setText("Сканування не виконано");
            } else {
                code.setText(scanned.getText().toString().trim());
                dialog.dismiss();
            }
        });
    }
}
