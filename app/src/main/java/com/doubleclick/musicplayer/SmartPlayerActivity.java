package com.doubleclick.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SmartPlayerActivity extends AppCompatActivity {
    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerintent;
    private String Keeper;
    private ImageView pausePlayBtn, nextBtn, PreviousBtn, imageView;
    private TextView songNameTxt;
    private Button voiceEnabledBtn;
    private LinearLayout LowerRelativeLayout;
    private String mode = "ON";
    private MediaPlayer MyMediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String mSongName;
    private int posiontSong;
    /** Handles audio focus when playing a sound file */
    private AudioManager mAudioManager;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartplayer);
        checkVoiceCommendPermission();
        pausePlayBtn = findViewById(R.id.play_pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        PreviousBtn = findViewById(R.id.previos_btn);
        imageView = findViewById(R.id.logo);
        LowerRelativeLayout = findViewById(R.id.lower);
        voiceEnabledBtn = findViewById(R.id.voice_enabled_btn);
        songNameTxt = findViewById(R.id.SongName);
        parentRelativeLayout = findViewById(R.id.parentRelativeLayout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(SmartPlayerActivity.this);
        speechRecognizerintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //locale.getDefault() >> andicte to current Languges
        speechRecognizerintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        validataReceiveValueAndStartPlaying();
        imageView.setBackgroundResource(R.drawable.logo);
//        MyMediaPlayer.setNextMediaPlayer(MediaPlayer.create(SmartPlayerActivity.this,Uri.parse(mySongs.get(position+1).toString())));
//        AutoPlay(mySongs.size());
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
            }
            @Override
            public void onResults(Bundle results) {
                // get resulte from Speech Recognizer and store it in ArrayList from type String
                ArrayList<String> matchesFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matchesFound != null) {
                    if (mode.equals("ON")) {
                        // get index 0  from ArrayList
                        Keeper = matchesFound.get(0);
                        if (Keeper.equals("pause the song") || Keeper.equals("قف") || Keeper.equals("ستوب") || Keeper.equals("وقف")||Keeper.equals("stop")) {
                            PlayPauseSong();
                            Toast.makeText(SmartPlayerActivity.this, "Commend = " + Keeper, Toast.LENGTH_LONG).show();
                        } else if (Keeper.equals("play the song") || Keeper.equals("بلاي") || Keeper.equals("اشتغل")||Keeper.equals("play")) {
                            PlayPauseSong();
                            Toast.makeText(SmartPlayerActivity.this, "Commend = " + Keeper, Toast.LENGTH_LONG).show();
                        }
                        else if (Keeper.equals("play next song") || Keeper.equals("اللي بعده")) {
                            PlayNextSong();
                            Toast.makeText(SmartPlayerActivity.this, "Commend = " + Keeper, Toast.LENGTH_LONG).show();
                        }
                        else if (Keeper.equals("play previous song") || Keeper.equals("اللي قبله") ) {
                            PlayPreviousSong();
                            Toast.makeText(SmartPlayerActivity.this, "Commend = " + Keeper, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });


        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerintent);
                        //put Keeper null when I touch screen agen
                        Keeper = "";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }
        });

// for Buttons //////////////////////////////////////////////////////////////
        pausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayPauseSong();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyMediaPlayer.getCurrentPosition()>0){
                    PlayNextSong();
                }

            }
        });

        PreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyMediaPlayer.getCurrentPosition()>0){
                    PlayPreviousSong();
                }

            }
        });
////////////////////////////////////////////////////////////////////////////////
        voiceEnabledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == "ON") {
                    mode = "OFF";
                    voiceEnabledBtn.setText("Voice Enabled Mode - OFF");
                    LowerRelativeLayout.setVisibility(View.VISIBLE);
                } else {
                    mode = "ON";
                    voiceEnabledBtn.setText("Voice Enabled Mode - ON");
                    LowerRelativeLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    //this is for check Permission of Voice Commend is Granted or Denied
    public void checkVoiceCommendPermission() {

        // take peremeter (this Activity)
        Dexter.withActivity(this)
                // to take permission Record Audio and show for the user
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
//M indicated to marshmello  >>>> public static final int M = 23;
                        //N indicated to nougat   >>>>> public static final int N = 24;
                        //
                        //        /**
                        //         * N MR1: Nougat++.
                        //         */
                        //        public static final int N_MR1 = 25;
                        //public static final int O = 26; >> oreo
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (!(ContextCompat.checkSelfPermission(SmartPlayerActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                                // enter to Setting to edit the permission the record audio >> that is happen when frist time open the app
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void StopMedia(){
        if (MyMediaPlayer != null) {
            MyMediaPlayer.stop();
            MyMediaPlayer.release();
            releaseMediaPlayer();
        }
    }

    private void validataReceiveValueAndStartPlaying() {
        StopMedia();
        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
        //لإرسال البيانات بين two activities يوجد عدة طرق, كـ shared Preference و database, لكن الطريقة المباشرة للإرسال هي عن طريق Intent بإستخدام الدوال الخاصة بها putExtra() والتي تتعامل مع جميع الـ Primitive types بطريقة مباشرة, لكن في حال أردنا أن نرسل object أو list of objects سنضطر لإستخدام ما يسمى بـ parcelable
        //بداية ما هو الـ parcelable, هو interface يعمل كعمل الـ Serializable في جافا لكن مخصص لأندرويد, يعمل على تحويل الـ object إلى byte واستعادته كـ object مرة أخرى وفي هذه المقالة سنتكلم عنه وعن الية التعامل معه
        mySongs = (ArrayList) intent.getExtras().getParcelableArrayList("Song");
        mSongName = mySongs.get(position).getName();
        String SongName = intent.getStringExtra("Name");
        songNameTxt.setText(SongName);
        position = intent.getExtras().getInt("Position", 0);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        MyMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);
        MyMediaPlayer.start();
    }


    private void PlayPauseSong() {
        imageView.setBackgroundResource(R.drawable.five);
        if (MyMediaPlayer.isPlaying()) {
            pausePlayBtn.setImageResource(R.drawable.play);
            MyMediaPlayer.pause();
        } else {
            pausePlayBtn.setImageResource(R.drawable.pause);
            MyMediaPlayer.start();
            imageView.setBackgroundResource(R.drawable.four);
        }

    }

    private void PlayNextSong() {

//        MyMediaPlayer.pause();
        MyMediaPlayer.stop();
        MyMediaPlayer.release();
        position = ((position + 1) % mySongs.size());
        Uri uri = Uri.parse(mySongs.get(position).toString());
        MyMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);
        MyMediaPlayer.start();
        //to get Name of Song
        mSongName = mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        imageView.setBackgroundResource(R.drawable.three);


        if (MyMediaPlayer.isPlaying()) {
            pausePlayBtn.setImageResource(R.drawable.pause);
//            MyMediaPlayer.pause();
        } else {
            pausePlayBtn.setImageResource(R.drawable.play);
            MyMediaPlayer.start();
            imageView.setImageResource(R.drawable.five);
        }
    }

    private void PlayPreviousSong(){
        MyMediaPlayer.pause();
        MyMediaPlayer.stop();
        MyMediaPlayer.release();
        position = ((position-1) < 0 ? (mySongs.size() - 1) : (position - 1));

        Uri uri = Uri.parse(mySongs.get(position).toString());
        MyMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);
        MyMediaPlayer.start();
        mSongName = mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        imageView.setBackgroundResource(R.drawable.two);

        if (MyMediaPlayer.isPlaying()) {
            pausePlayBtn.setImageResource(R.drawable.pause);
//            MyMediaPlayer.pause();
        } else {
            pausePlayBtn.setImageResource(R.drawable.play);
            MyMediaPlayer.start();
            imageView.setImageResource(R.drawable.five);
        }
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (MyMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            MyMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
//            MyMediaPlayer = null;

            // Regardless of whether or not we were granted audio focus, abandon it. This also
            // unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    /**
     * This listener gets triggered whenever the audio focus changes
     * (i.e., we gain or lose audio focus because of another app or device).
     */
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing short sound files.

                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                MyMediaPlayer.pause();
                MyMediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                MyMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                releaseMediaPlayer();
            }
        }
    };

//    @Override
//    protected void onStop() {
//        super.onStop();
//        // When the activity is stopped, release the media player resources because we won't
//        // be playing any more sounds.
//        releaseMediaPlayer();
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyMediaPlayer.stop();
    }


//    private void AutoPlay(final int size){
//            MyMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    PlayNextSong();
//
//                }
//            });
//    }
}

