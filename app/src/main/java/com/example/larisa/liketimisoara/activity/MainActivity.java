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
            database.insertAttraction(new Attraction(7, getString(R.string.extreme_fitness_name), AttractionType.SALA_FITNESS, getString(R.string.extreme_fitness_info),
                    R.drawable.extreme, false, 21.2386, 45.728675))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.friends_arena_name), AttractionType.SALA_FITNESS, getString(R.string.friends_arena_info),
                    R.drawable.friends, false, 21.2013, 45.7301899))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.iguana_fitness_name), AttractionType.SALA_FITNESS, getString(R.string.iguana_fitness_info),
                    R.drawable.iguana, false, 21.2561, 45.7639368))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.stil_fitness_name), AttractionType.SALA_FITNESS, getString(R.string.stil_fitness_info),
                    R.drawable.stil, false, 21.2441, 45.7434774))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.body_time_name), AttractionType.SALA_FITNESS, getString(R.string.body_time_info),
                    R.drawable.bodytime, false, 21.2313, 45.7739105))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.smart_fit3_name), AttractionType.SALA_FITNESS, getString(R.string.smart_fit3_info),
                    R.drawable.smartfit, false, 21.1998, 45.724749))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.continental_fitness_name), AttractionType.SALA_FITNESS, getString(R.string.continental_fitness_info),
                    R.drawable.continental, false,21.2324, 45.7553))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.world_class_name), AttractionType.SALA_FITNESS, getString(R.string.world_class_info),
                    R.drawable.worldclass, false, 21.2297,45.7627697))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.banu_fitness_name), AttractionType.SALA_FITNESS, getString(R.string.banu_fitness_info),
                    R.drawable.banusport, false, 21.242, 45.74354))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.squash_sport_name), AttractionType.SALA_SQUASH, getString(R.string.squash_sport_info),
                    R.drawable.squasharena, false, 21.2161, 45.7728636))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.squash_club_name), AttractionType.SALA_SQUASH, getString(R.string.squash_club_info),
                    R.drawable.squashclub, false, 21.246, 45.7546726))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.helios_squash_name), AttractionType.SALA_SQUASH, getString(R.string.helios_squash_info),
                    R.drawable.helios, false, 21.238, 45.7385141))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.the_squashers_name), AttractionType.SALA_SQUASH, getString(R.string.the_squashers_info),
                    R.drawable.squashers, false, 21.9050, 47.048961))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.arena_pool_name), AttractionType.BAZIN_INOT, getString(R.string.arena_pool_info),
                    R.drawable.arenapool, false, 21.2672, 45.769224))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.ice_dyp_name), AttractionType.BAZIN_INOT, getString(R.string.ice_dyp_info),
                    R.drawable.icedyp, false, 21.25910, 45.765198))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.smart_fit3_name), AttractionType.BAZIN_INOT, getString(R.string.smart_fit3_info),
                    R.drawable.smartfit, false, 21.1998, 45.724749))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.club_tivoli_name), AttractionType.TEREN_TENIS, getString(R.string.club_tivoli_info),
                    R.drawable.tivoli, false, 21.275, 45.763754))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.club_helios_name), AttractionType.TEREN_TENIS, getString(R.string.club_helios_info),
                    R.drawable.helios, false, 21.238, 45.7385141))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.berlin_sport_name), AttractionType.TEREN_TENIS, getString(R.string.berlin_sport_info),
                    R.drawable.berlin, false, 21.2337, 45.7883605))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.banu_sport_name), AttractionType.TEREN_TENIS, getString(R.string.banu_sport_info),
                    R.drawable.banusport, false, 21.242, 45.74354))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.tudo_taxi_name), AttractionType.COMPANIE_TAXI, getString(R.string.tudo_taxi_info),
                    R.drawable.tudo, false, 21.2672, 45.769224))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.fan_taxi_name), AttractionType.COMPANIE_TAXI, getString(R.string.fan_taxi_info),
                    R.drawable.fantaxi, false, 21.25910, 45.765198))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.radio_taxi_name), AttractionType.COMPANIE_TAXI, getString(R.string.radio_taxi_info),
                    R.drawable.taxilogo, false, 21.1998, 45.724749))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.pro_taxi_name), AttractionType.COMPANIE_TAXI, getString(R.string.pro_taxi_info),
                    R.drawable.taxilogo, false, 21.238, 45.7385141))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.grup_taxi_name), AttractionType.COMPANIE_TAXI, getString(R.string.grup_taxi_info),
                    R.drawable.taxilogo, false, 21.2337, 45.7883605))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.taxi_timisoara_name), AttractionType.COMPANIE_TAXI, getString(R.string.taxi_timisoara_info),
                    R.drawable.taxilogo, false, 21.242, 45.74354))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.deluxe_rent_name), AttractionType.INCHIRIERI_AUTO, getString(R.string.deluxe_rent_info),
                    R.drawable.deluxe, false, 21.23161, 45.7290652))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.novum_rent_name), AttractionType.INCHIRIERI_AUTO, getString(R.string.novum_rent_info),
                    R.drawable.novum, false, 21.2124, 45.743281))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.marvomil_rent_name), AttractionType.INCHIRIERI_AUTO, getString(R.string.marvomil_rent_info),
                    R.drawable.narvomil, false, 21.2322, 45.774424))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.nataly_rent_name), AttractionType.INCHIRIERI_AUTO, getString(R.string.nataly_rent_info),
                    R.drawable.nataly, false, 21.2400, 45.7605894))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.swiso_rent_name), AttractionType.INCHIRIERI_AUTO, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 21.207169, 45.742574))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.nataly_rent_info),
                    R.drawable.nataly, false, 21.22369, 45.75140))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 21.19121, 45.71155))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 21.21781, 45.75857))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 21.20471, 45.72830))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 21.22122, 45.77504))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 21.22218, 45.75748))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 21.22218, 45.75748))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 21.24957, 45.75904))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 21.24306, 45.74479))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, getString(R.string.swiso_rent_info),
                    R.drawable.swiso, false, 21.20994, 45.74716))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.art_museum_name), AttractionType.MUZEU, getString(R.string.art_museum_info),
                    R.drawable.muzeuarta, false, 21.2293, 45.75738))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.banat_museum_name), AttractionType.MUZEU, getString(R.string.banat_museum_info),
                    R.drawable.mbanatului, true, 21.233961, 45.757258))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.communist_museum_name), AttractionType.MUZEU, getString(R.string.commmunist_museum_info),
                    R.drawable.mconsumatorului, false, 21.22425, 45.74323))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.banat_village_museum_name), AttractionType.MUZEU, getString(R.string.banat_village_museum_info),
                    R.drawable.msatului, true, 21.2653, 45.77818))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.liberty_square_name), AttractionType.PIATA, getString(R.string.liberty_square_info),
                    R.drawable.liberatatiisquare, true, 21.22778, 45.755))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.victory_square_name), AttractionType.PIATA, getString(R.string.victory_square_info),
                    R.drawable.victorieisquare, true, 21.225003, 45.75286))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.traian_square_name), AttractionType.PIATA, getString(R.string.traian_square_info),
                    R.drawable.traiansquare, false, 21.2493, 45.7573))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.st_gheorghe_square_name), AttractionType.PIATA, getString(R.string.st_gheorghe_square_info),
                    R.drawable.gheorghesquare, false, 21.228, 45.75577))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.union_square_name), AttractionType.PIATA, getString(R.string.union_square_info),
                    R.drawable.uniriisquare, true, 21.2290, 45.7580))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.childrens_park_name), AttractionType.PARC, getString(R.string.childrens_park_info),
                    R.drawable.copiilor, false, 21.233, 45.751611))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.roses_park_name), AttractionType.PARC, getString(R.string.roses_park_info),
                    R.drawable.rozelor, true, 21.231682, 45.7500943))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.botanical_park_name), AttractionType.PARC, getString(R.string.botanical_park_info),
                    R.drawable.botanic, false, 21.22530, 45.7601963))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.cathedral_park_name), AttractionType.PARC, getString(R.string.cathedral_park_info),
                    R.drawable.catedralei, false, 21.2237, 45.74996))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.civic_park_name), AttractionType.PARC, getString(R.string.civic_park_info),
                    R.drawable.civic, false, 21.231559, 45.7540275))  ;
            database.insertAttraction(new Attraction(7, getString(R.string.justice_park_name), AttractionType.PARC, getString(R.string.justice_park_info),
                    R.drawable.justitiei, false, 21.227336, 45.7499622))  ;
            database.insertAttraction(new Attraction(0, getString(R.string.central_park_name),AttractionType.PARC, getString(R.string.info_central_park),
                    R.drawable.parculcentral, true, 21.2211, 45.7513));
            database.insertAttraction(new Attraction(0, getString(R.string.bastion_name),AttractionType.ALTE_OBIECTIVE, getString(R.string.bastion_info),
                    R.drawable.bastion, true, 21.23392, 45.75721));
            database.insertAttraction(new Attraction(0, getString(R.string.timisoreana_name),AttractionType.ALTE_OBIECTIVE, getString(R.string.timisoreana_info),
                    R.drawable.timisoreana, true, 21.248941, 45.754762));
            database.insertAttraction(new Attraction(0, getString(R.string.millenium_cathedral_name),AttractionType.CATEDRALA, getString(R.string.millenium_cathedral_info),
                    R.drawable.bisericamillenium, true, 21.247785, 45.756680));
            database.insertAttraction(new Attraction(0, getString(R.string.ortodox_cathedral_name),AttractionType.CATEDRALA, getString(R.string.ortodox_cathedral_info),
                    R.drawable.cathedral, false, 21.2242708, 45.7507216));
            database.insertAttraction(new Attraction(0, getString(R.string.assumption_church_name),AttractionType.CATEDRALA, getString(R.string.assumption_church_info),
                    R.drawable.assumption, false, 21.219880, 45.7725689));
            database.insertAttraction(new Attraction(0, getString(R.string.catherine_church_name),AttractionType.CATEDRALA, getString(R.string.catherine_church_info),
                    R.drawable.sfecaterina, false, 21.2280593, 45.7543008));
            database.insertAttraction(new Attraction(0, getString(R.string.roman_catholic_catedral_name),AttractionType.CATEDRALA, getString(R.string.roman_catholic_catedral_info),
                    R.drawable.cathedralstgheorghe, false, 21.2300940, 45.7581453));
            database.insertAttraction(new Attraction(0, getString(R.string.elim_church_name),AttractionType.CATEDRALA, getString(R.string.elim_church_info),
                    R.drawable.elimjpg, false, 21.2188, 45.7446917));
            database.insertAttraction(new Attraction(0, getString(R.string.birth_mary_name),AttractionType.CATEDRALA, getString(R.string.birth_mary_info),
                    R.drawable.birthofmarychurch, false, 21.21646, 45.746083));

            database.insertAttraction(new Attraction(0, getString(R.string.merlot_restaurant_name),AttractionType.RESTAURANT, getString(R.string.merlot_restaurant_info),
                    R.drawable.bisericamillenium, false, 21.2420, 45.756454));
            database.insertAttraction(new Attraction(0, getString(R.string.sabres_restaurant_name),AttractionType.RESTAURANT, getString(R.string.sabres_restaurant_info),
                    R.drawable.cathedral, false, 21.238341, 45.742456));
            database.insertAttraction(new Attraction(0, getString(R.string.grill_to_chill_name),AttractionType.RESTAURANT, getString(R.string.grill_to_chill_info),
                    R.drawable.assumption, false, 21.228497, 45.756759));
            database.insertAttraction(new Attraction(0, getString(R.string.caruso_restaurant_name),AttractionType.RESTAURANT, getString(R.string.caruso_restaurant_info),
                    R.drawable.sfecaterina, false, 21.229376, 45.7552659));
            database.insertAttraction(new Attraction(0, getString(R.string.amphora_enoteca_restaurant_name),AttractionType.RESTAURANT, getString(R.string.amphora_enoteca_restaurant_info),
                    R.drawable.cathedralstgheorghe, false, 21.233603, 45.756395));
            database.insertAttraction(new Attraction(0, getString(R.string.belvedere_restaurant_name),AttractionType.RESTAURANT, getString(R.string.belvedere_restaurant_info),
                    R.drawable.elimjpg, false, 21.22538, 45.754426));
            database.insertAttraction(new Attraction(0, getString(R.string.enoteca_restaurant_name),AttractionType.RESTAURANT, getString(R.string.enoteca_restaurant_info),
                    R.drawable.birthofmarychurch, false, 21.2283462, 45.75667079));
            database.insertAttraction(new Attraction(0, getString(R.string.mercy_restaurant_name),AttractionType.RESTAURANT, getString(R.string.merlot_restaurant_info),
                    R.drawable.elimjpg, false, 21.22868, 45.7563201));
            database.insertAttraction(new Attraction(0, getString(R.string.ristorante_al_duomo_name),AttractionType.RESTAURANT, getString(R.string.ristorante_al_duomo_info),
                    R.drawable.birthofmarychurch, false, 21.230911, 45.7579943));
            database.insertAttraction(new Attraction(0, getString(R.string.timsoreana_restaurant_name),AttractionType.RESTAURANT, getString(R.string.timisoareana_restaurant_info),
                    R.drawable.bisericamillenium, false, 21.225171, 45.7534005));
            database.insertAttraction(new Attraction(0, getString(R.string.chinese_restaurant_name),AttractionType.RESTAURANT, getString(R.string.chinese_restaurant_info),
                    R.drawable.cathedral, false, 21.2512388, 45.7632856));
            database.insertAttraction(new Attraction(0, getString(R.string.riviere_restaurant_name),AttractionType.RESTAURANT, getString(R.string.riviere_restaurant_info),
                    R.drawable.assumption, false, 21.22661, 45.7488825));
            database.insertAttraction(new Attraction(0, getString(R.string.banat_garden_name),AttractionType.RESTAURANT, getString(R.string.banat_garden_info),
                    R.drawable.sfecaterina, false, 21.226525, 45.7489663));
            database.insertAttraction(new Attraction(0, getString(R.string.la_strada_name),AttractionType.RESTAURANT, getString(R.string.la_strada_info),
                    R.drawable.cathedralstgheorghe, false, 21.22915, 45.7660743));
            database.insertAttraction(new Attraction(0, getString(R.string.sky_restaurant_name),AttractionType.RESTAURANT, getString(R.string.sky_restaurant_info),
                    R.drawable.elimjpg, false, 21.22185, 45.756769));
            database.insertAttraction(new Attraction(0, getString(R.string.flower_house_name),AttractionType.RESTAURANT, getString(R.string.flower_house_info),
                    R.drawable.birthofmarychurch, false, 21.226839, 45.7546834));

            database.insertAttraction(new Attraction(0, getString(R.string.tucano_cafe_name),AttractionType.CAFENEA, getString(R.string.tucano_cafe_info),
                    R.drawable.elimjpg, false, 21.2271253, 45.7567436));
            database.insertAttraction(new Attraction(0, getString(R.string.starbucks_name),AttractionType.CAFENEA, getString(R.string.starbucks_info),
                    R.drawable.birthofmarychurch, false, 21.2270906, 45.766370));
            database.insertAttraction(new Attraction(0, getString(R.string.segafredo_name),AttractionType.CAFENEA, getString(R.string.segafredo_info),
                    R.drawable.elimjpg, false, 21.22459, 45.7522865));
            database.insertAttraction(new Attraction(0, getString(R.string.cafeneaua_verde_name),AttractionType.CAFENEA, getString(R.string.cafeneaua_verde_info),
                    R.drawable.birthofmarychurch, false, 21.2268, 45.756137));
            database.insertAttraction(new Attraction(0, getString(R.string.cafe_opera_name),AttractionType.CAFENEA, getString(R.string.cafe_opera_info),
                    R.drawable.bisericamillenium, false, 21.2244554, 45.7520221));
            database.insertAttraction(new Attraction(0, getString(R.string.viviani_cafe_name),AttractionType.CAFENEA, getString(R.string.viviani_cafe_info),
                    R.drawable.cathedral, false, 21.21778, 45.7631301));
            database.insertAttraction(new Attraction(0, getString(R.string.garage_cafe_name),AttractionType.CAFENEA, getString(R.string.garage_cafe_info),
                    R.drawable.assumption, false, 21.230150, 45.7573328));
            database.insertAttraction(new Attraction(0, getString(R.string.bierhaus_name),AttractionType.CAFENEA, getString(R.string.bierhaus_info),
                    R.drawable.sfecaterina, false, 21.22714, 45.7585487));
            database.insertAttraction(new Attraction(0, getString(R.string.toniq_bar_name),AttractionType.CAFENEA, getString(R.string.toniq_bar_info),
                    R.drawable.cathedralstgheorghe, false, 21.2269, 45.7490329));
            database.insertAttraction(new Attraction(0, getString(R.string.vineri_15_name),AttractionType.CAFENEA, getString(R.string.vineri_15_name),
                    R.drawable.elimjpg, false, 21.230367, 45.748054));
            database.insertAttraction(new Attraction(0, getString(R.string.la_capite_name),AttractionType.CAFENEA, getString(R.string.la_capite_info),
                    R.drawable.birthofmarychurch, false, 21.230972, 45.7482099));

            database.insertAttraction(new Attraction(0, getString(R.string.heaven_club_name),AttractionType.CLUB, getString(R.string.heaven_club_info),
                    R.drawable.elimjpg, false, 21.2491382, 45.7396));
            database.insertAttraction(new Attraction(0, getString(R.string.epic_club_name),AttractionType.CLUB, getString(R.string.epic_club_info),
                    R.drawable.birthofmarychurch, false, 21.222902, 45.7692854));
            database.insertAttraction(new Attraction(0, getString(R.string.fratelli_club_name),AttractionType.CLUB, getString(R.string.fratelli_club_info),
                    R.drawable.elimjpg, false, 21.2366, 45.750845));
            database.insertAttraction(new Attraction(0, getString(R.string.club_taine_name),AttractionType.CLUB, getString(R.string.club_taine_info),
                    R.drawable.birthofmarychurch, false, 21.2302, 45.758429));
            database.insertAttraction(new Attraction(0, getString(R.string.club_daos_name),AttractionType.CLUB, getString(R.string.club_daos_info),
                    R.drawable.bisericamillenium, false, 21.21652, 45.7504715));
            database.insertAttraction(new Attraction(0, getString(R.string.bunker_name),AttractionType.CLUB, getString(R.string.bunker_info),
                    R.drawable.cathedral, false, 21.22704, 45.7586585));
            database.insertAttraction(new Attraction(0, getString(R.string.nouveau_name),AttractionType.CLUB, getString(R.string.nouveau_info),
                    R.drawable.assumption, false, 21.226541, 45.7576361));
            database.insertAttraction(new Attraction(0, getString(R.string.storia_name),AttractionType.CLUB, getString(R.string.storia_info),
                    R.drawable.sfecaterina, false, 21.228027, 45.756657));
            database.insertAttraction(new Attraction(0, getString(R.string.wakeup_name),AttractionType.CLUB, getString(R.string.wakeup_info),
                    R.drawable.cathedralstgheorghe, false, 21.238395, 45.7500517));
            database.insertAttraction(new Attraction(0, getString(R.string.jardin_name),AttractionType.CLUB, getString(R.string.jardin_info),
                    R.drawable.elimjpg, false, 21.22924, 45.7480009));
            database.insertAttraction(new Attraction(0, getString(R.string.scottish_name),AttractionType.CLUB, getString(R.string.scottish_info),
                    R.drawable.birthofmarychurch, false, 21.2286, 45.756618));
            database.insertAttraction(new Attraction(0, getString(R.string.atu_name),AttractionType.CLUB, getString(R.string.atu_info),
                    R.drawable.birthofmarychurch, false, 21.23854, 45.7499828));

            database.insertAttraction(new Attraction(0, getString(R.string.hotel_timisoara_name),AttractionType.HOTEL, getString(R.string.hotel_timisoara_info),
                    R.drawable.birthofmarychurch, false, 21.225327, 45.754758));
            database.insertAttraction(new Attraction(0, getString(R.string.iosefin_residence_name),AttractionType.HOTEL, getString(R.string.iosefin_residence_info),
                    R.drawable.elimjpg, false, 21.2158, 45.74664));
            database.insertAttraction(new Attraction(0, getString(R.string.hotel_central_name),AttractionType.HOTEL, getString(R.string.hotel_central_info),
                    R.drawable.birthofmarychurch, false, 21.226835, 45.757676));
            database.insertAttraction(new Attraction(0, getString(R.string.hotel_savoy_name),AttractionType.HOTEL, getString(R.string.hotel_savoy_info),
                    R.drawable.bisericamillenium, false, 21.2243426, 45.747708));
            database.insertAttraction(new Attraction(0, getString(R.string.hotel_excelsior_name),AttractionType.HOTEL, getString(R.string.hotel_excelsior_info),
                    R.drawable.cathedral, false, 21.219738, 45.7430566));
            database.insertAttraction(new Attraction(0, getString(R.string.hotel_perla_name),AttractionType.HOTEL, getString(R.string.hotel_perla_info),
                    R.drawable.assumption, false, 21.227554, 45.7421759));
            database.insertAttraction(new Attraction(0, getString(R.string.hotel_nh_name),AttractionType.HOTEL, getString(R.string.hotel_nh_info),
                    R.drawable.sfecaterina, false, 21.2420786, 45.754617));
            database.insertAttraction(new Attraction(0, getString(R.string.hotel_president_name),AttractionType.HOTEL, getString(R.string.hotel_president_info),
                    R.drawable.cathedralstgheorghe, false, 21.212774, 45.758356));
            database.insertAttraction(new Attraction(0, getString(R.string.hotel_arta_name),AttractionType.HOTEL, getString(R.string.hotel_arta_info),
                    R.drawable.elimjpg, false, 21.289125, 45.773353));
            database.insertAttraction(new Attraction(0, getString(R.string.hotel_euro_name),AttractionType.HOTEL, getString(R.string.hotel_euro_info),
                    R.drawable.birthofmarychurch, false, 21.24464, 45.7509937));
            database.insertAttraction(new Attraction(0, getString(R.string.hotel_pacific_name),AttractionType.HOTEL, getString(R.string.hotel_pacific_info),
                    R.drawable.birthofmarychurch, false, 21.24685599, 45.764335));

            database.insertAttraction(new Attraction(0, getString(R.string.hostel_nord_name),AttractionType.HOSTEL, getString(R.string.hostel_nord_info),
                    R.drawable.elimjpg, false, 21.20877, 45.749113));
            database.insertAttraction(new Attraction(0, getString(R.string.central_hostel_name),AttractionType.HOSTEL, getString(R.string.central_hostel_info),
                    R.drawable.birthofmarychurch, false, 21.22677, 45.754769));
            database.insertAttraction(new Attraction(0, getString(R.string.hotel_costel_name),AttractionType.HOSTEL, getString(R.string.hotel_costel_info),
                    R.drawable.birthofmarychurch, false, 21.244692, 45.758361));

            database.insertAttraction(new Attraction(0, getString(R.string.pensiune_elisei_name),AttractionType.PENSIUNE, getString(R.string.pensiune_elisei_info),
                    R.drawable.elimjpg, false, 21.218500, 45.7346672));
            database.insertAttraction(new Attraction(0, getString(R.string.pensiune_normandia_name),AttractionType.PENSIUNE, getString(R.string.pensiune_normandia_info),
                    R.drawable.birthofmarychurch, false, 21.231151, 45.733085));
            database.insertAttraction(new Attraction(0, getString(R.string.pensiune_timisreana_name),AttractionType.PENSIUNE, getString(R.string.pensiune_timisoreana_info),
                    R.drawable.birthofmarychurch, false, 21.21080, 45.7400199));
            database.insertAttraction(new Attraction(0, getString(R.string.pensiune_venus_name),AttractionType.PENSIUNE, getString(R.string.pensiune_venus_info),
                    R.drawable.elimjpg, false, 21.227288, 45.7354789));
            database.insertAttraction(new Attraction(0, getString(R.string.pensiune_vladut_name),AttractionType.PENSIUNE, getString(R.string.pensiune_vladut_info),
                    R.drawable.birthofmarychurch, false, 21.278629, 45.75612));
            database.insertAttraction(new Attraction(0, getString(R.string.pensiune_yasmine_name),AttractionType.PENSIUNE, getString(R.string.pensiune_yasmine_info),
                    R.drawable.birthofmarychurch, false, 21.26158, 45.759727));
            database.insertAttraction(new Attraction(0, getString(R.string.pensiune_voiaj_name),AttractionType.PENSIUNE, getString(R.string.pensiune_voiaj_info),
                    R.drawable.birthofmarychurch, false, 21.21456, 45.7745));









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
