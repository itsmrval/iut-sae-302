package com.absencemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class TakeAttendanceActivity extends AppCompatActivity {

    private Client client; // Déclarer client comme attribut
    private ListView seancesListView; // Déclarer seancesListView comme attribut
    private EditText textStatus; // Déclarer textStatus comme attribut

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_take_attendance);

        client = Client.getInstance(); // Initialiser client
        seancesListView = findViewById(R.id.seancesListView); // Initialiser seancesListView
        textStatus = new EditText(this); // Initialiser textStatus

        // Rafraîchir la liste des séances
        refreshSeancesList();

        // Associer l'écouteur de clic à la ListView
        seancesListView.setOnItemClickListener(new SeanceItemClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSeancesList(); // Rafraîchir la liste des séances
    }

    private void refreshSeancesList() {
        // Créer et démarrer un thread pour récupérer les séances
        MyThreadCommand mythreadcommand = new MyThreadCommand(client, "seancesattendance", this, seancesListView, textStatus);
        mythreadcommand.start();
    }

    // Classe interne pour gérer le clic sur un élément de la ListView
    private class SeanceItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Récupérer l'objet Seance cliqué
            Seance selectedSeance = (Seance) parent.getItemAtPosition(position);

            if (selectedSeance != null) {
                int seanceId = selectedSeance.getIdSeance();
                Log.d("SeanceClick", "ID de la séance cliquée : " + seanceId);

                // Vous pouvez transmettre cet ID à l'activité suivante
                Intent intent = new Intent(TakeAttendanceActivity.this, SaveAttendanceActivity.class);
                intent.putExtra("seance_id", seanceId);
                startActivity(intent);
            }
        }
    }


}