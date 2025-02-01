BACKEND_DIR = ./backend
CLIENT_DIR = ./clients/c

BACKEND_TARGET = server
CLIENT_TARGET = client

DIST_DIR = ./dist

GRADLE = ./gradle/bin/gradle

all: create_dist_dir backend client_c client_java


backend:
	$(MAKE) -C $(BACKEND_DIR) 

client_c:
	$(MAKE) -C $(CLIENT_DIR)

client_java:
	$(GRADLE) -p ./clients/java buildAndMoveJar

clean:
	$(MAKE) -C $(BACKEND_DIR) clean
	$(MAKE) -C $(CLIENT_DIR) clean
	$(GRADLE) -p ./clients/java cleanDistDir

create_dist_dir:
	mkdir -p $(DIST_DIR)

.PHONY: all backend client_c client_java clean
