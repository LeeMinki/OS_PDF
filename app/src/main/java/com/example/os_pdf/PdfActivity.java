package com.example.os_pdf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;

public class PdfActivity extends Activity {
    private String filePath;
    private PDFTextStripper pdfStripper;
    private String text;
    private TextView resultTextView;
    private ImageView resultImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_layout);
        PDFBoxResourceLoader.init(getApplicationContext());
        Intent intent = getIntent();
        filePath = intent.getExtras().getString("fileName");
//        viewPdf(fileName);
        extractText(filePath);
//        extractImage(filePath);
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
            text = pdfStripper.getText(document);
            resultTextView = findViewById(R.id.textView);
            resultTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
            resultTextView.setText(text);
            document.close();
        } catch (Exception e) {
            //...
        }
    }

//    private void extractImage(String pdfFilePath) {
//        try {
//            File file = new File(pdfFilePath);
//            PDDocument document = PDDocument.load(file);
//            pdfStripper = new PDFTextStripper();
//            PDFRenderer renderer = new PDFRenderer(document);
//            Bitmap image = renderer.renderImage(0);
//            resultImageView = findViewById(R.id.imageView);
//            resultImageView.setImageBitmap(image);
//            document.close();
//        } catch (Exception e) {
//            //...
//        }
//    }
}
