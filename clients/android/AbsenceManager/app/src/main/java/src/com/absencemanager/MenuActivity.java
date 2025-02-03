package src.com.absencemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialisation des boutons
        LinearLayout viewAttendanceButton = findViewById(R.id.view_attendance_button);
        LinearLayout takeAttendanceButton = findViewById(R.id.take_attendance_button);
        LinearLayout manageStudentsButton = findViewById(R.id.manage_students_button);
        LinearLayout manageSeancesButton = findViewById(R.id.manage_seances_button);
        LinearLayout disconnectButton = findViewById(R.id.disconnect_button);

        // Obtenez une référence à la Toolbar depuis le layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Définissez la Toolbar comme la barre d'action pour cet Activity
        setSupportActionBar(toolbar);

        // Obtenez l'instance de votre client
        Client client = Client.getInstance();

        // Récupérez la chaîne de caractères depuis strings.xml
        String appName = getString(R.string.app_name);

        // Ajoutez une information supplémentaire
        String newAppName = appName + " | " + client.getServerIP() + ":" + client.getServerPort() + " |";

        // Définissez le nouveau titre à la Toolbar
        getSupportActionBar().setTitle(newAppName);

        // Définir le listener sur chaque bouton
        viewAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_view = new Intent(MenuActivity.this, ViewAttendanceSeanceActivity.class);
                startActivity(intent_view);
            }
        });
        takeAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_take = new Intent(MenuActivity.this, TakeAttendanceActivity.class);
                startActivity(intent_take);

            }
        });
        manageStudentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_manage = new Intent(MenuActivity.this, ManageStudentsActivity.class);
                startActivity(intent_manage);
            }
        });
        manageSeancesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_manage = new Intent(MenuActivity.this, ManageSeancesActivity.class);
                startActivity(intent_manage);
            }
        });
       disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       try {
                            client.closeResources();
                       } catch (Exception e) {
                           e.printStackTrace();
                           Log.d("Client", "Erreur lors de la déconnexion");
                       }
                        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }

                }).start();
            }
        });

    }
}
