// seance.h
#ifndef SEANCE_H
#define SEANCE_H

#include <stdbool.h>
#include <stddef.h>
#include <time.h>

// Function to add a seance
bool add_seance(int id, const char* name, int unix_time);

// Function to retrieve a seance by ID
bool get_seance(int id, char* name_buffer, int* unix_time);

bool get_seance_list(char* result);

#endif // SEANCE_H
