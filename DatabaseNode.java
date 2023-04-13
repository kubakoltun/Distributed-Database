import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DatabaseNode extends Thread {
    private HashMap<String, String> database;
    private ServerSocket serverSocket;
    private int port;
    private boolean isRunning = true;

    public DatabaseNode(int port, String key, String value) {
        this.port = port;
        this.database = new HashMap<>();
        this.database.put(key, value);
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (isRunning) {
                Socket socket = serverSocket.accept();
                Thread t = new DatabaseServerThread(socket, this.database);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Socket> connectedNodes = new ArrayList<>();

    private void informNeighbors() {
        for (Socket socket : connectedNodes) {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("terminate");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void terminate() {
        isRunning = false;
        try {
            serverSocket.close();
            informNeighbors();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("OK");
    }

    public static void main(String[] args) {
        int port = 0;
        String record = "";
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-tcpport")) {
                    port = Integer.parseInt(args[i+1]);
                }
                if (args[i].equals("-record")) {
                    record = args[i+1];
                }
            }
            if (port == 0) {
                System.out.println("No value for -tcpport argument");
                return;
            }
            if (record.isEmpty()) {
                System.out.println("No value for -record argument");
                return;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        String[] keyValue = record.split(":");
        String key = keyValue[0];
        String value = keyValue[1];

        DatabaseNode node = new DatabaseNode(port, key, value);
        for (int i = 2; i < args.length; i++){
            if (args[i].startsWith("-connect")){
                String[] hostAndPort = args[i].substring(args[i].indexOf("-connect ") + 8).split(":");
                String host = hostAndPort[0];
                int connectPort = Integer.parseInt(hostAndPort[1]);
                connectToNode(host, connectPort);
            }
        }
        node.start();
        System.out.println("Ruszyla Maszyna");
    }

    private static void connectToNode(String host, int port){
        try {
            Socket socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class DatabaseServerThread extends Thread {
    private Socket socket;
    private HashMap<String, String> database;

    public DatabaseServerThread(Socket socket, HashMap<String, String> database) {
        this.socket = socket;
        this.database = database;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String input = in.readLine();
            String[] parts = input.split(" ");

            if (parts[0].equals("set-value")) {
                String[] keyValue = parts[1].split(":");
                String key = keyValue[0];
                String value = keyValue[1];
                if (database.containsKey(key)) {
                    database.put(key, value);
                    out.println("OK");
                } else {
                    out.println("ERROR");
                }
            }
            else if (parts[0].equals("new-record")) {
                String[] keyValue = parts[1].split(":");
                String key = keyValue[0];
                String value = keyValue[1];
                database.put(key, value);
                out.println("OK");
            }
            else if (parts[0].equals("terminate")) {
                out.println("OK");
                socket.close();
            }
            else if (parts[0].equals("get-value")) {
                String key = parts[1];
                if (database.containsKey(key)) {
                    out.println("Response is " + key + ":" + database.get(key));
                } else {
                    out.println("Response is ERROR");
                }
            }
            else if (parts[0].equals("find-key")) {
                String key = parts[1];
                if (database.containsKey(key)) {
                    out.println(InetAddress.getLocalHost().getHostAddress() + ":" + socket.getLocalPort());
                } else {
                    out.println("ERROR");
                }
            }
            else if (parts[0].equals("get-max")) {
                String maxKey = "";
                int maxValue = Integer.MIN_VALUE;
                for (String key : database.keySet()) {
                    int value = Integer.parseInt(database.get(key));
                    if (value > maxValue) {
                        maxValue = value;
                        maxKey = key;
                    }
                }
                out.println(maxKey + ":" + maxValue);
            }
            else if (parts[0].equals("get-min")) {
                String minKey = "";
                int minValue = Integer.MAX_VALUE;
                for (String key : database.keySet()) {
                    int value = Integer.parseInt(database.get(key));
                    if (value < minValue) {
                        minValue = value;
                        minKey = key;
                    }
                }
                out.println(minKey + ":" + minValue);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
