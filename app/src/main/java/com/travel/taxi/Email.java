package com.travel.taxi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.travel.taxi.Map.Intermediate;
import com.travel.taxi.Utils.LocalPersistence;
import com.travel.taxi.Utils.NetworkUtil;

public class Email extends AppCompatActivity {
    private ImageView mBackbtn;
    private ImageButton mNextbtn;
    private EditText mEmailinput;
    private TextView mSignupbtn;

    private void init() {
        mBackbtn = findViewById(R.id.backarrowbtn);
        mNextbtn = findViewById(R.id.nextbtn);
        mEmailinput = findViewById(R.id.emailtv);
        mSignupbtn = findViewById(R.id.signupbtn);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        init();


        Object data= LocalPersistence.readObjectFromFile(Email.this);
        if(data==null){
        mSignupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Email.this, SignUp.class));
                finish();

            }
        });
        mNextbtn.setOnClickListener(next -> {
            if (NetworkUtil.isConnectedToWifi(Email.this)||NetworkUtil.isConnectedToWifi(Email.this)){
                checkEmail();
            }
            else{
                NetworkUtil.showNoInternetAvailableErrorDialog(Email.this);
            }
        });

        mBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });}
        else{
            Intent intent=new Intent(Email.this, Intermediate.class);
            startActivity(intent);
        }
    }
    private void checkEmail() {
        String email=mEmailinput.getText().toString();
        if (!email.isEmpty()) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Intent intent=new Intent(Email.this, Password.class);
                intent.putExtra("email",email);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }else{
                Toast.makeText(this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Enter Email Address", Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onBackPressed();
    }


}
