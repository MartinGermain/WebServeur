package Client;

import IHM.ChatUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Integer.parseInt;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class Client {
    /**
     * The ChatGui for the Chat
     */
    private ChatUI chat = new ChatUI();

    private BufferedReader reader;
    private PrintWriter writer;

    private InetAddress address ;
    private int groupPort;
    private String name;
    private MulticastSocket socket;
    private Socket client;
    private Thread listenerThread;
    private Thread HistoryThread;

    /**
     * Sets the ActionListener on the ChatGui, creates a connection to the server
     * and starts a Thread that allows the client to receive messages from
     * the server
     */
    public Client() {
        this.setActionListener();
    }

    /**
     * Connect a client to the Chat
     * Lauch the ChatUI
     * Inform the others that a new user has joined the chat
     * 
     * @param address String address
     * @param groupPort String port on which we want to communicate
     * @param name String name of the user
     */
    public  void startClient(String address, String groupPort, String name) throws IOException {
        this.name=name;
        this.address =InetAddress.getByName(address);
        this.groupPort = parseInt(groupPort);
        
        //connect to server to deal with the chat's history
        connectToServer();

        //display the ChatUI
        chat.setVisible(true);
        // Create a multicast socket
        socket = new MulticastSocket(this.groupPort);
       // Join the group
        socket.joinGroup(this.address);
        
        //say to the others that we are online
        String msg= this.name +" a rejoint la discussion";
        DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), this.address, this.groupPort);
        socket.send(hi);
        
        //send to server
        writer.write(msg + " \n");
        writer.flush();
       
        //Create and start the Listener to get the messages from the chat
        listenerThread = new Thread(new getMessagesFromChat());
        listenerThread.start();          
        }


    /**
     * Sets the Key listener for the field, as soon as enter is released the
     * message will be shown on the text area and the field will be cleared
     */
    private void setActionListener() {
        chat.getMsg().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    try{
                        sendMessageToServer();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    } 
                }
            }
        });
        //Quit the chat when the Exit button is clicked
        chat.getExitbutton().addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                 try{
                    QuitConversation(); 
                }catch (Exception ex){
                    ex.printStackTrace();
                }  
            }
        });
        // Send the message when the Send button is clicked
        chat.getbuttonSend().addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                try{
                    sendMessageToServer(); 
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Sends a message to the server and the other users by getting the content of the JTextField,
     * and wipes it clean, adds the user to the message too.
     * The server save the messages
     */
    private void sendMessageToServer() throws IOException {
        String msg = name+ ": " + chat.getMsg().getText();
        chat.getMsg().setText("");
        
        DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), address, groupPort);
        
        //send only if it is not a clear request
        if(!(msg.contains("/clear"))){
            //send the message to the other users
            socket.send(hi);
        }else{
            chat.getArea().setText("");
        }
        //send to server
        writer.write(msg + " \n");
        writer.flush();
    }

    /**
     * Creating a connection to the Server with port number 4023 and localhost,
     * initializing a BufferedReader to get the messages from the server and
     * a PrintWriter to send messages to the Server
     * Main goal: get the history of the chat
     */
    private void connectToServer() {
        try {
            client = new Socket("localhost", 4023);
            reader = new BufferedReader(new InputStreamReader(client.
                    getInputStream()));
            writer = new PrintWriter(client.getOutputStream());
            
            //Create and start the listener to get the chat's history
            HistoryThread = new Thread(new getHistoryFromServer());
            HistoryThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Send a message to the other users when a client leave the conversation
     * Stop the workings listeners
     * Close the Streams and the socket
     */
     private void QuitConversation() throws IOException{
        String msg= this.name +" a quitté la discussion";
        //send message to the server
        writer.write(msg + " \n");
        writer.flush();
        
        DatagramPacket bye = new DatagramPacket(msg.getBytes(), msg.length(), this.address, this.groupPort);
        socket.send(bye);
        socket.leaveGroup(address);
        listenerThread.stop();
        HistoryThread.stop();
        writer.close();
        client.close();
        System.exit(0);
     }
     

    /**
     * Starts a client which will open a chatGui
     *
     * @param args String[] not used
     */
    public static void main(String[] args) throws IOException {
        InetAddress groupAddr = InetAddress.getByName("228.5.6.7");
        int groupPort = 6788;
        String name="andre";
        new Client();
    }

    /**
     * Private Class that implements Runnable and allows the client to
     * receive the messages form the chat and then display it on the ChatGui
     */
    private class getMessagesFromChat implements Runnable {
        boolean work=true;
        
        public void run() {
            if(work==true){
                String msg;
                try {
                    while (true) {
                        byte[] buf = new byte[1000];
                        DatagramPacket recv = new  DatagramPacket(buf, buf.length);  // Receive a datagram packet response
                        socket.receive(recv);
                        msg = new String(buf, 0, recv.getLength());
                        chat.getArea().append(msg+ "\n");
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
    
    /**
     * Private Class that implements Runnable and allows the client to
     * receive the history of the conversation form the server and then display it on the ChatGui
     */
    private class getHistoryFromServer implements Runnable {
        private boolean work=true;
        
        public void run() {
            if(work==true){
                String msg;
                try {
                    while ((msg = reader.readLine()) != null) {
                        chat.getArea().append(msg + "\n");
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

