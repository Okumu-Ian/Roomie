package com.company.roomie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

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

public class Home extends AppCompatActivity {

    private List<Houses> houses;
    private RecyclerView recyclerView;
    private HousesAdapter adapter;
    private Context mCtx;
    private FloatingActionButton fab;
    private UserSession session;
    private LinearLayout layout;

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
        fetchHouses();


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

    private void fetchHouses(){
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
        fetchHouses();
    }
}
