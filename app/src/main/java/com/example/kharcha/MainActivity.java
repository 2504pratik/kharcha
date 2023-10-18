package com.example.kharcha;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private PopupWindow popup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView editBtn = findViewById(R.id.editBtn);
        editBtn.setOnClickListener(view -> showPopUp());

    }

    private void showPopUp() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.pop_up, null);
        popup = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popup.setFocusable(true);

        Button submitBtn = popupView.findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(v -> popup.dismiss());

        TextView cancel = popupView.findViewById(R.id.cancelTv);
        cancel.setOnClickListener(v -> popup.dismiss());

        View rootView = getWindow().getDecorView().getRootView();

        popup.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }
}