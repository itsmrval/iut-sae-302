#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <signal.h>

#define PORT 8081
#define BUFFER_SIZE 1024

// Defining response messages
const char* RESPONSE_OK = "202 OK";
const char* RESPONSE_BAD_REQUEST = "400 Bad Request";
const char* RESPONSE_NOT_FOUND = "404 Not Found";
const char* RESPONSE_DISCONNECT = "499 Client Disconnected";
const char* RESPONSE_CLOSE = "499 Server Closed";

int server_socket; // Global server socket for cleanup

// Signal handler for SIGINT (Ctrl+C)
void handle_sigint() {
    printf("\n[INFO] Shutting down server...\n");
    close(server_socket); // Close the server socket
    exit(0);              // Exit the program
}

// Function to create a server socket
int create_server_socket(int port) {
    int server_fd = socket(AF_INET, SOCK_STREAM, 0);

    if (server_fd < 0) {
        perror("[ERROR] Socket creation failed");
        exit(EXIT_FAILURE);
    }

    // Enable SO_REUSEADDR to reuse the port immediately after closing
    int opt = 1;
    setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

    struct sockaddr_in address;
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(port);

    if (bind(server_fd, (struct sockaddr*)&address, sizeof(address)) < 0) {
        perror("[ERROR] Bind failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    if (listen(server_fd, 3) < 0) {
        perror("[ERROR] Listen failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    printf("[INFO] Server listening on port %d\n", port);
    return server_fd;
}

// Function to handle GET requests
void handle_get(int client_socket, const char* content) {
    send(client_socket, RESPONSE_OK, strlen(RESPONSE_OK), 0);
}

// Function to handle POST requests
void handle_post(int client_socket, const char* content) {
    printf("POST Content: %s\n", content);
    send(client_socket, RESPONSE_OK, strlen(RESPONSE_OK), 0);
}

// Function to handle ACTION requests
void handle_action(int client_socket, const char* content) {
    if (strcmp(content, "disconnect") == 0) {
        send(client_socket, RESPONSE_DISCONNECT, strlen(RESPONSE_DISCONNECT), 0);
    } else if (strcmp(content, "close") == 0) {
        send(client_socket, RESPONSE_CLOSE, strlen(RESPONSE_CLOSE), 0);
        close(client_socket);
        exit(EXIT_SUCCESS);
    } else {
        send(client_socket, RESPONSE_NOT_FOUND, strlen(RESPONSE_NOT_FOUND), 0);
    }
}

// Function to handle client requests and send them to the appropriate handler
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
    } else {
        printf("[%s] Invalid request format\n", client_ip);
        send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
    }
}

// Function to handle client connections
void handle_client(int client_socket, struct sockaddr_in client_addr) {
    char buffer[BUFFER_SIZE] = {0};
    char client_ip[INET_ADDRSTRLEN];

    inet_ntop(AF_INET, &client_addr.sin_addr, client_ip, INET_ADDRSTRLEN);
    printf("[%s] Connected\n", client_ip);

    while (1) {
        memset(buffer, 0, BUFFER_SIZE);
        int bytes_read = read(client_socket, buffer, BUFFER_SIZE);

        if (bytes_read <= 0) { 
            printf("[%s] Disconnected\n", client_ip);
            break;
        }

        handle_request(client_socket, buffer, client_ip);
    }

    close(client_socket);
}

int main() {
    // Register the signal handler for SIGINT
    signal(SIGINT, handle_sigint);

    // Create the server socket
    server_socket = create_server_socket(PORT);

    while (1) {
        struct sockaddr_in client_addr; // Client address
        socklen_t client_addr_len = sizeof(client_addr); // Client address length
        int client_socket = accept(server_socket, (struct sockaddr*)&client_addr, &client_addr_len);

        // Handle accept errors
        if (client_socket < 0) {
            perror("[ERROR] Accept failed");
            continue;
        }

        handle_client(client_socket, client_addr);
    }

    close(server_socket);
    printf("[INFO] Server socket closed\n");
    return 0;
}
