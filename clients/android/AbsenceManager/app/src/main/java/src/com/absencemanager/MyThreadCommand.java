package src.com.absencemanager;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

class MyThreadCommand extends Thread {

    private final String command;
    private Client client;
    protected Activity activity;
    private EditText textstatus;
    private ListView listView;
    private ArrayAdapter<?> adapter;
    private AttendanceCallback callback;
    private int position;

    protected List<Seance> seances;
    protected List<Etudiant> students;

    // Constructeur normal
    public MyThreadCommand(Client client, String command, Activity activity, ListView listView, EditText textstatus) {
        this.client = client;
        this.command = command;
        this.activity = activity;
        this.listView = listView;
        this.textstatus = textstatus;
    }

    // Nouveau constructeur avec callback et position
    public MyThreadCommand(Client client, String command, Activity activity, ListView listView,
                           EditText textstatus, AttendanceCallback callback, int position) {
        this(client, command, activity, listView, textstatus);
        this.callback = callback;
        this.position = position;
    }

    @Override
    public void run() {
        if ("students".equals(command)) {
            students = client.getStudents();
            updateListView(students, "");

        } else if ("studentsinseances".equals(command)) {
            students = client.getStudents();
            updateListView(students, "studentsinseances");
        } else if ("viewattendance".equals(command)) {
            students = client.getStudents();
            updateListView(students, "viewattendance");

        } else if ("seances".equals(command)) {
            seances = client.getSeances();
            updateListView(seances, "seances");

        } else if ("seancesattendance".equals(command)) {
            seances = client.getSeances();
            updateListView(seances, "attendance");

        } else if (command != null && command.startsWith("deleteseance:")) {
            String[] parts = command.split(":");
            if (parts.length == 2) {
                int seanceId = Integer.parseInt(parts[1]);
                client.deleteSeance(seanceId);
            }

        } else if (command != null && command.startsWith("deletestudent:")) {
            String[] parts = command.split(":");
            if (parts.length == 2) {
                int studentId = Integer.parseInt(parts[1]);
                client.deleteStudent(studentId);
            }

        } else if (command != null && command.startsWith("createseance:")) {
            Log.d("MyThreadGet", "Commande : " + command);
            String[] parts = command.split(":");
            if (parts.length == 3) {
                String seanceName = parts[1];
                long unixTime = Long.parseLong(parts[2]);
                client.createSeance(seanceName, unixTime);
            }

        } else if (command != null && command.startsWith("createstudent:")) {
            Log.d("MyThreadGet", "Commande : " + command);
            String[] parts = command.split(":");
            if (parts.length == 2) {
                String studentName = parts[1];
                client.createStudent(studentName);
            }

        } else if (command != null && command.startsWith("getattendancestudent:")) {
            Log.d("MyThreadGet", "Commande : " + command);
            String[] parts = command.split(":");
            if (parts.length == 3) {
                int seanceId = Integer.parseInt(parts[1]);
                int studentId = Integer.parseInt(parts[2]);
                final int attendance = client.getAttendanceStudent(seanceId, studentId);
                Log.d("MyThreadGet", "Attendance : " + attendance);

                if (callback != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onAttendanceReceived(attendance, position);
                        }
                    });
                }
            }

        } else if (command != null && command.startsWith("setattendance:")) {
            Log.d("MyThreadGet", "Commande : " + command);
            String[] parts = command.split(":");
            if (parts.length == 4) {
                int seanceId = Integer.parseInt(parts[1]);
                int studentId = Integer.parseInt(parts[2]);
                int absence = Integer.parseInt(parts[3]);
                client.setAbsence(seanceId, studentId, absence);
            }
        } else {
            updateStatusInUI(false);
        }
    }

    private void updateListView(final List<?> items, String choice) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (items != null && !items.isEmpty() && textstatus != null && listView != null) {
                    if (items.get(0) instanceof Etudiant && "studentsinseances".equals(choice)) {
                        adapter = new ArrayAdapter<Etudiant>(
                                activity,
                                R.layout.custom_list_take_attendance,
                                R.id.textViewItem,
                                (List<Etudiant>) items
                        );
                    } else if (items.get(0) instanceof Etudiant && "viewattendance".equals(choice)) {
                        adapter = new ArrayAdapter<Etudiant>(
                                activity,
                                R.layout.custom_list_item_attendance,
                                R.id.textViewItem,
                                (List<Etudiant>) items
                        );

                    } else if (items.get(0) instanceof Etudiant) {
                        adapter = new ArrayAdapter<Etudiant>(
                                activity,
                                R.layout.custom_list_item,
                                R.id.textViewItem,
                                (List<Etudiant>) items
                        );
                    } else if (items.get(0) instanceof Seance && "seances".equals(choice)) {
                        adapter = new ArrayAdapter<Seance>(
                                activity,
                                R.layout.custom_list_item,
                                R.id.textViewItem,
                                (List<Seance>) items
                        );
                    } else if (items.get(0) instanceof Seance && choice.equals("attendance")) {
                        adapter = new ArrayAdapter<Seance>(
                                activity,
                                R.layout.custom_list_item_attendance,
                                R.id.textViewItem,
                                (List<Seance>) items
                        );
                    }
                    listView.setAdapter(adapter);
                    updateStatusInUI(true);
                } else {
                    updateStatusInUI(false);
                }
            }
        });
    }

    private void updateStatusInUI(final boolean connected) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textstatus != null) {
                    textstatus.setText(connected ? "Connected" : "Not Connected");
                }
            }
        });
    }
}