package src.com.absencemanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class CreateSeanceActivity extends AppCompatActivity {

    private EditText editTextSeanceName;
    private Button buttonChooseDateTime;
    private TextView textViewDateTime;
    private Button buttonValidate;
    private Calendar selectedDateTime;

    private boolean isDateTimeSelected = false; // Indicateur pour vérifier si une date et une heure ont été sélectionnées

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_seance);

        // Initialisation des vues
        editTextSeanceName = findViewById(R.id.editTextSeanceName);
        buttonChooseDateTime = findViewById(R.id.buttonChooseDateTime);
        textViewDateTime = findViewById(R.id.textViewDateTime);
        buttonValidate = findViewById(R.id.buttonValidate);


        // Initialisation du calendrier
        selectedDateTime = Calendar.getInstance();

        // Gestion du clic sur le bouton pour choisir la date et l'heure
        buttonChooseDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        // Gestion du clic sur le bouton de validation
        buttonValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seanceName = editTextSeanceName.getText().toString();

                // Vérifier que le nom de la séance n'est pas vide
                if (seanceName.isEmpty()) {
                    Toast.makeText(CreateSeanceActivity.this, "Veuillez entrer un nom de séance", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Vérifier qu'une date et une heure ont été sélectionnées
                if (!isDateTimeSelected) {
                    Toast.makeText(CreateSeanceActivity.this, "Veuillez sélectionner une date et une heure", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convertir la date et l'heure sélectionnées en temps Unix (timestamp en secondes)
                long unixTime = selectedDateTime.getTimeInMillis() / 1000;

                // Envoyer les données au serveur via un thread
                Client client = Client.getInstance();

                // Créez et démarrez le thread pour envoyer la commande au serveur
                MyThreadGet myThreadGet = new MyThreadGet(client, "createseance:" + seanceName + ":" + unixTime, CreateSeanceActivity.this, null, null);
                myThreadGet.start();


                try {
                    // Attendre que le thread termine son exécution
                    myThreadGet.join();
                    // Afficher un message de confirmation
                    Toast.makeText(CreateSeanceActivity.this, "Séance créée avec succès", Toast.LENGTH_SHORT).show();

                    // Fermer cette activité et revenir à l'activité précédente (ManageSeancesActivity)
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // Gérer l'interruption du thread
                    Toast.makeText(CreateSeanceActivity.this, "Erreur lors de la création de la séance", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showDateTimePicker() {
        // Afficher le DatePickerDialog pour choisir la date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDateTime.set(Calendar.YEAR, year);
                        selectedDateTime.set(Calendar.MONTH, month);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Après avoir choisi la date, afficher le TimePickerDialog pour choisir l'heure
                        showTimePicker();
                    }
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        // Afficher le TimePickerDialog pour choisir l'heure
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDateTime.set(Calendar.MINUTE, minute);

                        // Mettre à jour le TextView avec la date et l'heure sélectionnées
                        updateDateTimeTextView();

                        // Marquer que l'utilisateur a sélectionné une date et une heure
                        isDateTimeSelected = true;
                    }
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateDateTimeTextView() {
        // Formater la date et l'heure pour l'affichage
        String dateTime = android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", selectedDateTime).toString();
        textViewDateTime.setText(dateTime);
    }
}