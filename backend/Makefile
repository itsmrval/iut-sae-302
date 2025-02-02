CC = gcc
CFLAGS = -Wall -g

UNAME_S := $(shell uname -s)

ifeq ($(UNAME_S), Linux)
    OPENSSL_CFLAGS := $(shell pkg-config --cflags openssl)
    OPENSSL_LIBS := $(shell pkg-config --libs openssl)
else ifeq ($(UNAME_S), Darwin)
    OPENSSL_CFLAGS := -I$(shell brew --prefix openssl)/include
    OPENSSL_LIBS := -L$(shell brew --prefix openssl)/lib -lssl -lcrypto
else
    OPENSSL_CFLAGS := -I"C:/OpenSSL/include"
    OPENSSL_LIBS := -L"C:/OpenSSL/lib" -lssl -lcrypto
endif

DIST_DIR = ../dist/server

SRCS = src/main.c src/utils.c src/student.c src/attendance.c src/seance.c src/openssl.c
OBJS = $(SRCS:src/%.c=$(DIST_DIR)/%.o)
EXEC = $(DIST_DIR)/server
OPENSSL_DIR = ../openssl/ssl

all: create_dist_dir $(EXEC) copy_ssl clean_o_files create_data_dir

$(EXEC): $(OBJS)
	$(CC) -o $@ $^ $(OPENSSL_LIBS)

$(DIST_DIR)/%.o: src/%.c
	$(CC) $(CFLAGS) $(OPENSSL_CFLAGS) -c $< -o $@

copy_ssl:
	cp -r $(OPENSSL_DIR) $(DIST_DIR)/

create_dist_dir:
	mkdir -p $(DIST_DIR)

create_data_dir:
	mkdir -p $(DIST_DIR)/data/

clean_o_files:
	rm -f $(DIST_DIR)/*.o

clean:
	rm -rf $(EXEC) $(DIST_DIR)

.PHONY: all clean clean_o_files copy_ssl
