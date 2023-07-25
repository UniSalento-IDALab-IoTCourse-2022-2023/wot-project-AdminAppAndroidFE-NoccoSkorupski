package com.unisalento.adminbeaconhospitalmaps;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.RemoteException;
import android.widget.Toast;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import java.util.Collection;

public class NuovoBeaconActivity extends AppCompatActivity implements BeaconConsumer {
    private BeaconManager beaconManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovobeacon);

        // Verifica e richiedi le autorizzazioni necessarie
        checkPermissions();

        // Inizializza il BeaconManager
        beaconManager = BeaconManager.getInstanceForApplication(this);
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
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (!beacons.isEmpty()) {
                    Beacon nearestBeacon = beacons.iterator().next();
                    String nearestBeaconUuid = nearestBeacon.getId1().toString();
                    // Ora hai l'UUID del beacon più vicino
                    runOnUiThread(() -> {
                        Toast.makeText(NuovoBeaconActivity.this, "Beacon più vicino UUID: " + nearestBeaconUuid, Toast.LENGTH_LONG).show();
                    });
                } else {
                    // Nessun beacon rilevato
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

}

