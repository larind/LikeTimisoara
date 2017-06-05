package com.example.larisa.liketimisoara.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.larisa.liketimisoara.Attraction;
import com.example.larisa.liketimisoara.AttractionType;
import com.example.larisa.liketimisoara.R;

import java.util.ArrayList;
import java.util.Collections;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final Attraction chosenAttraction = (Attraction) getIntent().getSerializableExtra("EXTRA_ATTRACTION");

        TextView attractionTitleTextView = (TextView) findViewById(R.id.attraction_title_textView);
        attractionTitleTextView.setText(chosenAttraction.getName());

        TextView attractionInfoTextView = (TextView) findViewById(R.id.details_textview);
        attractionInfoTextView.setText(chosenAttraction.getInfo());

        ImageView attractionPictureImageView = (ImageView) findViewById(R.id.details_imageview);
        attractionPictureImageView.setBackground(getResources().getDrawable(chosenAttraction.getImageResourceId()));

        TextView attractionCallTextView = (TextView) findViewById(R.id.phone_details);
        ImageButton attractionCallImageView = (ImageButton) findViewById(R.id.phone_image);

        if(chosenAttraction.getType().equals(AttractionType.RESTAURANT) || chosenAttraction.getType().equals(AttractionType.CAFENEA) || chosenAttraction.getType().equals(AttractionType.CLUB)){
            attractionCallImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + chosenAttraction.getPhone()));
                    startActivity(callIntent);
                }
            });
        }else{
            attractionCallTextView.setVisibility(View.GONE);
            attractionCallImageView.setVisibility(View.GONE);
        }

        Button attractionMapButton = (Button) findViewById(R.id.details_imagebutton);
        attractionMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mapIntent = new Intent(DetailsActivity.this, MapsActivity.class);
                mapIntent.putExtra("EXTRA_ATTRACTIONS", new ArrayList<>(Collections.singletonList(chosenAttraction)));
                startActivity(mapIntent);
            }
        });


    }
}
