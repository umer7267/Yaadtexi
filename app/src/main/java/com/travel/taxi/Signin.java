package com.travel.taxi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.travel.taxi.Map.Intermediate;
import com.travel.taxi.Utils.DefaultApplication;
import com.travel.taxi.Utils.LocalPersistence;

public class Signin extends AppCompatActivity {
    private LinearLayout mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Object data= LocalPersistence.readObjectFromFile(Signin.this);
        if(data==null) {
            mEmail = findViewById(R.id.linear);
            mEmail.setOnClickListener(v -> {
                        openActivity2(v);
                    }


            );
            DefaultApplication.activityResumed();

        }
        else{
            Intent intent=new Intent(Signin.this, Intermediate.class);
            startActivity(intent);
        }

    }

    public void openActivity2(View view) {
        Intent intent = new Intent(this, Email.class);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    @Override
    protected void onResume() {
        super.onResume();
        DefaultApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DefaultApplication.activityPaused();
    }
}
