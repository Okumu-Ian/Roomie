package com.company.roomie.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.company.roomie.Home;
import com.company.roomie.R;
import com.company.roomie.utils.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends Fragment {

    private View mView;
    private TextInputEditText email, password;
    private MaterialButton registerButton;
    private AppCompatTextView loginText;

    public Register() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_register,container,false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();

    }

    private void initUI(){
        //reference views
        email = mView.findViewById(R.id.edt_register_email);
        password = mView.findViewById(R.id.edt_register_password);
        registerButton = mView.findViewById(R.id.btn_register);
        loginText = mView.findViewById(R.id.txt_go_to_login);

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragments();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

    }

    private void switchFragments(){
        assert getActivity()!= null;
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container,new Login());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void createUser(){
        String url = "https://icelabs-eeyan.com/roomie/authentication.php";
        final String user_email;
        final String user_password;

        if(email.getText().toString().length() < 5){
            email.setError("Please provide good email");
        }else if (password.getText().toString().length() < 6){
            password.setError("Provide an email longer than 6 characters");
        }else{
            user_email = email.getText().toString();
            user_password = password.getText().toString();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String responsed) {
                            try {
                                JSONObject response = new JSONObject(responsed);
                                try {
                                    String statusCode = response.getString("Status");
                                    if(statusCode.equals("104")){
                                        assert getActivity() != null;
                                        getActivity().startActivity(new Intent(getActivity(), Home.class));
                                        UserSession data = new UserSession(getActivity());
                                        data.createUserSession();
                                        data.setUserEmail(user_email);
                                        data.setUserPassword(user_password);
                                        getActivity().finish();
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
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("email",user_email);
                    map.put("password1",user_password);
                    map.put("key","register");
                    return map;
                }
            };
            Volley.newRequestQueue(getActivity()).add(stringRequest);

        }
    }
}
