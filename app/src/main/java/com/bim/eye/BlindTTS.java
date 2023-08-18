package com.bim.eye;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;



public class BlindTTS {

    int ok;
    // Initialize Variable for all the Button ID's you created in the xml file
    private Button TTS;

    // Object for TextToSpeech Engine that will convert the Text into the Audio File
    private TextToSpeech textToSpeech;
    private String s;

    private ImageView test;

    private MainLayoutActivity mActivity;
    private  MediaMetadataRetriever mediaMetadataRetriever;

    private Bitmap frame;

    private MainActivity sActivity;
    public BlindTTS(MainActivity activity,String str) {
        s="";
        sActivity=activity;
        textToSpeech = new TextToSpeech(sActivity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                // Called to signal the completion of the TextToSpeech engine initialization.
                if (i == TextToSpeech.SUCCESS){
                    // Select Language
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                }
                speak(str);
            }
        });
    }

    public Bitmap getFrame(){
        return frame;
    };

    public void click()
    {
        TTS.performClick();
    }

    public BlindTTS(MainLayoutActivity activity, VideoView videoView, Uri videoUri) {
        ok=0;
        mActivity=activity;
        mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(mActivity, videoUri);


        // Assign Variables declared above to each and every Button by using findViewById...
        // ... to find the ID of the buttons from the xml file so that it links to the Java file...
        // ...and we can assign any task that the particular tool will perform through the Java file
        TTS = activity.findViewById(R.id.tts);

        // Creating a new Object for textToSpeech
        // getAppliationContext() is to Return the context of the single, global Application object of the current process.
        // OnInitListener() is nothing but an Interface definition of a callback to be invoked, indicating the completion of the TextToSpeech engine initialization.
        textToSpeech = new TextToSpeech(activity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                // Called to signal the completion of the TextToSpeech engine initialization.
                if (i == TextToSpeech.SUCCESS){
                    // Select Language
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        // Setting the button (button variable ID of the Java file) as a clickable item
        // View.OnClickListener() is an Interface definition for a callback to be invoked, when a view is clicked.
        TTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // Get EditText Value

                mActivity.setTvText(s);

                videoView.pause();
                Bitmap bitmap = getFrame();// Get the current video frame
                String extractedText="";
                if(bitmap!=null) {
                    extractedText = TextExtractor.extractTextFromImage(mActivity, bitmap);// Call the OCR method
                    mActivity.setTvText(s);

                }
                else{
                    extractedText="";
                    mActivity.setTvText(s);

                }
                if (extractedText != null) { // If text is successfully extracted
                    setString(extractedText); // Set the text to the TextView
                    setString(extractedText);
                    mActivity.setTvText(s);


                } else {
                    setString("i have nothing to say");
                    mActivity.setTvText(s);

                    Toast.makeText(mActivity.getApplicationContext(), "Text extraction failed", Toast.LENGTH_SHORT).show(); // Show an error message
                }

                //text detection

                videoView.pause();
                long currentPosition = videoView.getCurrentPosition();

                frame = mediaMetadataRetriever.getFrameAtTime(currentPosition * 1000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);// Multiply by 1000 to convert to microseconds



                // toString() Returns a string representation of the object.
                // getText() Returns the Text that the Text View is displaying
                // Text Convert to Speech
                // QUEUE_FLUSH  is Queue mode where all entries in the playback queue (media to be played and text to be synthesized) are dropped and replaced by the new entry.

                int speech = textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);

                videoView.start();
                s="";




            }
        });
    }

    public void setString(String s) {
        this.s = s;
    }


    public void speak(String str){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "end of utterance");
        textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, params);

// Add a break after each sentence
        String utteranceId = this.hashCode() + "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.playSilentUtterance(750, TextToSpeech.QUEUE_ADD, utteranceId);
        }

    }


    public String getString() {
        return s;
    }

    public void stop(){
        textToSpeech.stop();
    }
    public void release() throws IOException {
        // Release the TextToSpeech engine resources
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            mediaMetadataRetriever.release();
        }
    }
}
