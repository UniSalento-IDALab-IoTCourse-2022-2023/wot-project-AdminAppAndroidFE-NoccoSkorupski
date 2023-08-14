package com.unisalento.adminbeaconhospitalmaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SceltaMappaBeaconActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sceltamappabeacon);

    }
    public void openMapBeaconActivity(View view) {
        Intent intent = new Intent(this, MappaBeaconActivity.class);
        startActivity(intent);
    }
    public void openMapPABeaconActivity(View view) {
        Intent intent = new Intent(this, MappaPABeaconActivity.class);
        startActivity(intent);
    }


}
