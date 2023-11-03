package com.example.myskladservice.screens.table;

import androidx.appcompat.app.AppCompatActivity;

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

import com.example.myskladservice.R;
import com.example.myskladservice.processing.database.MS_SQLConnector;
import com.example.myskladservice.processing.database.MS_SQLError;
import com.example.myskladservice.processing.database.MS_SQLSelect;
import com.example.myskladservice.processing.dialogs.DialogsViewer;
import com.example.myskladservice.processing.shpreference.AppTableChecker;
import com.example.myskladservice.processing.shpreference.AppWorkData;
import com.example.myskladservice.processing.tasker.TaskInterface;
import com.example.myskladservice.screens.chaise.A_S_Menu;
import com.example.myskladservice.screens.chaise.A_S_Menu_N;
import com.example.myskladservice.screens.current.A_C_Position;
import com.example.myskladservice.screens.insert.A_I_AddPosition;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class A_T_Table extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppWorkData data = new AppWorkData(this);
        Intent intent; vibrator.vibrate(50);
        if (data.getUserType()) intent = new Intent(A_T_Table.this, A_S_Menu.class);
        else intent = new Intent(A_T_Table.this, A_S_Menu_N.class);
        startActivity(intent); finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState); setContentView(R.layout.d61_table);

        Intent two_btn_intent = new Intent(A_T_Table.this, A_T_Table.class);
        AppWorkData data = new AppWorkData(this);
        Deque<View> Products = new ArrayDeque<>();
        AppCompatActivity activity = this;
        Context context = this;

        ExecutorService executor = Executors.newFixedThreadPool(1);
        ImageButton add_position = findViewById(R.id.button_addposition);
        AtomicBoolean search_with = new AtomicBoolean(false);
        EditText search_request = findViewById(R.id.search_input);
        ImageButton btn_search = findViewById(R.id.search_btn);
        ImageButton btn_back = findViewById(R.id.button_beck);
        LinearLayout container = findViewById(R.id.TableView);
        TextView infostate = findViewById(R.id.infostate);

        Thread ProductsPrint = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet = MS_SQLSelect.SearchProducts(
                            mssqlConnection, data.getCompany(),
                            search_request.getText().toString().trim());
                    while (resultSet.next()) {
                        View temp = getLayoutInflater().inflate(
                                R.layout.template_view_table, container, false);
                        ImageButton button = temp.findViewById(R.id.button_positionselect);
                        TextView weight = temp.findViewById(R.id.view_weight);
                        TextView count = temp.findViewById(R.id.view_count);
                        ImageView image = temp.findViewById(R.id.photo_img);
                        TextView name = temp.findViewById(R.id.view_name);
                        TextView code = temp.findViewById(R.id.view_code);
                        TextView size = temp.findViewById(R.id.view_size);

                        button.setId(resultSet.getInt("id"));
                        String weight_s = resultSet.getInt("weignt") + " "
                                + getString(R.string.weight_points_os);
                        String size_s = resultSet.getInt("sizeh") +
                                getString(R.string.delimer_s) +
                                resultSet.getInt("sizew") +
                                getString(R.string.delimer_s) +
                                resultSet.getInt("sized")+  " " +
                                getString(R.string.size_points_os);

                        name.setText(resultSet.getString("name"));
                        code.setText(resultSet.getString("code"));
                        count.setText(resultSet.getString("count"));
                        weight.setText(weight_s); size.setText(size_s);

                        byte[] imageBytes = resultSet.getBytes("image");
                        boolean isEmpty = true; for (byte b : imageBytes) {
                            if (b != 0) { isEmpty = false; break; }
                        } if (!isEmpty) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes,
                                    0, imageBytes.length);
                            image.setImageBitmap(bitmap);
                        } Products.add(temp);
                    } msc.disconnect();
                } catch (SQLException e) {
                    MS_SQLError.ErrorOnUIThread(context, two_btn_intent, activity);
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (Products.isEmpty()) if (!search_with.get())
                            infostate.setText(R.string.position_list_empty);
                        else infostate.setText(R.string.position_search_empty);
                        for (View userView : Products) container.addView(userView); }
                });
            }
        }); executor.execute(ProductsPrint);


        btn_search.setOnClickListener(enter -> { infostate.setText("");
            String s_search = search_request.getText().toString().trim();
            Products.clear(); container.removeAllViews();
            search_with.set(!s_search.isEmpty());
            executor.execute(ProductsPrint);
        });


        btn_back.setOnClickListener(enter -> {
            vibrator.vibrate(50); onBackPressed();
        });

        add_position.setOnClickListener(enter -> { vibrator.vibrate(50);
            Intent intent = new Intent(this, A_I_AddPosition.class);
            startActivity(intent); finish();
        });
    }

    public void Button(View view) {
        AppTableChecker check = new AppTableChecker(this);
        check.ChangeChecker(view.getId());
        Intent intent = new Intent(this, A_C_Position.class);
        startActivity(intent); finish();
    }
}