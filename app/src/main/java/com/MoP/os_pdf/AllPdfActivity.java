package com.MoP.os_pdf;

import android.content.Intent;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class AllPdfActivity extends MainActivity {
    String filePath;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_pdf_layout);
        Intent intent = getIntent();
        filePath = intent.getExtras().getString("fileName");
        viewPdf(filePath);
    }

    private void viewPdf(String pdfFileName) {
        File pdfFile = new File(pdfFileName);
        setContentView(R.layout.all_pdf_layout);
        PDFView pdfView = findViewById(R.id.pdfView);
        pdfView.fromFile(pdfFile).load();
    }
}
