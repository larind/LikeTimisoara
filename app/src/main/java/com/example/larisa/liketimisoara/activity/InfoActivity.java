package com.example.larisa.liketimisoara.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.larisa.liketimisoara.Attraction;
import com.example.larisa.liketimisoara.AttractionType;
import com.example.larisa.liketimisoara.R;
import com.example.larisa.liketimisoara.activity.adapter.RecycleViewInfoAdapter;
import com.example.larisa.liketimisoara.db.DB;

import java.util.ArrayList;
import java.util.List;

import static android.widget.GridLayout.HORIZONTAL;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        AttractionType chosenAttractionType = (AttractionType) getIntent().getSerializableExtra("EXTRA_ATTRACTION_TYPE");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_info);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                HORIZONTAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        List<Attraction> attractions = DB.getInstance(getApplicationContext()).getAttractions(chosenAttractionType);

        RecycleViewInfoAdapter adapter = new RecycleViewInfoAdapter(attractions, this);
        recyclerView.setAdapter(adapter);

    }
}
