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
import java.lang.String;

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

import java.io.FileInputStream;
import java.io.InputStream;
//
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.StringUtil;

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

            int lastIndex = iterator.first();
            String temp = " ";
            String comp = " ";

            while (lastIndex != BreakIterator.DONE) {
                int firstIndex = lastIndex;
                lastIndex = iterator.next();

                if (lastIndex != BreakIterator.DONE) {
                    //한 문장씩 끊어줌
                    String sentence = text.substring(firstIndex, lastIndex);
                    int prev = 1;
                    int now = 1;
                    int len = 0;
                    //sentence 길이가 0 - 빈 문장이면, 추가 하지 않음
                    if(sentence.length() == 0){
                        continue;
                    }
                    //개행되는 부분을 공백으로 채워줌 - 나중에 equals 비교를 위해
                    sentence = sentence.replaceAll(System.getProperty("line.separator"), " ");
                    //문장을 일단 추가함.
                    sentences.add(sentence);

                    //지금 추가한 것의 전 문장의 index와 지금 문장의 index값을 저장
                    if(sentences.size()>1){
                        prev = sentences.size() - 2;
                        now = sentences.size() - 1;
                    }
                    else
                        continue;
                    //직전 문장 == temp
                    temp = sentences.get(prev).toString();
                    //직전 문장의 길이
                    len = temp.length();
                    //전 문장이 -으로 끝났는 지 확인하기 위해서
                    if(len > 2)
                        comp = temp.substring(len-2, len);

                    if(comp.equals("- ")){
                        String result = temp.concat(sentence);
                        sentences.remove(sentences.size() - 2);
                        sentences.remove(sentences.size() - 1);
                        sentences.add(result);
                    }
                    char check_lower = sentences.get(sentences.size() - 1).toString().charAt(0);
                    if(Character.isLowerCase(check_lower)&&sentences.size()>1){
                        String lower = sentences.get(sentences.size() - 1).toString();
                        String prev_str = sentences.get(sentences.size() - 2).toString();
                        String result = prev_str.concat(lower);
                        sentences.remove(sentences.size() - 2);
                        sentences.remove(sentences.size() - 1);
                        sentences.add(result);
                    }

                }
            }
//            for(int i = 0; i<sentences.size(); i++){
//                //sentences.add(i, sentences.get(i).toString().replaceAll(System.getProperty("line.separator"), " "));
//                char tmp = sentences.get(i + 1).toString().charAt(0);
//                if(Character.isLowerCase(tmp)){
////                    String result = sentences.get(i).toString().concat(sentences.get(i+1).toString());
////                    sentences.add(i, result);
////                    sentences.remove(i+1);
////                    i = i - 1;
//                    sentences.add(i, "소문자");
//                }
//                int len = sentences.get(i).toString().length();
//                String comp = " ";
////                if(len > 2)
////                    comp = sentences.get(i).toString().substring(len-2, len);
////                if(comp.equals("- ")){
////                    String result = sentences.get(i).toString().concat(sentences.get(i+1).toString());
////                    sentences.add(i, result);
////                    sentences.remove(i+1);
////                    i = i - 1;
////                }
//            }

            resultTextView.setText(sentences.get(0).toString());

//            InputStream inputStream = new FileInputStream("en-sent.bin");
//            SentenceModel model = new SentenceModel(inputStream);
//
//            //Instantiating the SentenceDetectorME class
//            SentenceDetectorME detector = new SentenceDetectorME(model);
//
//            String split[] = detector.sentDetect(text);
//
//            //Printing the sentences
//            for(String sent : split)
//                sentences.add(sent);
            //Detecting the position of the sentences in the raw tex
//
//            //Printing the spans of the sentences in the paragraph
//            for (Span span : spans)
//                System.out.println(span);



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
