#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "display.h"

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
}