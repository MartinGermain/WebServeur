package Server;

import Persistence.MessageLoader;
import Persistence.MessageLoader;

import javax.annotation.processing.SupportedSourceVersion;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * A simple chat Server that can communicate with multiple Clients, it simply
 * sends the message that it receives from a single client to all clients(
 * to the sender too).
 */
public class Server {
    /**
     * The Socket server which will receive and send messages from and to
     * clients
     */
    private ServerSocket server;
    /**
     * A list which contains all the clients outputstreams saved in a
     * PrintWriter
     */
    private List clientList = new CopyOnWriteArrayList<>();


    /**
     * Stores the messages of all clients in order to display theim to newly arrived clients
     **/

    private List<String> messageList = new CopyOnWriteArrayList<>();

    /** Message loader used to write and read messages from disk*/
    private MessageLoader messageLoader = new MessageLoader();

    /**
     * Saves the chat in a Json file at the provided path
     * 
     */
    private void saveMessages(){
        try {
            messageLoader.saveMessages(messageList,new File("./messages.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads the chat from a Json file loacated at the provided path
     *
     * @return the List that contains the chat
     */
    private List loadMessage()  {
        try {
            return messageLoader.loadMessages(new File("./messages.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Once the server is on, he will be waiting all the time for client to
     * connect, as soon as a client has connected a personal Thread will be
     * started for him by the private class Handler
     */
    private void listenToClients() {
        while (true) {
            try {
                Socket client = server.accept();
                System.out.println("Un nouveau client s'est connecté");
                PrintWriter writer = new PrintWriter(client.getOutputStream());
                clientList.add(writer);
                final int id = clientList.size() - 1;
                Thread clientThread = new Thread(new Handler(client, id));
                
                // Get the chat's history
                sendHistoryToClient(id);
                clientThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Starting server with port 4023
     */
    private void startServer() {

        List loadedMessages  =  loadMessage();
        if (loadedMessages ==null){
            messageList = new CopyOnWriteArrayList<>();
        }else{
            messageList = new CopyOnWriteArrayList<>(loadedMessages);
        }
        try {
            server = new ServerSocket(4023);
            System.out.println("New server started at port 4023");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save messages that the client has sent
     * Only if the message is not "/clear"
     * @param msg String message that will be send
     */
    private void SaveMessagesInJson(String msg) {
        if(!(msg.contains("/clear"))){
            messageList.add(msg);
            saveMessages();
        }
    }

    /**
     * Send message to a specific client
     */
    private void sendHistoryToClient(int id) {
        PrintWriter writer = (PrintWriter) clientList.get(id);
        for (String msg : messageList) {
            writer.println(msg);
            writer.flush();
        }
    }

    /**
     * Main method which starts the programm buy creating a server object and
     * then starting it and then start waiting for connection from clients
     *
     * @param args String[] not used
     */
    public static void main(String[] args) {

        Server s = new Server();
        s.startServer();
        s.listenToClients();

    }


    /**
     * Private class to handle the incoming messages from the clients,
     * starting a thread for each client.
     */
    private class Handler implements Runnable {
        /**
         * A BufferedReader to get the InputStream from the client
         */
        private BufferedReader reader;
        
        /**
         * A boolean to know when to stop
         */
        private boolean work=true;
        
        /**
         * The client Socket
         */
        private Socket clientSocket;

        /**
         * id
         */
        private int id;

        /**
         * Constructor which assigns the InputStream from the client to the
         * BufferedReader
         *
         * @param client Client
         * @param id 
         */
        Handler(Socket client, int id) {
            this.id = id;
            try {
                reader = new BufferedReader(new InputStreamReader(client
                        .getInputStream()));
                clientSocket=client;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * A Thread that will Listen to the client through the BufferedReader
         * and as soon as it receive's a message it will be send to the
         * Server to handle it.
         */
        @Override
        public void run() {
            if(work==true){
                try {
                    String msg;
                    while ((msg = reader.readLine()) != null) {
                        // Clear the List when asked
                        if(msg.contains("/clear")){
                            System.out.println("clearing message list");
                            messageList = new CopyOnWriteArrayList<>();
                        }else if(msg.contains(" a quitté la conversation ")) {
                            //Remove Client's OutputStream when the client leave
                            clientList.remove(clientSocket.getOutputStream());
                            //stop the listener
                            this.stop();
                        }
                        SaveMessagesInJson(msg);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
         public void stop() {
             work=false;
         }
    }

}
