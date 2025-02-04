#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "handlers.h"
#include "network.h"

#define BUFFER_SIZE 1024
#define MAX_ITEMS 50

typedef struct {
    int id;
    char name[100];
    time_t unix_time;
} Item;

static void display_spacer() {
    for (int i = 0; i < 50; i++) printf("\n");
}

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

static void parse_server_response(const char* response, Item* items, int* count) {
    *count = 0;
    char* resp_copy = strdup(response);
    char* status = strtok(resp_copy, "/");
    
    if (strcmp(status, "202") == 0) {
        char* data = strtok(NULL, "");
        char* item = strtok(data, ";");
        
        while (item != NULL && *count < MAX_ITEMS) {
            char* id_str = strstr(item, "id:");
            char* name_str = strstr(item, "name:");
            char* time_str = strstr(item, "unix_time:");
            
            if (id_str && name_str) {
                sscanf(id_str, "id:%d", &items[*count].id);
                
                char name[100] = {0};
                if (time_str) {
                    sscanf(name_str, "name:%[^,]", name);
                    sscanf(time_str, "unix_time:%ld", &items[*count].unix_time);
                } else {
                    sscanf(name_str, "name:%[^;]", name);
                    items[*count].unix_time = 0;
                }
                strcpy(items[*count].name, name);
                (*count)++;
            }
            item = strtok(NULL, ";");
        }
    }
    free(resp_copy);
}

void display_student_list(const char* response) {
    Item students[MAX_ITEMS];
    int count;
    parse_server_response(response, students, &count);
    
    display_title("Student Management");
    
    for (int i = 0; i < count; i++) {
        printf("(%d) %s\n", students[i].id, students[i].name);
    }
    printf("\nOptions:\n");
    printf("1. Go back\n");
    printf("2. Create new student\n");
    printf("3. Delete student\n");
    printf("Choice: ");
}

void display_attendance_list(const char* response) {
    Item students[MAX_ITEMS];
    int count;
    parse_server_response(response, students, &count);
    
    display_title("Attendance Management");
    
    for (int i = 0; i < count; i++) {
        printf("(%d) %s\n", students[i].id, students[i].name);
    }
    printf("\nOptions:\n");
    printf("1. Go back\n");
    printf("2. Take attendance\n");
    printf("Choice: ");
}

void display_seance_list(const char* response) {
    Item seances[MAX_ITEMS];
    int count;
    parse_server_response(response, seances, &count);
    
    display_title("Seance Management");
    
    for (int i = 0; i < count; i++) {
        struct tm *tm_info = localtime(&seances[i].unix_time);
        char time_str[20];
        strftime(time_str, 20, "%d/%m/%Y %H:%M", tm_info);
        printf("%d. %s (%s)\n", 
               seances[i].id, 
               seances[i].name,
               time_str);
    }
    
    printf("\nOptions:\n");
    printf("1. Go back\n");
    printf("2. Create seance\n");
    printf("3. Delete seance\n");
    printf("4. Manage attendance\n");
    printf("Choice: ");
}

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
        if (choice == 4) {
            handle_attendance_management(ssl);
        }
    }
}
void parse_attendance_response(const char* response, int* status) {
    *status = 0;
    
    if (strstr(response, "202/") != NULL) {
        char* status_str = strstr(response, "status:");
        if (status_str) {
            *status = (status_str[7] == '1') ? 1 : 0;
        }
    }
}

void display_seance_attendance_status(const char* seance_name, Item* students, int* student_statuses, int count) {
    display_title("Attendance");
    printf("Seance: %s\n\n", seance_name);
    
    for (int i = 0; i < count; i++) {
        printf("%s (ID: %d): %s\n", 
               students[i].name, 
               students[i].id, 
               student_statuses[i] == 1 ? "Present" : "Absent");
    }
    
    printf("\nOptions:\n");
    printf("1. Go back\n");
    printf("2. Take attendance\n");
    printf("Choice: ");
}

void handle_attendance_management(SSL* ssl) {
    char buffer[BUFFER_SIZE];
    char response[BUFFER_SIZE];
    int choice = 0;
    int seance_id;
    
    printf("Enter seance id: ");
    scanf("%d", &seance_id);
    getchar();
    
    snprintf(buffer, sizeof(buffer), "<g>seance", response);
    ssl_send_receive(ssl, buffer, response, sizeof(response));
    Item seances[MAX_ITEMS];
    int seance_count;
    parse_server_response(response, seances, &seance_count);
    
    char* seance_name = "Unknown Seance";
    for (int i = 0; i < seance_count; i++) {
        if (seances[i].id == seance_id) {
            seance_name = seances[i].name;
            break;
        }
    }
    
    while (1) {
        ssl_send_receive(ssl, "<g>student", response, sizeof(response));
        Item students[MAX_ITEMS];
        int count;
        parse_server_response(response, students, &count);
        
        int student_statuses[MAX_ITEMS] = {0}; 
        
        for (int i = 0; i < count; i++) {
            snprintf(buffer, sizeof(buffer), "<g>attendance/%d/%d", seance_id, students[i].id);
            ssl_send_receive(ssl, buffer, response, sizeof(response));
            
            int status;
            parse_attendance_response(response, &status);
            student_statuses[i] = status;
        }
        
        display_seance_attendance_status(seance_name, students, student_statuses, count);
        
        scanf("%d", &choice);
        getchar();
        
        if (choice == 1) {
            break;
        }
        else if (choice == 2) {
            printf("Enter status for each student (1: present, 0: absent):\n\n");
            
            for (int i = 0; i < count; i++) {
                printf("- %s (ID: %d): %s => : ", 
                       students[i].name, 
                       students[i].id,
                       student_statuses[i] == 1 ? "Present" : "Absent");
                       
                int status;
                scanf("%d", &status);
                getchar();
                
                snprintf(buffer, sizeof(buffer), "<p>attendance/%d/%d/%d", 
                        seance_id, students[i].id, status);
                ssl_send_receive(ssl, buffer, response, sizeof(response));
            }
            
            printf("\nAttendance completed for all students.\n");
        }
    }
}