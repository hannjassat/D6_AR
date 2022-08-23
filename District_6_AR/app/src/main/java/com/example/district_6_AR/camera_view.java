package com.example.district_6_AR;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/** camera_view currently opens up the devices camera
 * and shows a preview of the device's camera input with a
 * transparent overlay of st_marks church
 **/
public class camera_view extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    public static final int REQUEST_CODE = 100;
    private SurfaceView surfaceView;
    private String[] neededPermissions = new String[]{CAMERA};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        surfaceView = findViewById(R.id.surfaceView);
        if (surfaceView != null) {
            boolean result = checkPermission();
            if (result) {
                setViewVisibility(R.id.surfaceView, View.VISIBLE);
                surfaceHolder = surfaceView.getHolder();
                surfaceHolder.addCallback(this);
            }
        }

        //back button routes the user back to the site info page
        FloatingActionButton backButton = (FloatingActionButton) findViewById(R.id.floatingActionButton6);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(camera_view.this, site_info.class));
            }
        });


    }

    //checkPermission() checks to see if the user has enabled camera permissions.
    // The method has been set up to easily add more permissions if needed
    // This method can, in future, be added to a Permissions java
    // class that can be used across all classes on the app since
    // permissions will be required in a few different places
    private boolean checkPermission() {
        //depending on the API version, the method of gaining permissions differ
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            ArrayList<String> permissionsNotGranted = new ArrayList<>();
            for (String permission : neededPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNotGranted.add(permission);
                }
            }
            //currently, just one permission is requested but since this is an evolutionary prototype, functionality to gain more than one permission has been included
            if (permissionsNotGranted.size() > 0) {
                boolean shouldShowAlert = false;
                for (String permission : permissionsNotGranted) {
                    shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                }
                if (shouldShowAlert) {
                    showPermissionAlert(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]));
                } else {
                    requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]));
                }
                return false;
            }
        }
        return true;
    }

    //show a pop-up to alert the user that permissions are needed
    private void showPermissionAlert(final String[] permissions) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(R.string.permission_required);
        alertBuilder.setMessage(R.string.permission_message);
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(permissions);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    //method to ask the user for permissions on their device
    private void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(camera_view.this, permissions, REQUEST_CODE);
    }

    //method to decipher if the user ended up allowing or denying permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(camera_view.this, R.string.permission_warning, Toast.LENGTH_LONG).show();
                        setViewVisibility(R.id.showPermissionMsg, View.VISIBLE);
                        return;
                    }
                }

                setViewVisibility(R.id.surfaceView, View.VISIBLE);
                surfaceHolder = surfaceView.getHolder();
                surfaceHolder.addCallback(this);
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //used to set the visibility (visible or invisible) of the surface view.
    // Helps with toggling the camera view on and off
    private void setViewVisibility(int id, int visibility) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    //Need to be overridden by SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startCamera();
    }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }
    //method to open the camera and display the input to the user
    private void startCamera() {
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }










}






