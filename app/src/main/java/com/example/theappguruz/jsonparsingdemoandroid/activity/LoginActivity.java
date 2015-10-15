package com.example.theappguruz.jsonparsingdemoandroid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.theappguruz.jsonparsingdemoandroid.R;
import com.example.theappguruz.jsonparsingdemoandroid.constant.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {
    EditText etLoginUserName;
    EditText etLoginPassword;
    Button btnLogin;
    private ProgressDialog progressDialog;

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            if (msg.what == 1) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (msg.what == 2) {
                Toast.makeText(getApplicationContext(), "Username/Password incorrect", Toast.LENGTH_LONG).show();
            } else if (msg.what == 3) {

            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getAllWidgets();
    }

    private void getAllWidgets() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        etLoginUserName = (EditText) findViewById(R.id.etLoginUserName);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etLoginPassword.getText().toString().equals("") || etLoginUserName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Username and Password Both", Toast.LENGTH_LONG).show();
                } else {
                    onLogin();
                }
            }
        });
    }

    private void onLogin() {
        progressDialog = ProgressDialog
                .show(this, "", getString(R.string.loading));
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.get(Constants.TAG_MESSAGE).equals("Success")) {
                                handler.sendEmptyMessage(1);
                            } else {
                                handler.sendEmptyMessage(2);
                            }
                        } catch (JSONException e) {
                            handler.sendEmptyMessage(3);
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            handler.sendEmptyMessage(4);
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.TAG_USERNAME, etLoginUserName.getText().toString());
                params.put(Constants.TAG_PASSWORD, etLoginPassword.getText().toString());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.TIMEOUT_IN_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
