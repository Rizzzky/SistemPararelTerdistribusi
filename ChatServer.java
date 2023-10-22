import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatServer {
    private List<Socket> clients = new ArrayList<>();

    public ChatServer(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server telah berjalan di port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Koneksi diterima dari: " + clientSocket.getInetAddress());
                clients.add(clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message, Socket senderSocket) {
        for (Socket client : clients) {
            if (client != senderSocket) {
                try {
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    out.println("[" + new Date() + "] " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(12345);
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Pesan diterima dari " + clientSocket.getInetAddress() + ": " + message);
                    broadcastMessage(message, clientSocket);
                }

                clients.remove(clientSocket);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
