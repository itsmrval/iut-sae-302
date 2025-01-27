package src.com.client_java;

import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        System.out.println("Bienvenu dans le gestionnaire d'absences");
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        // Boucle pour demander à l'utilisateur de choisir jusqu'à ce qu'il entre une valeur valide
        while (true) {
            while (choice != 1 && choice != 2) {
                System.out.println("Veuillez choisir votre mode d'execution:\n [1] - Console\n [2] - Interface graphique");
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

            switch (choice) {
                case 1:
                    System.out.println("Bienvenue dans le gestionnaire d'absences [Console]");

                    // System.out.print("Veuillez entrer l'adresse IP du serveur|");
                    // String ip = scanner.next();
                    // System.out.print("Veuillez entrer le port du serveur|");
                    // int port = scanner.nextInt();

                    String ip="127.0.0.1";
                    int port=8081;

                    Client client = new Client();
                    try {
                        if (client.connectToServer(ip, port)) {
                        
                            // Ajoutez ici le code pour le mode console
                            // Par exemple, une boucle pour gérer les commandes de l'utilisateur
                            
                            while (true) {
          
                                List<Etudiant> etudiants = client.getStudents();
                                List<Seance> seances = client.getSeances();
                                
                                for (Seance seance : seances) {
                                    for (Etudiant etudiant : etudiants) {
                                        seance.ajouterEtudiant(etudiant);
                                        seance.setAbsence(etudiant.getIdEtudiant(), 0);
                                        client.setAbsence(seance.getIdSeance(), etudiant.getIdEtudiant(), 0);
                                    }
                                }
                                
                                while (true) {
                                    System.out.println("---------------Menu---------------");
                                    System.out.println("[1] Visualiser les étudiants");
                                    System.out.println("[2] Visualiser les séances");
                                    System.out.println("[3] Enregistrer une absence");
                                    System.out.println("[4] Creer un etudiant");
                                    System.out.println("[5] Creer une seance");
                                    System.out.println("[6] Quitter");
                                    System.out.print("Choix | ");
                        
                                    int choix = scanner.nextInt();
                                    switch (choix) {
                                        case 1:
                                            System.out.println("\n======================Students======================\n");
                                            for (Etudiant etudiant : etudiants) {
                                                System.out.println(etudiant);
                                            }
                                            System.out.print("\n======================================================\n");
                                            break;
                                        case 2:
                                            System.out.println("\n======================Seances======================n");
                                            for (Seance seance : seances) {
                                                System.out.println(seance);
                                            }
                                            System.out.print("\n======================================================\n");
                                            break;
                                        case 3:
                                            for (Seance seance : seances) {
                                                System.out.println(seance);
                                            }

                                            System.out.print("\nEntrez ID de la séance | ");
                                            int id_seance = scanner.nextInt();
            
                                            for (Seance seance : seances){
                                                int s_id=seance.getIdSeance();
                                                if (s_id==id_seance){
                                                    System.out.println("\n[Seance] - "+seance.getNomSeance()+"\n");
                                                    for (Etudiant etudiant : etudiants){
                                                        System.out.print("L'étudiant "+etudiant.getNomEtudiant()+" est-il présent? (O/N)| ");
                                                        String presence = scanner.next();
            
                                                        if (presence.equals("N") || presence.equals("n")){
                                                            int statut = 0;
                                                            System.out.println("Etudiant "+etudiant.getNomEtudiant()+" marqué absent");
                                                            client.setAbsence(id_seance, etudiant.getIdEtudiant(), statut);
                                                            seance.setAbsence(etudiant.getIdEtudiant(), 0);
                                                        }else if (presence.equals("O") || presence.equals("o")){
                                                            int statut = 1;
                                                            System.out.println("Etudiant "+etudiant.getNomEtudiant()+" marqué présent");
                                                            client.setAbsence(id_seance, etudiant.getIdEtudiant(), statut);
                                                            seance.setAbsence(etudiant.getIdEtudiant(), 1);
                                                        }
                                                    }
            
                                                    System.out.println("\n[INFO] - Absences marquées pour la séance ");
                                                    System.out.println(seance);
                                                }
            
                                            }
                                            
                                            break;

                                        case 4:
                                            System.out.print("Veuillez entrer le nom de l'étudiant | ");
                                            scanner.nextLine();
                                            String name = scanner.nextLine();
                                            client.createStudent(name);
                                            etudiants = client.getStudents();

                                        
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
                                            System.out.println("Au revoir!");
                                            client.closeResources();
                                            break;

                                        case 6:
                                            System.out.println("Au revoir!");
                                            client.closeResources();
                                            return;
                                        default:
                                            System.out.println("Veuillez entrer une réponse valide.");
                                    }
                                }


                            }
                        } else {
                            System.out.println("[ERROR] - Veuillez vérifier l'adresse IP et le port.");
                        }
                    } catch (Exception e) {
                        System.out.println("[ERROR] - Veuillez vérifier l'adresse IP et le port.");
                    }
                    // Réinitialiser le choix pour revenir au menu
                    choice = 0;
                    break;

                case 2:
                    System.out.println("Mode interface graphique");
                    // Client client_2 = new Client();
                    // if (client_2.connectToServer("127.0.0.1", 8081)) {
                    //     System.out.println("Connexion réussie");
                    //     new Connected(client_2);
                    // } else {
                    //     System.out.println("[ERROR] - Veuillez vérifier l'adresse IP et le port.");
                    // }

                    new Accueil();
                    return;

                default:
                    break;
            }
        scanner.close();
        
        }

    }
}