package mbrass.com.se_wag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class LauncherActivity extends AppCompatActivity {

    TextView greeting_msg,start_msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        String[] greetings = getResources().getStringArray(R.array.greeting_name);
        int random_index = (int) Math.round((Math.random()*100)%greetings.length);
        greeting_msg = findViewById(R.id.greetingId);
        start_msg= findViewById(R.id.startId);
        greeting_msg.setText(greetings[random_index]);


        Animation blink;
        blink = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);

        Animation zoomin;
        zoomin = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoomin);

        Animation zoomout;
        zoomout = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoomout);

        Animation bounce;
        bounce = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bounce);

        Animation fadeout;
        fadeout = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadeout);

       // greeting_msg.setAnimation(zoomin);
        start_msg.setAnimation(blink);
        greeting_msg.setAnimation(fadeout);
    }

    public void onClick(View v){
        Intent main_frame = new Intent(LauncherActivity.this, LevelActivity.class);
        startActivity(main_frame);
    }
}
