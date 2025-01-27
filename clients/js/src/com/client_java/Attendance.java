package src.com.client_java;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Attendance {
    private JFrame frame;
    private Client client;
    private int seanceId;
    private String seanceName;
    private int unixTime;
    private Map<Etudiant, JCheckBox> studentCheckBoxMap;

    public Attendance(Client client, Seance seance) {
        this.client = client;
        this.seanceId = seance.getIdSeance();
        this.seanceName = seance.getNomSeance();
        this.unixTime = seance.getUnixTime();
        this.studentCheckBoxMap = new HashMap<>();
        initialize();
    }

    private void initialize() {
        // Créer la fenêtre
        Instant instant = Instant.ofEpochSecond(unixTime);
        String formattedDate = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault())
            .format(instant);

        frame = new JFrame(seanceName +" - " + formattedDate);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        // Définir le fond sombre
        frame.getContentPane().setBackground(Color.DARK_GRAY);

        // Créer un panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); 

        // Label pour afficher l'information de connexion
        JLabel label = new JLabel(seanceName + " - " + formattedDate);
        label.setForeground(Color.WHITE);  // Définit la couleur du texte en blanc
        label.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(label, BorderLayout.NORTH);

        // Ajouter un panneau pour le bouton "Validate"
        JPanel topLeftPanel = new JPanel(new BorderLayout());
        topLeftPanel.setBackground(Color.DARK_GRAY);

        // Créer et configurer le bouton "Validate"
        JButton validateButton = new JButton("Validate");
        validateButton.setPreferredSize(new Dimension(80, 30)); 
        validateButton.setBackground(Color.GRAY);
        validateButton.setForeground(Color.GREEN);
        validateButton.setFocusPainted(false);
        validateButton.setBorder(new LineBorder(Color.WHITE, 1, true));
        validateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Vérifier l'état des cases à cocher et effectuer des actions en fonction de leur état
                for (Map.Entry<Etudiant, JCheckBox> entry : studentCheckBoxMap.entrySet()) {
                    Etudiant student = entry.getKey();
                    JCheckBox checkBox = entry.getValue();
                    if (checkBox.isSelected()) {
                        // Action si la case est cochée
                        client.setAbsence(seanceId, student.getIdEtudiant(), 1);

                    } else {
                        // Action si la case n'est pas cochée
                        client.setAbsence(seanceId, student.getIdEtudiant(), 0);
                    }
                }
                // Fermeture de la fenêtre actuelle
                frame.dispose();
            }
        });

        // Ajouter le bouton "Validate" à son panneau
        topLeftPanel.add(validateButton, BorderLayout.NORTH);

        // Ajouter le panneau du bouton en haut à gauche
        mainPanel.add(topLeftPanel, BorderLayout.WEST);

        // Ajouter les panneaux des étudiants
        addStudentsPanel(mainPanel);

        // Ajouter le panneau principal à la fenêtre
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Rendre la fenêtre visible
        frame.setVisible(true);
    }

    private void addStudentsPanel(JPanel mainPanel) {
        List<Etudiant> students = client.getStudents(); // Assuming this method exists in Client class

        JPanel studentsContainer = new JPanel();
        studentsContainer.setLayout(new GridBagLayout());
        studentsContainer.setBackground(Color.DARK_GRAY);
        studentsContainer.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding around the container

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        for (Etudiant student : students) {
            JPanel studentPanel = new JPanel();
            studentPanel.setBackground(Color.DARK_GRAY);
            studentPanel.setLayout(new GridBagLayout());
            studentPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding around each student panel

            JLabel labelStudent = new JLabel("Student: " + student.getNomEtudiant());
            labelStudent.setForeground(Color.WHITE);


            JCheckBox checkBox = new JCheckBox();
            checkBox.setBackground(Color.DARK_GRAY);
            checkBox.setForeground(Color.WHITE);

            int presence = client.getAttendanceStudent(seanceId, student.getIdEtudiant());
            if (presence == 1) {
                checkBox.setSelected(true);
            }
            // Store the checkbox in the map
            studentCheckBoxMap.put(student, checkBox);

            GridBagConstraints gbcLabel = new GridBagConstraints();
            gbcLabel.gridx = 0;
            gbcLabel.gridy = 0;
            gbcLabel.anchor = GridBagConstraints.WEST;
            studentPanel.add(labelStudent, gbcLabel);

            GridBagConstraints gbcCheckBox = new GridBagConstraints();
            gbcCheckBox.gridx = 1;
            gbcCheckBox.gridy = 0;
            gbcCheckBox.anchor = GridBagConstraints.EAST;
            studentPanel.add(checkBox, gbcCheckBox);

            gbc.gridy++;
            studentsContainer.add(studentPanel, gbc);
        }

        mainPanel.add(studentsContainer, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}