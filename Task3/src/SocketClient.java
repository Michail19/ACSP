import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SocketClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 50001;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected;

    public void start() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;

            System.out.println("Подключено к серверу " + SERVER_HOST + ":" + SERVER_PORT);
            System.out.println("Введите сообщения (для выхода введите 'quit'):");

            // Запускаем поток для чтения сообщений от сервера
            Thread readerThread = new Thread(this::readMessages);
            readerThread.start();

            // Читаем ввод пользователя
            Scanner scanner = new Scanner(System.in);
            while (connected && scanner.hasNextLine()) {
                String userInput = scanner.nextLine();

                if ("quit".equalsIgnoreCase(userInput)) {
                    break;
                }

                if (!userInput.trim().isEmpty()) {
                    out.println(userInput);
                }
            }

            scanner.close();
            stop();

        } catch (IOException e) {
            System.err.println("Ошибка подключения к серверу: " + e.getMessage());
        }
    }

    private void readMessages() {
        try {
            String serverMessage;
            while (connected && (serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);
            }
        } catch (IOException e) {
            if (connected) {
                System.err.println("Ошибка при чтении сообщений от сервера: " + e.getMessage());
            }
        }
    }

    public void stop() {
        connected = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            System.out.println("Отключено от сервера");
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SocketClient client = new SocketClient();

        // Добавляем обработчик завершения работы
        Runtime.getRuntime().addShutdownHook(new Thread(client::stop));

        client.start();
    }
}