#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "handlers.h"
#include "network.h"
#include "display.h"

#define BUFFER_SIZE 1024

void handle_student_management(SSL* ssl) {
    char buffer[BUFFER_SIZE];
    char response[BUFFER_SIZE];
    int choice = 0;
    
    while (choice != 1) {
        ssl_send_receive(ssl, "<g>student", response, sizeof(response));
        display_student_list(response);
        
        scanf("%d", &choice);
        getchar();
        
        if (choice == 2) {
            printf("Enter student name: ");
            char name[100];
            fgets(name, sizeof(name), stdin);
            name[strcspn(name, "\n")] = 0;
            
            snprintf(buffer, sizeof(buffer), "<p>student/%s", name);
            ssl_send_receive(ssl, buffer, response, sizeof(response));
            
            ssl_send_receive(ssl, "<g>student", response, sizeof(response));
            display_student_list(response);
        }
        if (choice == 3) {
            printf("Enter student id: ");
            int id;
            scanf("%d", &id);
            getchar();
            
            snprintf(buffer, sizeof(buffer), "<d>student/%d", id);
            ssl_send_receive(ssl, buffer, response, sizeof(response));
            
            ssl_send_receive(ssl, "<g>student", response, sizeof(response));
            display_student_list(response);
        }
    }
}

void handle_seance_management(SSL* ssl) {
    char buffer[BUFFER_SIZE];
    char response[BUFFER_SIZE];
    int choice = 0;
    
    while (choice != 1) {
        ssl_send_receive(ssl, "<g>seance", response, sizeof(response));
        display_seance_list(response);
    
        scanf("%d", &choice);
        getchar();
        if (choice == 2) {
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
            snprintf(datetime, sizeof(datetime), "%s %s", date, time);
            
            if (strptime(datetime, "%d/%m/%Y %H:%M", &tm) != NULL) {
                time_t unix_time = mktime(&tm);
                snprintf(buffer, sizeof(buffer), "<p>seance/%s/%ld", name, unix_time);
                ssl_send_receive(ssl, buffer, response, sizeof(response));
                
                ssl_send_receive(ssl, "<g>seance", response, sizeof(response));
                display_seance_list(response);
            }
        }
        if (choice == 3) {
            printf("Enter seance id: ");
            int id;
            scanf("%d", &id);
            getchar();

            
            snprintf(buffer, sizeof(buffer), "<d>seance/%d", id);
            ssl_send_receive(ssl, buffer, response, sizeof(response));
            
            ssl_send_receive(ssl, "<g>seance", response, sizeof(response));
            display_seance_list(response);
        }
    }
}

void handle_attendance_management(SSL* ssl) {
    char buffer[BUFFER_SIZE];
    char response[BUFFER_SIZE];
    int choice = 0;
    
    while (choice != 1) {
        ssl_send_receive(ssl, "<g>seance", response, sizeof(response));
        display_attendance_list(response);

        scanf("%d", &choice);
        getchar();

    if (choice == 2) {
        printf("Enter seance id: ");
        int id;
        scanf("%d", &id);
        getchar();
        
        printf("Enter student id: ");
        int student_id;
        scanf("%d", &student_id);
        getchar();

        printf("Enter attendance status (1: present, 0: absent): ");
        int status;
        scanf("%d", &status);
        getchar();

        snprintf(buffer, sizeof(buffer), "<p>attendance/%d/%d/%d", id, student_id, status);
        ssl_send_receive(ssl, buffer, response, sizeof(response));
        display_attendance_list(response);
    }
    }

    

}
