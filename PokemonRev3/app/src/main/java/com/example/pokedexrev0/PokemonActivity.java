package com.example.pokedexrev0;

import android.app.AppComponentFactory;
import android.graphics.Bitmap;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.sql.Time;


public class PokemonActivity extends AppCompatActivity {
    private TextView nameText;
    private TextView numberText;
    private String  url;
    private TextView type1;
    private TextView type2;
    private Button buttonCaught;
    private String pokeName;
    private RequestQueue requestQueue;
    private String pokeNumber;
    private boolean pokemonIsCaught = false;

    private ImageView pokeImage;
    private String pokeImageKey;


    private SharedPreferences preference;
    private SharedPreferences.Editor editPreference;

    private TextView description;
    private String desURL="https://pokeapi.co/api/v2/pokemon-species";




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);


            //variable = getIntentData.getStringData("intentVariableName");
            //String name = getIntent() .getStringExtra("name");
            //int number = getIntent() .getIntExtra("number",0);
            requestQueue= Volley.newRequestQueue(getApplicationContext());
            url = getIntent().getStringExtra("url");

            nameText = findViewById(R.id.pokemonName);
            numberText = findViewById(R.id.pokemonNumber);
            type1 = findViewById(R.id.pokemonType1);
            type2 = findViewById(R.id.pokemonType2);
            buttonCaught = findViewById(R.id.catchButton);
            pokeImage = findViewById(R.id.pokemonImage);
            description = findViewById(R.id.pokemonDes);
            //Log.d("PokeOnCreate",url);

            preference = PreferenceManager.getDefaultSharedPreferences(this);
            editPreference = preference.edit();


            load();

            //nameText.setText("Name: "+name);
            ///numberText.setText("Number: "+number);

            Log.d("PreferenceIssue", String.valueOf(preference.getAll()));


    }
    public void load(){
        type1.setText("");
        type2.setText("");
        nameText.setText("");
        numberText.setText("");
        String passThisThru;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("PokeRequest", "began request here");
                try {
                    JSONArray typeEntries = response.getJSONArray("types");
                    for (int i = 0;i<typeEntries.length();i++){
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        int slot= typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");
                        if (slot ==1){
                            type1.setText(type);
                        }
                        else{
                            type2.setText(type);
                        }
                    }

                    Log.d("typeTesting",response.getString("name"));
                    Log.d("typeTesting", response.getString("id"));
                    pokeName=response.getString("name");
                    pokeNumber=response.getString("id");
                    loadDes(pokeNumber);
                    Log.d("numberpoke:",pokeNumber);
                    nameText.setText(pokeName.substring(0,1).toUpperCase()+pokeName.substring(1));
                    pokeImageKey = response.getJSONObject("sprites").getString("front_default");
                    new DownloadSpriteTask().execute(pokeImageKey);
                    numberText.setText(pokeNumber);
                    if(preference.contains(pokeName)) {
                        pokemonIsCaught = true;
                        buttonCaught.setText("Release");
                    }
                    else {
                        pokemonIsCaught = false;
                        buttonCaught.setText("Catch");
                    }



                }
                catch (JSONException e){
                    Log.e("PokeActive","JSON error");
                }
            }

        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("PokeActive","Poke list error");
            }
        });
        requestQueue.add(request);
        Log.d("checktoggle", String.valueOf(pokemonIsCaught));

    }


    public void toggleCatch(View view) {
        //gotta catch em all!
        Log.d("toggleCatch", String.valueOf(pokemonIsCaught));
        if(!pokemonIsCaught) {
            pokemonIsCaught = true;
            buttonCaught.setText("Release");
            editPreference.putBoolean(pokeName, pokemonIsCaught).apply();
        }
        else{
            pokemonIsCaught=false;
            buttonCaught.setText("Catch");
            editPreference.remove(pokeName);
            editPreference.apply();
        }
        Log.d("toggleCatch", String.valueOf(pokemonIsCaught));
    }

    public void loadDes(String pokeNumber){
        description.setText("");
        Log.d("numtext: ", numberText.getText().toString());
        Log.d("desURL",desURL+"/"+pokeNumber+"/");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,  desURL + "/" + pokeNumber, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("PokeDesRequest", "began request here | "+desURL + "/" + numberText.getText().toString());
                try {
                    JSONArray flavorEntries = response.getJSONArray("flavor_text_entries");
                    for (int i = 0;i<flavorEntries.length();i++){
                        JSONObject flavorEntry = flavorEntries.getJSONObject(1);
                        String flavor = flavorEntry.getString("flavor_text");
                       // String lang= flavorEntry.getJSONObject("language")
                        Log.d("flavor: ",flavor+" | " + "https://pokeapi.co/api/v2/pokemon-species/133/");
                        description.setText(flavor);

                    }
                    Log.d("typeTesting",response.getString("name"));
                    Log.d("typeTesting", response.getString("id"));

                }
                catch (JSONException e){
                    Log.e("PokeActive","JSON error");
                }
            }

        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("PokeActive","Poke list error");
            }
        });
        requestQueue.add(request);

    }





    private class DownloadSpriteTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openStream());
            }
            catch (IOException e) {
                Log.e("cs50", "Download sprite error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // load the bitmap into the ImageView!
            pokeImage.setImageBitmap(bitmap);
        }
    }


}
