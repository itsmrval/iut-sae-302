# Makefile

CC = gcc
CFLAGS = -Wall -Wextra -std=c11
SRC = src/main.c src/utils.c src/student.c src/seance.c src/attendance.c
OBJ = $(SRC:.c=.o)
EXEC = server

all: $(EXEC)

$(EXEC): $(OBJ)
	$(CC) $(CFLAGS) -o $@ $^

%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -f src/*.o $(EXEC)

.PHONY: all clean
