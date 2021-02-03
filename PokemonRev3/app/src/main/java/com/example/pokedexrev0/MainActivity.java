package com.example.pokedexrev0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //basically a list of objects
        recyclerView = findViewById(R.id.recycler_view);

        //need an adapter to list out the pokemon
        adapter = new PokedexAdapter(getApplicationContext());
        layoutManager = new LinearLayoutManager(this);
        //linking the recyclerView to the adapter to populate the information in it
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

    }
}