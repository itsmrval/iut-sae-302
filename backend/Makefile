CC=gcc
FLAGS= -Wall
EXEC= 	backendC

%.o: %.c
	$(CC) $(FLAGS) -c $<

backendC: main.o
	$(CC) $(LIBS) $(FLAGS) -o $@ $^

all : $(EXEC)

clean :
	rm *.o
