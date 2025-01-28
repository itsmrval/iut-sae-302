#ifndef ATTENDANCE_MANAGER_H
#define ATTENDANCE_MANAGER_H

#include <gtk/gtk.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>

#define MAX_SEANCES 100
#define MAX_STUDENTS 50

// Structures for Seance and Student
typedef struct {
    int id;
    char name[50];
    time_t datetime;
} Seance;

typedef struct {
    int id;
    char name[50];
    int attendance[MAX_SEANCES];  // Attendance for each seance
} Student;

// Function declarations
void init_ui(GtkApplication *app, gpointer user_data);
void create_seance(GtkWidget *widget, gpointer user_data);
void manage_students(GtkWidget *widget, gpointer user_data);
void connect_to_server(const char *ip, int port);
void create_student(GtkWidget *widget, gpointer user_data);
void send_request(const char *request);
void on_window_destroy(GtkWidget *widget, gpointer user_data);

#endif
