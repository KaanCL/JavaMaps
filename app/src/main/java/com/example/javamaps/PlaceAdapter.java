package com.example.javamaps;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.javamaps.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {
  ArrayList<Place> placeArrayList;

  public PlaceAdapter(ArrayList<Place> placeArrayList){
      this.placeArrayList = placeArrayList;
  }



    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
      return new PlaceHolder(binding);

    }

    @Override
    public void onBindViewHolder(PlaceAdapter.PlaceHolder holder, int position) {
    holder.binding.PlaceText.setText(placeArrayList.get(position).placename);
    }
    @Override
    public int getItemCount() {
        return placeArrayList.size();
    }


    public class PlaceHolder extends RecyclerView.ViewHolder{
      private RecyclerRowBinding binding;

      public  PlaceHolder(RecyclerRowBinding binding){
          super(binding.getRoot());
          this.binding=binding;

      }


}


}

