package com.unisalento.adminbeaconhospitalmaps;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NuovoBeaconActivity extends AppCompatActivity implements BeaconConsumer {
    private BeaconManager beaconManager;
    private ArrayList<Beacon> beacons = new ArrayList<>();
    Collection<Beacon> beaconsInRange;
    private EditText nomeStanzeEditText;
    private EditText pianoEditText;
    private EditText repartoEditText;
    private String nearestBeaconUuid;
    final String CODICE_OSPEDALE = "01";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovobeacon);
        nomeStanzeEditText = findViewById(R.id.nomeStanze);
        pianoEditText = findViewById(R.id.piano);
        repartoEditText = findViewById(R.id.reparto);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Chiama onBackPressed per tornare all'attività precedente
            }
        });

        // Verifica e richiedi le autorizzazioni necessarie
        checkPermissions();

        // Inizializza il BeaconManager
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    private void checkPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Avvia la scansione dei beacon dopo aver ottenuto le autorizzazioni
                startBeaconScanning();
            } else {
                // Gestisci il caso in cui l'utente nega le autorizzazioni
                Toast.makeText(this, "Permesso di localizzazione negato", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startBeaconScanning() {
        beaconsInRange = new ArrayList<>();
        // Assicurati di aver richiesto i permessi necessari nel file Manifest
        beaconsInRange.addAll(beacons);
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beaconsInRange, Region region) {
                if (!beaconsInRange.isEmpty()) {
                    beacons.clear(); // Rimuovi i beacon precedenti dalla lista
                    beacons.addAll(beaconsInRange); // Aggiungi i nuovi beacon rilevati alla lista

                    // Assicurati che ci siano elementi nella lista 'beacons'
                    if (!beacons.isEmpty()) {
                        Beacon nearestBeacon = beacons.get(0); // Ottieni il primo elemento della lista
                        nearestBeaconUuid = nearestBeacon.getId1().toString();
                        // Ora hai l'UUID del beacon più vicino
                        runOnUiThread(() -> {
                            Toast.makeText(NuovoBeaconActivity.this, "Beacon più vicino UUID: " + nearestBeaconUuid, Toast.LENGTH_LONG).show();
                            Log.i("BeaconData", "UUID del beacon: " + nearestBeaconUuid);
                        });
                    }
                } else {
                    Log.i("Beacon Data", "Nessun beacon trovato.");
                }
            }
        });

        try {
            // Avvia la scansione dei beacon
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBeaconServiceConnect() {
        // Una volta connesso al BeaconManager, avvia la scansione dei beacon
        startBeaconScanning();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Assicurati di liberare le risorse quando l'Activity viene distrutta
        beaconManager.unbind(this);
    }


    public void submitForm(View view) {
        // Ottieni i valori inseriti negli EditText
        String nomeStanze = nomeStanzeEditText.getText().toString();
        String piano = pianoEditText.getText().toString();
        String reparto = repartoEditText.getText().toString();

        // Crea un JSON con i dati del form
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("beaconUUID", nearestBeaconUuid);
            jsonObject.put("idOspedale", CODICE_OSPEDALE);
            jsonObject.put("nomeStanze", nomeStanze);
            jsonObject.put("piano", piano);
            jsonObject.put("reparto", reparto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SubmitFormAsyncTask().execute(jsonObject.toString());

    }

    private class SubmitFormAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = "http://192.168.1.140:8081/api/amministratore/nuovoBeacon";
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
                    Toast.makeText(NuovoBeaconActivity.this, messaggio, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Gestisci il caso in cui la chiamata di rete ha restituito un errore
                Log.e("API_ERROR", "Errore nella chiamata API");
            }
        }
    }
}



