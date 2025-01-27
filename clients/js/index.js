const http = require("http");
const fs = require("fs");
const path = require("path");
const WebSocket = require("ws");
const net = require("net");

const HTTP_PORT = 8080; // Port for serving HTTP
const WS_PORT = 8082;  // Port for WebSocket
const TCP_SERVER_IP = "127.0.0.1"; // Raw TCP server IP
const TCP_SERVER_PORT = 8081;    // Raw TCP server port

// Serve the front-end HTML page
const httpServer = http.createServer((req, res) => {
    const filePath = path.join(__dirname, "static/index.html");

    // Serve index.html for the root request
    if (req.url === "/" || req.url === "/index.html") {
        fs.readFile(filePath, (err, data) => {
            if (err) {
                res.writeHead(500, { "Content-Type": "text/plain" });
                res.end("Internal Server Error");
            } else {
                res.writeHead(200, { "Content-Type": "text/html" });
                res.end(data);
            }
        });
    } else {
        res.writeHead(404, { "Content-Type": "text/plain" });
        res.end("404 Not Found");
    }
});

httpServer.listen(HTTP_PORT, () => {
    console.log(`HTTP server running at http://localhost:${HTTP_PORT}`);
});

// WebSocket server
const wss = new WebSocket.Server({ port: WS_PORT }, () => {
    console.log(`WebSocket server running on ws://localhost:${WS_PORT}`);
});

wss.on("connection", (ws) => {
    console.log("WebSocket client connected!");

    ws.on("message", (message) => {
        console.log(`Received message from client: ${message}`);

        // Connect to the raw TCP server
        const client = net.createConnection(
            { host: TCP_SERVER_IP, port: TCP_SERVER_PORT },
            () => {
                console.log("Connected to TCP server");
                client.write(message); // Forward the message to the TCP server
            }
        );

        client.on("data", (data) => {
            console.log(`Data from TCP server: ${data}`);
            ws.send(data.toString()); // Send data back to the WebSocket client
            client.end();
        });

        client.on("error", (err) => {
            console.error(`TCP connection error: ${err.message}`);
            ws.send(`Error: ${err.message}`);
        });

        client.on("end", () => {
            console.log("Disconnected from TCP server");
        });
    });

    ws.on("close", () => {
        console.log("WebSocket client disconnected");
    });
});
