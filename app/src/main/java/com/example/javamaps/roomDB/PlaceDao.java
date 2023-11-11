package com.example.javamaps.roomDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.javamaps.model.Place;

import java.util.List;

@Dao
public interface PlaceDao {


    @Query("SELECT * FROM place")
    List<Place> getAll();

    @Insert
    void insert(Place place);

    @Delete
    void delete(Place place);


}