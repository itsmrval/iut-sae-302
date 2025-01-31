package src.com.absencemanager;

import android.app.Activity;
import android.widget.EditText;

import java.io.InputStream;

class MyThread extends Thread {

    private Client client;
    private int port;
    private Activity activity;
    private String ip;
    private EditText textstatus;
    private boolean connected;
    private InputStream certInputStream;


    // Constructeur pour initialiser les variables nécessaires
    public MyThread(Activity activity, Client client, String ip, int port, InputStream certInputStream) {
        this.activity = activity;
        this.client = client;
        this.port = port;
        this.ip = ip;
        this.certInputStream = certInputStream;
        // Initialize the EditText
        textstatus = activity.findViewById(R.id.status_connect);
    }

    @Override
    public void run() {
        // Code à exécuter dans le thread
        connected = client.connectToServer(ip, port, certInputStream);
        updateStatusInUI();
    }

    private void updateStatusInUI() {
        activity.runOnUiThread(new UpdateUITask());
    }

    private class UpdateUITask implements Runnable {
        @Override
        public void run() {
            textstatus.setText(connected ? "Connected" : "Not Connected");
        }
    }

    public boolean isConnected() {
        return connected;
    }

}


