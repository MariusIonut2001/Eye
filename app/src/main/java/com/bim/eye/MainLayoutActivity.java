package com.bim.eye;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;


//The Main Class
//AppCompatActivity in the Video
public class MainLayoutActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_WRITE_SETTINGS =1 ;

    private static int LOADING_TIME = 10000; // in milliseconds
    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;
    private static final int REQUEST_LOCATION_SOURCE_SETTINGS = 1;

    private VideoView videoView;
    protected static final int RESULT_SPEECH = 1;
    private Button STT;
    private TextView tvText;
    private BlindTTS TTS;
    private Button ITT;


    public String getTvText() {
        return (String) tvText.getText();
    }

    public void setTvText(String text) {
        tvText.setText(text);
    }


    private RelativeLayout loadingLayout;

    private static final long DELAY_MS = 4000;

    static int ok=1;
    private Handler handler = new Handler();

    private Runnable buttonClickRunnable = new Runnable() {
        @SuppressLint("SuspiciousIndentation")
        @Override
        public void run() {


            if (ok==0)
            {TTS.click();}
            handler.postDelayed(this, DELAY_MS);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(buttonClickRunnable);
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        handler.postDelayed(buttonClickRunnable, DELAY_MS);
        videoView = findViewById(R.id.video);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video);


//         Create a MediaController object and set it to the VideoView
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Set the path of the video file to the VideoView

        videoView.setVideoURI(videoUri);

        // Start the video playback
        videoView.start();
        tvText = findViewById(R.id.tvText);
        tvText.setText("The text will be diplayed here");
        TTS = new BlindTTS(MainLayoutActivity.this, videoView, videoUri);


        TTS.setString("i have nothing to say");


        STT = findViewById(R.id.stt);

//        ITT = findViewById(R.id.itt);
//        ITT.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                videoView.pause();
//                Bitmap bitmap = TTS.getFrame(); // Get the current video frame
//                String extractedText = TextExtractor.extractTextFromImage(MainActivity.this, bitmap);// Call the OCR method
//                if (extractedText != null) { // If text is successfully extracted
//                    tvText.setText(extractedText); // Set the text to the TextView
//                    TTS.setString(extractedText);
//
//                } else {
//                    TTS.setString("i have nothing to say");
//                    Toast.makeText(getApplicationContext(), "Text extraction failed", Toast.LENGTH_SHORT).show(); // Show an error message
//                }
//            }
//        });


        STT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.pause();
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    tvText.setText("");
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private String getBatteryPercentage() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = (level / (float) scale) * 100;
        return String.format("%.0f%%", batteryPct);
    }

    private String getTimeAndDate() {
        Calendar calendar = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
        }
        SimpleDateFormat simpleDateFormat = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            simpleDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return simpleDateFormat.format(calendar.getTime());
        }
        return "not found.";
    }

    private void setBrightnessToMinimum() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_WRITE_SETTINGS);
        }
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 4);
    }

    private void setBrightnessToMaximum() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_WRITE_SETTINGS);
        }
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
    }

    private void disableAdaptiveBrightness() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_WRITE_SETTINGS);
        }
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        } catch (SecurityException e) {
            // Handle exception here
        }
    }



    private void enableAdaptiveBrightness() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_WRITE_SETTINGS);
        }
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        } catch (SecurityException e) {
            // Handle exception here
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_SPEECH:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    tvText.setText(text.get(0));
//                    tts.setString(text.get(0));
//                    videoView.start();
                    String command = text.get(0);
//                    tvText.setText(command);
//                    tts.setString(command);

                    // Handle speech commands
                    if (command.equals("close the app")) {
                        finish();
                    }
                    else if (command.equals("what is in front of me")) {
                        // Perform click action on the desired button view
                        TTS.click();
                    }else if (command.equals("raise volume")) {
                        // Increase the media volume by a fixed amount
                        AudioManager audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
                        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        int newVolume = Math.min(currentVolume + 2, maxVolume);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_SHOW_UI);
                    } else if (command.equals("lower volume")) {
                        // Decrease the media volume by a fixed amount
                        AudioManager audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
                        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        int newVolume = Math.max(currentVolume - 2, 0);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_SHOW_UI);
                    }
                    else if(command.equals("battery")){
                        tvText.setText(getBatteryPercentage());
                    } else if(command.equals("date and time")){
                        tvText.setText(getTimeAndDate());
                    } else if(command.equals("power saving")){
                        if (requestCode == REQUEST_WRITE_SETTINGS) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(this)) {
                                // Permission granted
                                setBrightnessToMinimum();

                            } else {
                                // Permission not granted
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
                                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else if(command.equals("maximum brightness")){
                        if (requestCode == REQUEST_WRITE_SETTINGS) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(this)) {
                                // Permission granted
                                setBrightnessToMaximum();

                            } else {
                                // Permission not granted
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);

                                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else if(command.equals("stop adaptive brightness")){
                        if (requestCode == REQUEST_WRITE_SETTINGS) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(this)) {
                                // Permission granted, enable adaptive brightness
                                disableAdaptiveBrightness();
                                Toast.makeText(this, "Adaptive brightness enabled", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Start the permission request
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, REQUEST_WRITE_SETTINGS);
                        }
                    }

                    else if(command.equals("sart adaptive brightness")){
                        if (requestCode == REQUEST_WRITE_SETTINGS) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(this)) {
                                // Permission granted, enable adaptive brightness
                                enableAdaptiveBrightness();
                                Toast.makeText(this, "Adaptive brightness enabled", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Start the permission request
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, REQUEST_WRITE_SETTINGS);
                        }
                    } else if (command.equals("start automatic detection")) {
                        ok=0;
                    }
                    else if (command.equals("stop automatic detection")) {
                        ok=1;
                    } else if (command.equals("help")) {
                        String str = "Sure, here is a list of all supported vocal commands:\n" +
                                "close the app which closes the app.\n" +
                                "raise volume and lower volume.\n" +
                                "What is in front of me.\n" +
                                "battery which gives battery info so that you know when to recharge your phone.\n" +
                                "date and time.\n" +
                                "power saving which is a command useful for saving battery by dimming the brightness.\n" +
                                "maximum brightness.\n" +
                                "start adaptive brightness.\n" +
                                "stop adaptive brightness.\n" +
                                "start automatic detection and stop automatic detection which are used to automate or to stop automating the detection process.";

                        TTS.speak(str);

                    } else if (command.equals("stop talking")) {
                        try {
                            TTS.release();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }


                    videoView.start();
                }
                break;
        }

    }



}