package com.absence.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Configuration des éléments de l'interface
        TextView srvTextView = findViewById(R.id.text_srv_connected);
        srvTextView.setText("SRV CONNECTED | " + getIntent().getStringExtra("serverDetails"));

        Button btnDisconnect = findViewById(R.id.btn_disconnect);
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retour à l'écran d'accueil
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish(); // Ferme cette activité
            }
        });

        Button btnManageStudents = findViewById(R.id.btn_manage_students);
        btnManageStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ManageStudentsActivity.class);
                startActivity(intent);

            }
        });



        Button btnManageSeances = findViewById(R.id.btn_manage_seances);
        btnManageSeances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer l'activité pour gérer les séances
                startActivity(new Intent(HomeActivity.this, ManageSeancesActivity.class));
            }
        });


        Button btnViewAbsence = findViewById(R.id.btn_view_absence);
        btnViewAbsence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer l'activité pour gérer les séances
                startActivity(new Intent(HomeActivity.this, ViewAttendance.class));
            }
        });

        Button btnTakeAbsence = findViewById(R.id.btn_take_absence);
        btnTakeAbsence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer l'activité pour gérer les séances
                startActivity(new Intent(HomeActivity.this, Attendance.class ));
            }
        });
    }
}
