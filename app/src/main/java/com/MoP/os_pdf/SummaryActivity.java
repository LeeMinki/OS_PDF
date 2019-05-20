package com.MoP.os_pdf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SummaryActivity extends Activity {
    TextView textview;
    ArrayList<String> test;
    int count;
    int fontSize;
    int number;
    String set = "Empty sentence";
    String entire = "";
    boolean check = true;
    Spinner spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_layout);
        Switch sw = (Switch)findViewById(R.id.switch1);
        spinner = findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SummaryActivity.this,"선택된 아이템 : "+ spinner.getItemAtPosition(position),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(SummaryActivity.this, "체크상태 = " + isChecked, Toast.LENGTH_SHORT).show();
                check = isChecked;
            }

        });


        Intent intent = getIntent();

        textview = (TextView)findViewById(R.id.textview_summary);


        test = getIntent().getStringArrayListExtra("text");
        count = intent.getExtras().getInt("count");
        fontSize = intent.getExtras().getInt("fontSize");
        number = intent.getExtras().getInt("number");
        Log.i("Test", "pdf view " + test.size() + "    " + count + "font size" + fontSize + "number" + number);
        setting(test);
    }
    public void setting(ArrayList<String> text){
        if(text.size() >= 0){
            set = "";
            for (int i = 0; i <= count; i++) {
                set += text.get(i);
            }
        }
        if(text.size()-1 >= count && text.size()-1 >= count + number){
            number = number - 1;
            for(int i = count + 1; i <= count + number; i++){
                set += text.get(i);
            }
        }
        for(int i = 0; i<text.size(); i++){
            entire += text.get(i);
        }

        textview.setTextSize(fontSize);
        textview.setText(set);
    }
}
