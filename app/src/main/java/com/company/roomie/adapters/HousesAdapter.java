package com.company.roomie.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.roomie.HouseActivity;
import com.company.roomie.R;
import com.company.roomie.models.Houses;

import java.util.List;

public class HousesAdapter  extends RecyclerView.Adapter<HousesAdapter.HouseHolder> {

    class HouseHolder extends RecyclerView.ViewHolder{

        private AppCompatImageView imageBanner;
        private AppCompatTextView houseTitle,housePrice;

        HouseHolder(@NonNull View itemView) {
            super(itemView);
            imageBanner = itemView.findViewById(R.id.img_house_grid);
            houseTitle = itemView.findViewById(R.id.txt_house_grid_title);
            housePrice = itemView.findViewById(R.id.txt_house_grid_price);
        }
    }

    private Context mContext;
    private List<Houses> houses;

    public HousesAdapter(Context mContext, List<Houses> houses) {
        this.mContext = mContext;
        this.houses = houses;
    }

    @NonNull
    @Override
    public HouseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HouseHolder(LayoutInflater.from(mContext).inflate(R.layout.houses_grid_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull HouseHolder holder, int position) {
        final Houses mHouse = houses.get(position);
        Glide.with(mContext).load(mHouse.getHouse_banner()).into(holder.imageBanner);
        holder.houseTitle.setText(mHouse.getHouse_title());
        holder.imageBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, HouseActivity.class);
                mIntent.putExtra("house_details",mHouse);
                mContext.startActivity(mIntent);
            }
        });
        holder.housePrice.setText("Price: $"+mHouse.getHouse_price());
    }

    @Override
    public int getItemCount() {
        return houses.size();
    }
}
