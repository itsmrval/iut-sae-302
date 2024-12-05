#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <stdbool.h>

#define PORT 8080
#define BUFFER_SIZE 1024

int main() {
    int server_fd, new_socket;
    struct sockaddr_in address;
    int addrlen = sizeof(address);
    char buffer[BUFFER_SIZE] = {0};
    bool server_running = true;

    // Create socket
    server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd < 0) {
        perror("[ERROR] Socket creation failed");
        exit(EXIT_FAILURE);
    }

    // Configure socket parameters
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    // Bind socket to port
    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0) {
        perror("Bind failed");
        exit(EXIT_FAILURE);
    }

    // Listen for connections
    if (listen(server_fd, 3) < 0) {
        perror("Listen failed");
        exit(EXIT_FAILURE);
    }

    printf("Server listening on port %d\n", PORT);

    while (server_running) {
        // Accept incoming connection
        new_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t*)&addrlen);
        if (new_socket < 0) {
            perror("Accept failed");
            continue;
        }

        printf("[CON] New client connected\n");

        // Handle client connection
        while (1) {
            memset(buffer, 0, BUFFER_SIZE);
            int valread = read(new_socket, buffer, BUFFER_SIZE);
            
            if (valread <= 0) {
                printf("[CON] Connection closed by client\n");
                break;
            }

            printf("[CLIENT] Received: %s\n", buffer);

            // Command example
            if (strcmp(buffer, "close") == 0) {
                printf("[CLIENT] Closing manually\n");
                break;
            }

            // exemple réponse
            char *response = "ACK\n";
            send(new_socket, response, strlen(response), 0);
        }

        // Close the client socket
        close(new_socket);
    }

    // Close server socket
    close(server_fd);

    return 0;
}