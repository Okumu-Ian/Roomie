package com.company.roomie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.company.roomie.adapters.HousesAdapter;
import com.company.roomie.models.Houses;
import com.company.roomie.utils.UserSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private TextInputEditText edt_full_name, edt_email,edt_password;
    private AppCompatButton btn_update;
    private RecyclerView recyclerView;
    private List<Houses> housesList;
    private HousesAdapter adapter;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initUI();
    }

    private void initUI(){
        housesList = new ArrayList<>();
        edt_full_name = findViewById(R.id.edt_profile_full_name);
        edt_email = findViewById(R.id.edt_profile_email);
        edt_password = findViewById(R.id.edt_profile_password);
        btn_update = findViewById(R.id.btn_profile_update);
        fab = findViewById(R.id.fab_add_house);

        recyclerView = findViewById(R.id.user_listings);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser(view);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this,AddHouse.class));
            }
        });

        fetchValues();
        fetchFavourites();

    }

    private void fetchValues(){
        UserSession session = new UserSession(this);
        if (session.getUserEmail() != null){
            if (session.getUserName() != null){
                edt_full_name.setText(session.getUserName());
            }else{
                edt_full_name.setText("");
            }
            edt_password.setText(session.getUserPassword());
            edt_email.setText(session.getUserEmail());
        }
    }

    private void updateUser(final View mView)
    {
        String url = "https://icelabs-eeyan.com/roomie/authentication.php";
        final String user_email;
        final String user_password;

        if(edt_email.getText().toString().length() < 5){
            edt_email.setError("Please provide good email");
        }else if (edt_password.getText().toString().length() < 6){
            edt_password.setError("Provide an email longer than 6 characters");
        }else if(edt_full_name.getText().toString().length() < 3){
            edt_full_name.setError("Provide a valid name!!");
        }else{
            user_email = edt_email.getText().toString();
            user_password = edt_password.getText().toString();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String responsed) {
                            try {
                                JSONObject response = new JSONObject(responsed);
                                try {
                                    String statusCode = response.getString("Status");
                                    if(statusCode.equals("104")){
                                        UserSession data = new UserSession(Profile.this);
                                        data.createUserSession();
                                        data.setUserEmail(user_email);
                                        data.setUserPassword(user_password);
                                        data.setUserName(edt_full_name.getText().toString());
                                        Snackbar.make(mView,response.getString("Message"),Snackbar.LENGTH_LONG).show();
                                    }else{
                                        Snackbar.make(mView,response.getString("Message"),Snackbar.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }catch (JSONException ex){
                                ex.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Profile.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("email",user_email);
                    map.put("password1",user_password);
                    map.put("key","update");
                    map.put("full_name",edt_full_name.getText().toString());
                    return map;
                }
            };
            Volley.newRequestQueue(Profile.this).add(stringRequest);

        }
    }

    private void fetchFavourites(){
        String url = "http://icelabs-eeyan.com/roomie/fetch_houses.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray array = new JSONArray(response);
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

                    adapter = new HousesAdapter(Profile.this,housesList);
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
                map.put("apicall","fetch_houses");
                map.put("user_email",new UserSession(Profile.this).getUserEmail());
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        housesList.clear();
        fetchFavourites();
    }
}
