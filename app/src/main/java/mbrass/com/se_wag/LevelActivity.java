package mbrass.com.se_wag;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Vector;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class LevelActivity extends AppCompatActivity {

    TableLayout levels;
    Typeface typeface;
    static Vector<Problem> Problems;
    int problem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        Problems = readProblems();
        readStatus();
        levels = (TableLayout) findViewById(R.id.levelsTable);
        TableRow t1 =null;
        int textSize = 100;
        ImageView img_a,img_b,img_c;
        TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        boolean flag=false;

        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "DidactGothic-Regular.ttf");

        Iterator it = MainActivity.problem_status.keySet().iterator();
        String tmp=null;
        while(it.hasNext()){
            tmp=(String)it.next();
            System.out.println("KERNEL :"+tmp+"::"+MainActivity.problem_status.get(tmp)+" DONE");
        }

//        for(int i=0;i<Problems.size();){
        for(int i=0;i<9;){

            img_a = new ImageView(this);
            setupLevel(img_a,i);
            i++;

            img_b = new ImageView(this);
            setupLevel(img_b,i);
            i++;

            img_c = new ImageView(this);
            setupLevel(img_c,i);
            i++;

            t1 = new TableRow(this);
            t1.addView(img_a);
            t1.addView(img_b);
            t1.addView(img_c);

            flag=!flag;
            t1.setPadding(0,30,0,30);
            levels.addView(t1);
        }
    }

    public void setupLevel(ImageView tv,int text){
        //tv.setTextColor(Color.parseColor("#800080"));
        tv.setId(Integer.valueOf(text));
        boolean flag = MainActivity.problem_status.get(String.valueOf(text))==null?false:true;

        System.out.println("CLAY "+text+"::"+Problems.get(text).isLock()+"::"+flag);
        if(Problems.get(text).isLock()||flag) {
            // TEXT + 1
            tv.setImageBitmap(textAsBitmap(String.valueOf(text+1), 50, Color.parseColor("#800080")));
            tv.setEnabled(true);
            System.out.println("Enabled :"+text);
        }
        else {
            tv.setImageResource(R.drawable.ic_lock_white);
            tv.setEnabled(false);
        }

        if(text%2==0)
            tv.setBackgroundResource(R.drawable.circle);
        else
            tv.setBackgroundResource(R.drawable.circle1);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("CLICKED "+view.getId());
                selectLevel(view);
            }
        });
        tv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    public void selectLevel(View v){
        problem=v.getId();
        openProblem();
    }


    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        //paint.setTypeface(typeface);
        paint.setColor(textColor);

        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }


    public void readStatus() {
        try {
            FileInputStream fileIn = openFileInput(MainActivity.fname);
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            char[] inputBuffer = new char[MainActivity.READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            s = s.trim();
            InputRead.close();

            System.out.println(" SPY : "+s);

            if(s.length()>0){
                String statuses[] = s.split("#");
                String values[];
                for(int i=0;i<statuses.length;i++){
                    System.out.println("Baby :"+statuses[i]);
                    values=statuses[i].split("=");
                    if(values.length==2)
                        MainActivity.problem_status.put(values[0],Boolean.valueOf(values[1]));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Vector<Problem> readProblems(){
        Vector<Problem> Problems = new Vector<Problem>();
        try {
            InputStream is = getResources().openRawResource(R.raw.problems);
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

            String jsonString = writer.toString();

            JSONTokener tokener = new JSONTokener(jsonString);
            JSONArray jarray = new JSONArray(tokener);

            Problem tmp=null;

            for(int i=0;i<jarray.length();i++){
                Problems.add(new Problem((JSONObject) jarray.get(i)));
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return Problems;
    }

    public void openProblem(){
        Intent level_frame = new Intent(LevelActivity.this, MainActivity.class);
        level_frame.putExtra("problemId", problem);
        startActivity(level_frame);
    }

}
