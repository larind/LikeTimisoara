package com.example.larisa.liketimisoara;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.akexorcist.localizationactivity.OnLocaleChangedListener;
import com.example.larisa.liketimisoara.activity.MainActivity;
import com.example.larisa.liketimisoara.activity.MapsActivity;
import com.example.larisa.liketimisoara.db.DB;

public class FirstScreen extends LocalizationActivity implements OnLocaleChangedListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        ImageButton roButton = (ImageButton) findViewById(R.id.ro_flag);
        ImageButton enButton = (ImageButton) findViewById(R.id.en_flag);

        roButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLanguage().equals("ro")) {
                    return;
                }

                setLanguage("ro");
            }
        });

        enButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLanguage().equals("en")) {
                    return;
                }

                setLanguage("en");
            }
        });

        Button attractionsButton = (Button) findViewById(R.id.atractions_button);
        attractionsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mainIntent = new Intent(FirstScreen.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });

    }

    @Override
    public void onAfterLocaleChanged() {
        super.onAfterLocaleChanged();
        DB.getInstance(getApplicationContext()).deleteAttractions();
        DB.getInstance(getApplicationContext()).initDatabase(getApplicationContext());
    }
}
