package com.MoP.os_pdf;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SummaryActivity extends Activity {
    public static TextView textview;
    static ArrayList<String> sentences;
    static int count;
    public static int fontSize;
    static int number;
    static int sumCount;
    static String set = "Empty sentence";
    static boolean check = false;
    Spinner spinner;
    SummaryTask asyncTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_layout);
        Switch sw = (Switch) findViewById(R.id.switch1);
        Intent intent = getIntent();
        textview = (TextView) findViewById(R.id.textview_summary);
        sentences = getIntent().getStringArrayListExtra("text");
        count = intent.getExtras().getInt("count");
        fontSize = intent.getExtras().getInt("fontSize");
        number = intent.getExtras().getInt("number");
        Log.i("Test", "pdf view " + sentences.size() + " count: " + count + " font size: " + fontSize + " number: " + number);
        spinner = findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sumCount = position + 3;
                setting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

//        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                check = isChecked;
//                setting();
//            }
//        });
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(SummaryActivity.this, "체크상태 = " + isChecked, Toast.LENGTH_SHORT).show();
                if (isChecked == false) {
                    TextView t1 = findViewById(R.id.false_check);
                    TextView t2 = findViewById(R.id.true_check);
                    t1.setTextColor(Color.parseColor("#000000"));
                    t2.setTextColor(Color.parseColor("#808080"));
                } else if (isChecked == true) {
                    TextView t1 = findViewById(R.id.false_check);
                    TextView t2 = findViewById(R.id.true_check);
                    t2.setTextColor(Color.parseColor("#000000"));
                    t1.setTextColor(Color.parseColor("#808080"));
                }
                check = isChecked;
                setting();
            }

        });
    }

    public void setting() {
        int end;
        if (check) {
            set = "";
            for (int i = 0; i < sentences.size(); i++) {
                set += sentences.get(i) + "\n";
            }
            check = false;
        } else {
            if (sentences.size() >= 0) {
                set = "";
                if (count + number >= sentences.size()) {
                    end = sentences.size();
                } else {
                    end = count + number;
                }
                Log.i("Test", "" + end);
                for (int i = 0; i < end; i++) {
                    set += sentences.get(i) + "\n";
                }
            }
            check = false;
        }
//        if (sentences.size() - 1 >= count && sentences.size() - 1 >= count + number) {
//            number = number - 1;
//            for (int i = count + 1; i <= count + number; i++) {
//                set += sentences.get(i);
//            }
//        }

        Log.i("Test", set);
        asyncTask = new SummaryTask();
        asyncTask.execute("http://13.209.168.0:3000/summary", set, String.valueOf(sumCount));
    }
}