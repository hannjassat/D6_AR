package com.example.district_6_AR;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * site_info displays textual information about a site on the user's tour (for the prototype St Mark's Church)
 * and provides access to media content - video and audio functioanlity as well as AR View
 * Although not included in the prototype, there will be functionality to react to/share the content
 **/
public class site_info
        extends AppCompatActivity
        implements MediaPlayer.OnCompletionListener {

    VideoView vw;
    MediaPlayer music;
	//using an array list so a playlist of videos can be played consecutively 
    ArrayList<Integer> videolist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_info);
        vw = (VideoView)findViewById(R.id.videoView);
        vw.setMediaController(new MediaController(this));        
	vw.setOnCompletionListener(this);
        vw.setZOrderOnTop(true);

	//For the demo, one video will be added to the playlist 
        videolist.add(R.raw.video);
        setVideo(videolist.get(0));

        //Read More Button
        Button readMoreButton = (Button) findViewById(R.id.button2);
        readMoreButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(site_info.this, read_more.class));

            }
        });

        //React Button
        FloatingActionButton reactButton = (FloatingActionButton)findViewById(R.id.floatingActionButton4);
        reactButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(site_info.this, reaction.class));
            }
        });

        //AR Button
        FloatingActionButton aRButton = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        aRButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(site_info.this, camera_view.class));
            }
        });

        music = MediaPlayer.create(site_info.this, R.raw.soundbite);
        //Audio Button
        FloatingActionButton audioButton = (FloatingActionButton) findViewById(R.id.floatingActionButton3);
        FloatingActionButton pauseButton = (FloatingActionButton) findViewById(R.id.floatingActionButton5);
        FloatingActionButton playButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        audioButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                vw.pause();
                music.start();
                audioButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        });


        playButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                vw.pause();
                music.start();
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);

            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                music.pause();
                pauseButton.setVisibility(View.INVISIBLE);
                playButton.setVisibility(View.VISIBLE);

            }
        });

        //AR Button
        FloatingActionButton tourButton = findViewById(R.id.floatingActionButton1);
        tourButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(site_info.this, map_tour.class));
            }
        });

    }


    /**This method retrieves the video from the source files and loads it into a media player
     */
    public void setVideo(int id)
    {
//set the path of the video to be played
        String uriPath
                = "android.resource://"
                + getPackageName() + "/" + id;
        Uri uri = Uri.parse(uriPath);
        vw.setVideoURI(uri);
        vw.start();
    }

    /** This method dictates what will occur when the video has finished playing.
     * For now, the video just repeats.
     */
    public void onCompletion(MediaPlayer mediaplayer)
    {
        setVideo(videolist.get(0));
        vw.start();
    }

}