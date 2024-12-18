// student.h
#ifndef STUDENT_H
#define STUDENT_H

#include <stdbool.h>
#include <stddef.h>

// Function to add a student
bool add_student(int id, const char* name);

// Function to retrieve a student by ID
bool get_student(int id, char* name);

bool get_student_list(char* result);

#endif // STUDENT_H
