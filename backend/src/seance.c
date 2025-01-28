#include "seance.h"
#include <stdio.h>
#include <string.h>

#define SEANCES_FILE "data/seances.csv"

bool delete_seance_attendance(int id);

bool add_seance(Seance* seance) {
    FILE *file = fopen(SEANCES_FILE, "a+");
    if (!file) return false;

    // Check for duplicate ID
    char line[256];
    while (fgets(line, sizeof(line), file)) {
        int existing_id;
        if (sscanf(line, "%d", &existing_id) == 1 && existing_id == seance->id) {
            fclose(file);
            return false; // ID already exists
        }
    }

    // Append new seance with proper formatting
    fprintf(file, "%d,%s,%d\n", seance->id, seance->name, seance->unix_time);
    
    fclose(file);
    return true;
}

bool get_seance(int id, char* name_buffer, int* unix_time) {
    FILE *file = fopen(SEANCES_FILE, "r");
    if (!file) return false;

    char line[256];
    while (fgets(line, sizeof(line), file)) {
        int current_id, temp_time;
        char temp_name[100];

        // Use temporary variables for safety
        if (sscanf(line, "%d,%99[^,],%d", &current_id, temp_name, &temp_time) == 3 && current_id == id) {
            // Write only if buffers are valid
            if (name_buffer) strncpy(name_buffer, temp_name, 100);
            if (unix_time) *unix_time = temp_time;

            fclose(file);
            return true;
        }
    }

    fclose(file);
    return false;
}

bool get_seance_list(char* result) {
    FILE *file = fopen(SEANCES_FILE, "r");
    if (!file) return false;

    char line[256];
    *result = '\0';

    while (fgets(line, sizeof(line), file)) {
        int id;
        char name[100];
        int time;
        if (sscanf(line, "%d,%99[^,],%d", &id, name, &time) == 3) {
            sprintf(result + strlen(result), "id:%d,name:%s,unix_time:%d;", id, name, time);
        }
    }

    fclose(file);
    return true;
}

bool delete_seance(int id) {
    FILE *file = fopen(SEANCES_FILE, "r");
    if (!file) return false;

    FILE *temp_file = fopen("data/temp_seances.csv", "w");
    if (!temp_file) {
        fclose(file);
        return false;
    }

    char line[256];
    bool found = false;

    while (fgets(line, sizeof(line), file)) {
        int current_id;
        sscanf(line, "%d", &current_id);
        if (current_id == id) {
            found = true;
            continue;
        }
        fprintf(temp_file, "%s", line);
    }

    fclose(file);
    fclose(temp_file);

    remove(SEANCES_FILE);
    rename("data/temp_seances.csv", SEANCES_FILE);
    delete_seance_attendance(id);

    return found;
}

