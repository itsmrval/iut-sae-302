#include <stdio.h>
#include <stdlib.h>
#include "network.h"
#include "handlers.h"

void display_title(const char* title);

int main(int argc, char *argv[]) {
    if (argc != 4) {
        fprintf(stderr, "Usage: %s <ip> <port> <cert_file>\n", argv[0]);
        exit(EXIT_FAILURE);
    }
    
    const char* hostname = argv[1];
    int port = atoi(argv[2]);
    const char* cert_file = argv[3];
    
    SSL* ssl = setup_ssl_connection(hostname, port, cert_file);
    
    while (1) {
        display_title("Main Menu");
        printf("1. Manage seances\n");
        printf("2. Manage students\n");
        printf("3. Exit\n");
        printf("Choice: ");
        
        int choice;
        scanf("%d", &choice);
        getchar();
        
        switch (choice) {
            case 1:
                handle_seance_management(ssl);
                break;
            case 2:
                handle_student_management(ssl);
                break;
            case 3:
                goto cleanup;
            default:
                printf("Invalid choice\n");
        }
    }
    
cleanup:
    cleanup_ssl_connection(ssl);
    return 0;
}