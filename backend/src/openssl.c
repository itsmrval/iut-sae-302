#include <openssl/ssl.h>
#include <openssl/err.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <netinet/in.h>
#include <arpa/inet.h>


SSL_CTX *ctx;
#define BUFFER_SIZE 1024

void handle_request(SSL *ssl, const char* request, const char* client_ip);


// Function to initialize OpenSSL
void init_openssl() {
    SSL_library_init();
    SSL_load_error_strings();
    OpenSSL_add_all_algorithms();

    ctx = SSL_CTX_new(TLS_server_method());
    if (!ctx) {
        perror("[ERROR] Unable to create SSL context");
        exit(EXIT_FAILURE);
    }

    // Load your certificate and private key
    if (SSL_CTX_use_certificate_file(ctx, "./ssl/cert.pem", SSL_FILETYPE_PEM) <= 0) {
        ERR_print_errors_fp(stderr);
        exit(EXIT_FAILURE);
    }

    if (SSL_CTX_use_PrivateKey_file(ctx, "./ssl/cert.key", SSL_FILETYPE_PEM) <= 0) {
        ERR_print_errors_fp(stderr);
        exit(EXIT_FAILURE);
    }
}

// Function to clean up OpenSSL
void cleanup_openssl() {
    SSL_CTX_free(ctx);
}

// Function to handle client connections with SSL
void handle_ssl_client(int client_socket, struct sockaddr_in client_addr) {
    SSL *ssl = SSL_new(ctx);
    SSL_set_fd(ssl, client_socket);

    if (SSL_accept(ssl) <= 0) {
        ERR_print_errors_fp(stderr);
    } else {
        char buffer[BUFFER_SIZE] = {0};
        char client_ip[INET_ADDRSTRLEN];

        inet_ntop(AF_INET, &client_addr.sin_addr, client_ip, INET_ADDRSTRLEN);
        printf("[%s] Connected\n", client_ip);

        while (1) {
            memset(buffer, 0, BUFFER_SIZE);
            int bytes_read = SSL_read(ssl, buffer, BUFFER_SIZE - 1);
            if (bytes_read <= 0) {
                printf("[%s] Disconnected\n", client_ip);
                break;
            }
            
            // We need to pass the SSL object to handle_request
            handle_request(ssl, buffer, client_ip);
        }
    }

    SSL_shutdown(ssl);
    SSL_free(ssl);
    close(client_socket);
}