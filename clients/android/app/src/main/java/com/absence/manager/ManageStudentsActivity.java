package com.absence.manager;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ManageStudentsActivity extends AppCompatActivity {

    private Client client;
    private ListView listView;
    private EditText nameField;
    private ArrayAdapter<Etudiant> adapter;  // ArrayAdapter pour afficher les étudiants dans la ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        // Initialiser les éléments de l'interface
        listView = findViewById(R.id.list_students);
        nameField = findViewById(R.id.name_edit);

        // Créer l'adaptateur pour la ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        // Obtenir l'instance du client
        client = Client.getInstance();

        // Récupérer et afficher les étudiants
        loadStudents();
    }

    private void loadStudents() {
        // Exécuter l'appel réseau dans un thread secondaire pour éviter de bloquer l'UI
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Récupérer les étudiants depuis le serveur
                List<Etudiant> students = client.getStudents();

                // Mettre à jour l'UI sur le thread principal
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Ajouter les étudiants à l'adaptateur
                        adapter.clear();
                        adapter.addAll(students);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
}
