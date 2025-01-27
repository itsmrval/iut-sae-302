package src.com.client_java;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.util.List;


public class ManageStudents {
    private JFrame frame;
    private Client client;
    private JPanel cards;
    private CardLayout cardLayout;

    public ManageStudents(Client client) {
        this.client = client;
        initialize();
    }




    private void initialize() {
        frame = new JFrame("Manage Students");
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setLayout(new BorderLayout());
    
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
    
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);
    
        // Panneau pour les boutons en haut
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Color.DARK_GRAY);
    
        addTopPanelButtons(topPanel);

        // Ajouter le panneau du haut au panneau principal
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Ajouter un panneau pour les séances au centre
        addStudentsPanel(mainPanel);

        // Ajouter le panneau principal à la carte
        cards.add(mainPanel, "mainPanel");
        

        //###############################################################################################################

        
        // Panel secondaire
        JPanel secondPanel = new JPanel(new BorderLayout());
        secondPanel.setBackground(Color.DARK_GRAY);

        // Utiliser un FlowLayout aligné à gauche pour topPanelTwo
        JPanel topPanelTwo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanelTwo.setBackground(Color.DARK_GRAY);

        JButton buttonBackMain = new JButton("Back");
        buttonBackMain.setPreferredSize(new Dimension(120, 30));
        buttonBackMain.setBackground(Color.GRAY);
        buttonBackMain.setForeground(Color.WHITE);
        buttonBackMain.setFocusPainted(false);
        buttonBackMain.setBorder(new LineBorder(Color.WHITE, 1, true));
        buttonBackMain.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cards, "mainPanel");
            }
        });

        topPanelTwo.add(buttonBackMain);
        secondPanel.add(topPanelTwo, BorderLayout.NORTH);

        // Ajouter un panneau au centre du second panel pour les champs
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.DARK_GRAY);

        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.insets = new Insets(10, 10, 10, 10);
        gbcCenter.gridx = 0;
        gbcCenter.gridy = 0;
        gbcCenter.anchor = GridBagConstraints.LINE_START;

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE);
        centerPanel.add(nameLabel, gbcCenter);

        gbcCenter.gridx = 1;
        JTextField nameField = new JTextField(15);
        nameField.setPreferredSize(new Dimension(200, 30));
        centerPanel.add(nameField, gbcCenter);


        gbcCenter.gridx = 0;
        gbcCenter.gridy++;
        gbcCenter.gridwidth = 2;
        gbcCenter.anchor = GridBagConstraints.CENTER;
        JButton validateButton = new JButton("Validate");
        validateButton.setPreferredSize(new Dimension(120, 30));
        validateButton.setBackground(Color.GRAY);
        validateButton.setForeground(Color.GREEN);
        validateButton.setFocusPainted(false);
        validateButton.setBorder(new LineBorder(Color.WHITE, 1, true));
        validateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim(); // Trimmer les espaces pour le nom

        
                if (name.isEmpty()) {
                    System.out.println("Veuillez entrer un nom.");
                    return;
                }
 
        
                
                // Appel de la méthode createStudent
                boolean result = client.createStudent(name);
                if (result) {
                    reloadMainPanel(mainPanel);
                    cardLayout.show(cards, "mainPanel");
                }           
                                
            }
        });
        centerPanel.add(validateButton, gbcCenter);

        secondPanel.add(centerPanel, BorderLayout.CENTER);

        cards.add(secondPanel, "secondPanel");

        frame.getContentPane().add(cards, BorderLayout.CENTER);
        frame.setVisible(true);



    }


    public void addStudentsPanel(JPanel mainPanel) {
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

            JLabel labelStudent = new JLabel(student.getNomEtudiant());
            labelStudent.setForeground(Color.WHITE);

            JButton studentButton = new JButton("Delete Student");
            studentButton.setBackground(Color.GRAY);
            studentButton.setForeground(Color.RED);
            studentButton.setFocusPainted(false);
            studentButton.setBorder(new LineBorder(Color.WHITE, 1, true)); // Rounded border
            studentButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    client.deleteStudent(student.getIdEtudiant());
                    // Supprimer uniquement le panneau correspondant de studentsContainer
                    studentsContainer.remove(studentPanel);
                    studentsContainer.revalidate();
                    studentsContainer.repaint();
            
                }
            });

            GridBagConstraints gbcLabel = new GridBagConstraints();
            gbcLabel.gridx = 0;
            gbcLabel.gridy = 0;
            gbcLabel.anchor = GridBagConstraints.WEST;
            studentPanel.add(labelStudent, gbcLabel);

            GridBagConstraints gbcSpace = new GridBagConstraints();
            gbcSpace.gridx = 1;
            gbcSpace.gridy = 0;
            gbcSpace.weightx = 1.0; // Add space between label and button
            studentPanel.add(Box.createHorizontalStrut(20), gbcSpace); // Adjust the width as needed

            GridBagConstraints gbcButton = new GridBagConstraints();
            gbcButton.gridx = 2;
            gbcButton.gridy = 0;
            gbcButton.anchor = GridBagConstraints.EAST;
            studentPanel.add(studentButton, gbcButton);

            gbc.gridy++;
            studentsContainer.add(studentPanel, gbc);
        }

        mainPanel.add(studentsContainer, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }




    // Ajouter le bouton "Back" au panneau du haut
    private void addTopPanelButtons(JPanel topPanel) {
        // Créer et configurer le bouton "Back"
        JButton buttonBack = new JButton("Back");
        buttonBack.setPreferredSize(new Dimension(120, 30));
        buttonBack.setBackground(Color.GRAY);
        buttonBack.setForeground(Color.WHITE);
        buttonBack.setFocusPainted(false);
        buttonBack.setBorder(new LineBorder(Color.WHITE, 1, true));
        buttonBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        GridBagConstraints gbcBack = new GridBagConstraints();
        gbcBack.insets = new Insets(10, 10, 10, 10);
        gbcBack.gridx = 0;
        gbcBack.gridy = 0;
        gbcBack.anchor = GridBagConstraints.WEST;
        topPanel.add(buttonBack, gbcBack);

        // Créer et configurer le bouton "Create Student"
        JButton buttonCreateStudent = new JButton("Create Student");
        buttonCreateStudent.setPreferredSize(new Dimension(120, 30));
        buttonCreateStudent.setBackground(Color.GRAY);
        buttonCreateStudent.setForeground(Color.GREEN);
        buttonCreateStudent.setFocusPainted(false);
        buttonCreateStudent.setBorder(new LineBorder(Color.WHITE, 1, true));
        buttonCreateStudent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cards, "secondPanel");
            }
        });

        GridBagConstraints gbcCreate = new GridBagConstraints();
        gbcCreate.insets = new Insets(10, 10, 10, 10);
        gbcCreate.gridx = 1;
        gbcCreate.gridy = 0;
        gbcCreate.anchor = GridBagConstraints.EAST;
        gbcCreate.weightx = 1.0; // Pour pousser le bouton à droite
        topPanel.add(buttonCreateStudent, gbcCreate);
    }

    
    private void reloadMainPanel(JPanel mainPanel) {
        mainPanel.removeAll();
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Color.DARK_GRAY);
        addTopPanelButtons(topPanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        addStudentsPanel(mainPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }


}