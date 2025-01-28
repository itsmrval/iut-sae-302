CC = gcc

CFLAGS = -Wall -g

OPENSSL_CFLAGS = -I$(shell brew --prefix openssl)/include
OPENSSL_LIBS = -L$(shell brew --prefix openssl)/lib -lssl -lcrypto

# Update the source file paths to include the src/ directory
SRCS = src/main.c

OBJS = $(SRCS:.c=.o)

EXEC = client

all: $(EXEC)

$(EXEC): $(OBJS)
	$(CC) -o $@ $^ $(OPENSSL_LIBS)

%.o: %.c
	$(CC) $(CFLAGS) $(OPENSSL_CFLAGS) -c $< -o $@

clean:
	rm -f $(OBJS) $(EXEC)

.PHONY: all clean
