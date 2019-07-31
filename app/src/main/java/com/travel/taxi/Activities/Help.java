package com.travel.taxi.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.travel.taxi.ApiResponse.Help.HelpResponse;
import com.travel.taxi.ApiResponse.Login.Response;
import com.travel.taxi.Connection.Services;
import com.travel.taxi.Connection.Utils;
import com.travel.taxi.R;
import com.travel.taxi.Utils.LocalPersistence;

import retrofit2.Call;
import retrofit2.Callback;

public class Help extends AppCompatActivity {


    private Services mApi;

    private ImageView mPhone, mEmail, mExplore;
    private ProgressDialog dialog;

    private String phone, email, explore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();
        init();

        apiCall();
    }

    private void apiCall() {
        mApi = Utils.getApiService();
        final String mAccessToken = ((Response) LocalPersistence.readObjectFromFile(Help.this)).getAccessToken();

        mApi.Help("Bearer " + mAccessToken).enqueue(new Callback<HelpResponse>() {
            @Override
            public void onResponse(Call<HelpResponse> call, retrofit2.Response<HelpResponse> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    phone = response.body().getContactNumber();
                    email = response.body().getContactEmail();
                    mEmail.setOnClickListener(v -> {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("*/*");
                        emailIntent.putExtra(Intent.ACTION_SENDTO, email);
                        if (emailIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(emailIntent);
                        }
                    });
                    mPhone.setOnClickListener(v -> {
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                        phoneIntent.setData(Uri.parse("tel:" + phone));
                        if (phoneIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(phoneIntent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<HelpResponse> call, Throwable t) {
                Log.e("error", t.getLocalizedMessage());
                dialog.dismiss();
            }
        });


    }

    private void init() {

        mPhone = findViewById(R.id.phone);
        mEmail = findViewById(R.id.email);

        mExplore = findViewById(R.id.explore);


        mExplore.setOnClickListener(v -> {
            Uri webpage = Uri.parse("http://www.yaadtaxi.com/");
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
            if (webIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(webIntent);
            }
        });
    }



}
