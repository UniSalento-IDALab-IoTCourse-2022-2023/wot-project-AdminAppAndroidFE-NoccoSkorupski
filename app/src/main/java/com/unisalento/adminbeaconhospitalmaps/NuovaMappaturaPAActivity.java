package com.unisalento.adminbeaconhospitalmaps;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NuovaMappaturaPAActivity extends AppCompatActivity {
    private ArrayList<String> optionsList = new ArrayList<>();
    private ArrayAdapter<String> optionsAdapter;
    final String CODICE_OSPEDALE = "01";
    private String uuidValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovamappatura);

        Intent intent = getIntent();
        uuidValue = intent.getStringExtra("UUID");
        TextView textView2 = findViewById(R.id.textView2);
        String testo = "Qual è la relazione del beacon " + uuidValue + " con gli altri?";
        textView2.setText(testo);
        // Recupera le viste AutoCompleteTextView dal layout
        Spinner uuidFronteAutoCompleteTextViewFronte = findViewById(R.id.uuidFronte);
        Spinner uuidFronteAutoCompleteTextViewRetro = findViewById(R.id.uuidRetro);
        Spinner uuidFronteAutoCompleteTextViewDestra = findViewById(R.id.uuidDestra);
        Spinner uuidFronteAutoCompleteTextViewSinistra = findViewById(R.id.uuidSinistra);


        // Crea un adattatore per le opzioni
        optionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, optionsList);

        // Collega l'adattatore all'AutoCompleteTextView
        uuidFronteAutoCompleteTextViewFronte.setAdapter(optionsAdapter);
        uuidFronteAutoCompleteTextViewRetro.setAdapter(optionsAdapter);
        uuidFronteAutoCompleteTextViewDestra.setAdapter(optionsAdapter);
        uuidFronteAutoCompleteTextViewSinistra.setAdapter(optionsAdapter);
        // Simili per gli altri AutoCompleteTextView

        // Effettua la chiamata API e popola le opzioni nel menu a tendina
        fetchOptionsData("http://192.168.1.140:8081/api/amministratore/allBeacon/" + CODICE_OSPEDALE+"/"+uuidValue);
    }

    // Metodo per effettuare la chiamata API e popolare le opzioni
    private void fetchOptionsData(String apiUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);

                        // Pulisci la lista di opzioni
                        optionsList.clear();

                        // Aggiungi le opzioni dalla risposta JSON alla lista
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String uuid = jsonObject.getString("uuid");
                            optionsList.add(uuid);
                        }
                        optionsList.add("none");

                        // Notifica l'adattatore del cambiamento nella lista di opzioni
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                optionsAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void submitForm(View view) {
        // Ottieni i valori dagli EditText e Spinner
        String uuidFronteValue = ((Spinner) findViewById(R.id.uuidFronte)).getSelectedItem().toString();
        String distanzaFronteValue = ((EditText) findViewById(R.id.distanzaFronte)).getText().toString();
        String uuidRetroValue = ((Spinner) findViewById(R.id.uuidRetro)).getSelectedItem().toString();
        String distanzaRetroValue = ((EditText) findViewById(R.id.distanzaRetro)).getText().toString();
        String uuidDestraValue = ((Spinner) findViewById(R.id.uuidDestra)).getSelectedItem().toString();
        String distanzaDestraValue = ((EditText) findViewById(R.id.distanzaDestra)).getText().toString();
        String uuidSinistraValue = ((Spinner) findViewById(R.id.uuidSinistra)).getSelectedItem().toString();
        String distanzaSinistraValue = ((EditText) findViewById(R.id.distanzaSinistra)).getText().toString();

        if ((distanzaDestraValue.isEmpty() && !uuidDestraValue.equals("none")) || (distanzaSinistraValue.isEmpty() && !uuidSinistraValue.equals("none")) || (distanzaFronteValue.isEmpty() && !uuidFronteValue.equals("none")) || (distanzaRetroValue.isEmpty() && !uuidRetroValue.equals("none"))) {
            // Mostra il messaggio di errore
            TextView errorTextView = findViewById(R.id.errorTextView);
            errorTextView.setVisibility(View.VISIBLE);
            return; // Esci dal metodo senza inviare la richiesta se un campo è vuoto
        }

        // Esegui la richiesta POST con i dati raccolti
        OkHttpClient client = new OkHttpClient();

        // Sostituisci l'URL con l'endpoint corretto per la tua API POST
        String url = "http://192.168.1.140:8081/api/amministratore/beaconMappatoDisabili";



        JSONArray viciniArray = new JSONArray();

        // Costruire gli oggetti JSON per ciascun elemento dell'array "vicini"
        if(!uuidFronteValue.equalsIgnoreCase("none")) {
            JSONObject vicino1 = new JSONObject();
            try {
                vicino1.put("direzione", "FRONTE");
                vicino1.put("beaconUUID", uuidFronteValue);
                vicino1.put("distanza", distanzaFronteValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            viciniArray.put(vicino1);
        }
        if(!uuidDestraValue.equalsIgnoreCase("none")) {
            JSONObject vicino2 = new JSONObject();
            try {
                vicino2.put("direzione", "DESTRA");
                vicino2.put("beaconUUID", uuidDestraValue);
                vicino2.put("distanza", distanzaDestraValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            viciniArray.put(vicino2);
        }

        if(!uuidSinistraValue.equalsIgnoreCase("none")) {
            JSONObject vicino3 = new JSONObject();
            try {
                vicino3.put("direzione", "SINISTRA");
                vicino3.put("beaconUUID", uuidSinistraValue);
                vicino3.put("distanza", distanzaSinistraValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            viciniArray.put(vicino3);
        }

        if(!uuidRetroValue.equalsIgnoreCase("none")) {
            JSONObject vicino4 = new JSONObject();
            try {
                vicino4.put("direzione", "RETRO");
                vicino4.put("beaconUUID", uuidRetroValue);
                vicino4.put("distanza", distanzaRetroValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            viciniArray.put(vicino4);
        }

        // Creare l'oggetto JSON principale
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("beaconUUID", uuidValue);
            mainObject.put("viciniPerDisabili", viciniArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Convertire l'oggetto JSON in una stringa JSON
        String jsonString = mainObject.toString();

        new NuovaMappaturaPAActivity.PostDataTask(url, jsonString).execute();

    }
    private void showLongDurationToast(String message) {
        Toast toast = Toast.makeText(NuovaMappaturaPAActivity.this, message, Toast.LENGTH_LONG);
        toast.setDuration(Toast.LENGTH_LONG * 5); // Moltiplica per 2 per fare durare il Toast più a lungo
        toast.show();
    }
    private class PostDataTask extends AsyncTask<Void, Void, String> {
        private String url;
        private String jsonString;

        public PostDataTask(String url, String jsonString) {
            this.url = url;
            this.jsonString = jsonString;
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonString);

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String messaggio = jsonResponse.getString("messaggio");
                    showLongDurationToast(messaggio);
                    onBackPressed();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(NuovaMappaturaPAActivity.this, "Errore durante la richiesta POST", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
