#ifndef NETWORK_H
#define NETWORK_H

#include <openssl/ssl.h>

SSL* setup_ssl_connection(const char* hostname, int port, const char* cert_file);
void cleanup_ssl_connection(SSL* ssl);
int ssl_send_receive(SSL* ssl, const char* request, char* response, int response_size);

#endif