#ifndef OPENSSL_H
#define OPENSSL_H

#include <openssl/ssl.h>
#include <openssl/err.h>

void init_openssl();

void cleanup_openssl();

void handle_ssl_client(int client_socket, struct sockaddr_in client_addr);

#endif // OPENSSL_H
