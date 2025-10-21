import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected;

    protected List<String> messageQueue;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.connected = true;
        this.messageQueue = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("Добро пожаловать в чат! Ваши сообщения будут отправлены всем участникам раз в 5 секунд.");

            String inputLine;
            while ((inputLine = in.readLine()) != null && connected) {
                if (!inputLine.trim().isEmpty()) {
                    messageQueue.add("Клиент-" + getClientId() + ": " + inputLine);
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
