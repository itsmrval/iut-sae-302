#ifndef SEANCE_H
#define SEANCE_H

#include <stdbool.h>
#include <stddef.h>

typedef struct {
    int id;
    char name[100];
    int unix_time;
} Seance;

bool add_seance(Seance* seance);
bool get_seance(int id, char* name_buffer, int* unix_time);
bool get_seance_list(char* result);
bool delete_seance(int id);

#endif // SEANCE_H
