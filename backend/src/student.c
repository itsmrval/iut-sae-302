// student.c
#include "student.h"
#include <stdio.h>
#include <string.h>

#define STUDENTS_FILE "data/students.csv"

// Add a student
bool add_student(int id, const char* name) {
    FILE *file = fopen(STUDENTS_FILE, "r+");
    if (!file) return false;

    char line[256];
    fgets(line, sizeof(line), file); // Skip header

    // Check for duplicate ID
    while (fgets(line, sizeof(line), file)) {
        int existing_id;
        if (sscanf(line, "%d", &existing_id) == 1 && existing_id == id) {
            fclose(file);
            return false;
        }
    }

    // Append new student
    fprintf(file, "%d,%s\n", id, name);
    fclose(file);
    return true;
}

// Retrieve a student by ID
bool get_student(int id, char* name) {
    FILE *file = fopen(STUDENTS_FILE, "r");
    if (!file) return false;

    char line[256];
    fgets(line, sizeof(line), file); // Skip header

    while (fgets(line, sizeof(line), file)) {
        int current_id;
        if (sscanf(line, "%d,%99[^\n]", &current_id, name) == 2 && current_id == id) {
            fclose(file);
            return true;
        }
    }

    fclose(file);
    return false;
}

// Retrieve student list
bool get_student_list(char* result) {
    FILE *file = fopen(STUDENTS_FILE, "r");
    if (!file) return false;

    char line[256];
    fgets(line, sizeof(line), file); // Skip header

    while (fgets(line, sizeof(line), file)) {
        char name[100];
        int id;
        if (sscanf(line, "%d,%99[^\n]", &id, name) == 2) {
            sprintf(result, "%sid:%d,name:%s;", result, id, name);
        }
    }

    fclose(file);
    return true;
}
