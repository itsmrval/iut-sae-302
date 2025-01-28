#include "student.h"
#include <stdio.h>
#include <string.h>

#define STUDENTS_FILE "data/students.csv"


bool delete_student_attendance(int id);

bool add_student(Student* student) {
    FILE *file = fopen(STUDENTS_FILE, "a+");
    if (!file) return false;

    char line[256];
    while (fgets(line, sizeof(line), file)) {
        int existing_id;
        if (sscanf(line, "%d", &existing_id) == 1 && existing_id == student->id) {
            fclose(file);
            return false;
        }
    }

    fprintf(file, "%d,%s\n", student->id, student->name);
    fclose(file);
    return true;
}

bool get_student(int id, char* name) {
    FILE *file = fopen(STUDENTS_FILE, "r");
    if (!file) return false;

    char line[256];
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

bool get_student_list(char* result) {
    FILE *file = fopen(STUDENTS_FILE, "r");
    if (!file) return false;

    char line[256];
    *result = '\0';

    while (fgets(line, sizeof(line), file)) {
        int id;
        char name[100];
        if (sscanf(line, "%d,%99[^\n]", &id, name) == 2) {
            sprintf(result + strlen(result), "id:%d,name:%s;", id, name);
        }
    }

    fclose(file);
    return true;
}

bool delete_student(int id) {
    FILE *file = fopen(STUDENTS_FILE, "r");
    if (!file) return false;

    FILE *temp_file = fopen("data/temp_students.csv", "w");
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

    remove(STUDENTS_FILE);
    rename("data/temp_students.csv", STUDENTS_FILE);
    delete_student_attendance(id);

    return found;
}
