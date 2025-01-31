package src.com.absencemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

        // Définir le listener sur chaque bouton
        viewAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_view = new Intent(MenuActivity.this, ViewAttendanceActivity.class);
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
                // A DEV
            }
        });

    }
}
