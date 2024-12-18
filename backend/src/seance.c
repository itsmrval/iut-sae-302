#include "seance.h"
#include <stdio.h>
#include <string.h>
#include <time.h>

#define SEANCES_FILE "data/seances.csv"

// Add a seance to seances.csv
bool add_seance(int id, const char *name, int unix_time) {
    FILE *file = fopen(SEANCES_FILE, "a+"); // Open in append mode
    if (!file) return false;

    char line[256];
    rewind(file); 

    // Skip header line
    if (!fgets(line, sizeof(line), file)) {
        fclose(file);
        return false; // File is empty or unreadable
    }

    // Check for duplicate ID
    while (fgets(line, sizeof(line), file)) {
        int existing_id;
        if (sscanf(line, "%d", &existing_id) == 1 && existing_id == id) {
            fclose(file);
            return false; // Duplicate ID found
        }
    }

    // Append new seance
    fprintf(file, "%d,%s,%d\n", id, name, unix_time);
    fclose(file);
    return true;
}


// Retrieve a seance by ID
bool get_seance(int id, char* name_buffer, int* unix_time) {
    FILE *file = fopen(SEANCES_FILE, "r");
    if (!file) {
        perror("Error opening file");
        return false;
    }

    char line[256];
    fgets(line, sizeof(line), file); // Skip header

    while (fgets(line, sizeof(line), file)) {
        int current_id;
        char name[100];
        int time;

        if (sscanf(line, "%d,%99[^,],%d", &current_id, name, &time) == 3 && current_id == id) {
            printf("Found seance: %d, %s, %d\n", current_id, name, time);

            // Safely copy the name to the buffer
            strncpy(name_buffer, name, 99);
            name_buffer[99] = '\0';

            // Assign time if unix_time is not NULL
            if (unix_time) {
                *unix_time = time;
            }

            fclose(file);
            return true;
        }
    }

    fclose(file);
    return false;
}
// Retrieve seance list
bool get_seance_list(char* result) {
    FILE *file = fopen(SEANCES_FILE, "r");
    if (!file) return false;

    char line[256];
    *result = '\0';

    fgets(line, sizeof(line), file); // Skip header

    while (fgets(line, sizeof(line), file)) {
        char name[100];
        int id;
        int time;
        if (sscanf(line, "%d,%99[^,],%d", &id, name, &time) == 3) {
            char entry[256];
            snprintf(entry, sizeof(entry), "id:%d,name:%s,unix_time:%d;", id, name, time);
            strcat(result, entry);
        }
    }

    fclose(file);
    return true;
}