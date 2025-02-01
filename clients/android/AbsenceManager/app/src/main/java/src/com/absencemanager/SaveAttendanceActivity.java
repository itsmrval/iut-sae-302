package src.com.absencemanager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SaveAttendanceActivity extends AppCompatActivity implements AttendanceCallback {

    private Client client;
    private ListView studentsListView;
    private EditText textStatus;
    private Button validateButton;
    private int seanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_attendance);

        client = Client.getInstance();
        studentsListView = findViewById(R.id.studentsListView);
        textStatus = new EditText(this);
        validateButton = findViewById(R.id.validateButton);

        seanceId = getIntent().getIntExtra("seance_id", -1);
        Log.d("SeanceDetail", "ID de la séance reçue : " + seanceId);

        refreshStudentsList();

        validateButton.setOnClickListener(new ValidateButtonClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStudentsList();
    }

    // Cette méthode est appelée quand on reçoit la valeur d'attendance du serveur
    @Override
    public void onAttendanceReceived(int attendance, int position) {
        View itemView = studentsListView.getChildAt(position);
        if (itemView != null) {
            CheckBox checkBox = itemView.findViewById(R.id.checkBoxSelect);
            if (checkBox != null) {
                checkBox.setChecked(attendance == 1);
            }
        }
    }

    private void refreshStudentsList() {
        MyThreadCommand myThreadCommand = new StudentListThreadCommand(client, "studentsinseances", this, studentsListView, textStatus);
        myThreadCommand.start();
    }

    private class StudentListThreadCommand extends MyThreadCommand {
        public StudentListThreadCommand(Client client, String command, Activity activity, ListView studentsListView, EditText textStatus) {
            super(client, command, activity, studentsListView, textStatus);
        }

        @Override
        public void run() {
            super.run();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (students != null) {
                        for (int i = 0; i < students.size(); i++) {
                            Etudiant etudiant = students.get(i);
                            MyThreadCommand myThreadCommand = new MyThreadCommand(
                                    client,
                                    "getattendancestudent:" + seanceId + ":" + etudiant.getIdEtudiant(),
                                    SaveAttendanceActivity.this,
                                    studentsListView,
                                    textStatus,
                                    SaveAttendanceActivity.this,
                                    i
                            );
                            myThreadCommand.start();
                        }
                    }
                }
            });
        }
    }

    private class ValidateButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < studentsListView.getCount(); i++) {
                View itemView = studentsListView.getChildAt(i);
                if (itemView != null) {
                    CheckBox checkBox = itemView.findViewById(R.id.checkBoxSelect);
                    Etudiant etudiant = (Etudiant) studentsListView.getItemAtPosition(i);
                    int etudiantId = etudiant.getIdEtudiant();

                    if (checkBox.isChecked()) {
                        String etudiantNom = etudiant.getNomEtudiant();
                        Log.d("SelectedStudent", "ID: " + etudiantId + ", Nom: " + etudiantNom);
                        MyThreadCommand mythreadattendance = new MyThreadCommand(client, "setattendance:" + seanceId + ":" + etudiantId + ":1", SaveAttendanceActivity.this, studentsListView, textStatus);
                        mythreadattendance.start();
                    } else {
                        Log.d("SelectedStudent", "ID: " + etudiantId);
                        MyThreadCommand mythreadattendance = new MyThreadCommand(client, "setattendance:" + seanceId + ":" + etudiantId + ":0", SaveAttendanceActivity.this, studentsListView, textStatus);
                        mythreadattendance.start();
                    }
                }
            }
            Toast.makeText(SaveAttendanceActivity.this, "Validation effectuée", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}