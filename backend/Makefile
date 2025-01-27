CC = gcc

CFLAGS = -Wall -g

OPENSSL_CFLAGS = -I$(shell brew --prefix openssl)/include
OPENSSL_LIBS = -L$(shell brew --prefix openssl)/lib -lssl -lcrypto

# Update the source file paths to include the src/ directory
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
