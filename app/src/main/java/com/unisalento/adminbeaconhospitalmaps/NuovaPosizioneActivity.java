package com.unisalento.adminbeaconhospitalmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NuovaPosizioneActivity extends AppCompatActivity implements SensorEventListener {

    final String CODICE_OSPEDALE = "01";
    private SensorManager sensorManager;
    private Sensor magnetometro;
    private float azimuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovaposizione);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Requisisci il permesso
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 225);
        } else {
            // Richiedi la posizione corrente
            sensorManager.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Ottieni il campo magnetico corrente
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Calcola l'azimut
        azimuth = (float) Math.atan2(y, x);

        // Controlla se l'azimut è negativo
        if (azimuth < 0) {
            // Se l'azimut è negativo, aggiungi 2 * Math.PI ad esso
            azimuth += 2 * Math.PI;
        }

        // Converti l'azimut in gradi
        azimuth = (float) (azimuth * 180 / Math.PI);



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void submitForm(View view) {

        // Crea un JSON con i dati del form
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("idOspedale", CODICE_OSPEDALE);
            jsonObject.put("coordinataIniziale", azimuth);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SubmitFormAsyncTask().execute(jsonObject.toString());

    }

    private class SubmitFormAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = "http://ec2-52-22-228-41.compute-1.amazonaws.com:8081/api/amministratore/nuoveCoordinate";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, params[0]);
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    // La richiesta è andata a buon fine
                    return response.body().string();
                } else {
                    // La richiesta ha restituito un errore
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String responseString) {
            if (responseString != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(responseString);
                    String messaggio = jsonResponse.getString("messaggio");
                    showLongDurationToast(messaggio);
                    onBackPressed();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Gestisci il caso in cui la chiamata di rete ha restituito un errore
                Log.e("API_ERROR", "Errore nella chiamata API");
            }
        }
    }

    private void showLongDurationToast(String message) {
        Toast toast = Toast.makeText(NuovaPosizioneActivity.this, message, Toast.LENGTH_LONG);
        toast.setDuration(Toast.LENGTH_LONG * 5); // Moltiplica per 2 per fare durare il Toast più a lungo
        toast.show();
    }
}
