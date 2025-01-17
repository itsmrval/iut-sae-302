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

#endif // ATTENDANCE_H
