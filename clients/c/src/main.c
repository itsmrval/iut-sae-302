#include <stdio.h>
#include <stdlib.h>
#include "network.h"
#include "handlers.h"

// Déclaration de la fonction d'affichage du titre, probablement définie dans un autre fichier.
void display_title(const char* title);

int main(int argc, char *argv[]) {
    // Vérifie que le programme reçoit exactement 3 arguments (en plus du nom du programme)
    if (argc != 4) {
        fprintf(stderr, "Usage: %s <ip> <port> <cert_file>\n", argv[0]);
        exit(EXIT_FAILURE);
    }
    
    // Récupération des arguments passés au programme
    const char* hostname = argv[1];   // Adresse IP ou nom d'hôte
    int port = atoi(argv[2]);           // Port de connexion, converti de chaîne de caractères à entier
    const char* cert_file = argv[3];    // Chemin vers le fichier de certificat SSL
    
    // Affiche un message indiquant la tentative de connexion
    printf("Connecting to %s:%d\n", hostname, port);
    
    // Établit une connexion SSL en utilisant les informations fournies
    SSL* ssl = setup_ssl_connection(hostname, port, cert_file);
    if (!ssl) {
        // Si la connexion SSL échoue, le programme se termine avec un code d'erreur
        exit(EXIT_FAILURE);
    }
    
    // Boucle principale du programme
    while (1) {
        // Affiche le titre du menu principal
        display_title("Main Menu");
        // Affiche les options disponibles dans le menu principal
        printf("1. Manage seances\n");
        printf("2. Manage students\n");
        printf("3. Exit\n");
        printf("Choice: ");
        
        int choice;
        // Lit le choix de l'utilisateur
        scanf("%d", &choice);
        getchar();  // Lit le caractère de fin de ligne laissé dans le buffer
        
        // Exécute l'action correspondante au choix de l'utilisateur
        switch (choice) {
            case 1:
                // Appelle la fonction de gestion des séances
                handle_seance_management(ssl);
                break;
            case 2:
                // Appelle la fonction de gestion des étudiants
                handle_student_management(ssl);
                break;
            case 3:
                // Quitte la boucle principale et passe au nettoyage des ressources
                goto cleanup;
            default:
                // Affiche un message d'erreur pour un choix invalide
                printf("Invalid choice\n");
        }
    }
    
cleanup:
    // Nettoie la connexion SSL en libérant les ressources allouées
    cleanup_ssl_connection(ssl);
    return 0;
}
