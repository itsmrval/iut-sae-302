package com.absencemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ManageSeancesActivity extends AppCompatActivity {

    private Client client; // Déclarer client comme attribut
    private ListView seancesListView; // Déclarer seancesListView comme attribut
    private EditText textStatus; // Déclarer textStatus comme attribut

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_seances);

        client = Client.getInstance(); // Initialiser client
        seancesListView = findViewById(R.id.seancesListView); // Initialiser seancesListView
        textStatus = new EditText(this); // Initialiser textStatus

        Button newSeanceButton = findViewById(R.id.newSeanceButton);


        newSeanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_create_seance = new Intent(ManageSeancesActivity.this, CreateSeanceActivity.class);
                startActivity(intent_create_seance);
            }
        });

        // Rafraîchir la liste des séances
        refreshSeancesList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSeancesList(); // Rafraîchir la liste des séances
    }

    private void refreshSeancesList() {
        // Créer et démarrer un thread pour récupérer les séances
        MyThreadCommand mythreadcommand = new MyThreadCommand(client, "seances", this, seancesListView, textStatus);
        mythreadcommand.start();
    }


    // Méthode appelée lors du clic sur le bouton poubelle
    public void trashButtonClick(View view) {
        // Récupérer la ligne parente (l'item de la liste)
        View parentRow = (View) view.getParent();

        // Trouver la position de l'item cliqué dans la ListView
        int position = seancesListView.getPositionForView(parentRow);

        // Vérifier que la position est valide
        if (position != ListView.INVALID_POSITION) {
            Log.d("TrashButtonClick", "Position de l'item cliqué : " + position);

            // Récupérer l'adaptateur et l'item à supprimer
            final ArrayAdapter<Seance> adapter = (ArrayAdapter<Seance>) seancesListView.getAdapter();
            final Seance itemToRemove = adapter.getItem(position);

            // Vérifier que l'item à supprimer n'est pas null
            if (itemToRemove != null) {
                // Créer un AlertDialog pour la confirmation
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
                alertdialog.setTitle("Confirmation");
                alertdialog.setMessage("Êtes-vous sûr de vouloir supprimer cette séance ?");

                // Créer un OnClickListener pour le bouton "Oui"
                DialogInterface.OnClickListener yesClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Supprimer l'item de l'adaptateur
                        adapter.remove(itemToRemove);
                        adapter.notifyDataSetChanged();

                        // Récupérer l'ID de la séance à supprimer
                        int seanceId = itemToRemove.getIdSeance();
                        Log.d("TrashButtonClick", "ID de la séance à supprimer : " + seanceId);

                        // Démarrer un thread pour supprimer la séance côté serveur
                        MyThreadCommand mythreadcommand = new MyThreadCommand(client, "deleteseance:" + seanceId, ManageSeancesActivity.this, seancesListView, textStatus);
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