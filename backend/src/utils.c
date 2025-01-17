#include "utils.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>

#define STUDENTS_FILE "data/students.csv"
#define SEANCES_FILE "data/seances.csv"
#define ATTENDANCE_FILE "data/attendance.csv"

// Initialize CSV files with headers if they don't exist
bool init_db() {
    FILE *file;

    // Initialize students.csv
    file = fopen(STUDENTS_FILE, "r");
    if (!file) {
        file = fopen(STUDENTS_FILE, "w");
        if (!file) {
            perror("[ERROR] Unable to create students.csv");
            return false;
        }
        fprintf(file, "id,name\n");
    }
    fclose(file);

    // Initialize seances.csv
    file = fopen(SEANCES_FILE, "r");
    if (!file) {
        file = fopen(SEANCES_FILE, "w");
        if (!file) {
            perror("[ERROR] Unable to create seances.csv");
            return false;
        }
        fprintf(file, "id,name,unix_time\n");
    }
    fclose(file);

    // Initialize attendance.csv
    file = fopen(ATTENDANCE_FILE, "r");
    if (!file) {
        file = fopen(ATTENDANCE_FILE, "w");
        if (!file) {
            perror("[ERROR] Unable to create attendance.csv");
            return false;
        }
        fprintf(file, "seance_id,student_id,status\n");
    }
    fclose(file);

    return true;
}

// Signal handler for graceful shutdown
void handle_sigint() {
    printf("\n[INFO] Caught SIGINT signal. Shutting down gracefully...\n");
    exit(0);
}
