package com.travel.taxi.Ride;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.travel.taxi.Adapter.CustomAutoCompleteAdapter;
import com.travel.taxi.Map.DashBoard;
import com.travel.taxi.Model.Place;
import com.travel.taxi.R;

import java.io.IOException;
import java.util.List;

public class PickLocation extends AppCompatActivity {

    private ImageView mCancelToLocation, mCancelFromLocation;
    private AutoCompleteTextView mToLocation, mFromLocation;
    private LinearLayout mPickLocation;
    private GoogleApiClient mGoogleApiClient;
    private Location to, from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addApi(Places.GEO_DATA_API).build();
        mGoogleApiClient.connect();
        mToLocation = findViewById(R.id.to);
        mFromLocation = findViewById(R.id.from);
        CustomAutoCompleteAdapter adapter = new CustomAutoCompleteAdapter(this, mGoogleApiClient);
        CustomAutoCompleteAdapter adapters = new CustomAutoCompleteAdapter(this, mGoogleApiClient);
        mToLocation.setAdapter(adapter);
        mFromLocation.setAdapter(adapters);
        mCancelToLocation = findViewById(R.id.close_to);
        mCancelFromLocation = findViewById(R.id.close_from);
        mPickLocation = findViewById(R.id.pick_location);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.getShort("type") == 0) {
                mFromLocation.setText(bundle.getSerializable("address").toString());
                mCancelFromLocation.setVisibility(View.VISIBLE);
                from = bundle.getParcelable("location");
            }

        }


        mToLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (s.toString().trim().length() != 0) {
                    mCancelToLocation.setVisibility(View.VISIBLE);
                } else {
                    mCancelToLocation.setVisibility(View.GONE);
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mFromLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().trim().length() != 0) {
                    mCancelFromLocation.setVisibility(View.VISIBLE);
                } else {
                    mCancelFromLocation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mCancelFromLocation.setOnClickListener(v -> {
            mFromLocation.setText("");
            mCancelFromLocation.setVisibility(View.GONE);
        });
        mCancelToLocation.setOnClickListener(v -> {
            mToLocation.setText("");
            mCancelToLocation.setVisibility(View.GONE);
        });


        mPickLocation.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), DashBoard.class);
            Bundle bundle = getIntent().getExtras();
            bundle.putSerializable("address", bundle.getSerializable("address"));

            bundle.putParcelable("location", from);
            bundle.putBoolean("pick", true);
            bundle.putBoolean("jaga", false);
            bundle.putSerializable("responce", getIntent().getExtras().getSerializable("responce"));
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.back).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), DashBoard.class))
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                to = (Location) data.getExtras().getSerializable("location");
                mToLocation.setText(data.getExtras().getSerializable("address").toString());
                mCancelToLocation.setVisibility(View.VISIBLE);

            }
        }
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, DashBoard.class);
        startActivity(intent);
        finish();
    }

    private void backPressed() {
        Bundle bundle = new Bundle();
        bundle.putShort("type", (short) 0);

        bundle.putParcelable("to", to);
        bundle.putParcelable("from", to);
        bundle.putSerializable("responce", getIntent().getExtras().getSerializable("responce"));
        Intent intent = new Intent(this, DashBoard.class);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void click(Place place) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.setCancelable(false);
        dialog.show();

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, place.getPlaceId())
                .setResultCallback(places -> {
                    if (places.getStatus().isSuccess()) {
                        place.setPlaceText(places.get(0).getName().toString());

                        Geocoder geocoder = new Geocoder(PickLocation.this, places.get(0).getLocale());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(places.get(0).getLatLng().latitude, places.get(0).getLatLng().longitude, 1);
                            if (addresses.get(0).getLocality() != null) {
                                place.setCity(addresses.get(0).getLocality());
                            } else if (addresses.get(0).getSubLocality() != null) {
                                place.setCity(addresses.get(0).getSubLocality());
                            } else if (addresses.get(0).getSubAdminArea() != null) {
                                place.setCity(addresses.get(0).getSubAdminArea());
                            }
//                                                    finalAutoPlace.setCity(prediction.getSecondaryText(null).toString().split(",")[0]);
                            place.setAddress(addresses.get(0).getAddressLine(0));
                            place.setLatitude(places.get(0).getLatLng().latitude);
                            place.setLongitude(places.get(0).getLatLng().longitude);
                            dialog.dismiss();
                            goToActivity(place);
                            //                                                    Log.e("tag",addresses.get(0).getAddressLine(2));
                        } catch (IOException e) {
                            dialog.dismiss();
                            Toast.makeText(PickLocation.this, " Network Error ", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                });
    }

    private void goToActivity(Place place) {
        Intent intent = new Intent(this, DashBoard.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("place", place);
        bundle.putSerializable("address", getIntent().getExtras().getSerializable("address"));
        bundle.putBoolean("pick", true);
        bundle.putParcelable("location", from);
        bundle.putSerializable("responce", getIntent().getExtras().getSerializable("responce"));
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

    }
}
