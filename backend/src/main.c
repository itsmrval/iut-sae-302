#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <signal.h> 
#include "utils.h"
#include "seance.h"
#include "student.h"
#include "attendance.h"
#include "openssl.h"
#define PORT 8081
#define BUFFER_SIZE 1024

// Defining response messages
const char* RESPONSE_OK = "202/OK";
const char* RESPONSE_BAD_REQUEST = "400/BAD";
const char* RESPONSE_DISCONNECTED = "499/DISCONNECTED";
const char* RESPONSE_CLOSED = "499/CLOSED";
const char* RESPONSE_ERROR = "500/ERROR";

// Global server socket for cleanup
int server_socket;

// Function Prototypes
void handle_request(SSL *ssl, const char* request, const char* client_ip);
void handle_client(int client_socket, struct sockaddr_in client_addr);
void handle_get(SSL *ssl, const char* content);
void handle_post(SSL *ssl, const char* content);
void handle_action(SSL *ssl, const char* content);
void handle_delete(SSL *ssl, const char* content);
void initialize_server();
void cleanup_and_exit();


int main() {
    // Initialize OpenSSL
    init_openssl();

    // Initialize the database
    if (!init_db()) {
        fprintf(stderr, "[ERROR] Failed to initialize the database.\n");
        cleanup_openssl(); // Clean up OpenSSL
        exit(EXIT_FAILURE);
    }

    // Setup signal handler for graceful shutdown
    signal(SIGINT, cleanup_and_exit);

    // Initialize server
    initialize_server();

    // Server loop
    while (1) {
        struct sockaddr_in address;
        socklen_t addrlen = sizeof(address);
        int client_socket = accept(server_socket, (struct sockaddr*)&address, &addrlen);
        if (client_socket < 0) {
            perror("[ERROR] Accept failed");
            continue;
        }

        handle_ssl_client(client_socket, address); // Use SSL client handler
    }

    return 0;
}

// Function to initialize the server
void initialize_server() {
    server_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (server_socket < 0) {
        perror("[ERROR] Socket creation failed");
        exit(EXIT_FAILURE);
    }

    int opt = 1;
    if (setsockopt(server_socket, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt)) < 0) {
        perror("[ERROR] setsockopt failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    struct sockaddr_in address;

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    if (bind(server_socket, (struct sockaddr*)&address, sizeof(address)) < 0) {
        perror("[ERROR] Bind failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    if (listen(server_socket, 3) < 0) {
        perror("[ERROR] Listen failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    printf("[INFO] Server is listening on port %d...\n", PORT);
}

// Function to handle client requests and delegate to appropriate handlers
void handle_request(SSL *ssl, const char* request, const char* client_ip) {
    if (strncmp(request, "<g>", 3) == 0) {
        printf("[%s] GET Request: %s\n", client_ip, request + 3);
        handle_get(ssl, request + 3);
    } else if (strncmp(request, "<p>", 3) == 0) {
        printf("[%s] POST Request: %s\n", client_ip, request + 3);
        handle_post(ssl, request + 3);
    } else if (strncmp(request, "<a>", 3) == 0) {
        printf("[%s] ACTION Request: %s\n", client_ip, request + 3);
        handle_action(ssl, request + 3);
    } else if (strncmp(request, "<d>", 3) == 0) {
        printf("[%s] DELETE Request: %s\n", client_ip, request + 3);
        handle_delete(ssl, request + 3);
    } else {
        printf("[%s] Invalid request format: %s\n", client_ip, request);
        SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
    }
}

// Function to handle GET requests
void handle_get(SSL *ssl, const char* content) {
    char response[BUFFER_SIZE];
    if (strncmp(content, "student", 7) == 0) {
        int id;
        if (sscanf(content + 8, "%d", &id) == 1) {
            char name[100];
            if (get_student(id, name)) {
                snprintf(response, sizeof(response), "202/id:%d,name:%s", id, name);
                SSL_write(ssl, response, strlen(response));
            } else {
                SSL_write(ssl, RESPONSE_ERROR, strlen(RESPONSE_ERROR));
            }
        } else {
            char list[BUFFER_SIZE] = "";
            if (get_student_list(list)) {
                snprintf(response, sizeof(response), "202/%s", list);
                SSL_write(ssl, response, strlen(response));
            } else {
                SSL_write(ssl, RESPONSE_ERROR, strlen(RESPONSE_ERROR));
            }
        }
    } else if (strncmp(content, "seance", 6) == 0) {
        int id;
        if (sscanf(content + 7, "%d", &id) == 1) {
            char name[100];
            int unix_time;
            if (get_seance(id, name, &unix_time)) {
                snprintf(response, sizeof(response), "202/id:%d,name:%s,unix_time:%d", id, name, unix_time);
                SSL_write(ssl, response, strlen(response));
            } else {
                SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
            }
        } else {
            char list[BUFFER_SIZE] = "";
            if (get_seance_list(list)) {
                snprintf(response, sizeof(response), "202/%s", list);
                SSL_write(ssl, response, strlen(response));
            } else {
                SSL_write(ssl, RESPONSE_ERROR, strlen(RESPONSE_ERROR));
            }
        }
    } else if (strncmp(content, "attendance", 10) == 0) {
        int id_student, id_seance;
        if (sscanf(content + 11, "%d/%d", &id_seance, &id_student) == 2) {
            int status;
            if (get_attendance(id_seance, id_student, &status)) {
                snprintf(response, sizeof(response), "202/seance_id:%d,student_id:%d,status:%d", id_seance, id_student, status);
                SSL_write(ssl, response, strlen(response));
            } else {
                SSL_write(ssl, RESPONSE_ERROR, strlen(RESPONSE_ERROR));
            }
        } else {
            SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
        }
    } else {
        SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
    }
}

// Function to handle POST requests
void handle_post(SSL *ssl, const char* content) {
    if (strncmp(content, "student/", 8) == 0) {
        char name[100];
        if (sscanf(content + 8, "%99[^/\n]", name) == 1) {
            Student student;
            student.id = 0;
            char existing_name[100];
            while (get_student(student.id, existing_name)) {
                student.id++;
            }
            strncpy(student.name, name, sizeof(student.name) - 1);
            student.name[sizeof(student.name) - 1] = '\0';

            if (add_student(&student)) {
                char response[BUFFER_SIZE];
                snprintf(response, sizeof(response), "202/id:%d,name:%s", student.id, student.name);
                SSL_write(ssl, response, strlen(response));
            } else {
                SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
            }
        } else {
            SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
        }
    } else if (strncmp(content, "attendance/", 11) == 0) {
        int seance_id, student_id, status;
        if (sscanf(content + 11, "%d/%d/%d", &seance_id, &student_id, &status) == 3) {
            Attendance attendance = {seance_id, student_id, status};
            if (set_attendance(&attendance)) {
                SSL_write(ssl, RESPONSE_OK, strlen(RESPONSE_OK));
            } else {
                SSL_write(ssl, RESPONSE_ERROR, strlen(RESPONSE_ERROR));
            }
        } else {
            SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
        }
    } else if (strncmp(content, "seance/", 7) == 0) {
        int unix_time;
        char name[256];
        if (sscanf(content + 7, "%255[^/]/%d", name, &unix_time) == 2) {
            Seance seance;
            seance.id = 0;
            char existing_name[100];
            while (get_seance(seance.id, existing_name, NULL)) {
                seance.id++;
            }
            strncpy(seance.name, name, sizeof(seance.name) - 1);
            seance.name[sizeof(seance.name) - 1] = '\0';
            seance.unix_time = unix_time;

            if (add_seance(&seance)) {
                char response[BUFFER_SIZE];
                snprintf(response, sizeof(response), "202/id:%d,name:%s", seance.id, seance.name);
                SSL_write(ssl, response, strlen(response));
            } else {
                SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
            }
        } else {
            SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
        }
    }
}

void handle_action(SSL *ssl, const char* content) {
    if (strcmp(content, "disconnect") == 0) {
        SSL_write(ssl, RESPONSE_DISCONNECTED, strlen(RESPONSE_DISCONNECTED));
    } else if (strcmp(content, "close") == 0) {
        SSL_write(ssl, RESPONSE_CLOSED, strlen(RESPONSE_CLOSED));
        cleanup_and_exit();
    } else {
        SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
    }
}

void handle_delete(SSL *ssl, const char* content) {
    if (strncmp(content, "student/", 8) == 0) {
        int id;
        if (sscanf(content + 8, "%d", &id) == 1) {
            if (delete_student(id)) {
                SSL_write(ssl, RESPONSE_OK, strlen(RESPONSE_OK));
            } else {
                SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
            }
        } else {
            SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
        }
    } else if (strncmp(content, "seance/", 7) == 0) {
        int id;
        if (sscanf(content + 7, "%d", &id) == 1) {
            if (delete_seance(id)) {
                SSL_write(ssl, RESPONSE_OK, strlen(RESPONSE_OK));
            } else {
                SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
            }
        } else {
            SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
        }
    } else {
        SSL_write(ssl, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST));
    }
}

// Cleanup function for graceful shutdown
void cleanup_and_exit() {
    cleanup_openssl();
    close(server_socket);
    printf("[INFO] Server is shutting down...\n");
    exit(0);
}