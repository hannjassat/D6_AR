package com.example.district_6_AR;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class map_tour extends AppCompatActivity {

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setApiKeyForApp();
        setContentView(R.layout.activity_map_tour);


        // inflate MapView from layout
        mMapView = findViewById(R.id.mapView);
        // create a map with the a topographic basemap
        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);
        // set the map to be displayed in this view
        mMapView.setMap(map);
        mMapView.setViewpoint(new Viewpoint(-33.93070992379463, 18.431214141973413, 10000));

        Button locationInfoButton = (Button) findViewById(R.id.button6);
        locationInfoButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(map_tour.this, site_info.class));

            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.imageButton2);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(map_tour.this, home_page.class));
            }
        });


        MediaPlayer music = MediaPlayer.create(map_tour.this, R.raw.soundbite);
        //Audio Button
        FloatingActionButton audioButton = (FloatingActionButton) findViewById(R.id.floatingActionButton8);
        FloatingActionButton playButton = (FloatingActionButton) findViewById(R.id.floatingActionButton11);
        FloatingActionButton pauseButton = (FloatingActionButton) findViewById(R.id.floatingActionButton12);
        audioButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                music.start();

                audioButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        });


        playButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
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
    }

    @Override
    protected void onPause() {
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        mMapView.dispose();
        super.onDestroy();
    }

    //method to set up API key for ARCGIS
    //there is a more secure way to do this later
    private void setApiKeyForApp(){
        ArcGISRuntimeEnvironment.setApiKey("AAPK0ef642bcb3a843729d51745029ad400doiBTIVv1dpibTFxLfE8ZaOW8M1FpegMJPMY3DrTugNyJYtqdeUMITQ9LNwdN0mo6");
    }

    private void addGraphics() {

        // create a graphics overlay and add it to the map view
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(graphicsOverlay);

        Point st_marks = new Point(-118.8065, 34.0005, SpatialReferences.getWgs84());

        // create an opaque orange (0xFFFF5733) point symbol with a blue (0xFF0063FF) outline symbol
        SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, -0xa8cd, 10f);

        SimpleLineSymbol blueOutlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 2f);
        simpleMarkerSymbol.setOutline(blueOutlineSymbol);
    }
}
