package com.bim.eye;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;


//The Main Class
//AppCompatActivity in the Video
public class MainActivity extends AppCompatActivity {

    private RelativeLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading_screen);
        loadingLayout = findViewById(R.id.loading_layout);
        String str = "Hello, my name is Iris, please allow me to be your eyes \n \n \n You can skip this by " +
                "tapping anywhere on the screen \n \n Ok, \n so you are still here so let's proceed with an indetail guide of the app" +
                "\n \n \n \n The app as 2 buttons in the bottom of your screen\n" +
                "With the left button you can give me commands by talking \n hint \n  \n \n use \n help \n  to see what i can do for you" +
                "\n \n \n \n Now onto the right button \n this button will detect what captures your camera\n" +
                "it can detect persons plants doors text and other objects" +
                "\n \n \n You also have a live video playing in a frame which has live feedback of what is detected and how" +
                "\n this is for the users that are not fully blind or for the users that have an care-taker\n" +
                "and on right from this video player you have the live text that is detected\n" +
                "\n \n \n \n I hope everything is clear!";

        BlindTTS tts=new BlindTTS(MainActivity.this,str);

        loadingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainLayoutActivity();

                    tts.stop();

            }
        });
    }

    private void startMainLayoutActivity() {
        Intent intent = new Intent(MainActivity.this, MainLayoutActivity.class);
        startActivity(intent);
        finish();
    }

}