#include "attendance.h"
#include "seance.h"
#include "student.h"
#include <stdio.h>
#include <string.h>

#define ATTENDANCE_FILE "data/attendance.csv"

bool set_attendance(int seance_id, int student_id, int status) {
    if (status != 0 && status != 1) {
        return false;
    }

    // Check if student exist
    char student_name[100];
    if (!get_student(student_id, student_name)) {
        printf("Student not found\n");
        return false;
    }

    // Check if seance exist
    char seance_name[100];
    if (!get_seance(seance_id, seance_name, NULL)) {
        printf("Seance not found\n");
        return false;
    }

    FILE *file = fopen(ATTENDANCE_FILE, "r");
    FILE *temp_file = fopen("data/temp_attendance.csv", "w");
    bool record_found = false;

    if (!file || !temp_file) {
        if (file) fclose(file);
        if (temp_file) fclose(temp_file);
        return false;
    }

    char line[256];
    if (fgets(line, sizeof(line), file)) {
        fputs(line, temp_file);
    }

    // Process records
    while (fgets(line, sizeof(line), file)) {
        int existing_seance_id, existing_student_id, existing_status;
        
        if (sscanf(line, "%d,%d,%d", &existing_seance_id, &existing_student_id, &existing_status) == 3) {
            if (existing_seance_id == seance_id && existing_student_id == student_id) {
                fprintf(temp_file, "%d,%d,%d\n", seance_id, student_id, status);
                record_found = true;
            } else {
                fputs(line, temp_file);
            }
        } else {
            fputs(line, temp_file);
        }
    }

    fclose(file);

    // Append new record if not found
    if (!record_found) {
        fprintf(temp_file, "%d,%d,%d\n", seance_id, student_id, status);
    }

    fclose(temp_file);

    // Replace original file with updated file
    remove(ATTENDANCE_FILE);
    rename("data/temp_attendance.csv", ATTENDANCE_FILE);

    return true;
}

// Function to get attendance status for a student in a seance
bool get_attendance(int seance_id, int student_id, int* status) {
    FILE *file = fopen(ATTENDANCE_FILE, "r");
    if (!file) return false;

    char line[256];
    while (fgets(line, sizeof(line), file)) {
        int existing_seance_id, existing_student_id, existing_status;
        if (sscanf(line, "%d,%d,%d", &existing_seance_id, &existing_student_id, &existing_status) == 3) {
            if (existing_seance_id == seance_id && existing_student_id == student_id) {
                *status = existing_status;
                fclose(file);
                return true;
            }
        }
    }

    fclose(file);
    return false;
}