package com.example.theappguruz.jsonparsingdemoandroid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.theappguruz.jsonparsingdemoandroid.R;
import com.example.theappguruz.jsonparsingdemoandroid.model.ContactModel;

/**
 * Created by Nikunj on 11-09-2015.
 */
public class ContactDetailActivity extends Activity {
    ContactModel contactDetail;
    TextView tvFirstName;
    TextView tvLastName;
    TextView tvAddress;

    TextView tvMobileNumber;
    TextView tvHomePhoneNumber;
    TextView tvOfficePhoneNumber;
    TextView tvDOB;

    private ProgressDialog progressDialog;
    ImageView ivContactImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail_layout);
        getAllWidgets();
        contactDetail = getContactDetail();

        setContactDetail();
    }

    private void getAllWidgets() {
        tvAddress = (TextView) findViewById(R.id.tvcontactDetailAddress);
        tvFirstName = (TextView) findViewById(R.id.tvContactDetailName);
        tvLastName = (TextView) findViewById(R.id.tvContactDetailLastName);
        tvHomePhoneNumber = (TextView) findViewById(R.id.tvcontactDetailHome);
        tvMobileNumber = (TextView) findViewById(R.id.tvcontactDetailMobile);
        tvOfficePhoneNumber = (TextView) findViewById(R.id.tvcontactDetailOffice);
        ivContactImage = (ImageView) findViewById(R.id.ivContactImage);
        tvDOB = (TextView) findViewById(R.id.tvContactDetailDOB);
    }

    private ContactModel getContactDetail() {
        ContactModel contactDetails = (ContactModel) getIntent().getSerializableExtra("ContactDetail");
        return contactDetails;
    }

    private void setContactDetail() {
        tvAddress.setText(contactDetail.getAddress());
        tvFirstName.setText(contactDetail.getFirstName());
        tvOfficePhoneNumber.setText(contactDetail.getOffice());
        tvMobileNumber.setText(contactDetail.getMobile());
        tvHomePhoneNumber.setText(contactDetail.getHome());
        tvLastName.setText(contactDetail.getLastName());
        tvDOB.setText(contactDetail.getDOB());
        Log.d("Path", contactDetail.getAvatar());

        progressDialog = ProgressDialog
                .show(this, "", getString(R.string.loading));
      /*  Picasso.with(this).load(contactDetail.getAvatar()).resize(200, 200).into(ivContactImage, new Callback() {
            @Override
            public void onSuccess() {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onError() {

            }
        });*/

        // Retrieves an image specified by the URL, displays it in the UI.
        ImageRequest request = new ImageRequest(contactDetail.getAvatar(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        ivContactImage.setImageBitmap(bitmap);
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
//                        ivContactImage.setImageResource(R.drawable.image_load_error);
                    }
                });
// Access the RequestQueue through your singleton class.
        Volley.newRequestQueue(this).add(request);
//        MySingleton.getInstance(this).addToRequestQueue(request);

    }
}
