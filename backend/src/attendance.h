// attendance.h
#ifndef ATTENDANCE_H
#define ATTENDANCE_H

#include <stdbool.h>

// Function to set attendance status for a student in a seance
bool set_attendance(int seance_id, int student_id, int status);

bool get_attendance(int seance_id, int student_id, int* status);

#endif // ATTENDANCE_H
