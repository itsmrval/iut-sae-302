package src.com.absencemanager;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

class MyThreadGet extends Thread {

    private final String command;
    private Client client;
    private Activity activity;
    private EditText textstatus;
    private ListView listView;
    private ArrayAdapter<?> adapter;

    // Constructeur pour initialiser les variables nécessaires
    public MyThreadGet(Client client, String command, Activity activity, ListView listView, EditText textstatus) {
        this.client = client;
        this.command = command;
        this.activity = activity;
        this.listView = listView;
        this.textstatus = textstatus;
    }

    @Override
    public void run() {
        if ("students".equals(command)) {  // Vérifie si la commande est "students"
            List<Etudiant> students = client.getStudents(); // Récupère la liste des étudiants du client
            updateListView(students);
        } else if ("seances".equals(command)) {  // Vérifie si la commande est "seances"
            List<Seance> seances = client.getSeances(); // Récupère la liste des séances du client
            updateListView(seances);
        } else if (command != null && command.startsWith("deleteseance:")) {  // Vérifie si la commande est "deleteseance"
            // Extraire l'ID de la séance à supprimer
            String[] parts = command.split(":");
            if (parts.length == 2) {
                int seanceId = Integer.parseInt(parts[1]); // Convertir l'ID en entier
                client.deleteSeance(seanceId); // Supprimer la séance côté serveur
            }
        } else if (command != null && command.startsWith("createseance:")) {  // Vérifie si la commande est "createseance"
            // Extraire l'ID de la séance à creer
            Log.d("MyThreadGet", "Commande : " + command);
            String[] parts = command.split(":");
            if (parts.length == 3) {
                String seanceName = parts[1];
                long unixTime = Long.parseLong(parts[2]);
                client.createSeance(seanceName, unixTime); // Creeer la séance côté serveur
            }


        } else {
            updateStatusInUI(false); // Commande incorrecte
        }
    }

    private void updateListView(final List<?> items) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (items != null && !items.isEmpty() && textstatus != null && listView != null) { // Vérifier si la liste n'est pas vide
                    if (items.get(0) instanceof Etudiant) {
                        adapter = new ArrayAdapter<Etudiant>(
                                activity,
                                R.layout.custom_list_item,
                                R.id.textViewItem,
                                (List<Etudiant>) items
                        );
                    } else if (items.get(0) instanceof Seance) {
                        adapter = new ArrayAdapter<Seance>(
                                activity,
                                R.layout.custom_list_item,
                                R.id.textViewItem,
                                (List<Seance>) items
                        );
                    }
                    listView.setAdapter(adapter);
                    updateStatusInUI(true);
                } else {
                    updateStatusInUI(false); // Gère aussi le cas items == null OU items vide
                }
            }
        });
    }

    private void updateStatusInUI(final boolean connected) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textstatus!=null) {
                    textstatus.setText(connected ? "Connected" : "Not Connected");
                }
            }
        });
    }


}
