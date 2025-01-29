package src.com.client_java;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Affiche un message de bienvenue
        System.out.println("Bienvenu dans le gestionnaire d'absences");
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        // Boucle principale pour demander à l'utilisateur de choisir un mode d'exécution
        while (true) {
            while (choice != 1 && choice != 2) {

                // Demande à l'utilisateur de choisir entre le mode console ou le mode graphique
                System.out.println("Veuillez choisir votre mode d'execution:\n [1] - Console\n [2] - Interface graphique");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    // Vérifie si le choix est valide
                    if (choice != 1 && choice != 2) {
                        System.out.println("Choix incorrect. Veuillez réessayer.");
                    }

                } else {
                    // Si l'entrée n'est pas un entier, affiche un message d'erreur
                    System.out.println("Entrée invalide. Veuillez entrer un nombre.");
                    scanner.next(); // Consomme l'entrée invalide
                }
            }

            switch (choice) {
                case 1:
                    // Si l'utilisateur choisit le mode console
                    System.out.println("Bienvenue dans le gestionnaire d'absences [Console]");

                    // Demande l'adresse IP et le port du serveur
                    System.out.print("Veuillez entrer l'adresse IP du serveur|");
                    String ip = scanner.next();
                    System.out.print("Veuillez entrer le port du serveur|");
                    int port = scanner.nextInt();

                    Client client = new Client();
                    try {
                        // Essaie de se connecter au serveur
                        if (client.connectToServer(ip, port)) {
                            // Boucle principale pour les opérations de gestion des absences
                            while (true) {
                                // Récupère la liste des étudiants et des séances depuis le serveur
                                List<Etudiant> etudiants = client.getStudents();
                                List<Seance> seances = client.getSeances();
                                
                                // Ajoute les étudiants aux séances et met à jour leur statut de présence
                                for (Seance seance : seances) {
                                    for (Etudiant etudiant : etudiants) {
                                        seance.ajouterEtudiant(etudiant);
                                        int status = client.getAttendanceStudent(seance.getIdSeance(), etudiant.getIdEtudiant());
                                        if (status == 1) {
                                            seance.setAbsence(etudiant.getIdEtudiant(), 1);
                                        } else {
                                            seance.setAbsence(etudiant.getIdEtudiant(), 0);
                                        }
                                    }
                                }
                                
                                // Menu des opérations disponibles pour l'utilisateur
                                while (true) {
                                    System.out.println("---------------Menu---------------");
                                    System.out.println("[1] Visualiser les étudiants");
                                    System.out.println("[2] Visualiser les séances");
                                    System.out.println("[3] Enregistrer les absences d'une seance");
                                    System.out.println("[4] Creer un etudiant");
                                    System.out.println("[5] Creer une seance");
                                    System.out.println("[6] Supprimer un etudiant");
                                    System.out.println("[7] Supprimer une seance");
                                    System.out.println("[0] Quitter");
                                    System.out.print("Choix | ");
                        
                                    int choix = scanner.nextInt();
                                    switch (choix) {
                                        case 1:
                                            // Affiche la liste des étudiants
                                            System.out.println("\n======================Students======================\n");
                                            for (Etudiant etudiant : etudiants) {
                                                System.out.println(etudiant);
                                            }
                                            System.out.print("\n======================================================\n");
                                            break;
                                        case 2:
                                            // Affiche la liste des séances
                                            System.out.println("\n======================Seances======================n");
                                            for (Seance seance : seances) {
                                                System.out.println(seance);
                                            }
                                            System.out.print("\n======================================================\n");
                                            break;
                                        case 3:
                                            // Enregistre l'absence ou la présence d'un étudiant pour une séance donnée
                                            for (Seance seance : seances) {
                                                System.out.println(seance);
                                            }

                                            // Demande l'ID de la séance pour laquelle enregistrer les présences
                                            System.out.print("\nEntrez ID de la séance | ");
                                            int id_seance = scanner.nextInt();
            
                                            for (Seance seance : seances){
                                                int s_id = seance.getIdSeance();
                                                if (s_id == id_seance) {
                                                    System.out.println("\n[Seance] - " + seance.getNomSeance() + "\n");
                                                    for (Etudiant etudiant : etudiants) {
                                                        // Demande pour chaque étudiant s'il est présent
                                                        System.out.print("L'étudiant " + etudiant.getNomEtudiant() + " est-il présent? (O/N)| ");
                                                        String presence = scanner.next();
            
                                                        if (presence.equals("N") || presence.equals("n")) {
                                                            int statut = 0;
                                                            System.out.println("Etudiant " + etudiant.getNomEtudiant() + " marqué absent");
                                                            client.setAbsence(id_seance, etudiant.getIdEtudiant(), statut);
                                                            seance.setAbsence(etudiant.getIdEtudiant(), 0);
                                                        } else if (presence.equals("O") || presence.equals("o")) {
                                                            int statut = 1;
                                                            System.out.println("Etudiant " + etudiant.getNomEtudiant() + " marqué présent");
                                                            client.setAbsence(id_seance, etudiant.getIdEtudiant(), statut);
                                                            seance.setAbsence(etudiant.getIdEtudiant(), 1);
                                                        }
                                                    }
            
                                                    // Confirmation du marquage des absences
                                                    System.out.println("\n[INFO] - Absences marquées pour la séance ");
                                                    System.out.println(seance);
                                                }else{
                                                    System.out.println("[INFO] - Séance non trouvée");
                                                }
                                            }
                                            break;

                                        case 4:
                                            // Demande de créer un nouvel étudiant
                                            System.out.print("Veuillez entrer le nom de l'étudiant | ");
                                            scanner.nextLine();
                                            String name = scanner.nextLine();
                                            client.createStudent(name);
                                            etudiants = client.getStudents();

                                            // Ajoute le nouvel étudiant aux séances
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
                                            break;

                                        case 5:
                                            // Demande de créer une nouvelle seance
                                            System.out.println("Au revoir!");
                                            client.closeResources();
                                            break;

                                        case 6:
                                            // Suppression d'un etudiant
                                            // Affiche la liste des étudiants
                                            System.out.println("\n======================Students======================\n");
                                            for (Etudiant etudiant : etudiants) {
                                                System.out.println(etudiant);
                                            }
                                            System.out.println("\n======================================================\n");
                                            System.out.println("Veuillez entrer l'ID de l'etudiant à supprimer |");
                                            int idsupp = scanner.nextInt();
                                            boolean etudiantTrouve = false;
                                            for (Etudiant etudiant : etudiants) {
                                                if (etudiant.getIdEtudiant() == idsupp) {
                                                    client.deleteStudent(idsupp);
                                                    etudiants = client.getStudents();
                                                    for (Seance seance : seances) {
                                                        seance.supprimerEtudiant(idsupp);
                                                    }
                                                    etudiantTrouve = true;
                                                    break;
                                                }
                                            }
                                            if (!etudiantTrouve) {
                                                System.out.println("[INFO] Etudiant non trouvé");
                                            }
                                            break;

                                        case 7:
                                            // Suppression d'une seance
                                            // Affiche la liste des séances
                                            System.out.println("\n======================Seances======================n");
                                            for (Seance seance : seances) {
                                                System.out.println(seance);
                                            }
                                            System.out.print("\n======================================================\n");
                                            System.out.println("Veuillez entrer l'ID de la séance à supprimer |");
                                            int idseancesupp = scanner.nextInt();
                                            boolean seanceTrouve = false;
                                            for (Seance seance : seances) {
                                                if (seance.getIdSeance() == idseancesupp) {
                                                    client.deleteSeance(idseancesupp);
                                                    seances = client.getSeances();
                                                    seanceTrouve = true;
                                                    break;
                                                }
                                            }
                                            if (!seanceTrouve) {
                                                System.out.println("[INFO] Seance non trouvée");
                                            }
                                            break;

                                        case 0:
                                            // Option pour quitter le programme
                                            System.out.println("Au revoir!");
                                            client.closeResources();
                                            return;
                                        default:
                                            // Message d'erreur si le choix est invalide
                                            System.out.println("Veuillez entrer une réponse valide.");
                                    }
                                }
                            }
                        } else {
                            // Message d'erreur si la connexion au serveur échoue
                            System.out.println("[ERROR] - Veuillez vérifier l'adresse IP et le port.");
                        }
                    } catch (Exception e) {
                        // Gestion de l'exception et message d'erreur
                        System.out.println("[ERROR] - Veuillez vérifier l'adresse IP et le port.");
                    }
                    // Réinitialise le choix pour permettre de revenir au menu
                    choice = 0;
                    break;

                case 2:
                    // Si l'utilisateur choisit le mode graphique
                    System.out.println("Mode interface graphique");
                    // Décommenter la partie suivante pour la connexion serveur dans le cas d'une interface graphique
                    // Client client_2 = new Client();
                    // if (client_2.connectToServer("127.0.0.1", 8081)) {
                    //     System.out.println("Connexion réussie");
                    //     new Connected(client_2);
                    // } else {
                    //     System.out.println("[ERROR] - Veuillez vérifier l'adresse IP et le port.");
                    // }

                    // Initialisation de l'interface graphique
                    new Accueil();
                    return;

                default:
                    break;
            }
        scanner.close();
        }
    }
}
