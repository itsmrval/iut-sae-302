package com.client_java;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Accueil {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel connectPanel;

    private JTextField textFieldIP;
    private JTextField textFieldPort;

    public  Accueil() {
        // Créer la fenêtre
        frame = new JFrame("Absence Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Définir le fond sombre
        frame.getContentPane().setBackground(Color.DARK_GRAY);

        // Créer un CardLayout pour basculer entre les panneaux
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialiser les panneaux
        initializeConnectPanel();
        
        // Ajouter les panneaux au CardLayout
        mainPanel.add(connectPanel, "connectPanel");
       

        // Ajouter le panneau principal à la fenêtre
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Rendre la fenêtre visible
        frame.setVisible(true);
    }

    private void initializeConnectPanel() {
        // Panneau de connexion
        connectPanel = new JPanel();
        connectPanel.setBackground(Color.DARK_GRAY);
        connectPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Créer les champs de texte et les étiquettes
        JLabel labelIP = new JLabel("Server Address:");
        labelIP.setForeground(Color.WHITE);
        textFieldIP = new JTextField(10);

        JLabel labelPort = new JLabel("Server Port:");
        labelPort.setForeground(Color.WHITE);
        textFieldPort = new JTextField(5);

        // Créer le bouton de connexion
        JButton buttonConnect = new JButton("Connect");
        buttonConnect.setBackground(Color.GRAY);
        buttonConnect.setForeground(Color.WHITE);

        // Ajouter des actions au bouton de connexion

        ActionListener actionlisten = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ip = textFieldIP.getText();
                int port;
                try {
                    port = Integer.parseInt(textFieldPort.getText());
                    if (port < 0 || port > 65535) {
                        throw new NumberFormatException("Port number out of range");
                    }
                    Client client = Client.getInstance(); // Initialisation de l'objet Client

                    if (client.connectToServer(ip, port)) {

                        frame.dispose(); // Fermer la fenêtre de connexion
                        new Connected(client); // Ouvrir la fenêtre Connected
    
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to connect to the server", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid port number", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        buttonConnect.addActionListener(actionlisten);

        // Ajouter les composants au panneau de connexion avec GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        connectPanel.add(labelIP, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        connectPanel.add(textFieldIP, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        connectPanel.add(labelPort, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        connectPanel.add(textFieldPort, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        connectPanel.add(buttonConnect, gbc);

        // Ajouter le copyright en bas du connectPanel
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel copyright = new JLabel("© 2024 Absence Manager | All Rights Reserved\n BERSIN & BONNEAU & GOYA & PUCCETI");
        copyright.setForeground(Color.WHITE);
        connectPanel.add(copyright, gbc);
    }
}