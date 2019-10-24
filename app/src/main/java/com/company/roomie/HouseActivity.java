package com.company.roomie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.company.roomie.adapters.CommentAdapter;
import com.company.roomie.models.HouseComment;
import com.company.roomie.models.Houses;
import com.company.roomie.utils.UserSession;
import com.google.android.material.button.MaterialButton;
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

public class HouseActivity extends AppCompatActivity {

    private AppCompatImageView banner;
    private AppCompatTextView txt_title, txt_description, txt_email,txt_price;
    private MaterialButton button;
    private FloatingActionButton fab;
    private Intent intent;
    private Houses house;
    private RecyclerView comments_list;
    private CommentAdapter adapter;
    private List<HouseComment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house);
        initUI();
    }

    private void initUI(){
        comments = new ArrayList<>();
        banner = findViewById(R.id.img_house_banner);
        txt_title = findViewById(R.id.txt_house_title);
        txt_description = findViewById(R.id.txt_house_description);
        button = findViewById(R.id.btn_comment);
        comments_list = findViewById(R.id.house_comment_list);
        fab = findViewById(R.id.fab_favourite);
        txt_email =findViewById(R.id.txt_house_email);
        txt_price = findViewById(R.id.txt_house_price);
        comments_list.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        comments_list.setItemAnimator(new DefaultItemAnimator());
        displayDetails();
    }

    private void displayDetails(){
        intent = getIntent();
        house = (Houses) intent.getSerializableExtra("house_details");
        Glide.with(this).load(house.getHouse_banner()).into(banner);
        txt_title.setText("House name: "+house.getHouse_title());
        txt_description.setText("Description: "+house.getHouse_description());
        txt_email.setText("Contact Email: "+house.getUser_id());
        txt_price.setText("Price: $"+house.getHouse_price()+"/Month");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSession data = new UserSession(HouseActivity.this);
                if (data.checkUserSession()){
                    showDialog();
                }else{
                    Snackbar.make(getWindow().getDecorView().getRootView(),
                            "You must be logged in to comment",
                            Snackbar.LENGTH_LONG).show();
                }

            }
        });
        fetchComments(house.getHouse_id());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSession session = new UserSession(HouseActivity.this);
                if(!session.checkUserSession()){
                    Snackbar.make(view,"Please Login first",Snackbar.LENGTH_LONG).show();
                }else
                favouriteHouse(view);
            }
        });
        getSupportActionBar().setTitle(house.getHouse_title());
        getSupportActionBar().setWindowTitle(house.getHouse_title());
    }

    private void fetchComments(final String house_id){
        String url = "https://icelabs-eeyan.com/roomie/fetch_houses.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        HouseComment comment = new HouseComment();
                        JSONObject object = array.getJSONObject(i);
                        comment.setUser_id(object.getString("user_email"));
                        comment.setComment_text(object.getString("comment_text"));
                        comments.add(comment);
                    }

                    adapter = new CommentAdapter(HouseActivity.this,comments);
                    comments_list.setAdapter(adapter);
                }
                catch (JSONException ex){
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(HouseActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("apicall","fetchComments");
                map.put("house_id",house_id);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void showDialog(){
        final Dialog dialog = new Dialog(this);
        View mView = LayoutInflater.from(this).inflate(R.layout.comment_layout, (ViewGroup) getWindow().getDecorView().getRootView(),false);
        AppCompatButton submitButton = mView.findViewById(R.id.btn_submit_comment);
        final TextInputEditText editText = mView.findViewById(R.id.edt_register_comment);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComments(editText.getText().toString());
                editText.setText("");
                dialog.dismiss();
            }
        });
        dialog.setContentView(mView);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void addComments(final String comment){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Commenting...");
        dialog.show();
        String url = "http://icelabs-eeyan.com/roomie/fetch_houses.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                comments.clear();
                fetchComments(house.getHouse_id());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(HouseActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("apicall","addComments");
                map.put("house_id", house.getHouse_id());
                map.put("user_email",new UserSession(HouseActivity.this).getUserEmail());
                map.put("comment_text",comment);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void favouriteHouse(final View mView){
        String url = "https://icelabs-eeyan.com/roomie/fetch_houses.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                JSONObject object = new JSONObject(response);
                String statusCode = object.getString("Status");
                if(statusCode.equals("404")){
                    Snackbar.make(mView,"Already Added to favourites",Snackbar.LENGTH_LONG).show();
                }else if (statusCode.equals("104")){
                    Snackbar.make(mView,"Added to favourites",Snackbar.LENGTH_LONG).show();
                }else{
                    Snackbar.make(mView,"Failed. Try again later.",Snackbar.LENGTH_LONG).show();
                }
                }catch (JSONException ex){
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(HouseActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("apicall","favourite_id");
                map.put("house_id", house.getHouse_id());
                map.put("user_email",new UserSession(HouseActivity.this).getUserEmail());
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

}
