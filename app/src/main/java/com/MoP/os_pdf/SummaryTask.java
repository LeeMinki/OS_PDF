package com.MoP.os_pdf;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class SummaryTask extends AsyncTask<String, Void, String> {
    List<String> sentences = new ArrayList<>();
    String set;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String sumURL = strings[0];
        String set = strings[1];
        String sumCount = strings[2];
        String algorithm = strings[3];
        Log.i("Summary", sumURL + ", " + set + ", " + sumCount + ", " + algorithm);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("text", set);
            jsonObject.accumulate("number", sumCount);
            jsonObject.accumulate("algorithm", algorithm);
            URL url = new URL(sumURL);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.connect();

            OutputStream outStream = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();

            int responseCode = con.getResponseCode();
            Log.i("Summary", "Response Code: " + responseCode);
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } catch (Exception e) {
            //System.out.println(e);
            Log.d("error", e.getMessage());
            return null;
        }
    }

    private void parsing(String result) {

        try {


            set = "";


            BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
            iterator.setText(result);

            int lastIndex = iterator.first();
            String temp = " ";
            String comp = " ";

            while (lastIndex != BreakIterator.DONE) {
                int firstIndex = lastIndex;
                lastIndex = iterator.next();

                if (lastIndex != BreakIterator.DONE) {

                    //한 문장씩 끊어줌
                    String sentence = result.substring(firstIndex, lastIndex);
                    set += sentence + "\n\n";
                }
            }



        } catch (Exception e) {
            Log.i("Exception", "" + e);
        }

    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("Summary", "onPostExcute: " + result);
        SummaryActivity sa = new SummaryActivity();
        parsing(result);
        sa.textview.setText(set);
        sa.textview.setTextSize(sa.fontSize);
    }

}
