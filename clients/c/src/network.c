#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <openssl/ssl.h>
#include <openssl/err.h>
#include "network.h"

// Crée un contexte SSL en utilisant la méthode TLS pour les clients.
static SSL_CTX* create_context() {
    // Utilise TLS_client_method pour définir la méthode de chiffrement TLS.
    const SSL_METHOD *method = TLS_client_method();
    // Crée un nouveau contexte SSL.
    SSL_CTX *ctx = SSL_CTX_new(method);
    if (!ctx) {
        // En cas d'échec, affiche un message d'erreur et retourne NULL.
        fprintf(stderr, "Failed to create SSL context\n");
        return NULL;
    }
    return ctx;
}

// Initialise les bibliothèques OpenSSL (chargement des chaînes d'erreurs et des algorithmes).
static void init_openssl() {
    SSL_load_error_strings();
    OpenSSL_add_ssl_algorithms();
}

// Libère les ressources utilisées par OpenSSL.
static void cleanup_openssl() {
    EVP_cleanup();
}

// Crée une socket et se connecte au serveur spécifié par l'adresse IP et le port.
static int create_socket(const char* hostname, int port) {
    // Crée une socket de type IPv4 (AF_INET) en mode flux (TCP).
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        perror("Socket creation failed");
        return -1;
    }

    // Prépare la structure d'adresse avec le type de famille, le port et l'adresse IP.
    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = inet_addr(hostname);

    // Tente de se connecter au serveur.
    if (connect(sock, (struct sockaddr*)&addr, sizeof(addr)) != 0) {
        perror("Connection failed");
        close(sock);
        return -1;
    }

    return sock;
}

// Établit une connexion SSL sécurisée vers le serveur en utilisant l'adresse, le port et le fichier de certificat.
SSL* setup_ssl_connection(const char* hostname, int port, const char* cert_file) {
    // Initialise OpenSSL
    init_openssl();
    
    // Crée un contexte SSL
    SSL_CTX *ctx = create_context();
    if (!ctx) return NULL;

    // Charge les certificats de vérification à partir du fichier fourni.
    if (SSL_CTX_load_verify_locations(ctx, cert_file, NULL) <= 0) {
        ERR_print_errors_fp(stderr);
        SSL_CTX_free(ctx);
        return NULL;
    }

    // Crée une socket et se connecte au serveur
    int sock = create_socket(hostname, port);
    if (sock < 0) {
        SSL_CTX_free(ctx);
        return NULL;
    }

    // Crée une structure SSL et associe la socket créée
    SSL *ssl = SSL_new(ctx);
    SSL_set_fd(ssl, sock);

    // Établit la connexion SSL sécurisée
    if (SSL_connect(ssl) <= 0) {
        ERR_print_errors_fp(stderr);
        SSL_free(ssl);
        close(sock);
        SSL_CTX_free(ctx);
        return NULL;
    }

    return ssl;
}

// Nettoie la connexion SSL en libérant toutes les ressources associées.
void cleanup_ssl_connection(SSL* ssl) {
    if (!ssl) return;
    
    // Récupère la socket associée à la connexion SSL
    int sock = SSL_get_fd(ssl);
    // Récupère le contexte SSL utilisé
    SSL_CTX *ctx = SSL_get_SSL_CTX(ssl);
    
    // Libère la structure SSL
    SSL_free(ssl);
    // Ferme la socket
    close(sock);
    
    // Libère le contexte SSL s'il existe
    if (ctx) SSL_CTX_free(ctx);
    
    // Nettoie les ressources OpenSSL
    cleanup_openssl();
}

// Envoie une requête via la connexion SSL et reçoit la réponse du serveur.
// 'request' : chaîne à envoyer, 'response' : buffer de réception, 'response_size' : taille du buffer de réponse.
int ssl_send_receive(SSL* ssl, const char* request, char* response, int response_size) {
    if (!ssl) return -1;

    // Envoie la requête via SSL
    if (SSL_write(ssl, request, strlen(request)) <= 0) {
        ERR_print_errors_fp(stderr);
        return -1;
    }

    // Lit la réponse depuis la connexion SSL dans le buffer 'response'
    int bytes = SSL_read(ssl, response, response_size - 1);
    if (bytes <= 0) {
        ERR_print_errors_fp(stderr);
        return -1;
    }

    // Termine la chaîne reçue pour en faire une chaîne de caractères valide.
    response[bytes] = '\0';
    return bytes;
}
