package com.company.roomie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.company.roomie.adapters.HousesAdapter;
import com.company.roomie.models.Houses;
import com.company.roomie.utils.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Favorites extends AppCompatActivity {

    private List<Houses> housesList;
    private RecyclerView recyclerView;
    private HousesAdapter adapter;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        initUI();
    }

    private void initUI(){
        housesList = new ArrayList<>();
        recyclerView = findViewById(R.id.favorite_list);
        layout = findViewById(R.id.nothing);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fetchFavourites();
    }

    private void fetchFavourites(){
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
                        housesList.add(house);
                    }

                    adapter = new HousesAdapter(Favorites.this,housesList);
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
                map.put("apicall","fetch_favourites");
                map.put("user_email",new UserSession(Favorites.this).getUserEmail());
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
