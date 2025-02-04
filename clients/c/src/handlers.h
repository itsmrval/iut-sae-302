#ifndef HANDLERS_H
#define HANDLERS_H

#include <openssl/ssl.h>

void display_student_list(const char* response);
void display_seance_list(const char* response);
void display_title(const char* title);
void display_attendance_list(const char* response);
void handle_student_management(SSL* ssl);
void handle_seance_management(SSL* ssl);
void handle_attendance_management(SSL* ssl);

#endif