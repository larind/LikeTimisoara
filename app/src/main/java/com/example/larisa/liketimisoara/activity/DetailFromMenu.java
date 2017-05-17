package com.example.larisa.liketimisoara.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.larisa.liketimisoara.Attraction;
import com.example.larisa.liketimisoara.R;
import com.example.larisa.liketimisoara.db.DB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailFromMenu extends AppCompatActivity {

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
        setContentView(R.layout.activity_detail_from_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<Attraction> top10Attractions = new ArrayList<>();

        List<Attraction> attractions = DB.getInstance(this).getAttractions(null);

        for (Attraction attraction : attractions) {

            if (attraction.isTop10()) {
                top10Attractions.add(attraction);
            }
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), top10Attractions);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_from_menu, menu);
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
        public static PlaceholderFragment newInstance(int sectionNumber, Attraction top10Attraction) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putSerializable(ARG_SECTION_ATTRACTION, top10Attraction);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final Attraction top10Attraction = (Attraction) getArguments().getSerializable(ARG_SECTION_ATTRACTION);
            View rootView = inflater.inflate(R.layout.fragment_detail_from_menu, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.title_top10);
            TextView textViewInfo = (TextView) rootView.findViewById(R.id.info_top10);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.image_top10);
            textView.setText(getString(R.string.section_format, top10Attraction.getName()));
            textViewInfo.setText(getString(R.string.section_format, top10Attraction.getInfo()));
            imageView.setImageDrawable(getResources().getDrawable(top10Attraction.getImageResourceId()));


            Button attractionMapButton = (Button) rootView.findViewById(R.id.button_top10);
            attractionMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent mapIntent = new Intent(getContext(), MapsActivity.class);
                    mapIntent.putExtra("EXTRA_ATTRACTIONS", new ArrayList<>(Collections.singletonList(top10Attraction)));
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

        private List<Attraction> top10Attractions;

        public SectionsPagerAdapter(FragmentManager fm, List<Attraction> top10Attractions) {
            super(fm);
            this.top10Attractions = top10Attractions;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, top10Attractions.get(position));
        }

        @Override
        public int getCount() {
            return top10Attractions.size();
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
