package com.MoP.os_pdf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class SummaryActivity extends Activity {
    TextView textview;
    ArrayList<String> test;
    int count;
    int fontSize;
    String set = "Empty sentence";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_layout);

        Intent intent = getIntent();

        textview = (TextView)findViewById(R.id.textview_summary);


        test = getIntent().getStringArrayListExtra("text");
        count = intent.getExtras().getInt("count");
        fontSize = intent.getExtras().getInt("fontSize");
        Log.i("Test", "pdf view " + test.size() + "    " + count);
        setting(test);
    }
    public void setting(ArrayList<String> text){
        if(text.size() >= 0){
            set = "";
            for (int i = 0; i <= count; i++) {
                set += text.get(i);
            }
        }
        textview.setTextSize(fontSize);
        textview.setText(set);
    }
}
