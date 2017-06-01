package com.example.larisa.liketimisoara.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.larisa.liketimisoara.Attraction;
import com.example.larisa.liketimisoara.AttractionType;
import com.example.larisa.liketimisoara.R;
import com.example.larisa.liketimisoara.db.DB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SportsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        AttractionType attractionType = (AttractionType) getIntent().getSerializableExtra("EXTRA_ATTRACTION");
        List<Attraction> attractions = DB.getInstance(this).getAttractions(attractionType);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), attractions);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sports, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_ATTRACTION = "section_attraction";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, Attraction attraction) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putSerializable(ARG_SECTION_ATTRACTION, attraction);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final Attraction attraction = (Attraction) getArguments().getSerializable(ARG_SECTION_ATTRACTION);
            View rootView = inflater.inflate(R.layout.fragment_sports, container, false);
            TextView title = (TextView) rootView.findViewById(R.id.sports_title);
            ImageView image = (ImageView) rootView.findViewById(R.id.sports_image);
            TextView info = (TextView) rootView.findViewById(R.id.sports_info);


            title.setText(getString(R.string.section_format, attraction.getName()));
            info.setText(getString(R.string.section_format, attraction.getInfo()));
            image.setImageDrawable(getResources().getDrawable(attraction.getImageResourceId()));
            Button mapsButton = (Button) rootView.findViewById(R.id.sports_maps);
            mapsButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {

                    Intent mapIntent = new Intent(getContext(), MapsActivity.class);
                    mapIntent.putExtra("EXTRA_ATTRACTIONS", new ArrayList<>(Collections.singletonList(attraction)));
                    startActivity(mapIntent);
                }
            });
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Attraction> attractions;

        public SectionsPagerAdapter(FragmentManager fm, List<Attraction> attractions) {
            super(fm);
            this.attractions = attractions;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, attractions.get(position));
        }

        @Override
        public int getCount() {
            return attractions.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
