package com.absencemanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateStudentActivity extends AppCompatActivity {

    private EditText editTextStudentName; // Champ pour le nom de l'étudiant
    private Button buttonValidate; // Bouton de validation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student);

        // Initialisation des vues
        editTextStudentName = findViewById(R.id.editTextStudentName);
        buttonValidate = findViewById(R.id.buttonValidate);

        // Gestion du clic sur le bouton de validation
        buttonValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentName = editTextStudentName.getText().toString();

                // Vérifier que le nom de l'étudiant n'est pas vide
                if (studentName.isEmpty()) {
                    Toast.makeText(CreateStudentActivity.this, "Veuillez entrer un nom d'étudiant", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Envoyer les données au serveur via un thread
                Client client = Client.getInstance();

                // Créez et démarrez le thread pour envoyer la commande au serveur
                MyThreadCommand mythreadcommand = new MyThreadCommand(client, "createstudent:" + studentName, CreateStudentActivity.this, null, null);
                mythreadcommand.start();

                try {
                    // Attendre que le thread termine son exécution
                    mythreadcommand.join();
                    // Afficher un message de confirmation
                    Toast.makeText(CreateStudentActivity.this, "Étudiant créé avec succès", Toast.LENGTH_SHORT).show();

                    // Fermer cette activité et revenir à l'activité précédente (ManageStudentsActivity)
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // Gérer l'interruption du thread
                    Toast.makeText(CreateStudentActivity.this, "Erreur lors de la création de l'étudiant", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}