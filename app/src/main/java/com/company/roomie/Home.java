package com.company.roomie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.company.roomie.adapters.HousesAdapter;
import com.company.roomie.models.Houses;
import com.company.roomie.utils.UserSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.sephiroth.android.library.rangeseekbar.RangeSeekBar;

public class Home extends AppCompatActivity {

    private List<Houses> houses;
    private RecyclerView recyclerView;
    private HousesAdapter adapter;
    private Context mCtx;
    private FloatingActionButton fab;
    private UserSession session;
    private LinearLayout layout,filter_layout;
    private AppCompatSpinner min, max;
    private AppCompatButton filterButton;
    private AppCompatSeekBar seekBar;
    private AppCompatTextView range_txt;
    private RangeSeekBar range;
    int progressValue = 5000 ;
    int minValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        houses = new ArrayList<>();
        mCtx = Home.this;
        session = new UserSession(mCtx);
        initUI();
    }

    private void initUI(){

        recyclerView = findViewById(R.id.roomies_list);
        fab = findViewById(R.id.fab_favourites);
        layout = findViewById(R.id.nothing);
        filterButton = findViewById(R.id.btn_filter);
        filter_layout = findViewById(R.id.filter_layout);
        range_txt = findViewById(R.id.text_value);
        range = findViewById(R.id.price_range_bar);
        fetchMax();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (session.checkUserSession())
                switchScreen();
                else Snackbar.make(view,"Favourites only for logged in members",Snackbar.LENGTH_LONG).show();
            }
        });
        GridLayoutManager manager = new GridLayoutManager(mCtx,2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fetchHouses(0, Integer.MAX_VALUE);

        range.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RangeSeekBar rangeSeekBar, int i, int i1, boolean b) {
                minValue = i;
                progressValue = i1;
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar rangeSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar rangeSeekBar) {
                range_txt.setText(((minValue) + " - " +(progressValue) + (" $/Month")));
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                houses.clear();
                fetchHouses(range.getProgressStart(),range.getProgressEnd());
                Toast.makeText(mCtx, ""+progressValue, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void switchScreen(){
        startActivity(new Intent(this,Favorites.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_profile){
            if (!session.checkUserSession()){
                Snackbar.make(getWindow().getDecorView().getRootView(),"Kindly Login first",Snackbar.LENGTH_LONG).show();
            }else
            startActivity(new Intent(this,Profile.class));
        }else if(id == R.id.action_logout){
            if(item.getTitle() == "Login"){
                startActivity(new Intent(Home.this,MainActivity.class));
            }else{
                new UserSession(this).deleteUserSession();
                finish();
            }

        }
        return true;
    }

    private void fetchMax(){
        String url = "https://icelabs-eeyan.com/roomie/fetch_houses.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    String max = object.getString("Max");
                    String min = object.getString("Min");

                    range.setMax(Integer.parseInt(max));
                    range_txt.setText(("0" + " - " +(range.getMax()) + (" $/Month")));

                }catch (JSONException ex){
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map= new HashMap<>();
                map.put("apicall","minmax");
                return map;
            }
        };
        Volley.newRequestQueue(mCtx).add(request);
    }

    private void fetchHouses(final int min, final int max){

        String url = "https://icelabs-eeyan.com/roomie/fetch_houses.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray array = new JSONArray(response);
                    if (array.length() < 0){
                        layout.setVisibility(View.VISIBLE);
                    }else{
                        layout.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Houses house = new Houses();
                        house.setHouse_banner(object.getString("house_banner"));
                        house.setHouse_description(object.getString("house_description"));
                        house.setHouse_id(object.getString("house_id"));
                        house.setHouse_title(object.getString("house_name"));
                        house.setUser_id(object.getString("user_email"));
                        house.setHouse_price(object.getString("house_price"));
                        houses.add(house);
                    }

                    adapter = new HousesAdapter(mCtx,houses);
                    recyclerView.setAdapter(adapter);

                }catch (JSONException ex){
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map= new HashMap<>();
                map.put("apicall","fetchAllHouses");
                map.put("min",String.valueOf(min));
                map.put("max",String.valueOf(max));
                return map;
            }
        };
        Volley.newRequestQueue(mCtx).add(request);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_logout);
        if (!session.checkUserSession()){
            item.setTitle("Login");
        }else{
            item.setTitle("Logout");
        }
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        houses.clear();
        fetchHouses(0,Integer.MAX_VALUE);
    }
}
