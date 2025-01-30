CC = gcc

CFLAGS = -Wall -g

OPENSSL_CFLAGS = -I./libs/openssl/include
OPENSSL_LIBS = -L./libs/openssl/lib -lssl -lcrypto

SRCS = src/main.c src/utils.c src/student.c src/attendance.c src/seance.c src/openssl.c

OBJS = $(SRCS:.c=.o)

EXEC = server

all: $(EXEC)

$(EXEC): $(OBJS)
	$(CC) -o $@ $^ $(OPENSSL_LIBS)

%.o: %.c
	$(CC) $(CFLAGS) $(OPENSSL_CFLAGS) -c $< -o $@

clean:
	rm -f $(OBJS) $(EXEC)

.PHONY: all clean
