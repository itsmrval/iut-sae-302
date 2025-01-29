package com.absence.manager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private EditText ipInput, portInput;
    private Button connectButton;
    private TextView resultView;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipInput = findViewById(R.id.ip_input);
        portInput = findViewById(R.id.port_input);
        connectButton = findViewById(R.id.connect_button);
        resultView = findViewById(R.id.result_view);

        connectButton.setOnClickListener(v -> {
            String ip = ipInput.getText().toString().trim();
            int port;

            try {
                port = Integer.parseInt(portInput.getText().toString().trim());
            } catch (NumberFormatException e) {
                updateResult("Invalid port number");
                return;
            }

            // Charger le certificat depuis res/raw
            InputStream certInputStream = getResources().openRawResource(R.raw.cert);

            // Démarrer la connexion dans un thread séparé
            new Thread(() -> connectToServer(ip, port, certInputStream)).start();
        });
    }

    private void connectToServer(String ip, int port, InputStream certInputStream) {
        try {
            client = new Client();
            boolean isConnected = client.connectToServer(ip, port, certInputStream);

            if (isConnected) {
                updateResult("Connected to server: " + ip + ":" + port);

                Intent intent = new Intent (MainActivity.this, HomeActivity.class);
                intent.putExtra("serverDetails", ip + ":" + port);
                startActivity(intent);



            } else {
                updateResult("Failed to connect to server");
            }
        } catch (Exception e) {
            e.printStackTrace();
            updateResult("Error: " + e.getMessage());
        }
    }

    private void updateResult(String message) {
        // Mettre à jour l'interface utilisateur dans le thread principal
        new Handler(Looper.getMainLooper()).post(() -> resultView.setText(message));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.closeResources();
    }

}
