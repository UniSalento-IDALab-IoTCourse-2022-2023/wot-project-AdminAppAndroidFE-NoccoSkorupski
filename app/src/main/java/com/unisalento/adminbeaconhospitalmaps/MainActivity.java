package com.unisalento.adminbeaconhospitalmaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void openNewBeaconActivity(View view) {
        Intent intent = new Intent(this, NuovoBeaconActivity.class);
        startActivity(intent);
    }

    public void openMapBeaconActivity(View view) {
        Intent intent = new Intent(this, MappaBeaconActivity.class);
        startActivity(intent);
    }
}