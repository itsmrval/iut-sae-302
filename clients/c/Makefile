CC = gcc

CFLAGS = -Wall -g

DIST_DIR = ../../dist/client_c

SRCS = src/main.c

OBJS = $(SRCS:src/%.c=$(DIST_DIR)/%.o)

EXEC = $(DIST_DIR)/client


all: create_dist_dir $(EXEC) clean_o_files

$(EXEC): $(OBJS)
	$(CC) -o $@ $^ $(OPENSSL_LIBS)

$(DIST_DIR)/%.o: src/%.c
	$(CC) $(CFLAGS) $(OPENSSL_CFLAGS) -c $< -o $@

create_dist_dir:
	mkdir -p $(DIST_DIR)

clean_o_files:
	rm -f $(DIST_DIR)/*.o

clean:
	rm -rf $(EXEC) $(DIST_DIR)


.PHONY: all clean
