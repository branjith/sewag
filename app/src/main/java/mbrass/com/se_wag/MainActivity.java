package mbrass.com.se_wag;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    int problemId,userAnswer=1000;
    Problem problem;
    static Typeface typeface= null;
    TextView eqn1,eqn2,eqn3,eqn4,eqn_answer,answerBtn;
    static Vector<String> emoji = new Vector<String>();
    static Vector<String> success_msg = new Vector<String>();
    static Vector<String> failure_msg = new Vector<String>();
    static HashMap<String,Boolean> problem_status = new HashMap<String, Boolean>();
    static final int READ_BLOCK_SIZE = 100, increment = 5;
    HashMap<Integer,Integer> abc;
    static String fname="problem_status_test1.txt";
    static boolean readmsg_flag=false;
    HorizontalWheelView solutionWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        problemId=(int)getIntent().getSerializableExtra("problemId");

        readEmoji();
        abc = setEmojiRandom();
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "DidactGothic-Regular.ttf");

        eqn1 = (TextView) findViewById(R.id.eqn1Txt);
        eqn2 = (TextView) findViewById(R.id.eqn2Txt);
        eqn3 = (TextView) findViewById(R.id.eqn3Txt);
        eqn4 = (TextView) findViewById(R.id.eqn4Txt);
        answerBtn = (TextView) findViewById(R.id.answerBtn);

        solutionWheel = (HorizontalWheelView) findViewById(R.id.horizontalWheelView);

        answerBtn.setTypeface(typeface);
        setupListeners();
        readMsg();
        setEqns(problemId);
    }

    public void setEqns(int problemId){
        problem=LevelActivity.Problems.get(problemId);
        String tmp=problem.getEqn1();
        if(tmp.contains("?"))
            eqn_answer=eqn1;
        setEqn(eqn1,tmp);

        tmp=problem.getEqn2();
        if(tmp.contains("?"))
            eqn_answer=eqn2;
        setEqn(eqn2,tmp);

        tmp=problem.getEqn3();
        if(tmp.contains("?"))
            eqn_answer=eqn3;
        setEqn(eqn3,tmp);

        tmp=problem.getEqn4();
        if(tmp.contains("?"))
            eqn_answer=eqn4;
        setEqn(eqn4,tmp);
    }

    public void readMsg(){
        if(readmsg_flag==false) {
            String success[] = getResources().getStringArray(R.array.success_name);
            String failure[] = getResources().getStringArray(R.array.failure_name);
            for (int x = 0; x < success.length; x++)
                success_msg.add(success[x]);
            for (int x = 0; x < failure.length; x++)
                failure_msg.add(failure[x]);
            readmsg_flag=true;
            System.out.println("Reading messaging");
        }
    }

    private void setupListeners() {
        solutionWheel.setListener(new HorizontalWheelView.Listener() {
            @Override
            public void onRotationChanged(double radians) {
                updateText();
            }
        });
    }

    private void updateText() {
        String text = String.format(Locale.US, "%.0f°", solutionWheel.getDegreesAngle());

        text = text.substring(0,text.length()-1);
        userAnswer = Integer.parseInt(text)/3;

        String tmp=(String) eqn_answer.getText();
        tmp=tmp.substring(0,tmp.indexOf("=")+1)+userAnswer;
        eqn_answer.setText(tmp);
    }

    public void writeStatus() {
        FileOutputStream f1 = null;
        try {
            f1 = openFileOutput(fname, Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(f1);
            System.out.println("Eyes "+problem_status.size());
            Iterator it = problem_status.keySet().iterator();
            String pid;
            while(it.hasNext()){
                pid=(String)it.next();
                outputWriter.write(pid+"="+problem_status.get(pid) + "#\n");
            }
            outputWriter.flush();
            outputWriter.close();
            f1.close();
            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readStatus() {
        try {
            FileInputStream fileIn = openFileInput(fname);
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            s = s.trim();
            InputRead.close();
            System.out.println("ENFIELD :"+s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updateAnswer(TextView tv){
        String tmp = (String)tv.getText();
        System.out.println("Quiet "+tmp);
        boolean flag=false;
        if(tmp.length()>0&&tmp.contains("?")){
            tmp=tmp.replaceAll("[?]",String.valueOf(userAnswer));
            tv.setText(tmp);
            flag=true;
        }
        return flag;
    }

    public void checkAnswer(View view) {
        ViewDialog alert = new ViewDialog();

        if(userAnswer==problem.getResult()){
            System.out.println("Correct "+String.valueOf(problemId));
            alert.showDialog(this, "success");
            problem_status.put(String.valueOf(problemId),true);
            writeStatus();
            readStatus();
            problemId++;
            setEqns(problemId);
        }
        else{
            System.out.println("Wrong");
            alert.showDialog(this, "failure");
        }
    }

    public HashMap<Integer,Integer> setEmojiRandom(){

        abc = new HashMap<>();
        int random = 0;

        while(abc.size()<3){
            random = (int)Math.round((Math.random()*100)%emoji.size());
            if(!abc.containsValue(random)) {
                abc.put(abc.size(), random);
            }
        }
        return abc;
    }

    public void readEmoji(){
        try {
            InputStream is = getResources().openRawResource(R.raw.characters);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            String str = writer.toString();

            String[] strs=str.split("\n");

            for(int i=0;i<strs.length;i++){
                emoji.add(strs[i]);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setEqn(TextView tv,String text){

        text=text.equalsIgnoreCase("null")?"":text.trim();
        text=text.replaceAll("a", emoji.get(abc.get(0)));
        text=text.replaceAll("b", emoji.get(abc.get(1)));
        text=text.replaceAll("c", emoji.get(abc.get(2)));
        if(typeface!=null)
            tv.setTypeface(MainActivity.typeface);
        tv.setText(text);
    }

    public void gotoLevels(View v){
        Intent level_frame = new Intent(MainActivity.this, LevelActivity.class);
        startActivity(level_frame);
    }

    public void shareScreenshot(View v) {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = rootView.getDrawingCache();
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e("GREC", e.getMessage(), e);
        }
        Uri uri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        String shareBody = "new puzzle";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "puzzle");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

}


class Problem {

    private int id;
    private int a;
    private int b;
    private int c;

    private int result;
    private String eqn1,eqn2,eqn3,eqn4;
    private boolean lock=false;


    public Problem(JSONObject jsonObject){
        try {
            setA(Integer.valueOf((String)jsonObject.get("a")));
            setB(Integer.valueOf((String)jsonObject.get("b")));
            setC(Integer.valueOf((String)jsonObject.get("c")));

            setResult(Integer.valueOf((String)jsonObject.get("result")));

            setEqn1((String)jsonObject.get("eqn1"));
            setEqn2((String)jsonObject.get("eqn2"));
            setEqn3((String)jsonObject.get("eqn3"));
            setEqn4((String)jsonObject.get("eqn4"));

            setLock(Boolean.valueOf((String)jsonObject.get("lock")));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public String getEqn1() {
        return eqn1;
    }

    public void setEqn1(String eqn1) {
        this.eqn1 = eqn1;
    }

    public String getEqn2() {
        return eqn2;
    }

    public void setEqn2(String eqn2) {
        this.eqn2 = eqn2;
    }

    public String getEqn3() {
        return eqn3;
    }

    public void setEqn3(String eqn3) {
        this.eqn3 = eqn3;
    }

    public String getEqn4() {
        return eqn4;
    }

    public void setEqn4(String eqn4) {
        this.eqn4 = eqn4;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) { this.lock = lock;    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

}

class ViewDialog {

    public void showDialog(Activity activity, String msg){

        int layout_id;

        if(msg == "success"){
            int random_index = (int) Math.round((Math.random()*100)%MainActivity.success_msg.size());
            msg =  MainActivity.success_msg.get(random_index);
            layout_id = R.layout.activity_dialog;
        }else{
            int random_index = (int) Math.round((Math.random()*100)%MainActivity.failure_msg.size());
            msg = MainActivity.failure_msg.get(random_index);
            layout_id = R.layout.activity_dailog_failure;
        }

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(layout_id);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);

        if(MainActivity.typeface!=null)
            text.setTypeface(MainActivity.typeface);

        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}