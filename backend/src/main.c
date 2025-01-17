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
void handle_request(int client_socket, const char* request, const char* client_ip);
void handle_client(int client_socket, struct sockaddr_in client_addr);
void handle_get(int client_socket, const char* content);
void handle_post(int client_socket, const char* content);
void handle_action(int client_socket, const char* content);
void handle_delete(int client_socket, const char* content);
void initialize_server();
void cleanup_and_exit();

int main() {
    // Initialize the database
    if (!init_db()) {
        fprintf(stderr, "[ERROR] Failed to initialize the database.\n");
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

        handle_client(client_socket, address);
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

// Function to handle each client connection
void handle_client(int client_socket, struct sockaddr_in client_addr) {
    char buffer[BUFFER_SIZE] = {0};
    char client_ip[INET_ADDRSTRLEN];

    inet_ntop(AF_INET, &client_addr.sin_addr, client_ip, INET_ADDRSTRLEN);
    printf("[%s] Connected\n", client_ip);

    while (1) {
        memset(buffer, 0, BUFFER_SIZE);
        int bytes_read = recv(client_socket, buffer, BUFFER_SIZE - 1, 0);

        if (bytes_read <= 0) {
            printf("[%s] Disconnected\n", client_ip);
            break;
        }

        handle_request(client_socket, buffer, client_ip);
    }

    close(client_socket);
}

// Function to handle client requests and delegate to appropriate handlers
void handle_request(int client_socket, const char* request, const char* client_ip) {
    if (strncmp(request, "<g>", 3) == 0) {
        printf("[%s] GET Request: %s\n", client_ip, request + 3);
        handle_get(client_socket, request + 3);
    } else if (strncmp(request, "<p>", 3) == 0) {
        printf("[%s] POST Request: %s\n", client_ip, request + 3);
        handle_post(client_socket, request + 3);
    } else if (strncmp(request, "<a>", 3) == 0) {
        printf("[%s] ACTION Request: %s\n", client_ip, request + 3);
        handle_action(client_socket, request + 3);
    } else if (strncmp(request, "<d>", 3) == 0) {
        printf("[%s] DELETE Request: %s\n", client_ip, request + 3);
        handle_delete(client_socket, request + 3);
    } else {
        printf("[%s] Invalid request format: %s\n", client_ip, request);
        send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
    }
}

// Function to handle GET requests
void handle_get(int client_socket, const char* content) {
    char response[BUFFER_SIZE];
    if (strncmp(content, "student", 7) == 0) {
        int id;
        if (sscanf(content + 8, "%d", &id) == 1) {
            char name[100];
            if (get_student(id, name)) {
                snprintf(response, sizeof(response), "202/id:%d,name:%s\n", id, name);
                send(client_socket, response, strlen(response), 0);
            } else {
                send(client_socket, RESPONSE_ERROR, strlen(RESPONSE_ERROR), 0);
            }
        } else {
            char list[BUFFER_SIZE] = "";
            if (get_student_list(list)) {
                snprintf(response, sizeof(response), "202/%s\n", list);
                send(client_socket, response, strlen(response), 0);
            } else {
                send(client_socket, RESPONSE_ERROR, strlen(RESPONSE_ERROR), 0);
            }
        }
    } else if (strncmp(content, "seance", 6) == 0) {
        int id;
        if (sscanf(content + 7, "%d", &id) == 1) {
            char name[100];
            int unix_time;
            if (get_seance(id, name, &unix_time)) {
                snprintf(response, sizeof(response), "202/id:%d,name:%s,unix_time:%d\n", id, name, unix_time);
                send(client_socket, response, strlen(response), 0);
            } else {
                send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
            }
        } else {
            char list[BUFFER_SIZE] = "";
            if (get_seance_list(list)) {
                snprintf(response, sizeof(response), "202/%s\n", list);
                send(client_socket, response, strlen(response), 0);
            } else {
                send(client_socket, RESPONSE_ERROR, strlen(RESPONSE_ERROR), 0);
            }
        }
    } else if (strncmp(content, "attendance", 10) == 0) {
        int id_student, id_seance;
        if (sscanf(content + 11, "%d/%d", &id_seance, &id_student) == 2) {
            int status;
            if (get_attendance(id_seance, id_student, &status)) {
                snprintf(response, sizeof(response), "202/seance_id:%d,student_id:%d,status:%d\n", id_seance, id_student, status);
                send(client_socket, response, strlen(response), 0);
            } else {
                send(client_socket, RESPONSE_ERROR, strlen(RESPONSE_ERROR), 0);
            }
        } else {
            send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
        }
    } else {
        send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
    }
}

// Function to handle POST requests
void handle_post(int client_socket, const char* content) {
    if (strncmp(content, "student/", 8) == 0) {
        char name[100];
        if (sscanf(content + 8, "%99[^/\n]", name) == 1) {
            Student student;
            student.id = 0; // Start with 0 and increment to find the next available ID
            char existing_name[100];
            while (get_student(student.id, existing_name)) {
                student.id++;
            }
            strncpy(student.name, name, sizeof(student.name) - 1);
            student.name[sizeof(student.name) - 1] = '\0'; // Ensure null termination

            if (add_student(&student)) {
                char response[BUFFER_SIZE];
                snprintf(response, sizeof(response), "202/id:%d,name:%s\n", student.id, student.name);
                send(client_socket, response, strlen(response), 0);
            } else {
                send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
            }
        } else {
            send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
        }
    } else if (strncmp(content, "attendance/", 11) == 0) {
        int seance_id, student_id, status;
        if (sscanf(content + 11, "%d/%d/%d", &seance_id, &student_id, &status) == 3) {
            Attendance attendance = {seance_id, student_id, status};
            if (set_attendance(&attendance)) {
                send(client_socket, RESPONSE_OK, strlen(RESPONSE_OK), 0);
            } else {
                send(client_socket, RESPONSE_ERROR, strlen(RESPONSE_ERROR), 0);
            }
        } else {
            send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
        }
    } else if (strncmp(content, "seance/", 7) == 0) {
        int unix_time;
        char name[256];

        printf("Incoming seance request: %s\n", content);

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
                snprintf(response, sizeof(response), "202/id:%d,name:%s\n", seance.id, seance.name);
                send(client_socket, response, strlen(response), 0);
            } else {
                send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
            }
        } else {
            send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
        }
    }
}

// Function to handle ACTION requests
void handle_action(int client_socket, const char* content) {
    if (strcmp(content, "disconnect") == 0) {
        send(client_socket, RESPONSE_DISCONNECTED, strlen(RESPONSE_DISCONNECTED), 0);
    } else if (strcmp(content, "close") == 0) {
        send(client_socket, RESPONSE_CLOSED, strlen(RESPONSE_CLOSED), 0);
        cleanup_and_exit();
    } else {
        send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
    }
}

// Function to handle DELETE requests
void handle_delete(int client_socket, const char* content) {
    if (strncmp(content, "student/", 8) == 0) {
        int id;
        if (sscanf(content + 8, "%d", &id) == 1) {
            if (delete_student(id)) {
                send(client_socket, RESPONSE_OK, strlen(RESPONSE_OK), 0);
            } else {
                send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
            }
        } else {
            send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
        }
    } else if (strncmp(content, "seance/", 7) == 0) {
        int id;
        if (sscanf(content + 7, "%d", &id) == 1) {
            if (delete_seance(id)) {
                send(client_socket, RESPONSE_OK, strlen(RESPONSE_OK), 0);
            } else {
                send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
            }
        } else {
            send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
        }
    } else {
        send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
    }
}


// Cleanup function for graceful shutdown
void cleanup_and_exit() {
    close(server_socket);
    printf("[INFO] Server is shutting down...\n");
    exit(0);
}
