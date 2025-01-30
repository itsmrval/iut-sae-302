package com.absence.manager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private EditText ipInput, portInput;
    private TextView resultView;

    private Processus connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipInput = findViewById(R.id.ip_input);
        portInput = findViewById(R.id.port_input);
        Button connectButton = findViewById(R.id.connect_button);
        resultView = findViewById(R.id.result_view);

        Client client = Client.getInstance();

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ipInput.getText().toString().trim();
                int port;

                try {
                    port = Integer.parseInt(portInput.getText().toString().trim());

                    // Charger le certificat depuis res/raw
                    InputStream certInputStream = getResources().openRawResource(R.raw.cert);

                    // Afficher le message de connexion
                    updateResult("Connecting to the server...");

                    // Démarrer la connexion dans un thread séparé pour ne pas bloquer le UI
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isConnected = client.connectToServer(ip, port, certInputStream);

                            // Mettre à jour l'UI depuis le thread principal
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isConnected) {
                                        updateResult("Connected to the server!");

                                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(intent);


                                    } else {
                                        updateResult("Failed to connect to the server.");
                                    }
                                }
                            });
                        }
                    }).start();


                } catch (NumberFormatException e) {
                    updateResult("Invalid port number");
                }
            }
        });
    }

    private void updateResult(String message) {
        resultView.setText(message);
    }
}
