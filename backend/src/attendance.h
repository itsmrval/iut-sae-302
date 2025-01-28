#ifndef ATTENDANCE_H
#define ATTENDANCE_H

#include <stdbool.h>

typedef struct {
    int seance_id;
    int student_id;
    int status;
} Attendance;

bool set_attendance(Attendance* attendance);
bool get_attendance(int seance_id, int student_id, int* status);
bool delete_seance_attendance(int seance_id);
bool delete_student_attendance(int student_id);

#endif // ATTENDANCE_H
