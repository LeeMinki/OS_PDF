package com.MoP.os_pdf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.PDPageTree;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.graphics.PDXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class PdfActivity extends Activity {
    private String filePath;
    private PDFTextStripper pdfStripper;
    private String text;
    private TextView resultTextView;
    private TextView resultTransView;
    private Button viewButton;
    private Button transButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_layout);
        PDFBoxResourceLoader.init(getApplicationContext());
        Intent intent = getIntent();
        filePath = intent.getExtras().getString("fileName");
        extractText(filePath);
        extractImage(filePath);

        // pdf 전체 보기
        viewButton = (Button) findViewById(R.id.view_button);
        viewButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PdfActivity.this, AllPdfActivity.class);
                intent.putExtra("fileName", filePath);
                startActivity(intent);
            }
        });

        // 번역하기
        transButton = (Button) findViewById(R.id.trans_button);
        transButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text.length() == 0) {
                    Toast.makeText(PdfActivity.this, "번역할 내용이 없습니다.", Toast.LENGTH_SHORT).show();
                } else
                    translate();
            }
        });
    }

    private void extractText(String pdfFilePath) {
        File file = new File(pdfFilePath);
        try {
            PDDocument document = PDDocument.load(file);
            pdfStripper = new PDFTextStripper();
            text = pdfStripper.getText(document);
            resultTextView = findViewById(R.id.textView);
            resultTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
            resultTextView.setText(text);
            document.close();
        } catch (Exception e) {
            //...
        }
    }

    private void extractImage(String pdfFilePath) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
        try {
            File file = new File(pdfFilePath);
            PDDocument document = PDDocument.load(file);
            PDPageTree list = document.getPages();
            for (PDPage page : list) {
                PDResources pdResources = page.getResources();
                for (COSName name : pdResources.getXObjectNames()) {
                    PDXObject o = pdResources.getXObject(name);
                    if (o instanceof PDImageXObject) {
                        PDImageXObject pdfImage = (PDImageXObject) o;
                        Bitmap image = pdfImage.getImage();
                        ImageView iv = new ImageView(this);
                        iv.setImageBitmap(image);
                        iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        layout.addView(iv);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Exception while trying to create pdf document - " + e);
        }
    }

    private void translate() {
        String clientId = "z7J6NPnUTX43IST_x_Tw";
        String clientSecret = "RoQrH2ZPJA";
        resultTransView = (TextView) findViewById(R.id.textView2);
        try {
            text = URLEncoder.encode(text, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            // post request
            String postParams = "source=ko&target=en&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                Log.i("Test", "정상 호출");
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            resultTransView.setText(response);
        } catch (Exception e) {
            //
        }
    }
}
