package com.example.restaurants.Data;


import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurants.Model.Restaurants;
import com.example.restaurants.R;
import com.example.restaurants.Res_Dets;
import com.example.restaurants.RestaurantDetails;
import com.example.restaurants.Show_me;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantsRecyclerAdapter extends RecyclerView.Adapter<RestaurantsRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Restaurants> restaurantsList;

    public RestaurantsRecyclerAdapter(Context context, List<Restaurants> restaurantsList) {
        this.context = context;
        this.restaurantsList = restaurantsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Restaurants restaurants = restaurantsList.get(position);

            Picasso.with(context).load(restaurants.getImageLink()).placeholder(android.R.drawable.ic_btn_speak_now).into(holder.resImage);

            holder.resname.setText("Name: "+restaurants.getName());
            holder.freeseats.setText("Free tables: "+restaurants.getFreeseats());
            holder.distance.setText("Distance: "+restaurants.getDistance()+"m");
    }

    @Override
    public int getItemCount() {
        return restaurantsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView resImage;
        TextView resname, freeseats, distance;
        Button showmeRes;
        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            resImage = (ImageView) itemView.findViewById(R.id.theImage);
            resname = (TextView) itemView.findViewById(R.id.resname);
            freeseats = (TextView) itemView.findViewById(R.id.freeseats);
            distance = (TextView) itemView.findViewById(R.id.distance);
            showmeRes = (Button) itemView.findViewById(R.id.showme);

            showmeRes.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Restaurants res = restaurantsList.get(getAdapterPosition());
                    String id = res.getId();
                    Intent intent = new Intent(context, RestaurantDetails.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("ID", id);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.showme){
                Restaurants resx = restaurantsList.get(getAdapterPosition());
                Intent intent = new Intent(context, Show_me.class);
                intent.putExtra("Latitude", resx.getLatitude());
                intent.putExtra("Longitude", resx.getLongitude());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }
}
