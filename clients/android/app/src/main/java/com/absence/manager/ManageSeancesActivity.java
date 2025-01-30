package com.absence.manager;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.List;

public class ManageSeancesActivity extends AppCompatActivity {

    private ListView seancesListView;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_seances);

        // Initialize ListView
        seancesListView = findViewById(R.id.seancesListView);

        // Initialize Client
        client = Client.getInstance();

        // Start background task to connect to the server and fetch seances
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Assuming the certificate input stream is available here
                InputStream certInputStream = getResources().openRawResource(R.raw.cert); // example

                // Connect to the server
                boolean connected = client.connectToServer("10.0.2.2", 8081, certInputStream);

                if (connected) {
                    // Fetch the seances
                    List<Seance> seances = client.getSeances();
                    Log log = null;
                    Log.d("ManageSeances", "Seances fetched: " + seances);
                    // Display the seances on the UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Check if we have any seances
                            if (seances.isEmpty()) {
                                Log.d("ManageSeances", "No seances found.");
                            } else {
                                // Create a list of strings to display
                                String[] seancesArray = new String[seances.size()];
                                for (int i = 0; i < seances.size(); i++) {
                                    Seance seance = seances.get(i);
                                    seancesArray[i] = "Seance: " + seance.getNomSeance() + " (ID: " + seance.getIdSeance() + ")";
                                }

                                // Set up ArrayAdapter to display seances
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(ManageSeancesActivity.this, android.R.layout.simple_list_item_1, seancesArray);
                                seancesListView.setAdapter(adapter);
                            }

                        }
                    });
                } else {
                    Log.e("ManageSeances", "Failed to connect to the server.");
                }

            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close client connection
        client.closeResources();
    }
}
