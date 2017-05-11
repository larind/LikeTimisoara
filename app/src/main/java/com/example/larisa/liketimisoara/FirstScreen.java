package com.example.larisa.liketimisoara;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.larisa.liketimisoara.activity.MainActivity;
import com.example.larisa.liketimisoara.activity.MapsActivity;

public class FirstScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        Button attractionsButton = (Button) findViewById(R.id.atractions_button);
        attractionsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mainIntent = new Intent(FirstScreen.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }
}
