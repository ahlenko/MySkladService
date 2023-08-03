package com.example.myskladservice.screens.table;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myskladservice.R;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.TaskInterface;
import com.example.myskladservice.screens.chaise.A_S_Menu;
import com.example.myskladservice.screens.chaise.A_S_Menu_N;
import com.example.myskladservice.screens.chaise.A_S_Option;
import com.example.myskladservice.screens.insert.A_I_AddPosition;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public class A_T_Table_N extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this);
        vibrator.vibrate(50);
        Intent intent;
        if (data.getUserType()) intent = new Intent(A_T_Table_N.this, A_S_Menu.class);
        else intent = new Intent(A_T_Table_N.this, A_S_Menu_N.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d62_table_non);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        AppWorkData data = new AppWorkData(this);
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
                            context, new Intent(A_T_Table_N.this, A_T_Table_N.class),
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

        TextView infostate = findViewById(R.id.infostate);

        LinearLayout container = findViewById(R.id.TableView);
        Deque<View> Products = new ArrayDeque<>();

        AtomicBoolean search_with = new AtomicBoolean(false);
        EditText search_request = findViewById(R.id.search_input);

        class ProductsPrint extends AsyncTask<Void, Void, Void> {

            private TaskInterface listener;

            public ProductsPrint(TaskInterface listener) {
                this.listener = listener;
            }

            protected Void doInBackground(Void... params) {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet;

                    resultSet = MS_SQLSelect.CompanyManager(mssqlConnection, data.getCompany());
                    resultSet.next(); int company = resultSet.getInt("id");
                    if (!search_with.get())
                        resultSet = MS_SQLSelect.ReadProducts(mssqlConnection, company);
                    else
                        resultSet = MS_SQLSelect.ReadProductsWith(mssqlConnection, company, search_request.getText().toString().trim());


                    while (resultSet.next()){
                        View temp = getLayoutInflater().inflate(R.layout.template_view_table_non, container, false);
                        ImageView image = temp.findViewById(R.id.photo_img);
                        TextView name = temp.findViewById(R.id.view_name);
                        TextView code = temp.findViewById(R.id.view_code);
                        TextView weight = temp.findViewById(R.id.view_weight);
                        TextView size = temp.findViewById(R.id.view_size);
                        TextView count = temp.findViewById(R.id.view_count);

                        String weight_s = resultSet.getInt("weignt") + " кг";
                        String size_s = resultSet.getInt("sizeh") + "х" +resultSet.getInt("sizew") + "х" + resultSet.getInt("sized")+  " см";

                        name.setText(resultSet.getString("name"));
                        code.setText(resultSet.getString("code"));
                        count.setText(resultSet.getString("count"));
                        weight.setText(weight_s);
                        size.setText(size_s);

                        byte[] imageBytes = resultSet.getBytes("image");
                        boolean isEmpty = true;
                        for (byte b : imageBytes) {
                            if (b != 0) {
                                isEmpty = false;
                                break;
                            }
                        } if (!isEmpty) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            image.setImageBitmap(bitmap);
                        }
                        Products.add(temp);
                    }
                } catch (SQLException e) {
                    DialogsViewer.twoButtonDialog(
                            context,  new Intent(A_T_Table_N.this, A_T_Table_N.class),
                            activity, "Помилка", "Невдале підключення до бази даних.\n" +
                                    "Повторіть спробу або вийдіть:", "Вийти", "Повторити", 1
                    );
                }
                return null;
            }
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (listener != null) {
                    listener.onTaskComplete();
                }
            }
        }

        ProductsPrint productsPrint = new ProductsPrint(new TaskInterface() {
            @Override
            public void onTaskComplete() {
                if (Products.isEmpty()) {
                    if (!search_with.get())
                        infostate.setText("Список товарів порожній");
                    else infostate.setText("Товарів не знайдено");
                }
                Iterator<View> iterator = Products.iterator();
                while (iterator.hasNext()) {
                    View userView = iterator.next();
                    container.addView(userView);
                }
            }
        });

        productsPrint.execute();

        ImageButton btn_search = findViewById(R.id.search_btn);
        btn_search.setOnClickListener(enter->{
            String s_search = search_request.getText().toString().trim();
            Products.clear();
            container.removeAllViews();

            ProductsPrint productsPrint5 = new ProductsPrint(new TaskInterface() {
                @Override
                public void onTaskComplete() {
                    if (Products.isEmpty()) {
                        if (!search_with.get())
                            infostate.setText("Список товарів порожній");
                        else infostate.setText("Товарів не знайдено");
                    }
                    for (View userView : Products) {
                        container.addView(userView);
                    }
                }
            });
            search_with.set(!s_search.isEmpty());
            productsPrint5.execute();
        });

        ImageButton btn_back = findViewById(R.id.button_beck);
        btn_back.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            onBackPressed();
        });

        ImageButton add_position = findViewById(R.id.button_ring);
        add_position.setOnClickListener (enter -> {
            vibrator.vibrate(50);
            Intent intent = new Intent(this, A_I_AddPosition.class);
            startActivity(intent);
        });
    }
}