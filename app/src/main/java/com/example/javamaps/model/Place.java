package com.example.javamaps.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Place {

  @PrimaryKey(autoGenerate = true)
   public int id;

  @ColumnInfo(name="PlaceName")
   public String placename;

  @ColumnInfo(name="Latitude")
   public double latitude;

  @ColumnInfo(name="Longitude")
   public double longitude;


    public Place(String placename, double latitude, double longitude) {
        this.placename = placename;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
