package src.com.absencemanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewAttendanceActivity extends AppCompatActivity implements AttendanceCallback {

    private Client client;
    private ListView studentsListView;
    private EditText textStatus;
    private int seanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        client = Client.getInstance();
        studentsListView = findViewById(R.id.studentsListView);
        textStatus = new EditText(this); // Initialiser textStatus

        seanceId = getIntent().getIntExtra("seance_id", -1);

        refreshStudentsList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStudentsList();
    }

    @Override
    public void onAttendanceReceived(int attendance, int position) {
        View itemView = studentsListView.getChildAt(position);
        if (itemView != null) {
            TextView statusText = itemView.findViewById(R.id.textViewStatus);
            if (statusText != null) {
                String status = attendance == 1 ? "Présent" : "Absent";
                statusText.setText(status);
            }
        }
    }

    private void refreshStudentsList() {
        StudentListThreadCommand myThreadCommand = new StudentListThreadCommand(client, "viewattendance", this, studentsListView, textStatus);
        myThreadCommand.start();
    }

    // Classe interne pour gérer le thread de mise à jour de l'UI
    private class UIUpdateRunnable implements Runnable {
        private List<Etudiant> students;

        public UIUpdateRunnable(List<Etudiant> students) {
            this.students = students;
        }

        @Override
        public void run() {
            if (students != null) {
                for (int i = 0; i < students.size(); i++) {
                    Etudiant etudiant = students.get(i);
                    MyThreadCommand myThreadCommand = new MyThreadCommand(
                            client,
                            "getattendancestudent:" + seanceId + ":" + etudiant.getIdEtudiant(),
                            ViewAttendanceActivity.this,
                            studentsListView,
                            textStatus,
                            ViewAttendanceActivity.this,
                            i
                    );
                    myThreadCommand.start();
                }
            }
        }
    }

    private class StudentListThreadCommand extends MyThreadCommand {
        public StudentListThreadCommand(Client client, String command, Activity activity, ListView studentsListView, EditText textStatus) {
            super(client, command, activity, studentsListView, textStatus);
        }

        @Override
        public void run() {
            super.run();
            UIUpdateRunnable updateRunnable = new UIUpdateRunnable(students);
            activity.runOnUiThread(updateRunnable);
        }
    }
}
