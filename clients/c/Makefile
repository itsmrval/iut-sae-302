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

DIST_DIR = ../../dist/client_c

SRCS = src/main.c src/handlers.c src/network.c
OBJS = $(SRCS:src/%.c=$(DIST_DIR)/%.o)
EXEC = $(DIST_DIR)/client
OPENSSL_DIR = ../../openssl/ssl

all: create_dist_dir $(EXEC) clean_o_files copy_ssl

$(EXEC): $(OBJS)
	$(CC) -o $@ $^ $(OPENSSL_LIBS) $(GTK_LIBS) $(GLIB_LIBS) $(PANGO_LIBS) $(HB_LIBS) $(CAIRO_LIBS) $(PIXBUF_LIBS) $(ATK_LIBS)

$(DIST_DIR)/%.o: src/%.c
	$(CC) $(CFLAGS) $(OPENSSL_CFLAGS) $(GTK_CFLAGS) $(GLIB_CFLAGS) $(PANGO_CFLAGS) $(HB_CFLAGS) $(CAIRO_CFLAGS) $(PIXBUF_CFLAGS) $(ATK_CFLAGS) -c $< -o $@

create_dist_dir:
	mkdir -p $(DIST_DIR)

copy_ssl:
	cp -r $(OPENSSL_DIR)/cert.pem $(DIST_DIR)/

clean_o_files:
	rm -f $(DIST_DIR)/*.o

clean:
	rm -rf $(EXEC) $(DIST_DIR)

.PHONY: all clean