package com.example.larisa.liketimisoara.activity;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.larisa.liketimisoara.Attraction;
import com.example.larisa.liketimisoara.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private List<Attraction> attractions;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        attractions = (List<Attraction>) getIntent().getSerializableExtra("EXTRA_ATTRACTIONS");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

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
