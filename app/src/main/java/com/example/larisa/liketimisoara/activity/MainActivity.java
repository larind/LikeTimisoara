package com.example.larisa.liketimisoara.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
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

import com.example.larisa.liketimisoara.Attraction;
import com.example.larisa.liketimisoara.AttractionType;
import com.example.larisa.liketimisoara.DataModel;
import com.example.larisa.liketimisoara.MyData;
import com.example.larisa.liketimisoara.R;
import com.example.larisa.liketimisoara.activity.adapter.CustomAdapter;
import com.example.larisa.liketimisoara.db.DB;
import com.example.larisa.liketimisoara.db.DBException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String DB_INIT_KEY = "DB_INIT_KEY";

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;

    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackVisible = false;
    public View mCardFrontLayout;
    private View mCardBackLayout;
    private Context context;

    private boolean initDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        context = this;

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        if (!sharedPreferences.getBoolean(DB_INIT_KEY, false)) {
            // enter first time

            sharedPreferences.edit().putBoolean(DB_INIT_KEY, true).apply();

            initDatabase();

        }

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<>();
        for (int i = 0; i < MyData.nameArray.length; i++) {
            data.add(new DataModel(
                    MyData.nameArray[i],
                    MyData.id_[i],
                    MyData.drawableArray[i],
                    MyData.attractionTypeArray[i]
            ));
        }

        adapter = new CustomAdapter(data, this);
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

        loadAnimations();

        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            final AlertDialog.Builder builder = dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initDatabase() {

        DB database = DB.getInstance(this);

        try {

         /*   database.insertAttraction(new Attraction(0, "Parcul Central",AttractionType.PARC, getString(R.string.info_central_park),
                    R.drawable.parculcentral, true, 21.2211, 45.7513));
            database.insertAttraction(new Attraction(1, "Parcul Botanic", AttractionType.PARC, "parc",
                    R.drawable.parc, true, 21.2253, 45.7602));
            database.insertAttraction(new Attraction(2, "Parcul Poporului", AttractionType.PARC, "parc",
                    R.drawable.parc, false, 21.2253, 45.7602));
            database.insertAttraction(new Attraction(3, "Parcul Justitiei" , AttractionType.PARC, "parc",
                    R.drawable.parc, false, 21.2253, 45.7602));
            database.insertAttraction(new Attraction(4, "Hotel Arta", AttractionType.HOTEL, "hotel",
                    R.drawable.parc, false, 45.7513, 21.2211));
            database.insertAttraction(new Attraction(5, "Muzeul Banat", AttractionType.MUZEU, "muzeu",
                    R.drawable.parc, false, 45.7513, 21.2211));
            database.insertAttraction(new Attraction(6, "SmartFit Studio 3", AttractionType.SALA_FITNESS, "CEA MAI TARE SALA DIN ORAS",
                    R.drawable.harta, false, 45.7513, 21.2211));*/
            database.insertAttraction(new Attraction(7, getString(R.string.extreme_fitness_name), AttractionType.SALA_FITNESS, getString(R.string.extreme_fitness_info),
                    R.drawable.extreme, false, 45.728675, 21.2386))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.friends_arena_name), AttractionType.SALA_FITNESS, getString(R.string.friends_arena_info),
                    R.drawable.friends, false, 45.7301899, 21.2013))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.iguana_fitness_name), AttractionType.SALA_FITNESS, getString(R.string.iguana_fitness_info),
                    R.drawable.iguana, false, 45.7639368, 21.2561))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.stil_fitness_name), AttractionType.SALA_FITNESS, getString(R.string.stil_fitness_info),
                    R.drawable.stil, false, 45.7434774, 21.2441))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.body_time_name), AttractionType.SALA_FITNESS, getString(R.string.body_time_info),
                    R.drawable.bodytime, false, 45.7739105, 21.2313))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.smart_fit3_name), AttractionType.SALA_FITNESS, getString(R.string.smart_fit3_info),
                    R.drawable.smartfit, false, 45.724749, 21.1998))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.continental_fitness_name), AttractionType.SALA_FITNESS, getString(R.string.continental_fitness_info),
                    R.drawable.continental, false, 45.7553, 21.2324))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.world_class_name), AttractionType.SALA_FITNESS, getString(R.string.world_class_info),
                    R.drawable.worldclass, false, 45.7627697, 21.2297))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.banu_fitness_name), AttractionType.SALA_FITNESS, getString(R.string.banu_fitness_info),
                    R.drawable.banusport, false, 45.74354, 21.242))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.squash_sport_name), AttractionType.SALA_SQUASH, getString(R.string.squash_sport_info),
                    R.drawable.squasharena, false, 45.7728636, 21.2161))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.squash_club_name), AttractionType.SALA_SQUASH, getString(R.string.squash_club_info),
                    R.drawable.squashclub, false, 45.7546726, 21.246))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.helios_squash_name), AttractionType.SALA_SQUASH, getString(R.string.helios_squash_info),
                    R.drawable.helios, false, 45.7385141, 21.238))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.the_squashers_name), AttractionType.SALA_SQUASH, getString(R.string.the_squashers_info),
                    R.drawable.squashers, false, 47.048961, 21.9050))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.arena_pool_name), AttractionType.BAZIN_INOT, getString(R.string.arena_pool_info),
                    R.drawable.arenapool, false, 45.769224, 21.2672))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.ice_dyp_name), AttractionType.BAZIN_INOT, getString(R.string.ice_dyp_info),
                    R.drawable.icedyp, false, 45.765198, 21.25910))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.smart_fit3_name), AttractionType.BAZIN_INOT, getString(R.string.smart_fit3_info),
                    R.drawable.smartfit, false, 45.724749, 21.1998))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.club_tivoli_name), AttractionType.TEREN_TENIS, getString(R.string.club_tivoli_info),
                    R.drawable.tivoli, false, 45.763754, 21.275))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.club_helios_name), AttractionType.TEREN_TENIS, getString(R.string.club_helios_info),
                    R.drawable.helios, false, 45.7385141, 21.238))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.berlin_sport_name), AttractionType.TEREN_TENIS, getString(R.string.berlin_sport_info),
                    R.drawable.berlin, false, 45.7883605, 21.2337))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.banu_sport_name), AttractionType.TEREN_TENIS, getString(R.string.banu_sport_info),
                    R.drawable.banusport, false, 45.74354, 21.242))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.tudo_taxi_name), AttractionType.COMPANIE_TAXI, getString(R.string.tudo_taxi_info),
                    R.drawable.tudo, false, 45.769224, 21.2672))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.fan_taxi_name), AttractionType.COMPANIE_TAXI, getString(R.string.fan_taxi_info),
                    R.drawable.fantaxi, false, 45.765198, 21.25910))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.radio_taxi_name), AttractionType.COMPANIE_TAXI, getString(R.string.radio_taxi_info),
                    R.drawable.taxilogo, false, 45.724749, 21.1998))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.pro_taxi_name), AttractionType.COMPANIE_TAXI, getString(R.string.pro_taxi_info),
                    R.drawable.taxilogo, false, 45.7385141, 21.238))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.grup_taxi_name), AttractionType.COMPANIE_TAXI, getString(R.string.grup_taxi_info),
                    R.drawable.taxilogo, false, 45.7883605, 21.2337))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.taxi_timisoara_name), AttractionType.COMPANIE_TAXI, getString(R.string.taxi_timisoara_info),
                    R.drawable.taxilogo, false, 45.74354, 21.242))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.deluxe_rent_name), AttractionType.INCHIRIERI_AUTO, getString(R.string.deluxe_rent_info),
                    R.drawable.deluxe, false, 45.7290652, 21.23161))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.novum_rent_name), AttractionType.INCHIRIERI_AUTO, getString(R.string.novum_rent_info),
                    R.drawable.novum, false, 45.743281, 21.2124))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.marvomil_rent_name), AttractionType.INCHIRIERI_AUTO, getString(R.string.marvomil_rent_info),
                    R.drawable.narvomil, false, 45.774424, 21.2322))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.nataly_rent_name), AttractionType.INCHIRIERI_AUTO, getString(R.string.nataly_rent_info),
                    R.drawable.nataly, false, 45.7605894, 21.2400))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.swiso_rent_name), AttractionType.INCHIRIERI_AUTO, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 45.742574, 21.207169))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.nataly_rent_info),
                    R.drawable.nataly, false, 45.75140, 21.22369))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 45.71155, 21.19121))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 45.75857, 21.21781))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 45.72830, 21.20471))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 45.77504, 21.22122))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 45.75748, 21.22218))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 45.75748, 21.22218))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 45.75904, 21.24957))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 45.74479, 21.24306))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 45.74716, 21.20994))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.art_museum_name), AttractionType.MUZEU, getString(R.string.art_museum_info),
                    R.drawable.muzeuarta, false, 45.75738, 21.2293))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.banat_museum_name), AttractionType.MUZEU, getString(R.string.banat_museum_info),
                    R.drawable.mbanatului, true, 45.757258, 21.233961))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.communist_museum_name), AttractionType.MUZEU, getString(R.string.commmunist_museum_info),
                    R.drawable.mconsumatorului, false, 45.74323, 21.22425))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.banat_village_museum_name), AttractionType.MUZEU, getString(R.string.banat_village_museum_info),
                    R.drawable.msatului, true, 45.77818, 21.2653))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.liberty_square_name), AttractionType.PIATA, getString(R.string.liberty_square_info),
                    R.drawable.liberatatiisquare, true, 45.755, 21.22778))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.victory_square_name), AttractionType.PIATA, getString(R.string.victory_square_info),
                    R.drawable.victorieisquare, true, 45.75286, 21.225003))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.traian_square_name), AttractionType.PIATA, getString(R.string.traian_square_info),
                    R.drawable.traiansquare, false, 45.7573, 21.2493))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.st_gheorghe_square_name), AttractionType.PIATA, getString(R.string.st_gheorghe_square_info),
                    R.drawable.gheorghesquare, false, 45.75577, 21.228))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.union_square_name), AttractionType.PIATA, getString(R.string.union_square_info),
                    R.drawable.uniriisquare, true, 45.7580, 21.2290))  ;

            database.insertAttraction(new Attraction(7, getString(R.string.childrens_park_name), AttractionType.PARC, getString(R.string.childrens_park_info),
                    R.drawable.copiilor, false, 45.751611, 21.233))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.roses_park_name), AttractionType.PARC, getString(R.string.roses_park_info),
                    R.drawable.rozelor, true, 45.7500943, 21.231682))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.botanical_park_name), AttractionType.PARC, getString(R.string.botanical_park_info),
                    R.drawable.botanic, false, 45.7601963, 21.22530))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.cathedral_park_name), AttractionType.PARC, getString(R.string.cathedral_park_info),
                    R.drawable.catedralei, false, 45.74996, 21.2237))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.civic_park_name), AttractionType.PARC, getString(R.string.civic_park_info),
                    R.drawable.civic, false, 45.7540275, 21.231559))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.justice_park_name), AttractionType.PARC, getString(R.string.justice_park_info),
                    R.drawable.justitiei, false, 45.7499622, 21.227336))  ;
            database.insertAttraction(new Attraction(0, getString(R.string.central_park_name),AttractionType.PARC, getString(R.string.info_central_park),
                    R.drawable.parculcentral, true, 21.2211, 45.7513));






        } catch (DBException ex) {


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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.obiective_apropiate) {
            Intent obiectiveApropiate = new Intent(MainActivity.this, DetailFromMenu.class);
            startActivity(obiectiveApropiate);

        } else if (id == R.id.top10) {
            Intent top10 = new Intent(MainActivity.this, DetailFromMenu.class);
            startActivity(top10);
        }
        else if (id == R.id.nav_fitness) {
            Intent fitnessActivity = new Intent(MainActivity.this, SportsActivity.class);
            fitnessActivity.putExtra("EXTRA_ATTRACTION", AttractionType.SALA_FITNESS);
            startActivity(fitnessActivity);
        }
        else if (id == R.id.nav_tenis) {
            Intent fitnessActivity = new Intent(MainActivity.this, SportsActivity.class);
            fitnessActivity.putExtra("EXTRA_ATTRACTION", AttractionType.TEREN_TENIS);
            startActivity(fitnessActivity);
        }
        else if (id == R.id.nav_bazine) {
            Intent fitnessActivity = new Intent(MainActivity.this, SportsActivity.class);
            fitnessActivity.putExtra("EXTRA_ATTRACTION", AttractionType.BAZIN_INOT);
            startActivity(fitnessActivity);
        }
        else if (id == R.id.nav_squash) {
            Intent fitnessActivity = new Intent(MainActivity.this, SportsActivity.class);
            fitnessActivity.putExtra("EXTRA_ATTRACTION", AttractionType.SALA_SQUASH);
            startActivity(fitnessActivity);
        }
        else if (id == R.id.nav_inchirieri) {
            Intent mapIntent = new Intent(MainActivity.this, SportsActivity.class);
            mapIntent.putExtra("EXTRA_ATTRACTION",AttractionType.INCHIRIERI_AUTO );
            startActivity(mapIntent);
        }
        else if (id == R.id.nav_bicicleta) {
            Intent mapsActivity = new Intent(MainActivity.this, MapsActivity.class);
            ArrayList<Attraction> attractions = (ArrayList<Attraction>)DB.getInstance(MainActivity.this).getAttractions(AttractionType.STATIE_BICICLETE);
            mapsActivity.putExtra("EXTRA_ATTRACTIONS", attractions);
            startActivity(mapsActivity);
        }
        else if (id == R.id.nav_taxi) {
            Intent fitnessActivity = new Intent(MainActivity.this, SportsActivity.class);
            fitnessActivity.putExtra("EXTRA_ATTRACTION", AttractionType.COMPANIE_TAXI);
            startActivity(fitnessActivity);
        }
        else if(id == R.id.istoric){
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

    private void init(View view) {

        View parent = (View) view.getParent();

        mCardFrontLayout = parent.findViewById(R.id.card_view_front);
        mCardFrontLayout.setCameraDistance(getResources().getDisplayMetrics().density * 8000);
        mCardBackLayout = parent.findViewById(R.id.card_view_back);
        mCardBackLayout.setCameraDistance(getResources().getDisplayMetrics().density * 8000);

        initDone = true;
    }

    public void flipToBack(View view) {

//        final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.in_animation);
//        final Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.out_animation);

        mCardFrontLayout = view;
        mCardBackLayout = ((View)view.getParent()).findViewById(R.id.card_view_back);

//        mCardFrontLayout.startAnimation(animationFadeOut);
//        mCardBackLayout.startAnimation(animationFadeIn);
//
//        mCardFrontLayout.setVisibility(View.INVISIBLE);
//        mCardBackLayout.setVisibility(View.VISIBLE);

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
        mCardFrontLayout = ((View)view.getParent().getParent()).findViewById(R.id.card_view_front);

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
