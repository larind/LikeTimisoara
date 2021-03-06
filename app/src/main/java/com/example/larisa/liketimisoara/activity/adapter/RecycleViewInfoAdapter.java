package com.example.larisa.liketimisoara.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.larisa.liketimisoara.Attraction;
import com.example.larisa.liketimisoara.R;
import com.example.larisa.liketimisoara.activity.DetailsActivity;
import com.example.larisa.liketimisoara.activity.InfoActivity;

import java.util.List;

public class RecycleViewInfoAdapter extends RecyclerView.Adapter<RecycleViewInfoAdapter.MyViewHolder> {

    private List<Attraction> dataSet;
    private Context context;

    static class MyViewHolder extends RecyclerView.ViewHolder {

        View rowView;
        TextView textViewName;

        MyViewHolder(View itemView) {
            super(itemView);
            this.rowView = itemView.findViewById(R.id.info_row_view);
            this.textViewName = (TextView) itemView.findViewById(R.id.infoTextView);
        }
    }

    public RecycleViewInfoAdapter(List<Attraction> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_item_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int listPosition) {

        TextView textViewName = holder.textViewName;

        textViewName.setText(dataSet.get(listPosition).getName());

        holder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent detailsIntent = new Intent(context, DetailsActivity.class);

                detailsIntent.putExtra("EXTRA_ATTRACTION", dataSet.get(holder.getAdapterPosition()));

                context.startActivity(detailsIntent);
            }
        });

        holder.textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent detailsIntent = new Intent(context, DetailsActivity.class);

                detailsIntent.putExtra("EXTRA_ATTRACTION", dataSet.get(holder.getAdapterPosition()));

                context.startActivity(detailsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}