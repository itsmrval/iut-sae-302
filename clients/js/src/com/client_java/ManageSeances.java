package src.com.client_java;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Date;
import com.toedter.calendar.JDateChooser;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class ManageSeances {
    private JFrame frame;
    private Client client;
    private JPanel cards;
    private CardLayout cardLayout;
    private Connected connected;

    public ManageSeances(Client client, Connected connected) {
        this.client = client;
        this.connected = connected;
        initialize();
    }




    private void initialize() {
        frame = new JFrame("Manage Seances");
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
        addSeancePanel(mainPanel);

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
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setForeground(Color.WHITE);
        centerPanel.add(dateLabel, gbcCenter);

        // JDateChooser for Date Selection
        gbcCenter.gridx = 1; // Positionner à la droite du label "Date"
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(120, 30));
        centerPanel.add(dateChooser, gbcCenter);

        gbcCenter.gridx = 2; // Position pour le time spinner
        SpinnerDateModel timeModel = new SpinnerDateModel();
        JSpinner timeSpinner = new JSpinner(timeModel);
        timeSpinner.setPreferredSize(new Dimension(80, 30));

        // Format de l'affichage du temps
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        SimpleDateFormat sdf = ((SimpleDateFormat) timeEditor.getFormat());
        sdf.applyPattern("HH:mm");
        timeSpinner.setEditor(timeEditor);

        centerPanel.add(timeSpinner, gbcCenter);

        // Making JDateChooser text field not editable
        JTextField dateTextField = ((JTextField) dateChooser.getDateEditor().getUiComponent());
        dateTextField.setEditable(false); // Disable direct text entry
        dateTextField.setForeground(Color.BLACK); // Text color
        dateTextField.setBackground(Color.WHITE); // Background color

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
                Date selectedDate = dateChooser.getDate();
                Calendar calendar = Calendar.getInstance();
        
                if (name.isEmpty()) {
                    System.out.println("Veuillez entrer un nom.");
                    return;
                }
        
                if (selectedDate == null) {
                    System.out.println("Veuillez sélectionner une date.");
                    return;
                }
        
                // Obtenir l'heure depuis le JSpinner
                Date time = (Date) timeSpinner.getValue();
                Calendar timeCalendar = Calendar.getInstance();
                timeCalendar.setTime(time);
        
                // Combiner la date et l'heure
                calendar.setTime(selectedDate);
                calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
        
                Date finalDateTime = calendar.getTime();
        
                // Conversion de la date complète en timestamp Unix
                long unixTime = finalDateTime.getTime() / 1000L;
                
                // Appel de la méthode createSeance
                boolean result = client.createSeance(name, unixTime);
                if (result) {
                    reloadMainPanel(mainPanel); // Recharger le panneau principal
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


    public void addSeancePanel(JPanel mainPanel) {
        List<Seance> seances = client.getSeances(); // Assuming this method exists in Client class

        JPanel seancesContainer = new JPanel();
        seancesContainer.setLayout(new GridBagLayout());
        seancesContainer.setBackground(Color.DARK_GRAY);
        seancesContainer.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding around the container

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        for (Seance seance : seances) {
            JPanel seancePanel = new JPanel();
            seancePanel.setBackground(Color.DARK_GRAY);
            seancePanel.setLayout(new GridBagLayout());
            seancePanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding around each seance panel

            // Convert Unix time to human-readable date and time
            long unixSeconds = seance.getUnixTime();
            Instant instant = Instant.ofEpochSecond(unixSeconds);
            String formattedDate = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneId.systemDefault())
                    .format(instant);

            JLabel labelSeance = new JLabel(seance.getNomSeance() + " - " + formattedDate);
            labelSeance.setForeground(Color.WHITE);

            JButton seanceButton = new JButton("Delete Seance");
            seanceButton.setBackground(Color.GRAY);
            seanceButton.setForeground(Color.RED);
            seanceButton.setFocusPainted(false);
            seanceButton.setBorder(new LineBorder(Color.WHITE, 1, true)); // Rounded border
            seanceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    client.deleteSeance(seance.getIdSeance());
                    // Supprimer uniquement le panneau correspondant de seancesContainer
                    seancesContainer.remove(seancePanel);
                    seancesContainer.revalidate();
                    seancesContainer.repaint();
            
                }
            });

            GridBagConstraints gbcLabel = new GridBagConstraints();
            gbcLabel.gridx = 0;
            gbcLabel.gridy = 0;
            gbcLabel.anchor = GridBagConstraints.WEST;
            seancePanel.add(labelSeance, gbcLabel);

            GridBagConstraints gbcSpace = new GridBagConstraints();
            gbcSpace.gridx = 1;
            gbcSpace.gridy = 0;
            gbcSpace.weightx = 1.0; // Add space between label and button
            seancePanel.add(Box.createHorizontalStrut(20), gbcSpace); // Adjust the width as needed

            GridBagConstraints gbcButton = new GridBagConstraints();
            gbcButton.gridx = 2;
            gbcButton.gridy = 0;
            gbcButton.anchor = GridBagConstraints.EAST;
            seancePanel.add(seanceButton, gbcButton);

            gbc.gridy++;
            seancesContainer.add(seancePanel, gbc);
        }

        mainPanel.add(seancesContainer, BorderLayout.CENTER);
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
                connected.refreshSeances(); // Rafraîchir la liste des séances

            }
        });

        GridBagConstraints gbcBack = new GridBagConstraints();
        gbcBack.insets = new Insets(10, 10, 10, 10);
        gbcBack.gridx = 0;
        gbcBack.gridy = 0;
        gbcBack.anchor = GridBagConstraints.WEST;
        topPanel.add(buttonBack, gbcBack);

        // Créer et configurer le bouton "Create Seance"
        JButton buttonCreateSeance = new JButton("Create Seance");
        buttonCreateSeance.setPreferredSize(new Dimension(120, 30));
        buttonCreateSeance.setBackground(Color.GRAY);
        buttonCreateSeance.setForeground(Color.GREEN);
        buttonCreateSeance.setFocusPainted(false);
        buttonCreateSeance.setBorder(new LineBorder(Color.WHITE, 1, true));
        buttonCreateSeance.addActionListener(new ActionListener() {
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
        topPanel.add(buttonCreateSeance, gbcCreate);
    }

    
    private void reloadMainPanel(JPanel mainPanel) {
        mainPanel.removeAll();
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Color.DARK_GRAY);
        addTopPanelButtons(topPanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        addSeancePanel(mainPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }


}