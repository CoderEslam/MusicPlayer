package com.doubleclick.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView ListviewSongs;
    private String[] itemsAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListviewSongs = findViewById(R.id.ListviewSongs);

        appExternalStorageStoragePermission();
    }

    public void appExternalStorageStoragePermission() {
        // take peremeter (this Activity)
        // and take permission for external storage
        Dexter.withActivity(this)
                // to read External Storage
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        displayAudioSongsName();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    //retuen a ArrayList from type File
    public ArrayList<File> ReadOnlyAudioSongs(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        try {
            File[] allFile = file.listFiles(); // file.listFiles return File[] type from array  this makes files in External Storage ordered a in a list as array and choose what he want
            for (File individualFile : allFile) {
                if (individualFile.isDirectory() && !individualFile.isHidden()) {
                    // Recursion استدعاء الداله في نفسها
                    //first read all of them then put it in arrayList
                    arrayList.addAll(ReadOnlyAudioSongs(individualFile));
                } else {
                    if (individualFile.getName().endsWith(".mp3") || individualFile.getName().endsWith(".aac") || individualFile.getName().endsWith(".wma") || individualFile.getName().endsWith(".wav")) {
                        // add only in array List if ended with (mp3) or (aac) or (wma) or (wav)
                        arrayList.add(individualFile);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Exception = ", e.getMessage());
        }
        return arrayList;
    }

    private void displayAudioSongsName() {
        final ArrayList<File> audioSongs = ReadOnlyAudioSongs(Environment.getExternalStorageDirectory());

        itemsAll = new String[audioSongs.size()];

        for (int songsCounter = 0; songsCounter < audioSongs.size(); songsCounter++) {
            //to get name of song and store it in String[] array
            itemsAll[songsCounter] = audioSongs.get(songsCounter).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, itemsAll);
        ListviewSongs.setAdapter(arrayAdapter);

        ListviewSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName = ListviewSongs.getItemAtPosition(position).toString();
                Intent intent = new Intent(MainActivity.this, SmartPlayerActivity.class);
//                MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.parse(audioSongs.get(position).toString()));
                intent.putExtra("Song", audioSongs);
                intent.putExtra("Name", songName);
                intent.putExtra("Position", position);
                startActivity(intent);
            }
        });
    }

}
