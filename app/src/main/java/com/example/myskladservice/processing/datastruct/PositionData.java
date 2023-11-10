package com.example.myskladservice.processing.datastruct;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myskladservice.R;

import java.util.ArrayList;

public class PositionData {
    public static String provider; public static String comment;
    public static byte[] image; public static String name;
    public static String group; public static String code;
    public static int size_1; public static int size_2;
    public static int size_3; public static int count;

    public static float weight;

    public static byte[] SetData (AppCompatActivity activity, byte[] imageBytes){
        ImageButton add_photo = activity.findViewById(R.id.button_addphoto);
        EditText e_name = activity.findViewById(R.id.input_name);
        EditText e_group = activity.findViewById(R.id.input_group);
        EditText e_code = activity.findViewById(R.id.input_barcode);
        EditText e_kg = activity.findViewById(R.id.enter_weihgt);
        EditText e_sm1 = activity.findViewById(R.id.enter_size1);
        EditText e_sm2 = activity.findViewById(R.id.enter_size2);
        EditText e_sm3 = activity.findViewById(R.id.enter_size3);
        EditText e_count = activity.findViewById(R.id.enter_startcount);
        EditText e_provider = activity.findViewById(R.id.enter_provider);
        EditText e_comment = activity.findViewById(R.id.entercomment);
        TextView ch_counter = activity.findViewById(R.id.viewcountchars);

        byte[] imageBytesR = PositionData.image;
        boolean isEmpty = true; for (byte b : imageBytesR)
            if (b != 0) { isEmpty = false; break; }
        if (!isEmpty) { imageBytes = imageBytesR;
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                imageBytesR, 0, imageBytesR.length);
            add_photo.setImageBitmap(bitmap);}

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

        return imageBytes;
    }
}
