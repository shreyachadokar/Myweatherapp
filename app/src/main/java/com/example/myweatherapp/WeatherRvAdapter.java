package com.example.myweatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRvAdapter extends RecyclerView.Adapter<WeatherRvAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherRvModal> weatherRvModalArrayList;

    public WeatherRvAdapter(Context context, ArrayList<WeatherRvModal> weatherRvModalArrayList) {
        this.context = context;
        this.weatherRvModalArrayList = weatherRvModalArrayList;
    }

    @NonNull
    @Override
    public WeatherRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.wheather_rv,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRvAdapter.ViewHolder holder, int position) {
        WeatherRvModal modal =weatherRvModalArrayList.get(position);
        holder.temp.setText(modal.getTemperature()+"°c");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.condition);
        holder.wind.setText(modal.getWindspeed()+"Km/h");
        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mn");
        SimpleDateFormat output=new SimpleDateFormat(" hh:mn aa");

        try{
            Date t=input.parse(modal.getTime());
            holder.time.setText(output.format(t));

        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherRvModalArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView wind,time,temp;
        private ImageView condition;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wind=itemView.findViewById(R.id.idwind);
            time=itemView.findViewById(R.id.idtime);
            temp=itemView.findViewById(R.id.idtemp);
            condition=itemView.findViewById(R.id.idcondition);
        }

    }
}