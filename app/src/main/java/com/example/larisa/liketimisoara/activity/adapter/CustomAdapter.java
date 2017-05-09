package com.example.larisa.liketimisoara.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.larisa.liketimisoara.Attraction;
import com.example.larisa.liketimisoara.DataModel;
import com.example.larisa.liketimisoara.R;
import com.example.larisa.liketimisoara.activity.DetailsActivity;
import com.example.larisa.liketimisoara.activity.InfoActivity;
import com.example.larisa.liketimisoara.activity.MapsActivity;
import com.example.larisa.liketimisoara.db.DB;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<DataModel> dataSet;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View cardView;
        TextView textViewName;
        ImageView imageViewIcon;
        ImageButton infoItemImageButton;
        ImageButton mapItemImageButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
            this.infoItemImageButton = (ImageButton) itemView.findViewById(R.id.itemInfo);
            this.mapItemImageButton = (ImageButton) itemView.findViewById(R.id.itemMap);
        }
    }

    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

                holder.cardView.setBackgroundColor(context.getResources().getColor(android.R.color.white));

        TextView textViewName = holder.textViewName;
        ImageView imageView = holder.imageViewIcon;

        textViewName.setText(dataSet.get(listPosition).getName());
        imageView.setImageResource(dataSet.get(listPosition).getImage());

        holder.infoItemImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent infoIntent = new Intent(context, InfoActivity.class);

                infoIntent.putExtra("EXTRA_ATTRACTION_TYPE", dataSet.get(listPosition).getType());

                context.startActivity(infoIntent);
            }
        });

        holder.mapItemImageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent mapIntent = new Intent(context, MapsActivity.class);

                ArrayList<Attraction> attractions = (ArrayList<Attraction>)DB.getInstance(context).getAttractions(dataSet.get(listPosition).getType());

                mapIntent.putExtra("EXTRA_ATTRACTIONS", attractions);

                context.startActivity(mapIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}