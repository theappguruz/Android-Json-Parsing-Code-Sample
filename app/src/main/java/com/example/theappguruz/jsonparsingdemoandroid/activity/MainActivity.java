package com.example.theappguruz.jsonparsingdemoandroid.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.theappguruz.jsonparsingdemoandroid.R;
import com.example.theappguruz.jsonparsingdemoandroid.constant.Constants;
import com.example.theappguruz.jsonparsingdemoandroid.model.ContactModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private ProgressDialog progressDialog;
    android.widget.LinearLayout parentLayout;

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            if (msg.what == 1) {
                displayContactList();
            } else if (msg.what == 2) {
                Toast.makeText(getApplicationContext(), "JSON Error", Toast.LENGTH_LONG);
            } else {
                Toast.makeText(getApplicationContext(), "Null Pointer Exception", Toast.LENGTH_LONG);
            }
            return false;
        }
    });
    ArrayList<ContactModel> jsonContacts = new ArrayList<ContactModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        callContactAPI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void callContactAPI() {
        progressDialog = ProgressDialog
                .show(this, "", getString(R.string.loading));

        Log.d("JSON", "ContactAPI");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_CONTACTS, new Response.Listener<String>() {
            @Override
            public void onResponse(final String responseString) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            Log.d("JSON", "JSON");
                            if (jsonObject.has(Constants.TAG_PAYLOAD)) {
                                JSONArray contacts = jsonObject.getJSONArray(Constants.TAG_PAYLOAD);

                                for (int i = 0; i < contacts.length(); i++) {
                                    JSONObject contact = contacts.getJSONObject(i);
                                    ContactModel contactModel = new ContactModel();
                                    contactModel.setAddress(contact.getString(Constants.TAG_ADDRESS));
                                    contactModel.setFirstName(contact.getString(Constants.TAG_FIRST_NAME));
                                    contactModel.setLastName(contact.getString(Constants.TAG_LAST_NAME));
                                    contactModel.setDOB(contact.getString(Constants.TAG_DOB));

                                    contactModel.setAvatar(contact.getString(Constants.TAG_AVATAR));

                                    JSONObject phoneObject = contact.getJSONObject(Constants.TAG_PHONE);
                                    contactModel.setMobile(phoneObject.getString(Constants.TAG_PHONE_MOBILE));
                                    contactModel.setHome(phoneObject.getString(Constants.TAG_PHONE_HOME));
                                    contactModel.setOffice(phoneObject.getString(Constants.TAG_PHONE_OFFICE));

                                    jsonContacts.add(contactModel);
                                }
                            }
                            handler.sendEmptyMessage(1);
                        } catch (JSONException e) {
                            handler.sendEmptyMessage(2);
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            handler.sendEmptyMessage(3);
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
                Log.d("JSON", "getParams");
                // params.put("Contacts", "Contacts");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.TIMEOUT_IN_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void displayContactList() {
        for (int i = 0; i < jsonContacts.size(); i++) {
            final ContactModel contactModel = jsonContacts.get(i);
            Holder holder = new Holder();
            View view = LayoutInflater.from(this).inflate(R.layout.inflate_contact, null);
            final com.rey.material.widget.LinearLayout inflateParentView = (com.rey.material.widget.LinearLayout) view.findViewById(R.id.inflateParentView);
            holder.tvName = (TextView) view.findViewById(R.id.tvName);
            view.setTag(i);
            holder.tvName.setText(contactModel.getFirstName());

            inflateParentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                    intent.putExtra("ContactDetail", contactModel);
                    startActivity(intent);

                }
            });
            parentLayout.addView(view);
        }
    }

    private class Holder {
        TextView tvName;
    }
}
