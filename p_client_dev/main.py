import socket

# Define server address and port
SERVER_IP = "127.0.0.1"
SERVER_PORT = 8080

def interactive_terminal():
    print(f"Connecting to server at {SERVER_IP}:{SERVER_PORT}...")
    try:
        # Create a socket object
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
            # Connect to the server
            client_socket.connect((SERVER_IP, SERVER_PORT))
            print("[INFO] Connected to the server. Type commands or 'exit' to quit.")

            while True:
                # Take input from the user
                user_input = input(">> ").strip()
                
                # Exit condition
                if user_input.lower() == "exit":
                    print("[INFO] Closing connection...")
                    break
                
                # Send the input to the server
                client_socket.sendall(user_input.encode())
                
                response = client_socket.recv(1024).decode()
                print(f"[SERVER RESPONSE]\n{response}")
                
                if "499" in response.lower():
                    print("[INFO] Server requested disconnect. Closing connection...")
                    break
    
    except Exception as e:
        print(f"[ERROR] {e}")

if __name__ == "__main__":
    interactive_terminal()
