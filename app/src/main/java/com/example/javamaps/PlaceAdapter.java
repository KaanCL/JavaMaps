package com.example.javamaps;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.javamaps.databinding.RecyclerRowBinding;
import com.example.javamaps.model.Place;
import com.example.javamaps.view.MapsActivity;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {
  List<Place> placeList;

  public PlaceAdapter(List<Place> placeList){
      this.placeList = placeList;
  }



    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
      return new PlaceHolder(binding);

    }

    @Override
    public void onBindViewHolder(PlaceAdapter.PlaceHolder holder, int position) {
    holder.binding.PlaceText.setText(placeList.get(position).placename);

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(holder.itemView.getContext(), MapsActivity.class);
        intent.putExtra("placename",placeList.get(position).placename);
        holder.itemView.getContext().startActivity(intent);

      }
    });



    }
    @Override
    public int getItemCount() {
        return placeList.size();
    }


    public class PlaceHolder extends RecyclerView.ViewHolder{
      private RecyclerRowBinding binding;

      public  PlaceHolder(RecyclerRowBinding binding){
          super(binding.getRoot());
          this.binding=binding;

      }


}


}

