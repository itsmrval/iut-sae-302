CC = gcc
CFLAGS = -Wall -g

DIST_DIR = ../../dist/client_c

OPENSSL_CFLAGS = -I./libs/openssl/include
OPENSSL_LIBS = -L./libs/openssl/lib -lssl -lcrypto

SRCS = src/main.c src/display.c src/handlers.c src/network.c
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
