#ifndef HANDLERS_H
#define HANDLERS_H

#include <openssl/ssl.h>

void handle_student_management(SSL* ssl);
void handle_seance_management(SSL* ssl);
void handle_attendance_management(SSL* ssl);

#endif