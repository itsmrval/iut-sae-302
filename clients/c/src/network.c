#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <openssl/ssl.h>
#include <openssl/err.h>
#include "network.h"

static SSL_CTX* create_context() {
    const SSL_METHOD *method = TLS_client_method();
    SSL_CTX *ctx = SSL_CTX_new(method);
    if (!ctx) {
        exit(EXIT_FAILURE);
    }
    return ctx;
}

static void init_openssl() {
    SSL_load_error_strings();
    OpenSSL_add_ssl_algorithms();
}

static void cleanup_openssl() {
    EVP_cleanup();
}

static int create_socket(const char* hostname, int port) {
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        exit(EXIT_FAILURE);
    }

    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = inet_addr(hostname);

    if (connect(sock, (struct sockaddr*)&addr, sizeof(addr)) != 0) {
        exit(EXIT_FAILURE);
    }

    return sock;
}

SSL* setup_ssl_connection(const char* hostname, int port, const char* cert_file) {
    init_openssl();
    SSL_CTX *ctx = create_context();
    
    if (SSL_CTX_load_verify_locations(ctx, cert_file, NULL) <= 0) {
        exit(EXIT_FAILURE);
    }
    
    int sock = create_socket(hostname, port);
    SSL *ssl = SSL_new(ctx);
    SSL_set_fd(ssl, sock);
    
    if (SSL_connect(ssl) <= 0) {
        exit(EXIT_FAILURE);
    }
    
    return ssl;
}

void cleanup_ssl_connection(SSL* ssl) {
    int sock = SSL_get_fd(ssl);
    SSL_CTX *ctx = SSL_get_SSL_CTX(ssl);
    SSL_free(ssl);
    close(sock);
    SSL_CTX_free(ctx);
    cleanup_openssl();
}

int ssl_send_receive(SSL* ssl, const char* request, char* response, int response_size) {
    SSL_write(ssl, request, strlen(request));
    int bytes = SSL_read(ssl, response, response_size - 1);
    response[bytes] = '\0';
    return bytes;
}