package com.travel.taxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.travel.taxi.Map.Intermediate;
import com.travel.taxi.Utils.LocalPersistence;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               checkSignIn();
            }
        },3000);



    }

    private void checkSignIn() {
        Object data= LocalPersistence.readObjectFromFile(StartupActivity.this);
        if(data==null){
            startActivity(new Intent(StartupActivity.this,Signin.class));
            finish();
        }else{
            Intent intent=new Intent(StartupActivity.this, Intermediate.class);
            startActivity(intent);
            finish();
        }
    }
}
