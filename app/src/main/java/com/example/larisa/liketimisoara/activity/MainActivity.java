package com.example.larisa.liketimisoara.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.larisa.liketimisoara.Attraction;
import com.example.larisa.liketimisoara.AttractionType;
import com.example.larisa.liketimisoara.DataModel;
import com.example.larisa.liketimisoara.MyData;
import com.example.larisa.liketimisoara.R;
import com.example.larisa.liketimisoara.activity.adapter.CustomAdapter;
import com.example.larisa.liketimisoara.db.DB;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String DB_INIT_KEY = "DB_INIT_KEY";

    private LocationManager locationManager;

    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    public View mCardFrontLayout;
    private View mCardBackLayout;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        context = this;

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!sharedPreferences.getBoolean(DB_INIT_KEY, false)) {
            // enter first time

            sharedPreferences.edit().putBoolean(DB_INIT_KEY, true).apply();

            DB.getInstance(getApplicationContext()).deleteAttractions();
            DB.getInstance(getApplicationContext()).initDatabase(getApplicationContext());
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        loadAnimations();

        ArrayList<DataModel> data = new ArrayList<>();
        for (int i = 0; i < MyData.nameArray.length; i++) {
            data.add(new DataModel(
                    MyData.nameArray[i],
                    MyData.id_[i],
                    MyData.drawableArray[i],
                    MyData.attractionTypeArray[i]
            ));
        }

        RecyclerView.Adapter adapter = new CustomAdapter(data, this);
        recyclerView.setAdapter(adapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {

        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            // notify user

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
                    dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(myIntent);
                            //get gps
                        }
                    });
                    dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        }
                    });
                    dialog.show();

                }
            });
        }
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void toggleGPSUpdates(View view) {

        if (checkLocation()) {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10 * 1000, 10, locationListenerGPS);
            } catch (SecurityException e) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show(); // lets the user know there is a problem with the gps
            }
        }
    }

    private List<Attraction> getNearbyAttractions(List<Attraction> allAttractions, double currentLatitude, double currentLongitude) {

        float[] distance = new float[1];

        List<Attraction> nearbyAttractions = new ArrayList<>();

        for (Attraction attraction : allAttractions) {

            Location.distanceBetween(currentLatitude, currentLongitude, attraction.getLatitude(), attraction.getLongitude(), distance);

            if (distance[0] <= 1000) {

                nearbyAttractions.add(attraction);
            }
        }

        return nearbyAttractions;
    }

    private final LocationListener locationListenerGPS = new LocationListener() {

        public void onLocationChanged(Location location) {

            double currentLongitude = location.getLongitude();
            double currentLatitude = location.getLatitude();

            List<Attraction> allAttractions = DB.getInstance(getApplicationContext()).getAttractions(null);

            ArrayList<Attraction> nearbyAttractions = (ArrayList<Attraction>) getNearbyAttractions(allAttractions, currentLatitude, currentLongitude);

            try {
                locationManager.removeUpdates(locationListenerGPS);
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }

            Intent mapsActivity = new Intent(MainActivity.this, MapsActivity.class);
            mapsActivity.putExtra("EXTRA_ATTRACTIONS", nearbyAttractions);
            startActivity(mapsActivity);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.obiective_apropiate) {
            toggleGPSUpdates(null);
        } else if (id == R.id.top10) {
            Intent top10 = new Intent(MainActivity.this, SportsActivity.class);
            top10.putExtra("EXTRA_IS_TOP_10", true);
            startActivity(top10);
        } else if (id == R.id.nav_fitness) {
            Intent fitnessActivity = new Intent(MainActivity.this, SportsActivity.class);
            fitnessActivity.putExtra("EXTRA_ATTRACTION", AttractionType.SALA_FITNESS);
            startActivity(fitnessActivity);
        } else if (id == R.id.nav_tenis) {
            Intent fitnessActivity = new Intent(MainActivity.this, SportsActivity.class);
            fitnessActivity.putExtra("EXTRA_ATTRACTION", AttractionType.TEREN_TENIS);
            startActivity(fitnessActivity);
        } else if (id == R.id.nav_bazine) {
            Intent fitnessActivity = new Intent(MainActivity.this, SportsActivity.class);
            fitnessActivity.putExtra("EXTRA_ATTRACTION", AttractionType.BAZIN_INOT);
            startActivity(fitnessActivity);
        } else if (id == R.id.nav_squash) {
            Intent fitnessActivity = new Intent(MainActivity.this, SportsActivity.class);
            fitnessActivity.putExtra("EXTRA_ATTRACTION", AttractionType.SALA_SQUASH);
            startActivity(fitnessActivity);
        } else if (id == R.id.nav_inchirieri) {
            Intent mapIntent = new Intent(MainActivity.this, SportsActivity.class);
            mapIntent.putExtra("EXTRA_ATTRACTION", AttractionType.INCHIRIERI_AUTO);
            startActivity(mapIntent);
        } else if (id == R.id.nav_bicicleta) {
            Intent mapsActivity = new Intent(MainActivity.this, MapsActivity.class);
            ArrayList<Attraction> attractions = (ArrayList<Attraction>) DB.getInstance(getApplicationContext()).getAttractions(AttractionType.STATIE_BICICLETE);
            mapsActivity.putExtra("EXTRA_ATTRACTIONS", attractions);
            startActivity(mapsActivity);
        } else if (id == R.id.nav_taxi) {
            Intent fitnessActivity = new Intent(MainActivity.this, SportsActivity.class);
            fitnessActivity.putExtra("EXTRA_ATTRACTION", AttractionType.COMPANIE_TAXI);
            startActivity(fitnessActivity);
        } else if (id == R.id.istoric) {
            Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(historyIntent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadAnimations() {
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.in_animation);
    }

    public void flipToBack(View view) {

        mCardFrontLayout = view;
        mCardBackLayout = ((View) view.getParent()).findViewById(R.id.card_view_back);

        mCardFrontLayout.setCameraDistance(getResources().getDisplayMetrics().density * 8000);
        mCardBackLayout.setCameraDistance(getResources().getDisplayMetrics().density * 8000);

        mSetRightOut.setTarget(mCardFrontLayout);
        mSetLeftIn.setTarget(mCardBackLayout);

        mCardFrontLayout.setVisibility(View.VISIBLE);
        mCardBackLayout.setVisibility(View.VISIBLE);

        mSetRightOut.removeAllListeners();
        mSetLeftIn.removeAllListeners();

        mSetRightOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCardFrontLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mSetLeftIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCardBackLayout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mSetRightOut.start();
        mSetLeftIn.start();
    }

    public void flipToFront(View view) {

        mCardBackLayout = (View) view.getParent();
        mCardFrontLayout = ((View) view.getParent().getParent()).findViewById(R.id.card_view_front);

        mCardBackLayout.setCameraDistance(getResources().getDisplayMetrics().density * 8000);
        mCardFrontLayout.setCameraDistance(getResources().getDisplayMetrics().density * 8000);

        mSetRightOut.setTarget(mCardBackLayout);
        mSetLeftIn.setTarget(mCardFrontLayout);

        mSetRightOut.removeAllListeners();
        mSetLeftIn.removeAllListeners();

        mCardFrontLayout.setVisibility(View.VISIBLE);
        mCardBackLayout.setVisibility(View.VISIBLE);

        mSetRightOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCardBackLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mSetLeftIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCardFrontLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mSetRightOut.start();
        mSetLeftIn.start();
    }
}
