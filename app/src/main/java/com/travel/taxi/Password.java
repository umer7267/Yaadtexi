package com.travel.taxi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.travel.taxi.ApiResponse.Login.LoginResponse;
import com.travel.taxi.Connection.Services;
import com.travel.taxi.Connection.Utils;
import com.travel.taxi.Map.Intermediate;
import com.travel.taxi.Utils.Constants;
import com.travel.taxi.Utils.LocalPersistence;
import com.travel.taxi.Utils.NetworkUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Password extends AppCompatActivity {
    private TextView mSignupbtn;
    private TextView mForgotPasssword;
    private ImageView mBackbtn;
    private ImageButton mNextbtn;

    private Services mApi;

    private EditText mPassword;
    private void init() {

        mSignupbtn = findViewById(R.id.signupbtn);
        mForgotPasssword = findViewById(R.id.forget_password);
        mBackbtn = findViewById(R.id.backarrowbtnpass);
        mPassword = findViewById(R.id.password);
        mNextbtn = findViewById(R.id.nextbtn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        init();
        mApi= Utils.getApiService();
        mSignupbtn.setOnClickListener(signin->{
                startActivity(new Intent(Password.this, SignUp.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
finish();

        });
        mNextbtn.setOnClickListener(next -> {
           signIn();
        });
        mBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void signIn() {
        String email =getIntent().getExtras().getString("email");
        String password=mPassword.getText().toString();
        ProgressDialog dialog=new ProgressDialog(Password.this,R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Signing In");


        if (!(password.isEmpty() && password.length()<6)){

            if (NetworkUtil.isConnectedToWifi(Password.this) || NetworkUtil.isConnectedToMobileNetwork(Password.this) || NetworkUtil.isConnectedToInternet(Password.this)) {
                dialog.show();
                mApi.Login(email,password).enqueue(new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        Log.e("code",response.code()+"");
                        if(response.isSuccessful()) {
                            dialog.dismiss();
                            if (response.body().getMessage() == null) {
                                Constants.curr_password = password;
                                LocalPersistence.witeObjectToFile(Password.this,response.body().getResponse());
                                Intent intent= new Intent(getApplicationContext(), Intermediate.class);
                                Bundle bundle=new Bundle();
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                Log.e("response",response.body().toString());
                                Toast.makeText(Password.this, "Successfully login", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                dialog.dismiss();
                                Toast.makeText(Password. this, "requested body:"+response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        }else{
                            if(response.code()==401){
                                dialog.dismiss();
                                Toast.makeText(Password. this, "Email or Password is incorrect", Toast.LENGTH_SHORT).show();

                            }
                            dialog.dismiss();

                        }

                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Log.e("failure", t.getMessage());

                        dialog.dismiss();
                        Toast.makeText(Password.this, "Your Request Can not be Completed", Toast.LENGTH_SHORT).show();

                    }
                });

            }else{
                NetworkUtil.showNoInternetAvailableErrorDialog(Password.this);
            }

        }else{
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onBackPressed();
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
