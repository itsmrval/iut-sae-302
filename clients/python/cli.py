import socket
import ssl

# Define server address and port
SERVER_IP = "localhost"
SERVER_PORT = 8081
CERT_FILE = "ssl/cert.pem"  # Path to the server's certificate

def interactive_terminal():
    print(f"Connecting to server at {SERVER_IP}:{SERVER_PORT}...")
    try:
        # Create a socket object
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
            # Wrap the socket with SSL
            context = ssl.create_default_context(ssl.Purpose.SERVER_AUTH)
            context.load_verify_locations(CERT_FILE)  # Load the server's certificate
            secure_socket = context.wrap_socket(client_socket, server_hostname=SERVER_IP)

            # Connect to the server
            secure_socket.connect((SERVER_IP, SERVER_PORT))
            print("[INFO] Connected to the server. Type commands or 'exit' to quit.")

            while True:
                # Take input from the user
                user_input = input(">> ").strip()
                
                # Exit condition
                if user_input.lower() == "exit":
                    print("[INFO] Closing connection...")
                    break
                
                # Send the input to the server
                secure_socket.sendall(user_input.encode())
                
                # Receive and display the server's response
                response = secure_socket.recv(1024).decode()
                print(f"[SERVER RESPONSE]\n{response}\n")
                
                if "499" in response.lower():
                    print("[INFO] Server requested disconnect. Closing connection...")
                    break
    
    except Exception as e:
        print(f"[ERROR] {e}")

if __name__ == "__main__":
    interactive_terminal()
