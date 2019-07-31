package com.travel.taxi.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.travel.taxi.ApiResponse.Login.Response;
import com.travel.taxi.ApiResponse.Password.PasswordResponse;
import com.travel.taxi.Connection.Services;
import com.travel.taxi.Connection.Utils;
import com.travel.taxi.R;
import com.travel.taxi.Utils.LocalPersistence;

import retrofit2.Call;
import retrofit2.Callback;


public class ChangePassword extends AppCompatActivity {
    Response data;
    private EditText newPass, againPass, oldPass;
    private Button updbtn;
    private Services mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        newPass = findViewById(R.id.updnewpassword);
        againPass = findViewById(R.id.updagainpassword);
        oldPass = findViewById(R.id.updoldpassword);
        updbtn = findViewById(R.id.updsavepassword);
        mApi = Utils.getApiService2();
        data = (Response) LocalPersistence.readObjectFromFile(ChangePassword.this);

        updbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    mApi.UpdatePassword("Bearer " + data.getAccessToken(),
                            newPass.getText().toString(),
                            againPass.getText().toString(),
                            oldPass.getText().toString()).enqueue(new Callback<PasswordResponse>() {
                        @Override
                        public void onResponse(Call<PasswordResponse> call, retrofit2.Response<PasswordResponse> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ChangePassword.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                           newPass.setText("");
                           againPass.setText("");
                           oldPass.setText("");
                            }
                        }

                        @Override
                        public void onFailure(Call<PasswordResponse> call, Throwable t) {

                        }
                    });
                }
                else{
                    Toast.makeText(ChangePassword.this, "something wrong", Toast.LENGTH_SHORT).show();
                }
            }


        });


    }

    public boolean isValid() {
        if (!(oldPass.getText().toString().isEmpty() && againPass.getText().toString().isEmpty() && newPass.getText().toString().isEmpty())) {
            if (newPass.getText().toString().equals(againPass.getText().toString())) {
               return true;

            } else {
                Toast.makeText(this, "password not matched", Toast.LENGTH_SHORT).show();

            }

        } else {
            Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
