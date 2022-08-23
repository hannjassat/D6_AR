package com.example.district_6_AR;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

/**read_more provides the user with a more in depth textual description of the site
 * The Read More page includes a scroll plane filled with text and images for the user to
 * scroll through
 */
public class read_more extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_more);

        //back button routes the user back to the site info page
        ImageButton backButton = (ImageButton) findViewById(R.id.imageButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(read_more.this, site_info.class));
            }
        });

    }

}