package com.MoP.os_pdf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

import java.io.File;
import java.io.IOException;

//import java.io.FileInputStream;
//import java.io.InputStream;
//
//import opennlp.tools.sentdetect.SentenceDetectorME;
//import opennlp.tools.sentdetect.SentenceModel;

public class PdfActivity extends Activity {
    private String filePath;
    private PDFTextStripper pdfStripper;
    private String text;
    private TextView resultTextView;
    List sentences = new ArrayList();
    BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
    private int count = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_layout);
        PDFBoxResourceLoader.init(getApplicationContext());
        Intent intent = getIntent();

        filePath = intent.getExtras().getString("fileName");
//        viewPdf(fileName);
        extractText(filePath);
        Button button = (Button)findViewById(R.id.button);
        Button button1 = (Button)findViewById(R.id.button1);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(0<=count && count < sentences.size()){
                    count++;

                    if(count<0)
                        count = 0;
                    if(count>sentences.size())
                        count = sentences.size();
                    resultTextView.setText(sentences.get(count).toString());
                }


            }
        });
        button1.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                if(0<=count && count < sentences.size()){
                    count--;


                    if(count<0)
                        count = 0;
                    if(count>sentences.size())
                        count = sentences.size();
                    resultTextView.setText(sentences.get(count).toString());
                }


            }
        });

        extractImage(filePath);
    }
//    private void viewPdf(String pdfFileName) {
//        File pdfFile = new File(pdfFileName);
//        setContentView(R.layout.pdf_layout);
//        PDFView pdfView = findViewById(R.id.pdfView);
//        pdfView.fromFile(pdfFile).load();
//    }

    private void extractText(String pdfFilePath) {
        File file = new File(pdfFilePath);
        try {
            PDDocument document = PDDocument.load(file);
            pdfStripper = new PDFTextStripper();
            resultTextView = findViewById(R.id.textView);
            text = pdfStripper.getText(document);

            resultTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
            iterator.setText(text);

//            String simple = "[.?!]";
//            String[] splitString = (text.split(simple));
//            for (String string : splitString)
//                sentences.add(string);



            int lastIndex = iterator.first();
            while (lastIndex != BreakIterator.DONE) {
                int firstIndex = lastIndex;
                lastIndex = iterator.next();

                if (lastIndex != BreakIterator.DONE) {
                    String sentence = text.substring(firstIndex, lastIndex);

                    //resultTextView.setText("sentence = " + sentence);
                    sentences.add(sentence);
                }
            }

            //resultTextView.setText(text);
            resultTextView.setText(sentences.get(0).toString());
            document.close();
        } catch (Exception e) {
            //...
        }
    }


    private void extractImage(String pdfFilePath) {
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout);
        try {
            File file = new File(pdfFilePath);
            PDDocument document = PDDocument.load(file);
            PDPageTree list = document.getPages();
            for (PDPage page : list) {
                PDResources pdResources = page.getResources();
                for (COSName name : pdResources.getXObjectNames()) {
                    PDXObject o = pdResources.getXObject(name);
                    if (o instanceof PDImageXObject) {
                        PDImageXObject pdfImage = (PDImageXObject)o;
                        Bitmap image = pdfImage.getImage();
                        ImageView iv = new ImageView(this);
                        iv.setImageBitmap(image);
                        iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        layout.addView(iv);
                    }
                }
            }

        } catch (IOException e){
            System.err.println("Exception while trying to create pdf document - " + e);
        }
    }
}
