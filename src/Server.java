// Exercise 17.18 Solution: Server.java
// Program sets up a Server that will receive connections
// from clients, send strings to the clients and receive
// string from the clients.

// Java core packages
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;

// Java extension packages
import javax.swing.*;

public class Server extends JFrame {

    private JTextField enterField;
    private JTextArea display;
    private JScrollPane scroller;
    private Vector clients;
    private int numberOfClients;
    private EncryptDecrypt myCrypt = new EncryptDecrypt();
    

    Random rnd = new Random();
    int intMin = 1;
    int intMax = 10;
    // set up GUI

    public Server() {
        super("Server");

        numberOfClients = 0;

        // create enterField and register listener
        enterField = new JTextField();
        enterField.setEnabled(false);

        enterField.addActionListener(
                // anonymous inner class
                new ActionListener() {

            // send message to clients
            public void actionPerformed(ActionEvent event) {
                for (int i = 0; i < clients.size(); i++) {
                    ((ClientThread) clients.elementAt(i)).sendData(
                            event.getActionCommand());
                }

                enterField.setText("");
            }

        } // end anonymous inner class

        ); // end call to addActionListener

        display = new JTextArea();
        display.setEnabled(false);
        display.setBackground(Color.BLACK);
        scroller = new JScrollPane(display);

        Container container = getContentPane();
        container.add(enterField, BorderLayout.NORTH);
        container.add(scroller, BorderLayout.CENTER);

        setSize(300, 150);
        show();
    }

    // set up and run server
    public void runServer() {
        // set up server and process connections
        try {

            // create ServerSocket
            ServerSocket server = new ServerSocket(5558, 100);

            clients = new Vector();

            // accept connections and add ClientThreads to Vector
            while (true) {
                display.append("Waiting for connection\n");
                numberOfClients++;
                int intRand = rnd.nextInt(intMax - intMin + 1) + intMin;

                clients.add(new ClientThread(server.accept(),
                        display, numberOfClients, intRand));
                ((ClientThread) clients.lastElement()).start();
// ClientThread ctTemp = (ClientThread)clients.lastElement();

                enterField.setEnabled(true);
            }
        } // process problems with I/O
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // execute application
    public static void main(String args[]) {
        Server application = new Server();

        application.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        application.runServer();
    }

    // private inner class ClientThread
    // manages each Client as a thread
    private class ClientThread extends Thread {

        private int clientNumber;
        private Socket connection;
        private ObjectOutputStream output;
        private ObjectInputStream input;
        private JTextArea display;
        private SecretKey secretKey;
        private int intKey;

        // set up a Client thread
        public ClientThread(Socket socket, JTextArea display, int number, int intInKey) {
            this.display = display;
            clientNumber = number;
            connection = socket;
            intKey = intInKey;
            try {
                secretKey = myCrypt.buildKey(12345678);
                this.display.append("\nKey Generated\n");
            } catch (GeneralSecurityException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // obtain streams from Socket
            try {
                output = new ObjectOutputStream(
                        connection.getOutputStream());
                output.flush();

                input = new ObjectInputStream(connection.getInputStream());
                sendData(Integer.toString(intInKey));
                sendData("Connection successful");

                this.display.append("Connection " + clientNumber + " received from: "
                        + connection.getInetAddress().getHostName() + "\n");
            } // process problems with IO
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        // send message to client
        public void sendData(String message) {
            // send object to client
            try {
                try {
                    output.writeObject(myCrypt.encryptString(message, secretKey));
                } catch (GeneralSecurityException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                output.flush();
                display.append("\nSERVER>>>" + message);
            } // process problems sending object
            catch (IOException ioException) {
                display.append("\nError writing object");
            }
        }

        // control thread's execution
        public void run() {
            String message = null;

            // process connection
            try {

                // read message from client
                do {

                    try {
                        message = (String) input.readObject();
                        display.append("\n Received Encrypted - " + message);
                        try {
                            display.append("\n Decrypted" + myCrypt.decryptString(message,secretKey));
                        } catch (GeneralSecurityException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        display.setCaretPosition(
                                display.getText().length());
                    } // process problems reading from client
                    catch (ClassNotFoundException classNotFoundException) {
                        display.append("\nUnknown object type received");
                    }

                } while (!message.equals("CLIENT>>> TERMINATE"));

                display.append("\nClient terminated connection");
                display = null;
            } // process problems with I/O
            catch (IOException ioException) {
                System.out.println("Client terminated connection");
            } // close streams and socket
            finally {

                try {
                    output.close();
                    input.close();
                    connection.close();
                } // process problems with I/O
                catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                clients.remove(this);
            }

        }  // end method run

    }  // end class ClientThread

}  // end class Server
