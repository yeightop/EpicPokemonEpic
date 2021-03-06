package com.example.pokedexrev0;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder> implements Filterable {



    public static class PokedexViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout containerView;
        public TextView textView;
        PokedexViewHolder(View itemView) {
            super(itemView);
            //containerView is each row we see
            containerView = itemView.findViewById(R.id.pokedex_row);
            //textView is the information in each row
            textView = itemView.findViewById(R.id.pokedex_row_txt);
            //making the rows do something when clicked
            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Pokemon current = (Pokemon) containerView.getTag();
                    Intent intent = new Intent(view.getContext(),PokemonActivity.class);

                    //putExtra(name of the placeholder, the value of the placeholder)
                    /*      old way needed name and number,  now we just need a URL
                    intent.putExtra("name",current.getName());
                    intent.putExtra("number",current.getNumber());
                     */
                    Log.d("PokeAdapter",current.getUrl());
                    intent.putExtra("url",current.getUrl());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }


    /*
    private List<Pokemon> pokemon = Arrays.asList(
            new Pokemon("Bulbasaur",1),
            new Pokemon("Ivysaur",2),
            new Pokemon("Venusaur",3),
            new Pokemon("The Pat Mayes",4),
            new Pokemon("Hunter Wilder",5)
        );
    */


    private List<Pokemon> pokemon = new ArrayList<>();
    private List<Pokemon> filtered;
    private RequestQueue requestQueue;


    @Override
    public Filter getFilter() {
        return new PokemonFilter();
    }

    private class PokemonFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String constraintNew = constraint.toString();
            // implement your search here!
            ArrayList<Pokemon> filteredPokemon = new ArrayList<>();
            for (Pokemon p: pokemon){
                if (p.getName().toLowerCase().contains(constraintNew.toLowerCase())){
                    Log.d("Search issue",p.getName());
                    filteredPokemon.add(p);
                }
            }
            FilterResults results = new FilterResults();

            results.values = filteredPokemon; // you need to create this variable!
            results.count = filteredPokemon.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filtered = (List<Pokemon>) results.values;
            notifyDataSetChanged();
        }
    }

    private SharedPreferences preference;

    PokedexAdapter(Context context){
        Log.d("PokedexAdapter","new PokedexAdapter");
        requestQueue = Volley.newRequestQueue(context);
        filtered=pokemon;
        preference = PreferenceManager.getDefaultSharedPreferences(context);
        loadPokemon();

        Log.d("PreferenceIssueAda", String.valueOf(preference.getAll()));
        Log.d("PreferenceIssueAda", "Created PokedexAdapter");
    }

    public void loadPokemon(){
        String url = "https://pokeapi.co/api/v2/pokemon/?limit=151";
        Log.d("loadPokemon",url);
        //from the volley library
        /*
        request object below (1st are we getting or setting info to the web,
                                2nd is url your sending it to
                                3rd listener as data we are sending
                                4th finally what method do you want at the end of the request
        */
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length();i++){
                        JSONObject result = results.getJSONObject(i);
                        String name = result.getString("name");
                        //String des = result.getString("flavor_text_entries");

                        pokemon.add(new Pokemon(
                                name.substring(0,1).toUpperCase()+name.substring(1),
                                result.getString("url")

                        ));
                        Log.d("pull des test", result.getString("url"));
                        //Log.d("flavor", result.getString("flavor_text_entries"));
                    }
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("pokeapi pull", "Json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("pokeapi pull","Pokemon list error");

            }
        });
        requestQueue.add(request);
    }

    @NonNull
    @Override
    //creates the view that we see on the screen
    public PokedexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokedex_row,parent,false);
        Log.d("PokedexAdapter", "oncreateviewholder");

        return new PokedexViewHolder(view);
    }

    @Override
    //telling what each row will look like
    //set the different properties of each row, but this is what reads in a click....  keyBinding to that ViewHolder
    public void onBindViewHolder(@NonNull PokedexViewHolder holder, int position) {
        Pokemon current = filtered.get(position);
        Log.d("PokedexAdapter", String.valueOf(filtered.size()));
        holder.textView.setText(current.getName());
        Log.d("PreferenceIssueAda", current.getName());
        Log.d("PreferenceIssueAda", String.valueOf(preference.getAll()));

        if(preference.contains(current.getName().toLowerCase())) {

            holder.textView.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.pokeball, 0);
            Log.d("preferenceStuff", "onlist " + current.getName());
        } else {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        }
        holder.containerView.setTag(current);
    }

    @Override
    //tells the list how long it should be
    public int getItemCount() {
        Log.d("PokedexAdapter", String.valueOf(filtered.size()));
        return filtered.size();
    }

}