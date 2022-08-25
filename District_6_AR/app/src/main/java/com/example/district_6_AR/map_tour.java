package com.example.district_6_AR;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.RouteTrackerLocationDataSource;
import com.esri.arcgisruntime.location.SimulatedLocationDataSource;
import com.esri.arcgisruntime.location.SimulationParameters;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.navigation.DestinationStatus;
import com.esri.arcgisruntime.navigation.ReroutingParameters;
import com.esri.arcgisruntime.navigation.RouteTracker;
import com.esri.arcgisruntime.navigation.TrackingStatus;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.tasks.networkanalysis.ClosestFacilityParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.ClosestFacilityTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Facility;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;
import com.esri.arcgisruntime.tasks.networkanalysis.TravelMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class map_tour extends AppCompatActivity {

    private MapView mMapView;
    private TextToSpeech mTextToSpeech;
    private boolean mIsTextToSpeechInitialized = false;

    private SimulatedLocationDataSource mSimulatedLocationDataSource;
    private RouteTracker mRouteTracker;
    private Graphic mRouteAheadGraphic;
    private Graphic mRouteTraveledGraphic;
    private Button mRecenterButton;


    private static final String TAG = map_tour.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setApiKeyForApp();
        setContentView(R.layout.activity_map_navigation);


        // inflate MapView from layout
        mMapView = findViewById(R.id.mapView);
        // create a map with the a topographic basemap
        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);
        // set the map to be displayed in this view
        mMapView.setMap(map);
        mMapView.setViewpoint(new Viewpoint(-33.93070992379463, 18.431214141973413, 10000));

        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(graphicsOverlay);

        mTextToSpeech = new TextToSpeech(this, status -> {
            if(status != TextToSpeech.ERROR){
                mTextToSpeech.setLanguage(Resources.getSystem().getConfiguration().locale);
                mIsTextToSpeechInitialized = true;
            }
        });

        // clear any graphics from the current graphics overlay
        mMapView.getGraphicsOverlays().get(0).getGraphics().clear();

        // generate a route with directions and stops for navigation
        RouteTask routeTask = new RouteTask(this, getString(R.string.routing_service_url));
        ListenableFuture<RouteParameters> routeParametersFuture = routeTask.createDefaultParametersAsync();
        routeParametersFuture.addDoneListener(() -> {

            try {
                // define the route parameters
                RouteParameters routeParameters = routeParametersFuture.get();
                routeParameters.setStops(getStops());
                routeParameters.setReturnDirections(true);
                routeParameters.setReturnStops(true);
                routeParameters.setReturnRoutes(true);
                //routeParameters.setTravelMode(routeParameters.getTravelMode());
                ListenableFuture<RouteResult> routeResultFuture = routeTask.solveRouteAsync(routeParameters);
                routeParametersFuture.addDoneListener(() -> {
                    try {
                        // get the route geometry from the route result
                        RouteResult routeResult = routeResultFuture.get();
                        Polyline routeGeometry = routeResult.getRoutes().get(0).getRouteGeometry();
                        // create a graphic for the route geometry
                        Graphic routeGraphic = new Graphic(routeGeometry,
                                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 5f));
                        // add it to the graphics overlay
                        mMapView.getGraphicsOverlays().get(0).getGraphics().add(routeGraphic);
                        // set the map view view point to show the whole route
                        mMapView.setViewpointAsync(new Viewpoint(routeGeometry.getExtent()));

                        //TODO : add a start navigation button

                        //create a button to start navigation with the given route
                        Button navigateRouteButton = findViewById(R.id.navigateRouteButton);
                        navigateRouteButton.setOnClickListener(v -> startNavigation(routeTask, routeParameters, routeResult));

                        // start navigating
                        startNavigation(routeTask, routeParameters, routeResult);
                    } catch (ExecutionException | InterruptedException e) {
                        String error = "Error creating default route parameters: " + e.getMessage();
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                        Log.e(TAG, error);
                    }
                });
            } catch (InterruptedException | ExecutionException e) {
                String error = "Error getting the route result " + e.getMessage();
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                Log.e(TAG, error);
            }
        });


        //TODO: add a recenter button

        // wire up recenter button
        mRecenterButton = findViewById(R.id.recenterButton);
        mRecenterButton.setEnabled(false);
        mRecenterButton.setOnClickListener(v -> {
            mMapView.getLocationDisplay().setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
            mRecenterButton.setEnabled(false);
        });






//        Button locationInfoButton = (Button) findViewById(R.id.button6);
//        locationInfoButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                startActivity(new Intent(map_tour.this, site_info.class));
//
//            }
//        });
//
//        ImageButton backButton = (ImageButton) findViewById(R.id.imageButton2);
//        backButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                startActivity(new Intent(map_tour.this, home_page.class));
//            }
//        });
//
//
//        MediaPlayer music = MediaPlayer.create(map_tour.this, R.raw.soundbite);
//        //Audio Button
//        FloatingActionButton audioButton = (FloatingActionButton) findViewById(R.id.floatingActionButton8);
//        FloatingActionButton playButton = (FloatingActionButton) findViewById(R.id.floatingActionButton11);
//        FloatingActionButton pauseButton = (FloatingActionButton) findViewById(R.id.floatingActionButton12);
//        audioButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                music.start();
//
//                audioButton.setVisibility(View.INVISIBLE);
//                pauseButton.setVisibility(View.VISIBLE);
//            }
//        });
//
//
//        playButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v) {
//                music.start();
//                playButton.setVisibility(View.INVISIBLE);
//                pauseButton.setVisibility(View.VISIBLE);
//
//            }
//        });
//
//        pauseButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v) {
//                music.pause();
//                pauseButton.setVisibility(View.INVISIBLE);
//                playButton.setVisibility(View.VISIBLE);
//
//            }
//        });
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

    private void startNavigation(RouteTask routeTask, RouteParameters routeParameters, RouteResult routeResult) {

        // clear any graphics from the current graphics overlay
        mMapView.getGraphicsOverlays().get(0).getGraphics().clear();

        // get the route's geometry from the route result
        Polyline routeGeometry = routeResult.getRoutes().get(0).getRouteGeometry();
        // create a graphic (with a dashed line symbol) to represent the route
        mRouteAheadGraphic = new Graphic(routeGeometry,
                new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, Color.MAGENTA, 5f));
        mMapView.getGraphicsOverlays().get(0).getGraphics().add(mRouteAheadGraphic);
        // create a graphic (solid) to represent the route that's been traveled (initially empty)
        mRouteTraveledGraphic = new Graphic(routeGeometry,
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 5f));
        mMapView.getGraphicsOverlays().get(0).getGraphics().add(mRouteTraveledGraphic);

        // get the map view's location display
        LocationDisplay locationDisplay = mMapView.getLocationDisplay();
        // set up a simulated location data source which simulates movement along the route
        mSimulatedLocationDataSource = new SimulatedLocationDataSource();
        SimulationParameters simulationParameters = new SimulationParameters(Calendar.getInstance(), 35, 5, 5);
        mSimulatedLocationDataSource.setLocations(routeGeometry, simulationParameters);

        // set up a RouteTracker for navigation along the calculated route
        mRouteTracker = new RouteTracker(getApplicationContext(), routeResult, 0, true);
        ReroutingParameters reroutingParameters = new ReroutingParameters(routeTask, routeParameters);
        mRouteTracker.enableReroutingAsync(reroutingParameters);

        // create a route tracker location data source to snap the location display to the route
        RouteTrackerLocationDataSource routeTrackerLocationDataSource = new RouteTrackerLocationDataSource(mRouteTracker, mSimulatedLocationDataSource);
        // set the route tracker location data source as the location data source for this app
        locationDisplay.setLocationDataSource(routeTrackerLocationDataSource);
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
        // if the user navigates the map view away from the location display, activate the recenter button
        locationDisplay.addAutoPanModeChangedListener(autoPanModeChangedEvent -> mRecenterButton.setEnabled(true));


        // get a reference to navigation text views
        TextView distanceRemainingTextView = findViewById(R.id.distanceRemainingTextView);
        TextView timeRemainingTextView = findViewById(R.id.timeRemainingTextView);
        TextView nextDirectionTextView = findViewById(R.id.nextDirectionTextView);

        // listen for changes in location
        locationDisplay.addLocationChangedListener(locationChangedEvent -> {
            // listen for new voice guidance events
            mRouteTracker.addNewVoiceGuidanceListener(newVoiceGuidanceEvent -> {
                // use Android's text to speech to speak the voice guidance
                speakVoiceGuidance(newVoiceGuidanceEvent.getVoiceGuidance().getText());
                nextDirectionTextView
                        .setText(getString(R.string.next_direction, newVoiceGuidanceEvent.getVoiceGuidance().getText()));
            });

            // get the route's tracking status
            TrackingStatus trackingStatus = mRouteTracker.getTrackingStatus();
            // set geometries for the route ahead and the remaining route
            mRouteAheadGraphic.setGeometry(trackingStatus.getRouteProgress().getRemainingGeometry());
            mRouteTraveledGraphic.setGeometry(trackingStatus.getRouteProgress().getTraversedGeometry());

            // get remaining distance information
            TrackingStatus.Distance remainingDistance = trackingStatus.getDestinationProgress().getRemainingDistance();
            // covert remaining minutes to hours:minutes:seconds
            String remainingTimeString = DateUtils
                    .formatElapsedTime((long) (trackingStatus.getDestinationProgress().getRemainingTime() * 60));

            // update text views
            distanceRemainingTextView.setText(getString(R.string.distance_remaining, remainingDistance.getDisplayText(),
                    remainingDistance.getDisplayTextUnits().getPluralDisplayName()));
            timeRemainingTextView.setText(getString(R.string.time_remaining, remainingTimeString));

            // if a destination has been reached
            if (trackingStatus.getDestinationStatus() == DestinationStatus.REACHED) {
                // if there are more destinations to visit. Greater than 1 because the start point is considered a "stop"
                if (mRouteTracker.getTrackingStatus().getRemainingDestinationCount() > 1) {
                    // switch to the next destination
                    mRouteTracker.switchToNextDestinationAsync();
                    Toast.makeText(this, "Navigating to the second stop, the Fleet Science Center.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Arrived at the final destination.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // start the LocationDisplay, which starts the RouteTrackerLocationDataSource and SimulatedLocationDataSource
        locationDisplay.startAsync();
        Toast.makeText(this, "Navigating to the first stop, the USS San Diego Memorial.", Toast.LENGTH_LONG).show();
    }

    /**
     * Uses Android's text to speak to say the latest voice guidance from the RouteTracker out loud.
     */
    private void speakVoiceGuidance(String voiceGuidanceText) {
        if (mIsTextToSpeechInitialized && !mTextToSpeech.isSpeaking()) {
            mTextToSpeech.speak(voiceGuidanceText, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    /**
     * Creates a list of stops along a route.
     */
    private static List<Stop> getStops() {
        List<Stop> stops = new ArrayList<>(3);
        // San Diego Convention Center
        Stop conventionCenter = new Stop(new Point(-117.160386, 32.706608, SpatialReferences.getWgs84()));
        stops.add(conventionCenter);
        // USS San Diego Memorial
        Stop memorial = new Stop(new Point(-117.173034, 32.712327, SpatialReferences.getWgs84()));
        stops.add(memorial);
        // RH Fleet Aerospace Museum
        Stop aerospaceMuseum = new Stop(new Point(-117.147230, 32.730467, SpatialReferences.getWgs84()));
        stops.add(aerospaceMuseum);
        return stops;
    }








}
