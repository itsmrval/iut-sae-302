#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080
#define BUFFER_SIZE 1024

// Response Messages
const char* RESPONSE_OK = "200 OK\n";
const char* RESPONSE_BAD_REQUEST = "400 Bad Request\n";
const char* RESPONSE_NOT_FOUND = "404 Not Found\n";
const char* RESPONSE_DISCONNECT = "499 Client Disconnected\n";

// Function prototypes
int create_server_socket(int port);
void handle_client(int client_socket);
void handle_request(int client_socket, const char* request);
void handle_get(int client_socket, const char* content);
void handle_post(int client_socket, const char* content);
void handle_action(int client_socket, const char* content);

int create_server_socket(int port) {
    int server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd < 0) {
        perror("[ERROR] Socket creation failed");
        exit(EXIT_FAILURE);
    }

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

void handle_get(int client_socket, const char* content) {
    printf("[INFO] Handling GET request. Content: %s\n", content);
    send(client_socket, RESPONSE_OK, strlen(RESPONSE_OK), 0);
}

void handle_post(int client_socket, const char* content) {
    printf("[INFO] Handling POST request. Content: %s\n", content);
    send(client_socket, RESPONSE_OK, strlen(RESPONSE_OK), 0);
}

void handle_action(int client_socket, const char* content) {
    if (strcmp(content, "disconnect") == 0) {
        printf("[INFO] Action: Disconnect\n");
        send(client_socket, RESPONSE_DISCONNECT, strlen(RESPONSE_DISCONNECT), 0);
    } else {
        printf("[INFO] Action: %s\n", content);
        send(client_socket, RESPONSE_OK, strlen(RESPONSE_OK), 0);
    }
}

void handle_request(int client_socket, const char* request) {
    if (strncmp(request, "<g>", 3) == 0) {
        handle_get(client_socket, request + 3);
    } else if (strncmp(request, "<p>", 3) == 0) {
        handle_post(client_socket, request + 3);
    } else if (strncmp(request, "<a>", 3) == 0) {
        handle_action(client_socket, request + 3);
    } else {
        printf("[ERROR] Invalid request format\n");
        send(client_socket, RESPONSE_BAD_REQUEST, strlen(RESPONSE_BAD_REQUEST), 0);
    }
}

void handle_client(int client_socket) {
    char buffer[BUFFER_SIZE] = {0};

    while (1) {
        memset(buffer, 0, BUFFER_SIZE);
        int bytes_read = read(client_socket, buffer, BUFFER_SIZE);

        if (bytes_read <= 0) {
            printf("[INFO] Client disconnected\n");
            break;
        }

        printf("[INFO] Received: %s\n", buffer);

        handle_request(client_socket, buffer);

        if (strncmp(buffer, "<a>disconnect", 12) == 0) {
            printf("[INFO] Disconnecting client\n");
            break;
        }
    }

    close(client_socket);
    printf("[INFO] Connection closed\n");
}

int main() {
    int server_socket = create_server_socket(PORT);

    while (1) {
        struct sockaddr_in client_addr;
        socklen_t client_addr_len = sizeof(client_addr);
        int client_socket = accept(server_socket, (struct sockaddr*)&client_addr, &client_addr_len);

        if (client_socket < 0) {
            perror("[ERROR] Accept failed");
            continue;
        }

        handle_client(client_socket);
    }

    close(server_socket);
    printf("[INFO] Server socket closed\n");
    return 0;
}
