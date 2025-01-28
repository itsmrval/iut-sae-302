#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <openssl/ssl.h>
#include <openssl/err.h>
#include <time.h>

#define BUFFER_SIZE 1024
#define MAX_STUDENTS 100
#define MAX_SEANCES 100

typedef struct {
    char id[32];
    char name[128];
} Student;

typedef struct {
    char id[32];
    char name[128];
    long timestamp;
} Seance;

SSL_CTX* create_ssl_context() {
    SSL_CTX* ctx = SSL_CTX_new(TLS_client_method());
    if (!ctx) {
        perror("SSL context creation failed");
        exit(1);
    }
    
    SSL_CTX_set_verify(ctx, SSL_VERIFY_NONE, NULL);
    return ctx;
}

SSL* establish_connection(const char* host, int port, SSL_CTX* ctx) {
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        perror("Socket creation failed");
        exit(1);
    }

    struct sockaddr_in server_addr;
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(port);
    inet_pton(AF_INET, host, &server_addr.sin_addr);

    if (connect(sock, (struct sockaddr*)&server_addr, sizeof(server_addr)) < 0) {
        perror("Connection failed");
        exit(1);
    }

    SSL* ssl = SSL_new(ctx);
    SSL_set_fd(ssl, sock);
    
    if (SSL_connect(ssl) <= 0) {
        ERR_print_errors_fp(stderr);
        exit(1);
    }

    return ssl;
}

char* send_request(SSL* ssl, const char* request) {
    static char response[BUFFER_SIZE];
    
    if (SSL_write(ssl, request, strlen(request)) <= 0) {
        perror("Send failed");
        return NULL;
    }
    
    int bytes = SSL_read(ssl, response, BUFFER_SIZE - 1);
    if (bytes <= 0) {
        perror("Receive failed");
        return NULL;
    }
    
    response[bytes] = '\0';
    
    if (strncmp(response, "499/", 4) == 0) {
        printf("Server disconnected\n");
        exit(0);
    }
    
    if (strncmp(response, "400", 3) == 0 || strncmp(response, "500", 3) == 0) {
        printf("Error: %s\n", response);
        return NULL;
    }
    
    return response + 4;  // Skip status code
}

void list_seances(SSL* ssl) {
    char* response = send_request(ssl, "<g>seance");
    if (!response) return;
    
    printf("\nSeances:\n");
    printf("%-5s %-30s %-20s\n", "ID", "Name", "Date");
    printf("----------------------------------------\n");
    
    char* seance = strtok(response, ";");
    while (seance) {
        char id[32], name[128];
        long timestamp;
        sscanf(seance, "id:%[^,],name:%[^,],time:%ld", id, name, &timestamp);
        
        time_t t = timestamp;
        struct tm* tm_info = localtime(&t);
        char date_str[20];
        strftime(date_str, 20, "%Y-%m-%d %H:%M", tm_info);
        
        printf("%-5s %-30s %-20s\n", id, name, date_str);
        seance = strtok(NULL, ";");
    }
}

void list_students(SSL* ssl) {
    char* response = send_request(ssl, "<g>student");
    if (!response) return;
    
    printf("\nStudents:\n");
    printf("%-5s %-30s\n", "ID", "Name");
    printf("----------------------------------------\n");
    
    char* student = strtok(response, ";");
    while (student) {
        char id[32], name[128];
        sscanf(student, "id:%[^,],name:%[^,]", id, name);
        printf("%-5s %-30s\n", id, name);
        student = strtok(NULL, ";");
    }
}

void create_seance(SSL* ssl) {
    char name[128];
    printf("Enter seance name: ");
    scanf("%s", name);
    
    time_t now = time(NULL);
    struct tm* tm_info = localtime(&now);
    
    char request[256];
    snprintf(request, sizeof(request), "<p>seance/%s/%ld", name, (long)now);
    send_request(ssl, request);
}

void create_student(SSL* ssl) {
    char name[128];
    printf("Enter student name: ");
    scanf("%s", name);
    
    char request[256];
    snprintf(request, sizeof(request), "<p>student/%s", name);
    send_request(ssl, request);
}

void view_seance_attendance(SSL* ssl) {
    char seance_id[32];
    printf("Enter seance ID: ");
    scanf("%s", seance_id);
    
    char* student_response = send_request(ssl, "<g>student");
    if (!student_response) return;
    
    printf("\nAttendance for Seance %s:\n", seance_id);
    printf("%-30s %-10s\n", "Student Name", "Status");
    printf("----------------------------------------\n");
    
    char* student = strtok(student_response, ";");
    while (student) {
        char student_id[32], name[128];
        sscanf(student, "id:%[^,],name:%[^,]", student_id, name);
        
        char request[256];
        snprintf(request, sizeof(request), "<g>attendance/%s/%s", seance_id, student_id);
        char* attendance = send_request(ssl, request);
        
        char status[32];
        if (attendance && strstr(attendance, ":1")) {
            strcpy(status, "Present");
        } else {
            strcpy(status, "Absent");
        }
        
        printf("%-30s %-10s\n", name, status);
        student = strtok(NULL, ";");
    }
}

void toggle_attendance(SSL* ssl) {
    char seance_id[32], student_id[32];
    printf("Enter seance ID: ");
    scanf("%s", seance_id);
    printf("Enter student ID: ");
    scanf("%s", student_id);
    
    char request[256];
    snprintf(request, sizeof(request), "<g>attendance/%s/%s", seance_id, student_id);
    char* current = send_request(ssl, request);
    
    char new_status = (current && strstr(current, ":1")) ? '0' : '1';
    snprintf(request, sizeof(request), "<p>attendance/%s/%s/%c", seance_id, student_id, new_status);
    send_request(ssl, request);
}

int main(int argc, char* argv[]) {
    if (argc != 3) {
        printf("Usage: %s <host> <port>\n", argv[0]);
        return 1;
    }
    
    SSL_library_init();
    SSL_CTX* ctx = create_ssl_context();
    SSL* ssl = establish_connection(argv[1], atoi(argv[2]), ctx);
    
    while (1) {
        printf("\nAttendance Management System\n");
        printf("1. List Seances\n");
        printf("2. Create Seance\n");
        printf("3. List Students\n");
        printf("4. Create Student\n");
        printf("5. View Seance Attendance\n");
        printf("6. Toggle Attendance\n");
        printf("0. Exit\n");
        printf("Choice: ");
        
        int choice;
        scanf("%d", &choice);
        
        switch (choice) {
            case 0:
                send_request(ssl, "<a>disconnect");
                SSL_free(ssl);
                SSL_CTX_free(ctx);
                return 0;
            case 1:
                list_seances(ssl);
                break;
            case 2:
                create_seance(ssl);
                break;
            case 3:
                list_students(ssl);
                break;
            case 4:
                create_student(ssl);
                break;
            case 5:
                view_seance_attendance(ssl);
                break;
            case 6:
                toggle_attendance(ssl);
                break;
            default:
                printf("Invalid choice\n");
        }
    }
    
    return 0;
}
