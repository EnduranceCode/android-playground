package com.example.android.callingmethodspractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static android.graphics.Color.RED;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("Determination");
        textView.setTextColor(RED);
        textView.setTextSize(32);
        setContentView(textView);
    }
}
