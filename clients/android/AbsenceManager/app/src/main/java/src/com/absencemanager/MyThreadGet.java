package src.com.absencemanager;

import android.app.Activity;
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
        } else {
            updateStatusInUI(false); // Commande incorrecte
        }
    }

    private void updateListView(final List<?> items) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (items != null) {
                    if (items.get(0) instanceof Etudiant) {
                        adapter = new ArrayAdapter<>(activity, R.layout.custom_list_item, (List<Etudiant>) items);
                    } else if (items.get(0) instanceof Seance) {
                        adapter = new ArrayAdapter<>(activity, R.layout.custom_list_item, (List<Seance>) items);
                    }
                    listView.setAdapter(adapter);
                    updateStatusInUI(true);
                } else {
                    updateStatusInUI(false);
                }
            }
        });
    }

    private void updateStatusInUI(final boolean connected) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textstatus.setText(connected ? "Connected" : "Not Connected");
            }
        });
    }


}
