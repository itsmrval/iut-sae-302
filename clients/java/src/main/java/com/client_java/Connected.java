package com.client_java;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Connected {
    private JFrame frame;
    private Client client;
    private JPanel mainPanel;
    private JPanel seancesContainer;

    public Connected(Client client) {
        this.client = client;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Absence Manager");
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setLayout(new BorderLayout());

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);

        // Panneau pour les boutons en haut
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Color.DARK_GRAY);
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.insets = new Insets(10, 10, 10, 10);
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;
        gbcTop.anchor = GridBagConstraints.NORTHWEST;

        // Créer et configurer le bouton "Disconnect"
        JButton buttonDisconnect = new JButton("Disconnect");
        buttonDisconnect.setPreferredSize(new Dimension(120, 30)); // Taille carrée et petite
        buttonDisconnect.setBackground(Color.GRAY);
        buttonDisconnect.setForeground(Color.WHITE);
        buttonDisconnect.setFocusPainted(false); // Désactiver l'effet de peinture
        buttonDisconnect.setBorder(new LineBorder(Color.WHITE, 1, true));

        ActionListener actionlistendisconnect = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    client.closeResources();
                    frame.dispose();
                    new Accueil();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        buttonDisconnect.addActionListener(actionlistendisconnect);

        // Ajouter le bouton "Disconnect" au panneau du haut
        topPanel.add(buttonDisconnect, gbcTop);

        // Créer et configurer le label "SRV"
        JLabel labelSrv = new JLabel("SRV CONNECTED | " + client.getServerIP() + ":" + client.getServerPort());
        labelSrv.setForeground(Color.WHITE);
        gbcTop.gridx = 1;
        gbcTop.insets = new Insets(15, 10, 10, 10); // Ajouter un espace vertical pour centrer le label
        topPanel.add(labelSrv, gbcTop);

        // Créer et configurer le bouton "Manage Students"
        JButton buttonStudent = new JButton("Manage Students");
        buttonStudent.setPreferredSize(new Dimension(120, 30)); // Taille carrée et petite
        buttonStudent.setBackground(Color.GRAY);
        buttonStudent.setForeground(Color.WHITE);
        buttonStudent.setFocusPainted(false); // Désactiver l'effet de peinture
        buttonStudent.setBorder(new LineBorder(Color.WHITE, 1, true));
        
        ActionListener actionlistenstudent = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    new ManageStudents(client);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        buttonStudent.addActionListener(actionlistenstudent);

        // Ajouter le bouton "Manage Students" au panneau du haut
        gbcTop.gridx = 2;
        gbcTop.insets = new Insets(10, 10, 10, 10); // Réinitialiser les insets pour le bouton
        topPanel.add(buttonStudent, gbcTop);

        // Créer et configurer le bouton "Manage Seances"
        JButton buttonSeance = new JButton("Manage Seances");
        buttonSeance.setPreferredSize(new Dimension(120, 30)); // Taille carrée et petite
        buttonSeance.setBackground(Color.GRAY);
        buttonSeance.setForeground(Color.WHITE);
        buttonSeance.setFocusPainted(false); // Désactiver l'effet de peinture
        buttonSeance.setBorder(new LineBorder(Color.WHITE, 1, true));

        ActionListener actionlistenseance = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    new ManageSeances(client, Connected.this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        buttonSeance.addActionListener(actionlistenseance);

        // Ajouter le bouton "Manage Seances" au panneau du haut
        gbcTop.gridx = 3;
        topPanel.add(buttonSeance, gbcTop);

        // Ajouter le panneau du haut au panneau principal
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Ajouter un panneau pour les séances au centre
        addSeancePanel(mainPanel);

        // Ajouter le panneau principal à la fenêtre
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void addSeancePanel(JPanel mainPanel) {

        // Récupérer la liste des séances du client
        List<Seance> seances = client.getSeances();

        seancesContainer = new JPanel();
        seancesContainer.setLayout(new GridBagLayout());
        seancesContainer.setBackground(Color.DARK_GRAY);
        seancesContainer.setBorder(new EmptyBorder(20, 20, 20, 20)); // Ajouter un padding autour du conteneur

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Créer un panneau pour chaque séance
        for (Seance seance : seances) {
            JPanel seancePanel = new JPanel();
            seancePanel.setBackground(Color.DARK_GRAY);
            seancePanel.setLayout(new GridBagLayout());
            seancePanel.setBorder(new EmptyBorder(10, 10, 10, 10)); 

            // Convertir le temps Unix en date lisible
            long unixSeconds = seance.getUnixTime();
            Instant instant = Instant.ofEpochSecond(unixSeconds);
            String formattedDate = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneId.systemDefault())
                    .format(instant);

            JLabel labelSeance = new JLabel(seance.getNomSeance() + " - " + formattedDate);
            labelSeance.setForeground(Color.WHITE);

            // Créer et configurer le bouton "Take Attendance"
            JButton seanceButton = new JButton("Take Attendance");
            seanceButton.setBackground(Color.GRAY);
            seanceButton.setForeground(Color.WHITE);
            seanceButton.setFocusPainted(false);
            seanceButton.setBorder(new LineBorder(Color.WHITE, 1, true)); // Rounded border
            seanceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Handle button click
                    new Attendance(client, seance);
                }
            });

            // Créer et configurer le bouton "View Attendance"
            JButton seanceViewAttendance = new JButton("View Attendance");
            seanceViewAttendance.setBackground(Color.GRAY);
            seanceViewAttendance.setForeground(Color.WHITE);
            seanceViewAttendance.setFocusPainted(false);
            seanceViewAttendance.setBorder(new LineBorder(Color.WHITE, 1, true)); // Rounded border
            seanceViewAttendance.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("View attendance for seance: " + seance.getNomSeance());
                    new ViewAttendance(client, seance);
                }
            });

            // Ajouter les composants au panneau de la séance avec GridBagConstraints
            GridBagConstraints gbcLabel = new GridBagConstraints();
            gbcLabel.gridx = 0;
            gbcLabel.gridy = 0;
            gbcLabel.anchor = GridBagConstraints.WEST;
            seancePanel.add(labelSeance, gbcLabel);

            GridBagConstraints gbcSpace = new GridBagConstraints();
            gbcSpace.gridx = 1;
            gbcSpace.gridy = 0;
            gbcSpace.weightx = 1.0; // Ajuster la largeur de l'espace
            seancePanel.add(Box.createHorizontalStrut(20), gbcSpace); 

            GridBagConstraints gbcButton = new GridBagConstraints();
            gbcButton.gridx = 2;
            gbcButton.gridy = 0;
            gbcButton.anchor = GridBagConstraints.EAST;
            seancePanel.add(seanceButton, gbcButton);

            // Ajouter un espace horizontal entre les boutons
            GridBagConstraints gbcSpaceBetweenButtons = new GridBagConstraints();
            gbcSpaceBetweenButtons.gridx = 3;
            gbcSpaceBetweenButtons.gridy = 0;
            gbcSpaceBetweenButtons.weightx = 0.1; // Ajuster la largeur de l'espace
            seancePanel.add(Box.createHorizontalStrut(20), gbcSpaceBetweenButtons); 

            GridBagConstraints gbcButtonView = new GridBagConstraints();
            gbcButtonView.gridx = 4;
            gbcButtonView.gridy = 0;
            gbcButtonView.anchor = GridBagConstraints.EAST;
            seancePanel.add(seanceViewAttendance, gbcButtonView);

            gbc.gridy++;
            seancesContainer.add(seancePanel, gbc);
        }

        mainPanel.add(seancesContainer, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Method to refresh the seances list
    public void refreshSeances() {
        mainPanel.remove(seancesContainer);
        addSeancePanel(mainPanel);
    }
    
}