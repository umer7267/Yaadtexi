package com.travel.taxi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.travel.taxi.Map.DashBoard;
import com.travel.taxi.R;

public class Coupon extends AppCompatActivity {
    LinearLayout back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        back=findViewById(R.id.backcopn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Coupon.this,DashBoard.class));

            }
        });
    }
}
