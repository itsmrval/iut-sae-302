package com.client_java;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;
import java.util.Date;
import java.util.ArrayList;
import java.util.TimeZone;

public class Main {
    
    // Déclarer les listes comme variables statiques
    private static List<Etudiant> etudiants = new ArrayList<>();
    private static List<Seance> seances = new ArrayList<>();
    private static Client client; 
    
    
    public static void main(String[] args) {
        // Affiche un message de bienvenue
        System.out.println("Bienvenue dans le gestionnaire d'absences");
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        
        // Boucle principale pour demander à l'utilisateur de choisir un mode d'exécution
        while (true) {
            choice = choisirModeExecution(scanner);
            
            switch (choice) {
                case 1:
                    executerModeConsole(scanner);
                    choice = 0; // Réinitialise le choix pour permettre de revenir au menu
                    break;
                    
                case 2:
                    executerModeGraphique();
                    return;
                    
                default:
                    break;
            }
        }
    }
    
    // Fonction pour choisir le mode d'exécution (console ou graphique)
    private static int choisirModeExecution(Scanner scanner) {
        int choice = 0;
        while (choice != 1 && choice != 2) {
            System.out.println("Veuillez choisir votre mode d'exécution:\n [1] - Console\n [2] - Interface graphique");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice != 1 && choice != 2) {
                    System.out.println("Choix incorrect. Veuillez réessayer.");
                }
            } else {
                System.out.println("Entrée invalide. Veuillez entrer un nombre.");
                scanner.next(); // Consomme l'entrée invalide
            }
        }
        return choice;
    }
    
    // Fonction pour exécuter le mode console
    private static void executerModeConsole(Scanner scanner) {
        System.out.println("Bienvenue dans le gestionnaire d'absences [Console]");
        
        // Demande l'adresse IP et le port du serveur
        System.out.print("Veuillez entrer l'adresse IP du serveur|");
        String ip = scanner.next();
        System.out.print("Veuillez entrer le port du serveur|");
        int port = scanner.nextInt();
        scanner.nextLine(); // Consomme la nouvelle ligne
        


        client = Client.getInstance();
        try {
            if (client.connectToServer(ip, port)) {
                System.out.println("[INFO] - Connexion réussie au serveur" + ip + ":" + port);
                gererMenuPrincipal(scanner);
            } else {
                System.out.println("[ERROR] - Veuillez vérifier l'adresse IP et le port.");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] - Veuillez vérifier l'adresse IP et le port. Erreur : " + e.getMessage());
            e.printStackTrace();

        }
    }
    
    // Fonction pour gérer le menu principal en mode console
    private static void gererMenuPrincipal(Scanner scanner) {
        updateListsFromServer();

        while (true) {
            afficherMenu();
            int choix = scanner.nextInt();
            switch (choix) {
                case 1:
                    afficherEtudiants();
                    break;
                case 2:
                    afficherSeances();
                    break;
                case 3:
                    enregistrerAbsences(scanner);
                    break;
                case 4:
                    creerEtudiant(scanner);
                    break;
                case 5:
                    creerSeance(scanner);
                    break;
                case 6:
                    supprimerEtudiant(scanner);
                    break;
                case 7:
                    supprimerSeance(scanner);
                    break;
                case 8:
                    visualiserAbsencesSeance(scanner);
                    break;
                case 0:
                    System.out.println("Au revoir!");
                    client.closeResources();
                    return;
                default:
                    System.out.println("Veuillez entrer une réponse valide.");
            }
        }
    }

    // Mettez à jour les listes avec les données du serveur
    private static void updateListsFromServer() {
        etudiants.clear();
        seances.clear();
        etudiants.addAll(client.getStudents());
        seances.addAll(client.getSeances());
    }
    
    // Fonction pour afficher le menu des opérations disponibles
    private static void afficherMenu() {
        System.out.println("---------------Menu---------------");
        System.out.println("[1] Visualiser les étudiants");
        System.out.println("[2] Visualiser les séances");
        System.out.println("[3] Enregistrer les absences d'une seance");
        System.out.println("[4] Creer un etudiant");
        System.out.println("[5] Creer une seance");
        System.out.println("[6] Supprimer un etudiant");
        System.out.println("[7] Supprimer une seance");
        System.out.println("[8] Visualiser les absences");
        System.out.println("[0] Quitter");
        System.out.print("Choix | ");
    }
    
    // Fonction pour afficher la liste des étudiants
    private static void afficherEtudiants() {
        System.out.println("\n======================Students======================\n");
        for (Etudiant etudiant : etudiants) {
            System.out.println(etudiant);
        }
        System.out.print("\n======================================================\n");
    }
    
    // Fonction pour afficher la liste des séances
    private static void afficherSeances() {
        System.out.println("\n======================Seances======================\n");
        for (Seance seance : seances) {
            System.out.println(seance);
        }
        System.out.print("\n======================================================\n");
    }
    
    private static void enregistrerAbsences(Scanner scanner) {
        afficherSeances();
    
        System.out.print("\nEntrez ID de la séance | ");
        int id_seance = scanner.nextInt();
    
        for (Seance seance : seances) {
            if (seance.getIdSeance() == id_seance) {
                System.out.println("\n[Seance] - " + seance.getNomSeance() + "\n");
                for (Etudiant etudiant : etudiants) {
                    boolean responseValid = false;
    
                    while (!responseValid) {
                        System.out.print("L'étudiant " + etudiant.getNomEtudiant() + " est-il présent? (O/N)| ");
                        String presence = scanner.next();
    
                        if (presence.equalsIgnoreCase("N")) {
                            client.setAbsence(id_seance, etudiant.getIdEtudiant(), 0);
                            seance.setAbsence(etudiant.getIdEtudiant(), 0);
                            System.out.println("Etudiant " + etudiant.getNomEtudiant() + " marqué absent");
                            responseValid = true;
                        } else if (presence.equalsIgnoreCase("O")) {
                            client.setAbsence(id_seance, etudiant.getIdEtudiant(), 1);
                            seance.setAbsence(etudiant.getIdEtudiant(), 1);
                            System.out.println("Etudiant " + etudiant.getNomEtudiant() + " marqué présent");
                            responseValid = true;
                        } else {
                            System.out.println("Réponse invalide. Veuillez entrer 'O' pour présent ou 'N' pour absent.");
                        }
                    }
                }
                System.out.println("\n[INFO] - Absences marquées pour la séance ");
                System.out.println(seance);
                return;
            }
        }
        System.out.println("[INFO] - Séance non trouvée");
    }
    
    private static void visualiserAbsencesSeance(Scanner scanner) {
        System.out.println("Liste des séances disponibles :");
        for (Seance seance : seances) {
            System.out.println(seance);
        }
    
        System.out.print("\nEntrez l'ID de la séance pour visualiser les absences | ");
        int id_seance = scanner.nextInt();
    
        Seance seanceChoisie = null;
        for (Seance seance : seances) {
            if (seance.getIdSeance() == id_seance) {
                seanceChoisie = seance;
                break;
            }
        }
    
        if (seanceChoisie != null) {
            System.out.println("\nAbsences pour la séance : " + seanceChoisie.getNomSeance());
            for (Etudiant etudiant : etudiants) {
                String status = client.getAttendanceStudent(seanceChoisie.getIdSeance(), etudiant.getIdEtudiant()) == 0 ? "Absent" : "Present";
                System.out.println("Etudiant : " + etudiant.getNomEtudiant() + " - Status : " + status);
            }

        } else {
            System.out.println("[INFO] Séance non trouvée.");
        }
    }

    

    // Fonction pour créer un nouvel étudiant
    private static void creerEtudiant(Scanner scanner) {
        System.out.print("Veuillez entrer le nom de l'étudiant | ");
        scanner.nextLine();
        String name = scanner.nextLine();
        client.createStudent(name);
        updateListsFromServer();

        for (Seance seance : seances) {
            for (Etudiant etudiant : etudiants) {
                boolean etudiantExists = false;
                for (Etudiant existingEtudiant : seance.getListEtudiant()) {
                    if (existingEtudiant.getIdEtudiant() == etudiant.getIdEtudiant()) {
                        etudiantExists = true;
                        break;
                    }
                }
                if (!etudiantExists) {
                    seance.ajouterEtudiant(etudiant);
                    seance.setAbsence(etudiant.getIdEtudiant(), 0);
                }
            }
        }
    }

    // Fonction pour créer une nouvelle séance
    private static void creerSeance(Scanner scanner) {
        scanner.nextLine(); // Consomme la nouvelle ligne
        System.out.println("Veuillez entrer le nom de la séance :");
        String nomSeance = scanner.nextLine();
    
        String dateSeance = demanderDateSeance(scanner);
        String heureSeance = demanderHeureSeance(scanner);
    
        String dateTime = dateSeance + " " + heureSeance;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        long unixTime = 0;

        try {
            Date date = sdf.parse(dateTime);
            unixTime = date.getTime() / 1000; // Convert to seconds
        } catch (ParseException e) {
            e.printStackTrace();
        }
    
        client.createSeance(nomSeance, unixTime);
        updateListsFromServer();
        fillObjects();
    }
    
    // Fonction pour demander la date de la séance
    private static String demanderDateSeance(Scanner scanner) {
        String dateSeance;
        while (true) {
            System.out.println("Veuillez entrer la date de la séance (format JJ/MM/AAAA) :");
            dateSeance = scanner.nextLine();
            if (dateSeance.matches("^(0[1-9]|[12][0-9]|3[01])/([0][1-9]|1[0-2])/\\d{4}$")) {
                break;
            } else {
                System.out.println("Format de date invalide. Veuillez réessayer.");
            }
        }
        return dateSeance;
    }
    
    // Fonction pour demander l'heure de la séance
    private static String demanderHeureSeance(Scanner scanner) {
        String heureSeance;
        while (true) {
            System.out.println("Veuillez entrer l'heure de la séance (format HH:MM) :");
            heureSeance = scanner.nextLine();
            if (heureSeance.matches("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
                break;
            } else {
                System.out.println("Format d'heure invalide. Veuillez réessayer.");
            }
        }
        return heureSeance;
    }
    
    // Fonction pour supprimer un étudiant
    private static void supprimerEtudiant(Scanner scanner) {
        afficherEtudiants();
        System.out.println("Veuillez entrer l'ID de l'étudiant à supprimer |");
        int idsupp = scanner.nextInt();
        boolean etudiantTrouve = false;
        for (Etudiant etudiant : etudiants) {
            if (etudiant.getIdEtudiant() == idsupp) {
                client.deleteStudent(idsupp);
                etudiantTrouve = true;
                break;
            }
        }
        if(etudiantTrouve){
            updateListsFromServer();
        }else{
            System.out.println("[INFO] Etudiant non trouvé");
        }

    }
    
    // Fonction pour supprimer une séance
    private static void supprimerSeance(Scanner scanner) {
        afficherSeances();
        System.out.println("Veuillez entrer l'ID de la séance à supprimer |");
        int idseancesupp = scanner.nextInt();
        boolean seanceTrouve = false;
        for (Seance seance : seances) {
            if (seance.getIdSeance() == idseancesupp) {
                client.deleteSeance(idseancesupp);
                seanceTrouve = true;
                break;
            }
        }
        if(seanceTrouve){
            updateListsFromServer();
            fillObjects();
        }else {
            System.out.println("[INFO] Séance non trouvée");
        }
    }

    // Ajoutez cette méthode pour remplir les objets étudiants et séances
    private static void fillObjects() {
        for (Seance seance : seances) {
            for (Etudiant etudiant : etudiants) {
                seance.ajouterEtudiant(etudiant);
                int status = client.getAttendanceStudent(seance.getIdSeance(), etudiant.getIdEtudiant());
                seance.setAbsence(etudiant.getIdEtudiant(), status);
            }
        }
    }
    
    // Fonction pour exécuter le mode graphique
    private static void executerModeGraphique() {
        System.out.println("Mode interface graphique");
        new Accueil();
    }
}
