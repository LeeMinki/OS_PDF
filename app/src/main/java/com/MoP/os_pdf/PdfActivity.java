package com.MoP.os_pdf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageTree;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.graphics.PDXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

//

public class PdfActivity extends Activity {
    private String filePath;
    private PDFTextStripper pdfStripper;
    private String text;
    private TextView resultTextView;
    List<String> sentences = new ArrayList<>();

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
        Button button = (Button) findViewById(R.id.button);
        Button button1 = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (0 <= count && count < sentences.size()) {
                    count++;

                    if (count < 0)
                        count = 0;
                    if (count >= sentences.size())
                        count = sentences.size();
                    resultTextView.setText(sentences.get(count));
                }


            }
        });
        button1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (0 <= count && count < sentences.size()) {
                    count--;


                    if (count < 0)
                        count = 0;
                    if (count > sentences.size())
                        count = sentences.size();
                    resultTextView.setText(sentences.get(count));
                }


            }
        });

        extractImage(filePath);

        // pdf 전체 보기
        Button viewButton = (Button) findViewById(R.id.view_button);
        viewButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PdfActivity.this, AllPdfActivity.class);
                intent.putExtra("fileName", filePath);
                startActivity(intent);
            }
        });
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
            parsing(text);


            resultTextView.setText(sentences.get(0));
            document.close();
        } catch (Exception e) {
            //...
        }
    }

    private void parsing(String text) {

//        //  정규 표현식으로 split
//        String[] words = text.split("\\.\\r\\n|\\.\\r|\\.\\n|\\.\\n\\r|\\.\\s|\\r\\n|\\r|\\n|\\n\\r");
//
//        for (String wo : words ){
//            //wo.split("\n");
//            sentences.add(wo);
//        }

//breakiterator 사용하기
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
                if (sentence.length() == 0) {
                    continue;
                }

                //개행되는 부분을 공백으로 채워줌 - 나중에 equals 비교를 위해
                sentence = sentence.replaceAll(System.getProperty("line.separator"), " ");

                //문장을 일단 추가함.
                sentences.add(sentence);

                //지금 추가한 것의 전 문장의 index와 지금 문장의 index값을 저장
                if (sentences.size() > 1) {
                    prev = sentences.size() - 2;
                    now = sentences.size() - 1;
                } else
                    continue;
                //직전 문장 == temp
                temp = sentences.get(prev).toString();

                //직전 문장의 길이
                len = temp.length();
                //전 문장이 -으로 끝났는 지 확인하기 위해서


                if (len > 2)
                    comp = temp.substring(len - 2, len);

                if (comp.equals("- ")) {
                    String result = temp.concat(sentence);
                    sentences.remove(sentences.size() - 2);
                    sentences.remove(sentences.size() - 1);
                    sentences.add(result);
                }

                //멘 앞 글자가 소문자이면 그 앞에 것과 합쳐줌
                char check_lower = sentences.get(sentences.size() - 1).charAt(0);
                if (Character.isLowerCase(check_lower) && sentences.size() > 1) {
                    merge();
                }

                // 지금 글자 수가 적고 끝이 .으로 끝나면.
                String dot_str = sentences.get(sentences.size() - 1);
//                if(len > 0 && dot_str.substring(len - 1, len).equals(". ")){
//                    sentences.add("dot으로 끝남..");
//                }
                len = dot_str.length();
                if(len > 1){
                    if (dot_str.substring(len - 2, len).equals(". ") && (dot_str.length() < 25)) {
                        merge();
                    }
                }


                //맨 앞글자 특수문자이면 그 앞이랑 합쳐줌
                String special = sentences.get(sentences.size() - 1);
                if(special.length()>0){
                    String check_spe = special.substring(0, 1);
                    if(!check_spe.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*")){
                        merge();
                    }
                }



                //년도 인지, 앞에 숫자 4개로 이루어져 있는지..
                String number = sentences.get(sentences.size() - 1);
                number = number.trim();
                if(number.length()>3){
                    String num = number.substring(0, 4);
                    if (isNumber(num))
                        merge();
                }

                //전치사로 끝나고 끝에 .?!가 없을 때...
                if(sentences.size()>1){
                    String str = sentences.get(sentences.size() - 2);
                    String[] array = str.split(" ");
                    String[] preposition = {"about", "above", "after", "against", "along", "around", "at", "beside", "beneath",
                            "between", "but", "by", "down", "during", "for", "from", "in", "into", "of", "off", "on", "out",
                            "over", "per", "round", "since", "through", "till", "to", "toward", "until", "up", "upon", "with",
                            "within", "without", "as"};
                    if(array.length > 0){
                        //마지막 글자 : array[array.length - 1]

                        String temp_pre = array[array.length - 1].trim();
                        int len_pre = temp_pre.length();
                        if(len_pre>0){
                            String temp_pre_check = temp_pre.substring(len_pre - 1, len_pre);
                            if(!(temp_pre_check.equals(".") || temp_pre_check.equals("?") || temp_pre_check.equals("!"))){
                                temp_pre_check = temp_pre.substring(0, len_pre);
                                for(int i = 0; i<preposition.length; i++){
                                    String tmp_lower = temp_pre_check.toLowerCase();
                                    if(tmp_lower.equals(preposition[i])){
                                        merge();
                                    }
                                }
                            }
                        }

                    }
//                    else{
//                        //한 문자 밖에 없을 때 확인 즉, array에 아무것도 안담길 때
//                        String temp_pre = str.trim();
//                        int len_pre = temp_pre.length();
//                        if(len_pre>0){
//                            String temp_pre_check = temp_pre.substring(len_pre - 1, len_pre);
//                            if(!(temp_pre_check.equals(".") || temp_pre_check.equals("?") || temp_pre_check.equals("!"))){
//                                temp_pre_check = temp_pre.substring(0, len_pre);
//                                for(int i = 0; i<preposition.length; i++){
//                                    String tmp_lower = temp_pre_check.toLowerCase();
//                                    if(tmp_lower.equals(preposition[i])){
//                                        merge();
//                                    }
//                                }
//                            }
//                        }
//                    }
                }

//
                //관사 체크
                if(sentences.size() > 1){
                    String str_g = sentences.get(sentences.size() - 2);
                    String[] array_g = str_g.split(" ");
                    String[] article = {"a", "an", "the"};
                    if(array_g.length>0){
                        String temp_g = array_g[array_g.length - 1].trim();
                        int len_g = temp_g.length();
                        if(len_g > 0){
                            String temp_g_check = temp_g.substring(len_g - 1, len_g);
                            if(!(temp_g_check.equals(".") || temp_g_check.equals("?") || temp_g_check.equals("!"))){
                                temp_g_check = temp_g.substring(0, len_g);
                                for(int i = 0; i<article.length; i++){
                                    String tmp_lower = temp_g_check.toLowerCase();
                                    if(tmp_lower.equals(article[i])){
                                        merge();
                                    }
                                }
                            }
                        }

                    }

                }
                //맨 끝이 ,&:(로 끝나면 그 다음거랑 합쳐줌.




                //reference를 만나면 그 뒤에 잘라버리기
                //무조건 하나는 받고 시작하므로 size는 1이상임.
                String reference = sentences.get(sentences.size() - 1);
                reference = reference.trim();
                reference = reference.toLowerCase();
                if(reference.equals("references")){
                    sentences.remove(sentences.size() - 1);
                    break;
                }




            }
        }


        //opennlp 사용하기
//        try {
//            InputStream inputStream = getAssets().open("en-sent.bin");
//            SentenceModel model = new SentenceModel(inputStream);
//
//            //Instantiating the SentenceDetectorME class
//            SentenceDetectorME detector = new SentenceDetectorME(model);
//
//            //Detecting the sentence
//            String sentencesArray[] = detector.sentDetect(text);
//            sentences = Arrays.asList(sentencesArray);
//
//        } catch (Exception e) {
//            //
//        }
    }
    private void merge(){
        String upper = sentences.get(sentences.size() - 1).toString();
        String prev_str = sentences.get(sentences.size() - 2).toString();
        String result = prev_str.concat(upper);
        sentences.remove(sentences.size() - 2);
        sentences.remove(sentences.size() - 1);
        sentences.add(result);
    }

    //문자열이 숫자(정수, 실수)인지 아닌지 판별한다.
    static boolean isNumber(String str) {
        char tempCh;
        int dotCount = 0;	//실수일 경우 .의 개수를 체크하는 변수
        boolean result = true;

        for (int i=0; i<str.length(); i++){
            tempCh= str.charAt(i);	//입력받은 문자열을 문자단위로 검사
            //아스키 코드 값이 48 ~ 57사이면 0과 9사이의 문자이다.
            if ((int)tempCh < 48 || (int)tempCh > 57){
                //만약 0~9사이의 문자가 아닌 tempCh가 .도 아니거나
                //.의 개수가 이미 1개 이상이라면 그 문자열은 숫자가 아니다.
                if(tempCh!='.' || dotCount > 0){
                    result = false;
                    break;
                }else{
                    //.일 경우 .개수 증가
                    dotCount++;
                }
            }
        }
        return result;
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
}
