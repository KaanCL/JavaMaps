package com.example.javamaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.javamaps.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
private ActivityMainBinding binding;
    ArrayList<Place> placeArrayList;
    PlaceAdapter placeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        placeArrayList=new ArrayList<Place>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeAdapter = new PlaceAdapter(placeArrayList);
        binding.recyclerView.setAdapter(placeAdapter);

       getData();

    }

    public void getData(){

        try {
            SQLiteDatabase database = this .openOrCreateDatabase("Place",MODE_PRIVATE,null);

            Cursor cursor = database.rawQuery("SELECT * FROM place",null);
            int placenameIx=cursor.getColumnIndex("placename");
            int idIx = cursor.getColumnIndex("id");


            while(cursor.moveToNext()){
                String placename = cursor.getString(placenameIx);
                int id = cursor.getInt(idIx);
                Place place = new Place(placename,id);
                placeArrayList.add(place);
            }

            cursor.close();

        }catch (Exception e){e.printStackTrace();}




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.travel_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.add_place){
            Intent intent = new Intent(MainActivity.this,MapsActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}
