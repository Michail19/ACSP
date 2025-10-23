import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class SocketServer {
    private static final int PORT = 50001;
    private static final int BROADCAST_INTERVAL = 5000;

    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private List<String> messageQueue;
    private ScheduledExecutorService scheduler;

    public SocketServer() {
        clients = new CopyOnWriteArrayList<>();
        messageQueue = new ArrayList<>();
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен на порту " + PORT);

            scheduler.scheduleAtFixedRate(this::broadcastMessages,
                    BROADCAST_INTERVAL, BROADCAST_INTERVAL, TimeUnit.MILLISECONDS);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();

                System.out.println("Новый клиент подключен. Всего клиентов: " + clients.size());
            }

        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            scheduler.shutdown();
            for (ClientHandler client : clients) {
                client.stop();
            }
            System.out.println("Сервер остановлен");
        } catch (IOException e) {
            System.err.println("Ошибка при остановке сервера: " + e.getMessage());
        }
    }

    public synchronized void addMessage(String message) {
        messageQueue.add(message);
        System.out.println("Сообщение добавлено в очередь: " + message);
    }

    private synchronized void broadcastMessages() {
        if (messageQueue.isEmpty()) {
            return;
        }

        String broadcastPacket = createBroadcastPacket();

        Iterator<ClientHandler> iterator = clients.iterator();
        while (iterator.hasNext()) {
            ClientHandler client = iterator.next();
            if (client.isConnected()) {
                client.sendMessage(broadcastPacket);
            } else {
                iterator.remove();
                System.out.println("Клиент отключен. Осталось клиентов: " + clients.size());
            }
        }

        messageQueue.clear();
        System.out.println("Широковещательная рассылка выполнена");
    }

    private String createBroadcastPacket() {
        StringBuilder packet = new StringBuilder();
        packet.append("=== Широковещательное сообщение ===\n");
        packet.append("Время: ").append(new Date()).append("\n");
        packet.append("Количество сообщений: ").append(messageQueue.size()).append("\n");
        packet.append("Сообщения:\n");

        for (int i = 0; i < messageQueue.size(); i++) {
            packet.append(i + 1).append(". ").append(messageQueue.get(i)).append("\n");
        }
        packet.append("===================================\n");

        return packet.toString();
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private boolean connected;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.connected = true;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Отправляем приветственное сообщение
                out.println("Добро пожаловать в чат! Ваши сообщения будут отправлены всем участникам раз в 5 секунд.");

                String inputLine;
                while ((inputLine = in.readLine()) != null && connected) {
                    if (!inputLine.trim().isEmpty()) {
                        addMessage("Клиент-" + getClientId() + ": " + inputLine);
                    }
                }

            } catch (IOException e) {
                System.err.println("Ошибка обработки клиента: " + e.getMessage());
            } finally {
                stop();
            }
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        public void stop() {
            connected = false;
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии соединения с клиентом: " + e.getMessage());
            }
        }

        public boolean isConnected() {
            return connected && !clientSocket.isClosed() && clientSocket.isConnected();
        }

        private String getClientId() {
            return clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
        }
    }

    public static void main(String[] args) {
        SocketServer server = new SocketServer();

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        server.start();
    }
}
