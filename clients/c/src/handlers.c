#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// Inclusion des fichiers d'en-tête pour les gestionnaires et le réseau.
#include "handlers.h"
#include "network.h"

// Définition de la taille du buffer et du nombre maximum d'éléments à traiter.
#define BUFFER_SIZE 1024
#define MAX_ITEMS 50

// Structure représentant un élément (peut être un étudiant ou une séance)
typedef struct {
    int id;             // Identifiant de l'élément
    char name[100];     // Nom de l'élément
    time_t unix_time;   // Date/heure en format Unix (utilisé pour les séances)
} Item;

// Fonction statique qui affiche 50 sauts de ligne pour créer un espacement visuel.
static void display_spacer() {
    for (int i = 0; i < 50; i++) printf("\n");
}

// Affiche un titre encadré d'un bord décoratif.
void display_title(const char* title) {
    int len = strlen(title);
    printf("\n╔");
    for(int i = 0; i < len + 2; i++) printf("═");
    printf("╗\n");
    printf("║ %s ║\n", title);
    printf("╚");
    for(int i = 0; i < len + 2; i++) printf("═");
    printf("╝\n\n");
}

// Analyse la réponse du serveur et la convertit en tableau d'objets Item.
// La réponse attendue est de la forme "202/<data>" où <data> contient les éléments séparés par des points-virgules.
static void parse_server_response(const char* response, Item* items, int* count) {
    *count = 0;
    char* resp_copy = strdup(response); // Copie modifiable de la réponse
    char* status = strtok(resp_copy, "/");
    
    if (strcmp(status, "202") == 0) { // Vérifie si le statut est 202 (succès)
        char* data = strtok(NULL, "");
        char* item = strtok(data, ";");
        
        // Parcours de chaque élément dans les données
        while (item != NULL && *count < MAX_ITEMS) {
            // Recherche des champs "id:", "name:" et "unix_time:" dans l'élément
            char* id_str = strstr(item, "id:");
            char* name_str = strstr(item, "name:");
            char* time_str = strstr(item, "unix_time:");
            
            if (id_str && name_str) {
                // Extraction de l'identifiant
                sscanf(id_str, "id:%d", &items[*count].id);
                
                char name[100] = {0};
                if (time_str) {
                    // Si un timestamp est présent, on extrait le nom jusqu'à la virgule et le unix_time
                    sscanf(name_str, "name:%[^,]", name);
                    sscanf(time_str, "unix_time:%ld", &items[*count].unix_time);
                } else {
                    // Sinon, on extrait le nom jusqu'au point-virgule et on définit unix_time à 0
                    sscanf(name_str, "name:%[^;]", name);
                    items[*count].unix_time = 0;
                }
                strcpy(items[*count].name, name);
                (*count)++;
            }
            // Passe à l'élément suivant
            item = strtok(NULL, ";");
        }
    }
    free(resp_copy); // Libération de la mémoire allouée pour la copie de la réponse
}

// Affiche la liste des étudiants et les options de gestion associées.
void display_student_list(const char* response) {
    Item students[MAX_ITEMS];
    int count;
    // Analyse de la réponse du serveur pour récupérer les étudiants
    parse_server_response(response, students, &count);
    
    display_title("Student Management");
    
    // Affichage de chaque étudiant
    for (int i = 0; i < count; i++) {
        printf("(%d) %s\n", students[i].id, students[i].name);
    }
    // Affichage des options disponibles
    printf("\nOptions:\n");
    printf("1. Go back\n");
    printf("2. Create new student\n");
    printf("3. Delete student\n");
    printf("Choice: ");
}

// Affiche la liste des étudiants pour la gestion des présences.
void display_attendance_list(const char* response) {
    Item students[MAX_ITEMS];
    int count;
    // Analyse de la réponse du serveur pour obtenir la liste des étudiants
    parse_server_response(response, students, &count);
    
    display_title("Attendance Management");
    
    // Affichage de chaque étudiant
    for (int i = 0; i < count; i++) {
        printf("(%d) %s\n", students[i].id, students[i].name);
    }
    // Affichage des options disponibles
    printf("\nOptions:\n");
    printf("1. Go back\n");
    printf("2. Take attendance\n");
    printf("Choice: ");
}

// Affiche la liste des séances avec leur date et heure formatées.
void display_seance_list(const char* response) {
    Item seances[MAX_ITEMS];
    int count;
    // Analyse de la réponse du serveur pour obtenir la liste des séances
    parse_server_response(response, seances, &count);
    
    display_title("Seance Management");
    
    // Parcours des séances et affichage avec formatage de la date/heure
    for (int i = 0; i < count; i++) {
        struct tm *tm_info = localtime(&seances[i].unix_time);
        char time_str[20];
        strftime(time_str, 20, "%d/%m/%Y %H:%M", tm_info);
        printf("%d. %s (%s)\n", 
               seances[i].id, 
               seances[i].name,
               time_str);
    }
    
    // Affichage des options de gestion des séances
    printf("\nOptions:\n");
    printf("1. Go back\n");
    printf("2. Create seance\n");
    printf("3. Delete seance\n");
    printf("4. Manage attendance\n");
    printf("Choice: ");
}

// Gère le menu de gestion des étudiants (affichage, création, suppression).
void handle_student_management(SSL* ssl) {
    char buffer[BUFFER_SIZE];
    char response[BUFFER_SIZE];
    int choice = 0;
    
    while (choice != 1) {
        // Demande la liste des étudiants au serveur
        ssl_send_receive(ssl, "<g>student", response, sizeof(response));
        display_student_list(response);
        
        // Récupère le choix de l'utilisateur
        scanf("%d", &choice);
        getchar();
        
        if (choice == 2) {
            // Création d'un nouvel étudiant
            printf("Enter student name: ");
            char name[100];
            fgets(name, sizeof(name), stdin);
            name[strcspn(name, "\n")] = 0; // Suppression du saut de ligne
            
            // Envoie la requête de création d'étudiant au serveur
            snprintf(buffer, sizeof(buffer), "<p>student/%s", name);
            ssl_send_receive(ssl, buffer, response, sizeof(response));
            
            // Actualise et affiche la liste des étudiants
            ssl_send_receive(ssl, "<g>student", response, sizeof(response));
            display_student_list(response);
        }
        if (choice == 3) {
            // Suppression d'un étudiant
            printf("Enter student id: ");
            int id;
            scanf("%d", &id);
            getchar();
            
            // Envoie la requête de suppression au serveur
            snprintf(buffer, sizeof(buffer), "<d>student/%d", id);
            ssl_send_receive(ssl, buffer, response, sizeof(response));
            
            // Actualise et affiche la liste des étudiants
            ssl_send_receive(ssl, "<g>student", response, sizeof(response));
            display_student_list(response);
        }
    }
}

// Gère le menu de gestion des séances (affichage, création, suppression, gestion des présences).
void handle_seance_management(SSL* ssl) {
    char buffer[BUFFER_SIZE];
    char response[BUFFER_SIZE];
    int choice = 0;
    
    while (choice != 1) {
        // Demande la liste des séances au serveur
        ssl_send_receive(ssl, "<g>seance", response, sizeof(response));
        display_seance_list(response);
    
        // Récupère le choix de l'utilisateur
        scanf("%d", &choice);
        getchar();
        if (choice == 2) {
            // Création d'une nouvelle séance
            char name[100], date[20], time[10];
            
            printf("Enter seance name: ");
            fgets(name, sizeof(name), stdin);
            name[strcspn(name, "\n")] = 0;
            
            printf("Enter date (DD/MM/YYYY): ");
            fgets(date, sizeof(date), stdin);
            date[strcspn(date, "\n")] = 0;
            
            printf("Enter time (HH:MM): ");
            fgets(time, sizeof(time), stdin);
            time[strcspn(time, "\n")] = 0;
            
            struct tm tm = {0};
            char datetime[50];
            // Concatène la date et l'heure dans une seule chaîne
            snprintf(datetime, sizeof(datetime), "%s %s", date, time);
            
            // Conversion de la chaîne en structure tm
            if (strptime(datetime, "%d/%m/%Y %H:%M", &tm) != NULL) {
                time_t unix_time = mktime(&tm); // Conversion en timestamp Unix
                // Envoie la requête de création de séance au serveur
                snprintf(buffer, sizeof(buffer), "<p>seance/%s/%ld", name, unix_time);
                ssl_send_receive(ssl, buffer, response, sizeof(response));
                
                // Actualise et affiche la liste des séances
                ssl_send_receive(ssl, "<g>seance", response, sizeof(response));
                display_seance_list(response);
            }
        }
        if (choice == 3) {
            // Suppression d'une séance
            printf("Enter seance id: ");
            int id;
            scanf("%d", &id);
            getchar();
            
            // Envoie la requête de suppression de séance au serveur
            snprintf(buffer, sizeof(buffer), "<d>seance/%d", id);
            ssl_send_receive(ssl, buffer, response, sizeof(response));
            
            // Actualise et affiche la liste des séances
            ssl_send_receive(ssl, "<g>seance", response, sizeof(response));
            display_seance_list(response);
        }
        if (choice == 4) {
            // Passage à la gestion des présences pour une séance
            handle_attendance_management(ssl);
        }
    }
}

// Analyse la réponse concernant la présence et extrait le statut (présent ou absent).
void parse_attendance_response(const char* response, int* status) {
    *status = 0;
    
    if (strstr(response, "202/") != NULL) {
        char* status_str = strstr(response, "status:");
        if (status_str) {
            // Interprète le caractère suivant "status:" (1 pour présent, sinon absent)
            *status = (status_str[7] == '1') ? 1 : 0;
        }
    }
}

// Affiche l'état des présences pour chaque étudiant d'une séance donnée.
void display_seance_attendance_status(const char* seance_name, Item* students, int* student_statuses, int count) {
    display_title("Attendance");
    printf("Seance: %s\n\n", seance_name);
    
    // Affiche le nom, l'identifiant et l'état (présent/absent) de chaque étudiant
    for (int i = 0; i < count; i++) {
        printf("%s (ID: %d): %s\n", 
               students[i].name, 
               students[i].id, 
               student_statuses[i] == 1 ? "Present" : "Absent");
    }
    
    // Affichage des options de gestion de présence
    printf("\nOptions:\n");
    printf("1. Go back\n");
    printf("2. Take attendance\n");
    printf("Choice: ");
}

// Gère le processus de gestion des présences pour une séance.
void handle_attendance_management(SSL* ssl) {
    char buffer[BUFFER_SIZE];
    char response[BUFFER_SIZE];
    int choice = 0;
    int seance_id;
    
    // Demande à l'utilisateur de saisir l'identifiant de la séance
    printf("Enter seance id: ");
    scanf("%d", &seance_id);
    getchar();
    
    // Récupère la liste des séances pour retrouver le nom de la séance
    snprintf(buffer, sizeof(buffer), "<g>seance", response);
    ssl_send_receive(ssl, buffer, response, sizeof(response));
    Item seances[MAX_ITEMS];
    int seance_count;
    parse_server_response(response, seances, &seance_count);
    
    // Recherche du nom de la séance correspondant à l'identifiant saisi
    char* seance_name = "Unknown Seance";
    for (int i = 0; i < seance_count; i++) {
        if (seances[i].id == seance_id) {
            seance_name = seances[i].name;
            break;
        }
    }
    
    while (1) {
        // Récupère la liste des étudiants
        ssl_send_receive(ssl, "<g>student", response, sizeof(response));
        Item students[MAX_ITEMS];
        int count;
        parse_server_response(response, students, &count);
        
        int student_statuses[MAX_ITEMS] = {0}; 
        
        // Pour chaque étudiant, récupère le statut de présence pour la séance
        for (int i = 0; i < count; i++) {
            snprintf(buffer, sizeof(buffer), "<g>attendance/%d/%d", seance_id, students[i].id);
            ssl_send_receive(ssl, buffer, response, sizeof(response));
            
            int status;
            parse_attendance_response(response, &status);
            student_statuses[i] = status;
        }
        
        // Affiche le statut des présences pour la séance
        display_seance_attendance_status(seance_name, students, student_statuses, count);
        
        // Récupère le choix de l'utilisateur
        scanf("%d", &choice);
        getchar();
        
        if (choice == 1) {
            break; // Quitte la gestion des présences
        }
        else if (choice == 2) {
            // Permet de saisir le statut de présence pour chaque étudiant
            printf("Enter status for each student (1: present, 0: absent):\n\n");
            
            for (int i = 0; i < count; i++) {
                printf("- %s (ID: %d): %s => : ", 
                       students[i].name, 
                       students[i].id,
                       student_statuses[i] == 1 ? "Present" : "Absent");
                       
                int status;
                scanf("%d", &status);
                getchar();
                
                // Envoie la nouvelle valeur de présence au serveur pour chaque étudiant
                snprintf(buffer, sizeof(buffer), "<p>attendance/%d/%d/%d", 
                        seance_id, students[i].id, status);
                ssl_send_receive(ssl, buffer, response, sizeof(response));
            }
            
            printf("\nAttendance completed for all students.\n");
        }
    }
}
