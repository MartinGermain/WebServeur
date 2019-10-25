/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import IHM.ChatLoginUI;
import IHM.ChatUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import static java.lang.Integer.parseInt;
import java.net.Socket;

/**
 *
 * @author martingermain
 */
public class ChatClient {
    
    private ChatUI chat = new ChatUI();
    private String nom;
    
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket client;
    Thread listenerThread;

    /**
     * Sets the ActionListener on the ChatGui, creates a connection to the server
     * and starts a Thread that allows the client to receive messages from
     * the server
     */
    public ChatClient() {
        this.setActionListener();
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
                    sendMessageToServer();
                }
            }
        });
        // Quit the chat when the Exit button is clicked
        chat.getExitbutton().addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                QuitConversation();  
            }
        });
        // Send the message when the Send button is clicked
        chat.getbuttonSend().addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                sendMessageToServer();  
            }
        });
    }

    /**
     * Sends a message to the server by getting the content of the JTextField,
     * and wipes it clean, adds the user to the message too.
     */
    private void sendMessageToServer() {
        String msg = this.nom + ": " + chat.getMsg().getText();
        chat.getMsg().setText("");
        //clear the history 
        if(msg.contains("/clear")){  
            chat.getArea().setText("");
        }
        writer.write(msg + " \n");
        writer.flush();    
    }

    /**
     * Creating a connection to the Server 
     * initializing a BufferedReader to get the messages from the server and
     * a PrintWriter to send messages to the Server
     * 
     * @param adress String adress
     * @param port String port number
     * @param nom String Username
     */
    public void connectToServer(String adress, String port, String nom) {
        try {
            chat.setVisible(true);
            client = new Socket(adress, parseInt(port));
            reader = new BufferedReader(new InputStreamReader(client.
                    getInputStream()));
            writer = new PrintWriter(client.getOutputStream());
            this.nom = nom;
            writer.println(nom + " s'est connecté a la salle de discussion.");
            writer.flush();
            
            // get the messages from the chat
            listenerThread = new Thread(new getMessagesFromServer());
            listenerThread.start();
        } catch (Exception e) {
            chat.dispose();
            System.out.println("erreur lors de la saisie des données ");
            System.out.println("Port saisi : "+ port);
            System.out.println("Adresse saisie : "+ adress);
            new ChatLoginUI().setVisible(true);
            //e.printStackTrace();
        }

    }
    
    private void QuitConversation(){
        try{
            writer.write(nom + " a quitté la conversation \n");
            writer.flush();
            listenerThread.stop();
            reader.close();
            writer.close();
            client.close();
            System.exit(0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Starts a client which will open a chatGui
     *
     * @param args String[] not used
     */
    public static void main(String[] args) {
        new ChatClient();
    }

    /**
     * Private Class that implements Runnable and allows the client to
     * receive the messages form the server and then display it on the ChatGui
     */
    private class getMessagesFromServer implements Runnable {
        boolean work=true;
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
