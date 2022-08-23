package com.example.district_6_AR;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class home_page extends AppCompatActivity {
    Button start_tour_button;
    Button website_button;
    FloatingActionButton download_button;
    MaterialToolbar info_button;

    // for download popup
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    // for info popup
    private AlertDialog.Builder dialogBuilderInfo;
    private AlertDialog dialogInfo;

    /**
     Creates the home page containing various buttons.
     The home page directs users to the website and the tour.
     The download function is currently represented by an popup message.
     The information button provides information on how to use the home page.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        start_tour_button = findViewById(R.id.start_tour);  // casting
        website_button = findViewById(R.id.visit_website);
        download_button = (FloatingActionButton) findViewById(R.id.download);
        info_button = (MaterialToolbar) findViewById(R.id.toolbar2);

        start_tour_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setContentView(R.layout.activity_site_info);
                Intent intent = new Intent(home_page.this, map_tour.class);
                startActivity(intent);
            }
        });

        // Press Download icon
        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDownloadPopUP();
            }
        });

        // Press Info icon
        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInfoPopUP();
            }
        });

        website_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.districtsix.co.za/"));
                startActivity(intent);

            }
        });

    }

    /**
     Creates the download popup, displays the text and enables user to close the popup.
     */

    public void createDownloadPopUP(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View downloadPopUp = getLayoutInflater().inflate(R.layout.activity_download_popup, null);

        dialogBuilder.setView(downloadPopUp);
        dialog = dialogBuilder.create();
        dialog.show();

        // close the popup
        FloatingActionButton exit_download = (FloatingActionButton) downloadPopUp.findViewById(R.id.exit_button);
        exit_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     Creates the information popup, displays the text and enables user to close the popup.
     */

    // creates info pop up
    public void createInfoPopUP(){
        dialogBuilderInfo = new AlertDialog.Builder(this);
        final View infoPopUp = getLayoutInflater().inflate(R.layout.activity_home_info, null);

        dialogBuilderInfo.setView(infoPopUp);
        dialogInfo = dialogBuilderInfo.create();
        dialogInfo.show();

        // close the popup
        FloatingActionButton exit_info = (FloatingActionButton) infoPopUp.findViewById(R.id.exit_button1);
        exit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInfo.dismiss();
            }
        });
    }
}