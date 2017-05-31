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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.larisa.liketimisoara.Attraction;
import com.example.larisa.liketimisoara.AttractionType;
import com.example.larisa.liketimisoara.DataModel;
import com.example.larisa.liketimisoara.MyData;
import com.example.larisa.liketimisoara.R;
import com.example.larisa.liketimisoara.activity.adapter.CustomAdapter;
import com.example.larisa.liketimisoara.db.DB;
import com.example.larisa.liketimisoara.db.DBException;

import java.util.ArrayList;
import java.util.List;

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
                    R.drawable.harta, false, 45.7513, 21.2211));      */
            database.insertAttraction(new Attraction(7, R.string.extreme_fitness_name, AttractionType.SALA_FITNESS, R.string.extreme_fitness_info,
                    R.drawable.extreme, false, 45.728675, 21.2386))  ;
            database.insertAttraction(new Attraction(7, R.string.friends_arena_name, AttractionType.SALA_FITNESS, R.string.friends_arena_info,
                    R.drawable.friends, false, 45.7301899, 21.2013))  ;
            database.insertAttraction(new Attraction(7, R.string.iguana_fitness_name, AttractionType.SALA_FITNESS, R.string.iguana_fitness_info,
                    R.drawable.iguana, false, 45.7639368, 21.2561))  ;
            database.insertAttraction(new Attraction(7, R.string.stil_fitness_name, AttractionType.SALA_FITNESS, R.string.stil_fitness_info,
                    R.drawable.stil, false, 45.7434774, 21.2441))  ;
            database.insertAttraction(new Attraction(7, R.string.body_time_name, AttractionType.SALA_FITNESS, R.string.body_time_info,
                    R.drawable.bodytime, false, 45.7739105, 21.2313))  ;
            database.insertAttraction(new Attraction(7, R.string.smart_fit3_name, AttractionType.SALA_FITNESS, R.string.smart_fit3_info,
                    R.drawable.smartfit, false, 45.724749, 21.1998))  ;
            database.insertAttraction(new Attraction(7, R.string.continental_fitness_name, AttractionType.SALA_FITNESS, R.string.continental_fitness_info,
                    R.drawable.continental, false, 45.7553, 21.2324))  ;
            database.insertAttraction(new Attraction(7, R.string.world_class_name, AttractionType.SALA_FITNESS, R.string.world_class_info,
                    R.drawable.worldclass, false, 45.7627697, 21.2297))  ;
            database.insertAttraction(new Attraction(7, R.string.banu_fitness_name, AttractionType.SALA_FITNESS, R.string.banu_fitness_info,
                    R.drawable.banusport, false, 45.74354, 21.242))  ;
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
