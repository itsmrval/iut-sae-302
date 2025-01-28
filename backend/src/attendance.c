#include "attendance.h"
#include <stdio.h>

#define ATTENDANCE_FILE "data/attendance.csv"

bool set_attendance(Attendance* attendance) {
    FILE *file = fopen(ATTENDANCE_FILE, "r+");
    if (!file) return false;

    char line[256];
    bool found = false;
    long pos = 0;

    while (fgets(line, sizeof(line), file)) {
        int current_seance_id, current_student_id;
        if (sscanf(line, "%d,%d,", &current_seance_id, &current_student_id) == 2) {
            if (current_seance_id == attendance->seance_id && current_student_id == attendance->student_id) {
                fseek(file, pos, SEEK_SET);
                fprintf(file, "%d,%d,%d\n", attendance->seance_id, attendance->student_id, attendance->status);
                found = true;
                break;
            }
        }
        pos = ftell(file);
    }

    if (!found) {
        fseek(file, 0, SEEK_END);
        fprintf(file, "%d,%d,%d\n", attendance->seance_id, attendance->student_id, attendance->status);
    }

    fclose(file);
    return true;
}


bool get_attendance(int seance_id, int student_id, int* status) {
    FILE *file = fopen(ATTENDANCE_FILE, "r");
    if (!file) return false;

    char line[256];
    while (fgets(line, sizeof(line), file)) {
        int current_seance_id, current_student_id, current_status;
        if (sscanf(line, "%d,%d,%d", &current_seance_id, &current_student_id, &current_status) == 3) {
            if (current_seance_id == seance_id && current_student_id == student_id) {
                *status = current_status;

                fclose(file);
                return true;
            }
        }
    }

    *status = 0;
    fclose(file);
    return true;
}

// delete all attendance records for a given seance
bool delete_seance_attendance(int seance_id) {
    FILE *file = fopen(ATTENDANCE_FILE, "r+");
    if (!file) return false;

    FILE *temp = fopen("data/temp.csv", "w");
    if (!temp) {
        fclose(file);
        return false;
    }

    char line[256];
    while (fgets(line, sizeof(line), file)) {
        int current_seance_id;
        if (sscanf(line, "%d,", &current_seance_id) == 1) {
            if (current_seance_id != seance_id) {
                fprintf(temp, "%s", line);
            }
        }
    }

    fclose(file);
    fclose(temp);

    remove(ATTENDANCE_FILE);
    rename("data/temp.csv", ATTENDANCE_FILE);

    return true;
}

// delete all attendance records for a given student
bool delete_student_attendance(int student_id) {
    FILE *file = fopen(ATTENDANCE_FILE, "r+");
    if (!file) return false;

    FILE *temp = fopen("data/temp.csv", "w");
    if (!temp) {
        fclose(file);
        return false;
    }

    char line[256];
    while (fgets(line, sizeof(line), file)) {
        int current_student_id;
        if (sscanf(line, ",%d,", &current_student_id) == 1) {
            if (current_student_id != student_id) {
                fprintf(temp, "%s", line);
            }
        }
    }

    fclose(file);
    fclose(temp);

    remove(ATTENDANCE_FILE);
    rename("data/temp.csv", ATTENDANCE_FILE);

    return true;
}