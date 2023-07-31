package com.unisalento.adminbeaconhospitalmaps;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.altbeacon.beacon.BeaconConsumer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MappaBeaconActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    final String CODICE_OSPEDALE = "01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mappabeacon);

        tableLayout = findViewById(R.id.tableLayout);

        // Effettua la chiamata API e riempi la tabella con i dati ricevuti
        new FetchDataTask().execute();
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Chiama onBackPressed per tornare all'attività precedente
            }
        });
    }

    private class FetchDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            // Effettua la chiamata API GET utilizzando OkHttpClient
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://192.168.1.140:8081/api/amministratore/allBeacon/"+CODICE_OSPEDALE) // Sostituisci con l'URL della tua API
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
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
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String col1Value = jsonObject.getString("uuid");
                        String col2Value = jsonObject.getString("stanza");
                        String col3Value = jsonObject.getString("reparto");

                        // Aggiungi una nuova riga alla tabella per ogni risultato della chiamata API
                        TableRow tableRow = new TableRow(MappaBeaconActivity.this);
                        tableRow.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT
                        ));

                        TextView col1TextView = new TextView(MappaBeaconActivity.this);
                        col1TextView.setText(col1Value);
                        col1TextView.setPadding(8, 8, 8, 8);
                        tableRow.addView(col1TextView);

                        TextView col2TextView = new TextView(MappaBeaconActivity.this);
                        col2TextView.setText(col2Value);
                        col2TextView.setPadding(8, 8, 8, 8);
                        tableRow.addView(col2TextView);

                        TextView col3TextView = new TextView(MappaBeaconActivity.this);
                        col3TextView.setText(col3Value);
                        col3TextView.setPadding(8, 8, 8, 8);
                        tableRow.addView(col3TextView);

                        TextView col4TextView = new TextView(MappaBeaconActivity.this);
                        col4TextView.setPadding(8, 8, 8, 8);
                        tableRow.addView(col4TextView);

                        // Aggiungi un pulsante nella colonna
                        Button button = new Button(MappaBeaconActivity.this);
                        button.setText("Mappa");
                        String uuidValue = col1Value;
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MappaBeaconActivity.this, NuovaMappaturaActivity.class);
                                intent.putExtra("UUID", uuidValue);
                                startActivity(intent);
                            }
                        });
                        tableRow.addView(button);

                        View separator = new View(MappaBeaconActivity.this);
                        separator.setBackgroundColor(Color.BLACK);
                        separator.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 1));
                        tableLayout.addView(separator);

                        tableLayout.addView(tableRow);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
