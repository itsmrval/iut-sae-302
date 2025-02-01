package src.com.absencemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import src.com.absencemanager.Client;
import src.com.absencemanager.MyThread;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private EditText ipInput;
    private EditText portInput;
    private EditText textstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        ipInput = findViewById(R.id.ip_input);
        portInput = findViewById(R.id.port_input);
        textstatus = findViewById(R.id.status_connect);
        Button connectButton = findViewById(R.id.connect_button);

        // Set click listener for the Connect button
        connectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipInput.getText().toString();
                String port = portInput.getText().toString();

                if (validateInput(ip, port)) {

                    InputStream certInputStream = getResources().openRawResource(R.raw.cert);
                    Client client = Client.getInstance(); // Obtenir l'instance unique du client
                    MyThread myThread = new MyThread(MainActivity.this, client, ip, Integer.parseInt(port), certInputStream);
                    // Démarrer le thread
                    myThread.start();

                    try {
                        // Attendre que le thread se termine
                        myThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (myThread.isConnected()) {
                        Log.d("MainActivity", "Connexion réussie");
                        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                        intent.putExtra("ip", ip);
                        startActivity(intent);
                    }
                } else {
                    textstatus.setText("Veuillez entrer une adresse IP et un port valide.");
                }
            }
        });
    }

    private boolean validateInput(String ip, String port) {
        // Simple validation logic for IP and port
        return !ip.isEmpty() && !port.isEmpty();
    }
}