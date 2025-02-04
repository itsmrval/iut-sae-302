package com.absencemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ManageStudentsActivity extends AppCompatActivity {

    private Client client; // Déclarer client comme attribut
    private ListView studentsListView; // Déclarer studentsListView comme attribut
    private EditText textStatus; // Déclarer textStatus comme attribut

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_manage_students);

        client = Client.getInstance(); // Initialiser client
        studentsListView = findViewById(R.id.studentsListView); // Initialiser studentsListView
        textStatus = new EditText(this); // Initialiser textStatus

        Button newStudentButton = findViewById(R.id.newStudentButton);

        newStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_create_student = new Intent(ManageStudentsActivity.this, CreateStudentActivity.class);
                startActivity(intent_create_student);
            }
        });

        // Rafraîchir la liste des étudiants
        refreshStudentsList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStudentsList(); // Rafraîchir la liste des étudiants
    }

    private void refreshStudentsList() {
        // Créer et démarrer un thread pour récupérer les étudiants
        MyThreadCommand mythreadcommand = new MyThreadCommand(client, "students", this, studentsListView, textStatus);
        mythreadcommand.start();
    }

    // Méthode appelée lors du clic sur le bouton poubelle
    public void trashButtonClick(View view) {
        // Récupérer la ligne parente (l'item de la liste)
        View parentRow = (View) view.getParent();

        // Trouver la position de l'item cliqué dans la ListView
        int position = studentsListView.getPositionForView(parentRow);

        // Vérifier que la position est valide
        if (position != ListView.INVALID_POSITION) {
            Log.d("TrashButtonClick", "Position de l'item cliqué : " + position);

            // Récupérer l'adaptateur et l'item à supprimer
            final ArrayAdapter<Etudiant> adapter = (ArrayAdapter<Etudiant>) studentsListView.getAdapter();
            final Etudiant itemToRemove = adapter.getItem(position);

            // Vérifier que l'item à supprimer n'est pas null
            if (itemToRemove != null) {
                // Créer un AlertDialog pour la confirmation
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
                alertdialog.setTitle("Confirmation");
                alertdialog.setMessage("Êtes-vous sûr de vouloir supprimer cet étudiant ?");

                // Créer un OnClickListener pour le bouton "Oui"
                DialogInterface.OnClickListener yesClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Supprimer l'item de l'adaptateur
                        adapter.remove(itemToRemove);
                        adapter.notifyDataSetChanged();

                        // Récupérer l'ID de l'étudiant à supprimer
                        int studentId = itemToRemove.getIdEtudiant();
                        Log.d("TrashButtonClick", "ID de l'étudiant à supprimer : " + studentId);

                        // Démarrer un thread pour supprimer l'étudiant côté serveur
                        MyThreadCommand mythreadcommand = new MyThreadCommand(client, "deletestudent:" + studentId, ManageStudentsActivity.this, studentsListView, textStatus);
                        mythreadcommand.start();
                    }
                };

                // Ajouter un bouton "Oui" pour confirmer la suppression
                alertdialog.setPositiveButton("Oui", yesClickListener);

                // Ajouter un bouton "Non" pour annuler
                alertdialog.setNegativeButton("Non", null);

                // Afficher le pop-up
                alertdialog.show();
            } else {
                Log.e("TrashButtonClick", "L'élément à supprimer est null !");
            }
        } else {
            Log.e("TrashButtonClick", "Position invalide : " + position);
        }
    }
}