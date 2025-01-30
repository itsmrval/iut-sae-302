package com.absence.manager;

import android.content.Intent;
import android.os.Looper;
import android.app.Activity; // Assurez-vous que votre classe Processus hérite ou reçoit un contexte d'Activity

import java.io.InputStream;

public class Processus {

    private Client client;
    private String ip;
    private int port;
    private InputStream certInputStream;
    private Activity activity; // Ajout du contexte de l'activité

    public Processus(Activity activity) {
        this.activity = activity;
    }

    public void connectToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Stocke l'état de la connexion dans isConnected
                boolean connected = client.connectToServer(ip, port, certInputStream);
            }
        }).start();
    }
}
