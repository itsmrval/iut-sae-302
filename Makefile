BACKEND_DIR = ./backend
CLIENT_DIR = ./clients/c

BACKEND_TARGET = server
CLIENT_TARGET = client

DIST_DIR = ./dist

all: create_dist_dir backend client_c


backend:
	$(MAKE) -C $(BACKEND_DIR) 

client_c:
	$(MAKE) -C $(CLIENT_DIR)


clean:
	$(MAKE) -C $(BACKEND_DIR) clean
	$(MAKE) -C $(CLIENT_DIR) clean

create_dist_dir:
	mkdir -p $(DIST_DIR)

.PHONY: all backend client clean
