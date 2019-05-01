package com.MoP.os_pdf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class PdfActivity extends Activity {
    private String filePath;
    private PDFTextStripper pdfStripper;
    private String text;
    private TextView resultTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_layout);
        PDFBoxResourceLoader.init(getApplicationContext());
        Intent intent = getIntent();
        filePath = intent.getExtras().getString("fileName");
//        viewPdf(fileName);
        extractText(filePath);
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
//        try {
//            File file = new File(pdfFilePath);
//            PDDocument document = PDDocument.load(file);
//            pdfStripper = new PDFTextStripper();
//
//            PDFRenderer renderer = new PDFRenderer(document);
//            Bitmap image = renderer.renderImage(0);
//            resultImageView = findViewById(R.id.imageView);
//            resultImageView.setImageBitmap(image);
//            document.close();
//        } catch (Exception e) {
//            //...
//        }
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
