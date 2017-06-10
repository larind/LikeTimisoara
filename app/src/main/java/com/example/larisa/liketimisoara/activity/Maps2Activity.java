package com.example.larisa.liketimisoara.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.example.larisa.liketimisoara.Attraction;
import com.example.larisa.liketimisoara.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class Maps2Activity extends LocalizationActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private List<Attraction> allNearbyAttractions;
    private List<Attraction> attractions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps22);

        final SupportMapFragment map2Fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        map2Fragment.getMapAsync(this);

        Spinner spinner = (Spinner) findViewById(R.id.attraction_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_location, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                attractions.clear();

                if (parent.getItemAtPosition(position).toString().equals("All") || parent.getItemAtPosition(position).toString().equals("Toate")) {

                    attractions = new ArrayList<>(allNearbyAttractions);

                    map2Fragment.getMapAsync(Maps2Activity.this);

                    return;
                }

                for (Attraction attraction : allNearbyAttractions) {

                    String attractionType;

                    if (getLanguage().equals("en")) {
                        attractionType = attraction.getType().getEnName();
                    } else {
                        attractionType = attraction.getType().getRoName();
                    }

                   if (parent.getItemAtPosition(position).toString().equals(attractionType)) {
                        attractions.add(attraction);
                    }
                }

                if (attractions.isEmpty()) {
                    Toast.makeText(Maps2Activity.this, getString(R.string.message_nolocation2) + " " + parent.getSelectedItem().toString() + " " + getString(R.string.message_nolocation2), Toast.LENGTH_LONG).show();
                }
                map2Fragment.getMapAsync(Maps2Activity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        attractions = (List<Attraction>) getIntent().getSerializableExtra("EXTRA_ATTRACTIONS");
        allNearbyAttractions = new ArrayList<>((List<Attraction>) getIntent().getSerializableExtra("EXTRA_ATTRACTIONS"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (mMap == null) {
            mMap = googleMap;
        }

        mMap.clear();

        setupMarkers(attractions);

        if (attractions.size() == 1) {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(attractions.get(0).getLatitude(), attractions.get(0).getLongitude()),
                    (float) 13));

        } else if (attractions.size() > 1) {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.7541, 21.2259), (float) 11.5));
        }
    }

    private void setupMarkers(List<Attraction> attractions) {

        for (Attraction attraction : attractions) {
            mMap.addMarker(createMarker(attraction.getLatitude(), attraction.getLongitude(), attraction.getName()));
        }
    }

    @NonNull
    private MarkerOptions createMarker(double latitude, double longitude, String landmarkTitle) {
        LatLng landmarkPosition = new LatLng(latitude, longitude);
        return new MarkerOptions().position(landmarkPosition).title(landmarkTitle);
    }
}
