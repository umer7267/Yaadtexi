package com.travel.taxi.Map;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.travel.taxi.Activities.Coupon;
import com.travel.taxi.Activities.Help;
import com.travel.taxi.Activities.Payments;
import com.travel.taxi.Activities.Trips;
import com.travel.taxi.Activities.UpdateProfile;
import com.travel.taxi.Activities.Wallet;
import com.travel.taxi.Adapter.TaxiSlection;
import com.travel.taxi.ApiResponse.Estimated.EstimatedPrice;
import com.travel.taxi.ApiResponse.Login.Response;
import com.travel.taxi.ApiResponse.Provider.Providers;
import com.travel.taxi.ApiResponse.Rid.CancelRide;
import com.travel.taxi.ApiResponse.Rid.RideRequest;
import com.travel.taxi.ApiResponse.Rid.UpdateLocation;
import com.travel.taxi.ApiResponse.Service.Service;
import com.travel.taxi.Connection.Services;
import com.travel.taxi.Connection.Utils;
import com.travel.taxi.Helper.DrawingHelper;
import com.travel.taxi.Model.Place;
import com.travel.taxi.Model.Taxi;
import com.travel.taxi.R;
import com.travel.taxi.Ride.PickLocation;
import com.travel.taxi.Signin;
import com.travel.taxi.Utils.LocalPersistence;
import com.travel.taxi.Utils.NetworkUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.location.LocationManager.GPS_PROVIDER;

public class DashBoard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private SupportMapFragment mMap;
    private GoogleMap map;
    private Location FromLocation;


    private ImageView mCurrentLocation;
    private ImageView mCenter;

    private TextView mWhereToGo;
    private Location mCurrentLocationLongitudeLatitutde, mToLocation, mFromLocation;

    private boolean state;
    private boolean screenMain;
    private boolean visibility;
    boolean gps;

    private boolean bottomOn = false;

    private LinearLayout mPickLocation;
    private EditText mToLocations, mFromLocations;

    private Location From;
    private Place To;
    private View include;
    private View include1;
    Bundle savedInstanceState;
    private Services mApi;
    DrawerLayout drawer;
    NavigationView navigationView;
    View detailsLayout;
    public Service slected;
    private CountDownTimer timer;
    public static CircleImageView profileImage;
    public static TextView nameView;
    private TaxiSlection adapter;
    private RecyclerView taxiServicesList;
    private boolean handle = false;
    private AtomicBoolean up;


    //for date and time
    int year, dayOfMonth;
    String monthOfYear;

    int hourOfDay, minute;
    private EstimatedPrice fare;


    boolean check;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_dash_board);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View v = navigationView.getHeaderView(0);
        String image = ((Response) LocalPersistence.readObjectFromFile(DashBoard.this)).getUser().getPicture();
        profileImage = v.findViewById(R.id.profileimageView);
        nameView = v.findViewById(R.id.name);
        nameView.setText(((Response) LocalPersistence.readObjectFromFile(DashBoard.this)).getUser().getFirstName() + " " + ((Response) LocalPersistence.readObjectFromFile(DashBoard.this)).getUser().getLastName());

        Picasso.get()
                .load("http://yaadtaxi.com/userprofilepics/" + image)
                .placeholder(R.drawable.ic_dummy_user)
                .into(profileImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoard.this, UpdateProfile.class));
            }
        });

        timer = new CountDownTimer(4 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                changeMarker();
            }

            @Override
            public void onFinish() {
                UpdateLocation();
            }
        };
        statusCheck();
        mApi = Utils.getApiService();
        mMap = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //to get the state of the screen where a pin selection screen or main screen
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("pick")) {
                screenMain = false;
                mMap.onCreate(savedInstanceState);
                visibility(screenMain);
                setMainForPickLocation();
                Log.e("error", "does not occurr");
                From = getIntent().getExtras().getParcelable("location");
                To = (Place) getIntent().getExtras().getSerializable("place");
            } else {
                Log.e("error", "occurr");

                screenMain = true;
                mMap.onCreate(savedInstanceState);
                visibility(screenMain);
                setMainMapWithDrawer();
                if (mCurrentLocationLongitudeLatitutde != null)
                    timer.start();

            }
        } else {


            screenMain = true;
            mMap.onCreate(savedInstanceState);
            visibility(screenMain);
            setMainMapWithDrawer();
            if (mCurrentLocationLongitudeLatitutde != null)
                timer.start();

        }


    }




    private void UpdateLocation() {
        Log.e("where", "in  UpdateLocation");
        changeMarker();
        callUpdateLocation();
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    private void callUpdateLocation() {
        Log.e("where", "in  callUpdateLocation");

        String Token = ((Response) LocalPersistence.readObjectFromFile(DashBoard.this)).getAccessToken();

        mApi.UpdateLocation("Bearer " + Token, mCurrentLocationLongitudeLatitutde.getLatitude(), mCurrentLocationLongitudeLatitutde.getLongitude())
                .enqueue(new Callback<UpdateLocation>() {
                    @Override
                    public void onResponse(Call<UpdateLocation> call, retrofit2.Response<UpdateLocation> response) {
                        if (response.isSuccessful()) {
                            Log.e("response", response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdateLocation> call, Throwable t) {

                    }
                });
    }


    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void endingDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("This Application Can Not Work Without Location Permission")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int id) {

//                            mCurrentLocationLongitudeLatitutde = getcurrentLocation();

                        dialog.cancel();
                        onResume();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    private FusedLocationProviderClient fusedLocationClient;

    private void buildAlertMessageNoGps() {
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        // getting GPS status
        boolean isGPSEnabled = locationManager.isProviderEnabled(GPS_PROVIDER);
        Log.e("", "" + isGPSEnabled);
        // getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled) {
            gps = true;
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

//                            mCurrentLocationLongitudeLatitutde = getcurrentLocation();
                            getcurrentLocation();
                            dialog.cancel();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                            Toast.makeText(DashBoard.this, "Application Can not operate without location service", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }


    public void setMainForPickLocation() {
        mCenter = findViewById(R.id.center);

        mMap.getMapAsync(this);
        mCurrentLocation = findViewById(R.id.current_location);

        mCurrentLocation.setOnClickListener(v -> {

//            mCurrentLocationLongitudeLatitutde = getcurrentLocation();
            getcurrentLocation();
            mCurrentLocation.setVisibility(View.GONE);

        });

        TextView mDone = findViewById(R.id.done);
        mDone.setOnClickListener(v ->
        {


            getAddress();

            visibility();
            map.setOnMapClickListener(latLng -> {
                onmapClicked();
                map.setOnMapClickListener(null);
            });


        });
    }

    /**
     * this will change the appearence of dashboard to picklocation
     *
     * @param screen
     */
    public void visibility(boolean screen) {

        changeAppearence(screen);

    }

    public void changeAppearence(boolean screen) {
        if (!screen) {
            findViewById(R.id.main_relative_layout).setVisibility(View.GONE);
            findViewById(R.id.seconrelative).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.main_relative_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.seconrelative).setVisibility(View.GONE);
        }
    }

    private void changeMarker() {
        Log.e("where", "in  changeMarker");


//
//        LocationManager locationManager = (LocationManager)
//                getSystemService(Context.LOCATION_SERVICE);
////
////        Criteria criteria = new Criteria();
////        map.clear();
////        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////            // TODO: Consider calling
////            //    Activity#requestPermissions
////            // here to request the missing permissions, and then overriding
////            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
////            //                                          int[] grantResults)
////            // to handle the case where the user grants the permission. See the documentation
////            // for Activity#requestPermissions for more details.
////            return null;
////        }
////        Log.d("GPS Enabled", "GPS Enabled");
////        if (locationManager != null)
////        {
////           Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
////            if (location != null)
////            {
////               double latitude = location.getLatitude();
////                double longitude = location.getLongitude();
////                LatLng loc = new LatLng(latitude, longitude);
////                CameraPosition.Builder builder = new CameraPosition.Builder();
////                builder.zoom(16);
////                builder.target(loc);
////                map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
////                // Creating a marker
////                MarkerOptions markerOptions = new MarkerOptions();
////
////                // Setting the position for the marker
////                markerOptions.position(loc);
////                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
////                map.addMarker(markerOptions);
////                return location;
////            }
////        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location currentlocation) {
                        // Got last known location. In some rare situations this can be null.
                        if (currentlocation != null) {
                            double latitude = currentlocation.getLatitude();
                            double longitude = currentlocation.getLongitude();
                            LatLng loc = new LatLng(latitude, longitude);

                            mCurrentLocationLongitudeLatitutde = currentlocation;
//         Creating a marker
                            if (mCurrentLocationLongitudeLatitutde.getLongitude() == currentlocation.getLongitude() &&
                                    mCurrentLocationLongitudeLatitutde.getLatitude() == currentlocation.getLatitude()) {
                                Log.e("location", "is  same");

                            } else {
                                if (map != null)
                                    map.clear();
                                mCurrentLocationLongitudeLatitutde = currentlocation;
                                MarkerOptions markerOptions = new MarkerOptions();

                                // Setting the position for the marker
                                markerOptions.position(loc);
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));

                                map.addMarker(markerOptions);
                            }
                        }
                    }


                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("error", e.getLocalizedMessage());
            }
        });

    }

    private Integer no_of_passenger_int, no_of_bag_int;


    private void returnVisibilty() {
        mPickLocation.setVisibility(View.VISIBLE);
        include.setVisibility(View.VISIBLE);
//        include.startAnimation(slideUp);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                include.getHeight() + 250,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        include.startAnimation(animate);
        detailsLayout.setVisibility(View.GONE);

        handle = false;
    }

    /**
     * this method will generate the necessary bottom recyclerview
     */
    private void inflatebottomView() {
        include = findViewById(R.id.downselection);

        bottomOn = false;
        include.setVisibility(View.VISIBLE);
//        include.startAnimation(slideUp);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                include.getHeight() + 250,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        include.startAnimation(animate);
        taxiServicesList = findViewById(R.id.bottom_car_layout);
        mApi = Utils.getApiService();
        final List<Service>[] data = new List[]{new ArrayList<>()};

        adapter = new TaxiSlection(data[0], this);
        taxiServicesList.setAdapter(adapter);
        taxiServicesList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        getServicesData(adapter);

        ArrayList<Taxi> seats = new ArrayList<>();


    }

    public void handleSecondClickOnTheServiceItem(Service item) {

        handle = true;

        mPickLocation.setVisibility(View.GONE);


        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        include.setVisibility(View.GONE);
        include.startAnimation(slideDown);

        detailsLayout = findViewById(R.id.service_details);
        detailsLayout.setVisibility(View.VISIBLE);
        TextView serviceName = detailsLayout.findViewById(R.id.service_detail);

        TextView baseFare = detailsLayout.findViewById(R.id.basic_fare);
        TextView farepkm = detailsLayout.findViewById(R.id.farepkm);
        TextView fare_type = detailsLayout.findViewById(R.id.fare_type);
        TextView bags = detailsLayout.findViewById(R.id.bags);
        TextView capacity = detailsLayout.findViewById(R.id.capacity);
        TextView done = detailsLayout.findViewById(R.id.done);
        ImageView car = detailsLayout.findViewById(R.id.car);


        Picasso.get()
                .load(item.getImage())
                .placeholder(R.drawable.car)
                .into(car);
        done.setOnClickListener(v -> {
            returnVisibilty();
        });

        baseFare.setText(String.valueOf(item.getFixed()));
        farepkm.setText(String.valueOf(item.getPrice()));
        fare_type.setText(String.valueOf(item.getCalculator()));
        bags.setText(String.valueOf(item.getBags()));
        capacity.setText(String.valueOf(item.getCapacity()));
        serviceName.setText(String.valueOf(item.getName()));


    }


    /**
     * this will inflate the data in the cars list
     */
    private void getServicesData(TaxiSlection adapter) {

        final String mAccessToken = ((Response) LocalPersistence.readObjectFromFile(DashBoard.this)).getAccessToken();
        mApi.Services("Bearer " + mAccessToken).enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, retrofit2.Response<List<Service>> response) {
                List<Service> jsonString = response.body();
                adapter.update(jsonString);
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                Log.e("onResponsefailure", t.getLocalizedMessage());

            }
        });

    }

    /**
     * to change the visibilty of the code while a user presses the done on the pick location screen done button
     */

    private void visibility() {
        mPickLocation.setVisibility(View.VISIBLE);
//            overridePendingTransition(R.anim.slide_out_top, R.anim.slide_out_bottom);
        //animation that will be making the thing look like floating
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
        inflatebottomView();


//        include.animate().translationY(include.getHeight()-360);
        TextView mDonebottom = include.findViewById(R.id.done);
        mDonebottom.setOnClickListener(view -> {
            bottomOn = true;
            Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
            include.setVisibility(View.GONE);
            include.startAnimation(slideDown);

            inflateEsimateedView();


//
//            mPickLocation.animate()
//                    .translationY(-mPickLocation.getHeight())
//                    .alpha(0.0f)
//                    .setDuration(500)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                            mPickLocation.setVisibility(View.GONE);
//                        }
//                    });
            up = new AtomicBoolean(true);
            DrawingHelper helper = new DrawingHelper(map, this);
            String url = helper.getDirectionsUrl(new LatLng(From.getLatitude(), From.getLongitude()),
                    new LatLng(To.getLatitude(), To.getLongitude()));
            helper.run(url, 150);
            map.setOnMapClickListener(latLng -> {
                if (up.get()) {
                    deflateEstimatedView();
                    up.set(false);

                } else {
                    up.set(true);

                    inflateEsimateedViewWithData();
                }
//                onmapClicked();
//                map.setOnMapClickListener(null);
            });

        });
    }

    private void inflateEsimateedViewWithData() {
        Log.e("inflateE", "inflateEsimateedViewWithData: ");
        include1.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                include1.getHeight() + 250,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        include1.startAnimation(animate);


    }

    public void deflateEstimatedView() {

        Log.e("deflateEstimatedView", "deflateEstimatedView: ");
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        include1.setVisibility(View.GONE);
        include1.startAnimation(slideDown);


    }

    private void inflateEsimateedView() {

        if (slected != null) {
            ProgressDialog dialog = StartOnScreen(this);
            dialog.setMessage("Loading..");
            dialog.setCancelable(false);
            dialog.show();
            final String mAccessToken = ((Response) LocalPersistence.readObjectFromFile(DashBoard.this)).getAccessToken();
            mApi.EstimatedPrices("Bearer " + mAccessToken,
                    From.getLatitude(),
                    From.getLongitude(),
                    To.getLatitude(),
                    To.getLongitude(),
                    slected.getId()).enqueue(new Callback<EstimatedPrice>() {

                @Override
                public void onResponse(Call<EstimatedPrice> call, retrofit2.Response<EstimatedPrice> response) {
                    if (response.isSuccessful()) {
                        dialog.dismiss();
                        include1 = findViewById(R.id.second);
                        EstimatedPrice fare = response.body();
                        include1.setVisibility(View.VISIBLE);
                        TranslateAnimation animate = new TranslateAnimation(
                                0,                 // fromXDelta
                                0,                 // toXDelta
                                include1.getHeight() + 250,  // fromYDelta
                                0);                // toYDelta
                        animate.setDuration(500);
                        animate.setFillAfter(true);
                        include1.startAnimation(animate);

                        ((TextView) include1.findViewById(R.id.price)).setText(String.valueOf(fare.getEstimatedFare()));
                        ((TextView) include1.findViewById(R.id.eta)).setText(String.valueOf(fare.getTime()));
                        TextView mRideButton = include1.findViewById(R.id.ride);//ride
                        TextView mSchedule = include1.findViewById(R.id.schedule);//schedule


                        mSchedule.setOnClickListener(v -> {
                            showDialogAlert(false, fare);
                        });


                        mRideButton.setOnClickListener(ride -> {
                            showDialogAlert(true, fare);


                        });
                    }
                }

                @Override
                public void onFailure(Call<EstimatedPrice> call, Throwable t) {
                    dialog.dismiss();
                    Log.e("responseerror", t.getLocalizedMessage());
                }
            });


        } else {
            Toast.makeText(this, "Please Select The Car", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogAlert(Boolean flag, EstimatedPrice fare) {
        this.fare = fare;
        View alertLayout = LayoutInflater.from(DashBoard.this).inflate(R.layout.no_of_passenger_withholdings_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
        builder.setView(alertLayout);
        TextView done = alertLayout.findViewById(R.id.submit);
        EditText no_of_passenger = alertLayout.findViewById(R.id.no_of_passenger);
        EditText no_of_bags = alertLayout.findViewById(R.id.no_of_bag);

        AlertDialog dialog = builder.create();
        dialog.show();
        done.setOnClickListener(submit -> {

            if (!TextUtils.isEmpty(no_of_passenger.getText()) && !TextUtils.isEmpty(no_of_bags.getText())) {
                dialog.dismiss();
                if (flag)
                    requestRide(no_of_bags.getText().toString(), no_of_passenger.getText().toString(), fare.getDistance());
                else {
                    no_of_bag_int = Integer.valueOf(no_of_bags.getText().toString());
                    no_of_passenger_int = Integer.valueOf(no_of_passenger.getText().toString());
                    showDateTimePicker();

                }
            }
            if (!TextUtils.isEmpty(no_of_passenger.getText())) {
                no_of_passenger.setError("please fill this");
            }
            if (!TextUtils.isEmpty(no_of_bags.getText())) {
                no_of_bags.setError("please fill this");
            }
        });

    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                DashBoard.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
// If you're calling this from a support Fragment
//        dpd.show(getFragmentManager(), "Datepickerdialog");
// If you're calling this from an AppCompatActivity
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog dpd = TimePickerDialog.newInstance(
                DashBoard.this,
                now.getTime().getHours(),
                now.getTime().getMinutes(),
                true
        );
// If you're calling this from a support Fragment
//        dpd.show(getFragmentManager(), "Datepickerdialog");
// If you're calling this from an AppCompatActivity
        dpd.show(getSupportFragmentManager(), "TimePickerDialog");
        dpd.setVersion(TimePickerDialog.Version.VERSION_2);
    }

    @NotNull
    private ProgressDialog StartOnScreen(DashBoard dashBoard) {
        return new ProgressDialog(dashBoard);
    }

    private void requestRide(String noofbags, String no_of_passenger, Integer fare) {
        ProgressDialog dialog = StartOnScreen(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.show();
        final String mAccessToken = ((Response) LocalPersistence.readObjectFromFile(DashBoard.this)).getAccessToken();
        mApi.RequestRide("Bearer " + mAccessToken,
                From.getLatitude(),
                From.getLongitude(),
                To.getLatitude(),
                To.getLongitude(),
                slected.getId(),
                Double.valueOf(fare),
                "CASH",
                Integer.valueOf(noofbags),
                Integer.valueOf(no_of_passenger)).enqueue(new Callback<RideRequest>() {
            @Override
            public void onResponse(Call<RideRequest> call, retrofit2.Response<RideRequest> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    RideRequest request = response.body();
                    if (request.getMessage().equals("No Drivers Found")) {
                        Toast.makeText(DashBoard.this, "No Ride Available", Toast.LENGTH_SHORT).show();
                        backToMainScreen();
                    } else {
                        inflateDriveriew(request);
                    }
                } else {
                    if (response.code() == 500) {
                        dialog.dismiss();
                        Toast.makeText(DashBoard.this, "internal server error", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.e("code", response.code() + "");
            }


            @Override
            public void onFailure(Call<RideRequest> call, Throwable t) {
                dialog.dismiss();
                Log.e("responce failure", t.getLocalizedMessage());
            }
        });

    }


    private void requestRide(String noofbags, String no_of_passenger, Integer fare, String schedule_date, String schedule_time) {
        ProgressDialog dialog = StartOnScreen(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.show();
        final String mAccessToken = ((Response) LocalPersistence.readObjectFromFile(DashBoard.this)).getAccessToken();
        mApi.RequestRide("Bearer " + mAccessToken,
                From.getLatitude(),
                From.getLongitude(),
                To.getLatitude(),
                To.getLongitude(),
                slected.getId(),
                Double.valueOf(fare),
                "CASH",
                Integer.valueOf(noofbags),
                Integer.valueOf(no_of_passenger),
                schedule_date,
                schedule_time).enqueue(new Callback<RideRequest>() {
            @Override
            public void onResponse(Call<RideRequest> call, retrofit2.Response<RideRequest> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    RideRequest request = response.body();
                    if (request.getMessage().equals("No Drivers Found")) {
                        Toast.makeText(DashBoard.this, "No Ride Available", Toast.LENGTH_SHORT).show();
                        backToMainScreen();
                    } else {
                        Toast.makeText(DashBoard.this, "Ride Successfully registered", Toast.LENGTH_SHORT).show();
                        backToMainScreen();
                    }
                } else {
                    if (response.code() == 500) {
                        dialog.dismiss();
                        Toast.makeText(DashBoard.this, "internal server error", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.e("code", response.code() + "");
            }


            @Override
            public void onFailure(Call<RideRequest> call, Throwable t) {
                dialog.dismiss();
                Log.e("responce failure", t.getLocalizedMessage());
            }
        });

    }


    /**
     * this function will take the current screen
     */
    private void backToMainScreen() {
        getIntent().removeExtra("pick");
        onResume();
    }

    public void inflateDriveriew(RideRequest request) {

        ProgressDialog dialog = StartOnScreen(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);

        dialog.show();
        final String mAccessToken = ((Response) LocalPersistence.readObjectFromFile(DashBoard.this)).getAccessToken();
        mApi.GetProviders("Bearer " + mAccessToken,
                From.getLatitude(),
                From.getLongitude(),
                null).enqueue(new Callback<List<Providers>>() {
            @Override
            public void onResponse(Call<List<Providers>> call, retrofit2.Response<List<Providers>> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    makeAnimationDriver(response.body(), request);

                    Log.e("respinse", response.body().toString());


                }
            }

            @Override
            public void onFailure(Call<List<Providers>> call, Throwable t) {
                dialog.dismiss();
            }
        });


    }

    private void makeAnimationDriver(List<Providers> body, RideRequest request) {
        include1 = findViewById(R.id.third);
        mPickLocation.animate()
                .translationY(-mPickLocation.getHeight())
                .alpha(0.0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mPickLocation.setVisibility(View.GONE);
                    }
                });
        include1.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                include1.getHeight() + 250,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        include1.startAnimation(animate);

        Providers providers = null;
        for (Providers provider : body) {
            if (provider.getId().equals(request.getCurrentProvider())) {
                providers = provider;
            }
        }
        if (providers != null) {
            if (providers.getAvatar() != null)
                Picasso.get()
                        .load(providers.getAvatar())
                        .placeholder(R.drawable.ic_dummy_user)
                        .into((ImageView) include1.findViewById(R.id.provider_image));
            ((TextView) include1.findViewById(R.id.driver_name)).setText(providers.getFirstName());
            ((TextView) include1.findViewById(R.id.car_name)).setText(providers.getFirstName());


            ((RatingBar) include1.findViewById(R.id.rating)).setRating(Float.parseFloat(providers.getRating()));

            TextView mCallToDriver = include1.findViewById(R.id.call);

            String phone = providers.getMobile();

            mCallToDriver.setOnClickListener(v -> {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + phone));
                if (phoneIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(phoneIntent);
                }
            });

        }

        TextView mCancelRequest = include1.findViewById(R.id.done);

        mCancelRequest.setOnClickListener(v -> CancelRequest(request.getRequestId()));


    }

    private void CancelRequest(Integer requestId) {
        Log.e("request", requestId + "");
        View alertLayout = LayoutInflater.from(DashBoard.this).inflate(R.layout.cancel_request, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
        builder.setView(alertLayout);
        TextView done = alertLayout.findViewById(R.id.submit);
        EditText reason_to_cancel = alertLayout.findViewById(R.id.reason);

        AlertDialog dialog = builder.create();
        dialog.show();
        done.setOnClickListener(submit -> {

            if (TextUtils.isEmpty(reason_to_cancel.getText())) {
                dialog.dismiss();
                callCacelRideApi(requestId, null);
            } else {
                callCacelRideApi(requestId, reason_to_cancel.getText().toString());
                dialog.dismiss();
            }

        });

    }

    private void callCacelRideApi(Integer requestId, String reason) {
        ProgressDialog dialog = StartOnScreen(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.show();
        final String mAccessToken = ((Response) LocalPersistence.readObjectFromFile(DashBoard.this)).getAccessToken();
        mApi.CancelRide("Bearer " + mAccessToken,
                Double.valueOf(requestId),
                reason).enqueue(new Callback<CancelRide>() {
            @Override
            public void onResponse(Call<CancelRide> call, retrofit2.Response<CancelRide> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    Log.e("reaponse", response.body().getMessage());
                    backToMainScreen();
                    map.setOnMapClickListener(null);
                }

            }

            @Override
            public void onFailure(Call<CancelRide> call, Throwable t) {
                dialog.dismiss();
            }
        });


    }


    public void onmapClicked() {
        bottomOn = false;
        include.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                -include.getHeight() + 250,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);

        include.startAnimation(slideDown);
        if (include1 != null) {
            include1.setVisibility(View.GONE);
            include1.startAnimation(slideDown);
        }
        mPickLocation.animate()
                .translationY(-mPickLocation.getHeight())
                .alpha(0.0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mPickLocation.setVisibility(View.GONE);
                    }
                });
        visibility(true);
        setMainMapWithDrawer();

    }


    private void getAddress(Place place) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(mCurrentLocationLongitudeLatitutde.getLatitude(),
                    mCurrentLocationLongitudeLatitutde.getLongitude(),
                    1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        findViewById(R.id.card).setVisibility(View.GONE);
        mCenter.setVisibility(View.GONE);

        com.travel.taxi.Model.Address addresss = new com.travel.taxi.Model.Address(address, city, state, country, postalCode, knownName);
        mToLocations = findViewById(R.id.to);
        mFromLocations = findViewById(R.id.from);
        mPickLocation = findViewById(R.id.to_from);

        mFromLocations.setText(getIntent().getExtras().getSerializable("address").toString());
        FromLocation = getIntent().getExtras().getParcelable("location");
        findViewById(R.id.back).setOnClickListener(view -> onBackPressed());
        mToLocations.setText(addresss.toString());
    }


    private void getAddress() {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(mCurrentLocationLongitudeLatitutde.getLatitude(), mCurrentLocationLongitudeLatitutde.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        findViewById(R.id.card).setVisibility(View.GONE);
        mCenter.setVisibility(View.GONE);

        To = new Place();
        To.setLongitude(mCurrentLocationLongitudeLatitutde.getLongitude());
        To.setLatitude(mCurrentLocationLongitudeLatitutde.getLatitude());

        com.travel.taxi.Model.Address addresss = new com.travel.taxi.Model.Address(address, city, state, country, postalCode, knownName);
        mToLocations = findViewById(R.id.to);
        mFromLocations = findViewById(R.id.from);
        mPickLocation = findViewById(R.id.to_from);

        mFromLocations.setText(getIntent().getExtras().getSerializable("address").toString());
        FromLocation = getIntent().getExtras().getParcelable("location");
        findViewById(R.id.back).setOnClickListener(view -> onBackPressed());
        mToLocations.setText(addresss.toString());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                endingDialog();
                return;
            }
        }
        googleMap.clear();
        map = googleMap;
        //to change the map appearence according to the desired screen
        if (screenMain) {
            getcurrentLocation();
            onMapReadyMain(
                    googleMap
            );
        } else {
            onMapReadyPickLocation(
                    googleMap
            );
        }

    }

    /**
     * this will initiate the method required for the map on the main screen
     */

    public void setMainMapWithDrawer() {


        mMap.getMapAsync(this);


        mCurrentLocation = findViewById(R.id.current_location);

        getcurrentLocation();
        mCurrentLocation.setOnClickListener(v -> {
            buildAlertMessageNoGps();


//            mCurrentLocationLongitudeLatitutde = getcurrentLocation();
            getcurrentLocation();
            mCurrentLocation.setVisibility(View.GONE);
        });

        mWhereToGo = findViewById(R.id.where_to_go);
        mWhereToGo.setOnClickListener(v -> {
            getcurrentLocation();

            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                if (mCurrentLocationLongitudeLatitutde != null) {
                    addresses = geocoder.getFromLocation(mCurrentLocationLongitudeLatitutde.getLatitude(), mCurrentLocationLongitudeLatitutde.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                }
            } catch (IOException e) {
                Log.e("error", e.getLocalizedMessage());

            }
            if (addresses != null) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                com.travel.taxi.Model.Address addresss = new com.travel.taxi.Model.Address(address, city, state, country, postalCode, knownName);
                Intent intent = new Intent(getApplicationContext(), PickLocation.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("address", addresss);
                bundle.putShort("type", (short) 0);
                bundle.putParcelable("location", mCurrentLocationLongitudeLatitutde);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "No Network coverage ", Toast.LENGTH_SHORT).show();
            }

        });


        findViewById(R.id.drawer).setOnClickListener(v ->
        {

            drawer.openDrawer(Gravity.LEFT);


        });

        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * this will set the map accordingly to the PickLocation Screen requirements
     *
     * @param googleMap
     */
    public void onMapReadyPickLocation(GoogleMap googleMap) {
        map = googleMap;
        try {

            boolean success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyles));

        } catch (Resources.NotFoundException e) {
            Log.e("error", e.getLocalizedMessage());
        }
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            Place place = (Place) bundle.getSerializable("place");
            if (place != null) {

                double latitude = place.getLatitude();
                double longitude = place.getLongitude();
                LatLng loc = new LatLng(latitude, longitude);
                CameraPosition.Builder builder = new CameraPosition.Builder();
                builder.zoom(16);
                builder.target(loc);
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
                mCurrentLocationLongitudeLatitutde = new Location(GPS_PROVIDER);
                mCurrentLocationLongitudeLatitutde.setLongitude(place.getLongitude());
                mCurrentLocationLongitudeLatitutde.setLatitude(place.getLatitude());
                placeMarker();
            } else
//                mCurrentLocationLongitudeLatitutde = getcurrentLocation();
                getcurrentLocation();
        } else {


        }

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();

        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            }
        }

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (i == REASON_GESTURE) {

                    mCurrentLocation.setVisibility(View.VISIBLE);
                }
            }
        });
        map.setMyLocationEnabled(false);

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
//                mCenter.setVisibility(View.GONE);
//                MarkerOptions markerOptions = new MarkerOptions().position(map.getCameraPosition().target)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
//                map.addMarker(markerOptions);
                if (getIntent().getExtras() != null) {
                    Bundle bundle = getIntent().getExtras();
                    Place place = (Place) bundle.getSerializable("place");
                    if (place == null) {
                        Location centerLocation = new Location("");
                        centerLocation.setLatitude(map.getCameraPosition().target.latitude);
                        centerLocation.setLongitude(map.getCameraPosition().target.longitude);
                        mCurrentLocationLongitudeLatitutde = centerLocation;
                    }
                }
            }
        });


    }

    /**
     * this will set the map accordingly to the Main Screen requirements
     *
     * @param googleMap
     */
    public void onMapReadyMain(GoogleMap googleMap) {

        map = googleMap;
        map.clear();
        getcurrentLocation();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            }
        }

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (i == REASON_GESTURE) {

                    mCurrentLocation.setVisibility(View.VISIBLE);
                }
            }
        });
        map.setMyLocationEnabled(false);


        try {
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyles));

//            mCurrentLocationLongitudeLatitutde = getcurrentLocation();
            getcurrentLocation();
            state = !state;

            if (!success) {
                Toast.makeText(this, "no styles", Toast.LENGTH_SHORT).show();
            }
        } catch (Resources.NotFoundException e) {
            Log.e("error", e.getLocalizedMessage());
        }
    }

    private void placeMarker() {

        fromLocation();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                Bundle bundle = data.getExtras();
                mFromLocation = (Location) bundle.getSerializable("from");
                state = false;
                mToLocation = (Location) bundle.getSerializable("to");
                if (mToLocation != null) {
                    setToCamera(mToLocation);
                } else {
                    Toast.makeText(this, "No Destination Selected", Toast.LENGTH_SHORT).show();
//                    mCurrentLocationLongitudeLatitutde = getcurrentLocation();
                    getcurrentLocation();
                }

            }
        }
    }


    private Location getcurrentLocation() {
//        buildAlertMessageNoGps();

        if (map != null)
            map.clear();
        check = true;
//
//        LocationManager locationManager = (LocationManager)
//                getSystemService(Context.LOCATION_SERVICE);
////
////        Criteria criteria = new Criteria();
////        map.clear();
////        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////            // TODO: Consider calling
////            //    Activity#requestPermissions
////            // here to request the missing permissions, and then overriding
////            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
////            //                                          int[] grantResults)
////            // to handle the case where the user grants the permission. See the documentation
////            // for Activity#requestPermissions for more details.
////            return null;
////        }
////        Log.d("GPS Enabled", "GPS Enabled");
////        if (locationManager != null)
////        {
////           Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
////            if (location != null)
////            {
////               double latitude = location.getLatitude();
////                double longitude = location.getLongitude();
////                LatLng loc = new LatLng(latitude, longitude);
////                CameraPosition.Builder builder = new CameraPosition.Builder();
////                builder.zoom(16);
////                builder.target(loc);
////                map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
////                // Creating a marker
////                MarkerOptions markerOptions = new MarkerOptions();
////
////                // Setting the position for the marker
////                markerOptions.position(loc);
////                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
////                map.addMarker(markerOptions);
////                return location;
////            }
////        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return null;
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location currentlocation) {
                        // Got last known location. In some rare situations this can be null.
                        if (currentlocation != null) {
                            double latitude = currentlocation.getLatitude();
                            double longitude = currentlocation.getLongitude();
                            LatLng loc = new LatLng(latitude, longitude);
                            CameraPosition.Builder builder = new CameraPosition.Builder();
                            builder.zoom(16);
                            builder.target(loc);
                            mCurrentLocationLongitudeLatitutde = currentlocation;
                            map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
//         Creating a marker
                            timer.start();
                            MarkerOptions markerOptions = new MarkerOptions();

                            // Setting the position for the marker
                            markerOptions.position(loc);
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
                            map.addMarker(markerOptions);
                        } else {
                            locationRequest = LocationRequest.create();
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            locationRequest.setInterval(20 * 1000);
                            locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    if (locationResult == null) {
                                        return;
                                    }
                                    for (Location currentlocation : locationResult.getLocations()) {
                                        if (currentlocation != null) {
                                            if (check) {
                                                check = false;
                                                double latitude = currentlocation.getLatitude();
                                                double longitude = currentlocation.getLongitude();
                                                LatLng loc = new LatLng(latitude, longitude);
                                                CameraPosition.Builder builder = new CameraPosition.Builder();
                                                builder.zoom(16);
                                                builder.target(loc);
                                                mCurrentLocationLongitudeLatitutde = currentlocation;
                                                map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
//         Creating a marker
                                                timer.start();
                                                MarkerOptions markerOptions = new MarkerOptions();

                                                // Setting the position for the marker
                                                markerOptions.position(loc);
                                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
                                                map.addMarker(markerOptions);
                                                break;
                                            }
                                        }
                                    }
                                }

                                ;
                            };
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    Activity#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for Activity#requestPermissions for more details.
                                    return;
                                }
                            }
                            fusedLocationClient.requestLocationUpdates(locationRequest,
                                    locationCallback,
                                    null);
                        }
                    }


                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("error", e.getLocalizedMessage());
            }
        });
        return null;
    }

    private void fromLocation() {
        map.clear();
//
//        LocationManager locationManager = (LocationManager)
//                getSystemService(Context.LOCATION_SERVICE);
////
////        Criteria criteria = new Criteria();
////        map.clear();
////        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////            // TODO: Consider calling
////            //    Activity#requestPermissions
////            // here to request the missing permissions, and then overriding
////            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
////            //                                          int[] grantResults)
////            // to handle the case where the user grants the permission. See the documentation
////            // for Activity#requestPermissions for more details.
////            return null;
////        }
////        Log.d("GPS Enabled", "GPS Enabled");
////        if (locationManager != null)
////        {
////           Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
////            if (location != null)
////            {
////               double latitude = location.getLatitude();
////                double longitude = location.getLongitude();
////                LatLng loc = new LatLng(latitude, longitude);
////                CameraPosition.Builder builder = new CameraPosition.Builder();
////                builder.zoom(16);
////                builder.target(loc);
////                map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
////                // Creating a marker
////                MarkerOptions markerOptions = new MarkerOptions();
////
////                // Setting the position for the marker
////                markerOptions.position(loc);
////                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
////                map.addMarker(markerOptions);
////                return location;
////            }
////        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location currentlocation) {
                        // Got last known location. In some rare situations this can be null.
                        if (currentlocation != null) {
                            double latitude = currentlocation.getLatitude();
                            double longitude = currentlocation.getLongitude();
                            LatLng loc = new LatLng(latitude, longitude);

//         Creating a marker
                            MarkerOptions markerOptions = new MarkerOptions();

                            // Setting the position for the marker
                            markerOptions.position(loc);
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
                            map.addMarker(markerOptions);
                        }
                    }


                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("error", e.getLocalizedMessage());
            }
        });


    }

    private void setToCamera(@org.jetbrains.annotations.NotNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng loc = new LatLng(latitude, longitude);
        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.zoom(16);
        builder.target(loc);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));


    }


    @Override
    protected void onResume() {
        Log.e("mess", "resume");

        super.onResume();
        if (mMap != null) {
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().getBoolean("pick")) {
                    screenMain = false;
                    mMap.onCreate(savedInstanceState);
                    visibility(screenMain);
                    setMainForPickLocation();
                    Log.e("error", "does not occurr");
                    From = getIntent().getExtras().getParcelable("location");
                    To = (Place) getIntent().getExtras().getSerializable("place");
                    Log.e("mess", "resume3");

                } else {
                    Log.e("error", "occurr");

                    screenMain = true;
                    mMap.onCreate(savedInstanceState);
                    visibility(screenMain);
                    setMainMapWithDrawer();
                    getcurrentLocation();
                    if (mCurrentLocationLongitudeLatitutde != null)
                        timer.start();
                    Log.e("mess", "resume2");

                }
            } else {
                screenMain = true;
                mMap.onCreate(savedInstanceState);
                visibility(screenMain);
                setMainMapWithDrawer();
                getcurrentLocation();

                if (mCurrentLocationLongitudeLatitutde != null)
                    timer.start();
                Log.e("mess", "resume3");
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("mess", "start");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMap != null)
            mMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMap.onLowMemory();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (handle) {
                returnVisibilty();
            } else if (visibility) {
                if (bottomOn) {
                    onmapClicked();
                    bottomOn = false;

                } else {
                    visibility = true;

                    visibility(visibility);
                    screenMain = true;
                    setMainMapWithDrawer();
                }
            } else {

                super.onBackPressed();


            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.payment) {
            startActivity(new Intent(DashBoard.this, Payments.class));

        } else if (id == R.id.your_trip) {
            startActivity(new Intent(DashBoard.this, Trips.class));
        } else if (id == R.id.coupon) {
            startActivity(new Intent(DashBoard.this, Coupon.class));
        } else if (id == R.id.nav_tools) {
            startActivity(new Intent(DashBoard.this, Wallet.class));

        } else if (id == R.id.share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            Intent chooser = Intent.createChooser(shareIntent, "Complete Action using..");
            startActivity(chooser);

        } else if (id == R.id.logout) {
            logoutDialog();
        } else if (id == R.id.help) {
            startActivity(new Intent(DashBoard.this, Help.class));

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Logout() {
        String Token = ((Response) LocalPersistence.readObjectFromFile(DashBoard.this)).getAccessToken();
        if (NetworkUtil.isConnectedToInternet(getApplicationContext())) {
            ProgressDialog dialog = new ProgressDialog(DashBoard.this, R.style.AppCompatAlertDialogStyle);
            dialog.setMessage("Logging Out");
            dialog.show();
            mApi.Logout(Token).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        LocalPersistence.deletefile(DashBoard.this);

                        dialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), Signin.class));
                        finish();
                    } else {

                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "An error Occurred", Toast.LENGTH_SHORT).show();


                        dialog.dismiss();

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialog.dismiss();
                }
            });
        } else
            NetworkUtil.showNoInternetAvailableErrorDialog(getApplicationContext());


    }

    private void logoutDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("YaadTaxi User App")
                .setIcon(ContextCompat.getDrawable(this, R.drawable.yaadtaxi))
                .setMessage("Are you sure want to logout")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {

                    dialog.dismiss();
                    Logout();

                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.dismiss();
                });

        final AlertDialog alert = builder.create();
        //2. now setup to change color of the button
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(DashBoard.this, R.color.back));
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(DashBoard.this, R.color.back));
            }
        });

        alert.show();
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        switch (monthOfYear) {
            case 0:
                this.monthOfYear = "January";
                break;
            case 1:
                this.monthOfYear = "February";
                break;
            case 2:
                this.monthOfYear = "March";
                break;
            case 3:
                this.monthOfYear = "April";
                break;
            case 4:
                this.monthOfYear = "May";
                break;
            case 5:
                this.monthOfYear = "June";
                break;
            case 6:
                this.monthOfYear = "July";
                break;
            case 7:
                this.monthOfYear = "August";
                break;
            case 8:
                this.monthOfYear = "September";
                break;
            case 9:
                this.monthOfYear = "October";
                break;
            case 10:
                this.monthOfYear = "November";
                break;
            case 11:
                this.monthOfYear = "December";
                break;

        }


        this.year = year;
        this.dayOfMonth = dayOfMonth;
        showTimePicker();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;

        Log.e("onTimeSet: ", "onTimeSet: month" + monthOfYear + "  year" + year + " day" + dayOfMonth + " year" + year + "\nhour"
                + hourOfDay + " minute" + minute);

        requestRide(String.valueOf(no_of_bag_int), String.valueOf(no_of_passenger_int), fare.getDistance(), "" + dayOfMonth + " " + monthOfYear + " " + year, hourOfDay + ":" + minute);

    }
}
