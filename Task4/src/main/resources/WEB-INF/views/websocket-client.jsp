<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebSocket Client</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        .container {
            border: 1px solid #ccc;
            padding: 20px;
            border-radius: 5px;
        }
        input, textarea, button {
            width: 100%;
            margin: 10px 0;
            padding: 10px;
            box-sizing: border-box;
        }
        #messages {
            height: 300px;
            border: 1px solid #ccc;
            overflow-y: auto;
            padding: 10px;
            margin: 10px 0;
            background-color: #f9f9f9;
        }
        .message {
            margin: 5px 0;
            padding: 8px;
            border-radius: 4px;
        }
        .sent {
            background-color: #d4edda;
            border-left: 4px solid #28a745;
        }
        .received {
            background-color: #d1ecf1;
            border-left: 4px solid #17a2b8;
        }
        .system {
            background-color: #fff3cd;
            border-left: 4px solid #ffc107;
            font-style: italic;
        }
        .status {
            padding: 10px;
            margin: 10px 0;
            border-radius: 4px;
            font-weight: bold;
        }
        .connected {
            background-color: #d4edda;
            color: #155724;
        }
        .disconnected {
            background-color: #f8d7da;
            color: #721c24;
        }
        button {
            background-color: #007bff;
            color: white;
            border: none;
            cursor: pointer;
        }
        button:hover {
            background-color: #0056b3;
        }
        button:disabled {
            background-color: #6c757d;
            cursor: not-allowed;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>WebSocket Client Test</h1>
    <p>Endpoint: <code>/webs</code></p>

    <div id="status" class="status disconnected">
        Status: Disconnected
    </div>

    <div>
        <button id="connectBtn" onclick="connect()">Connect to WebSocket</button>
        <button id="disconnectBtn" onclick="disconnect()" disabled>Disconnect</button>
    </div>

    <div>
        <input type="text" id="messageInput" placeholder="Enter your message here..." disabled>
        <button id="sendBtn" onclick="sendMessage()" disabled>Send Message</button>
    </div>

    <h3>Messages:</h3>
    <div id="messages"></div>
</div>

<script>
    let socket = null;
    const statusElement = document.getElementById('status');
    const messagesElement = document.getElementById('messages');
    const messageInput = document.getElementById('messageInput');
    const connectBtn = document.getElementById('connectBtn');
    const disconnectBtn = document.getElementById('disconnectBtn');
    const sendBtn = document.getElementById('sendBtn');

    function connect() {
        try {
            // Создаем WebSocket соединение
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const wsUrl = `${protocol}//${window.location.host}${window.location.pathname.replace('websocket-client', '')}webs`;

            socket = new WebSocket(wsUrl);

            socket.onopen = function(event) {
                updateStatus('Connected', 'connected');
                addMessage('System: WebSocket connection established successfully', 'system');
                setConnectionState(true);
            };

            socket.onmessage = function(event) {
                addMessage(`Received: ${event.data}`, 'received');
            };

            socket.onclose = function(event) {
                updateStatus('Disconnected', 'disconnected');
                addMessage('System: WebSocket connection closed', 'system');
                setConnectionState(false);
            };

            socket.onerror = function(error) {
                updateStatus('Connection Error', 'disconnected');
                addMessage('System: WebSocket error occurred', 'system');
                console.error('WebSocket error:', error);
                setConnectionState(false);
            };

        } catch (error) {
            console.error('Failed to connect:', error);
            addMessage('System: Failed to establish connection', 'system');
        }
    }

    function disconnect() {
        if (socket) {
            socket.close();
            socket = null;
        }
    }

    function sendMessage() {
        if (socket && socket.readyState === WebSocket.OPEN) {
            const message = messageInput.value.trim();
            if (message) {
                socket.send(message);
                addMessage(`Sent: ${message}`, 'sent');
                messageInput.value = '';
            }
        }
    }

    function updateStatus(message, className) {
        statusElement.textContent = `Status: ${message}`;
        statusElement.className = `status ${className}`;
    }

    function addMessage(message, type) {
        const messageElement = document.createElement('div');
        messageElement.className = `message ${type}`;
        messageElement.textContent = message;
        messagesElement.appendChild(messageElement);
        messagesElement.scrollTop = messagesElement.scrollHeight;
    }

    function setConnectionState(connected) {
        connectBtn.disabled = connected;
        disconnectBtn.disabled = !connected;
        messageInput.disabled = !connected;
        sendBtn.disabled = !connected;

        if (connected) {
            messageInput.focus();
        }
    }

    // Обработка нажатия Enter в поле ввода
    messageInput.addEventListener('keypress', function(event) {
        if (event.key === 'Enter') {
            sendMessage();
        }
    });

    // Автоматическое подключение при загрузке страницы
    window.onload = connect;
</script>
</body>
</html>
