package com.wikagedung.myyusuf.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private String username;
    private String password;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();

        if(settings.contains("username") && settings.contains("password")) {
            username = settings.getString("username", null);
            password = settings.getString("password", null);
            //login_json();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

            intent.putExtra("username", LoginActivity.this.username);
            intent.putExtra("password", LoginActivity.this.password);
            startActivity(intent);
            LoginActivity.this.finish();
        }

        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.username_text);
        usernameEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(usernameEditText, InputMethodManager.SHOW_IMPLICIT);

        passwordEditText = (EditText) findViewById(R.id.password_text);

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    LoginActivity.this.login();
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void showDialogWindow(String title, String message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void login() throws Exception{

        this.username = usernameEditText.getText().toString();
        this.password = passwordEditText.getText().toString();

        if(this.username.trim().equals("") || this.password.trim().equals("")){
            showDialogWindow("Login Error", "Wrong Username or Password");
            return;
        }

        login_json();

        // New Code


//        if(editor != null) {
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//        }
//        else {
//            return;
//        }

        // End New Code
//        String baseURL = DashboardConstant.BASE_URL + "mobilelogin";
//
//        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){
//
//            @Override
//            public void onResponse(JSONObject response) {
//
//                try {
//
//                    String status = response.getString("loggedIn");
//                    if(status != null){
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//
//                        intent.putExtra("username", LoginActivity.this.username);
//                        intent.putExtra("password", LoginActivity.this.password);
//                        startActivity(intent);
//                    }else{
//                        showDialogWindow("Login Error", "Wrong Username or Password");
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//        };
//        Response.ErrorListener errorListener = new Response.ErrorListener(){
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.v("Login", "Error login status : " + error.getMessage());
//                showDialogWindow("Login Error", error.getMessage());
//            }
//
//        };
//
//        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, baseURL, listener, errorListener){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Log.v("KAT", "MyAuth: authenticating with username : " + LoginActivity.this.username + ", password : " + LoginActivity.this.password);
//                Map<String, String> map = new HashMap<String, String>();
//                String key = "Authorization";
//                String encodedString = Base64.encodeToString(
//                        String.format("%s:%s", LoginActivity.this.username, LoginActivity.this.password)
//                                .getBytes(), Base64.NO_WRAP);
//                String value = String.format("Basic %s", encodedString);
//                map.put(key, value);
//
//                return map;
//            }
//        };
//
//        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void login_json() {
        String baseURL = DashboardConstant.BASE_URL + "mobilelogin";

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                try {

                    String status = response.getString("loggedIn");
                    if(status != null){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        intent.putExtra("username", LoginActivity.this.username);
                        intent.putExtra("password", LoginActivity.this.password);
                        startActivity(intent);
                    }else{
                        showDialogWindow("Login Error", "Wrong Username or Password");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Login", "Error login status : " + error.getMessage());
                showDialogWindow("Login Error", error.getMessage());
            }

        };
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, baseURL, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.v("KAT", "MyAuth: authenticating with username : " + LoginActivity.this.username + ", password : " + LoginActivity.this.password);
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(
                        String.format("%s:%s", LoginActivity.this.username, LoginActivity.this.password)
                                .getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            this.finishAffinity();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

//    @Override
//    public View onCreateView(String name, Context context, AttributeSet attrs) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        return super.onCreateView(name, context, attrs);
//    }
}
