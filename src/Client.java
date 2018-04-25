// Exercise 17.18 Solution: Client.java
// Program sets up a Client that will read information
// sent from a Server and display the information.

// Java core packages
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
// Java extension packages
import javax.swing.*;

public class Client extends JFrame {

    private JTextField enterField;
    private JTextArea displayArea;
    private JScrollPane scroller;
    ObjectOutputStream output;
    ObjectInputStream input;
    String message = "";
    int intKey;
    SecretKey secretKey;

    private EncryptDecrypt myCrypt = new EncryptDecrypt();

    // set up GUI
    public Client() {
        super("Client");

        // set closing operation
        addWindowListener(
                new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                sendData("TERMINATE");
                System.exit(0);
            }

        });

        // create enterField and register listener
        enterField = new JTextField();
        enterField.setEnabled(false);

        enterField.addActionListener(
                // anonymous inner class
                new ActionListener() {
            // send message to server
            public void actionPerformed(ActionEvent event) {
                sendData(event.getActionCommand());
                enterField.setText("");
            }

        }); // end call to addActionListener

        displayArea = new JTextArea();
        displayArea.setEnabled(false);
        displayArea.setBackground(Color.black);

        scroller = new JScrollPane(displayArea);

        Container container = getContentPane();
        container.add(enterField, BorderLayout.NORTH);
        container.add(scroller, BorderLayout.CENTER);

        setSize(300, 150);
        show();
    }

    // connect to server, get streams, process connection
    public void runClient() {
        Socket client;

        // connect to server, get streams, process connection
        try {
            displayArea.setText("Attempting connection\n");

            // create Socket to make connection to server
            String IP = JOptionPane.showInputDialog("Please Enter the IP of the server");
            JOptionPane.showMessageDialog(null, "Correct key is 12345678");
            long keySeed = Long.parseLong(JOptionPane.showInputDialog("Input Secret Seed (Long)"));
            try {
                secretKey = myCrypt.buildKey(keySeed);
                this.displayArea.append("Key Generated\n");
            } catch (GeneralSecurityException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

            client = new Socket(InetAddress.getByName(IP), 5558);

            // display connection information
            displayArea.append("Connected to: " + client.getInetAddress().getHostName());

            // set up output stream for objects
            output = new ObjectOutputStream(client.getOutputStream());

            // flush output buffer to send header information
            output.flush();

            // set up input stream for objects
            input = new ObjectInputStream(client.getInputStream());

            displayArea.append("\nGot I/O streams\n");
            String strTemp = "";
            try {
                strTemp = myCrypt.decryptString((String) input.readObject(),secretKey);
            } catch (GeneralSecurityException ex) {
                this.displayArea.append("Incorrect Key");
            }
            intKey = Integer.parseInt(strTemp);
            displayArea.append("\n Received key - " + intKey);

            // enable enterField so client user can send messages
            enterField.setEnabled(true);

            // process messages sent from server
            do {
                // read message and display it
                try {
                    message = (String) input.readObject();
                    displayArea.append("\n Encrypted - " + message);
                    try {
                        displayArea.append("\n Decrypted - " + myCrypt.decryptString(message, secretKey));
                    } catch (GeneralSecurityException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    displayArea.setCaretPosition(
                            displayArea.getText().length());
                } // catch problems reading from server
                catch (ClassNotFoundException classNotFoundException) {
                    displayArea.append("\nUnknown object type received");
                }
            } while (!message.equals("SERVER>>> TERMINATE"));

            displayArea.append("\nClosing connection.\n");

            // close streams and socket
            output.close();
            input.close();
            client.close();

            displayArea.append("Connection closed.");
        } // server closed connection // server closed connection
        catch (ClassNotFoundException cnf) {

        } catch (EOFException eofException) {
            System.err.println("Server terminated connection");
        } // process problems communicating with server
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // send message to server
    private void sendData(String string) {
        // send object to client
        try {
            try {
                message = myCrypt.encryptString("CLIENT>>> " + string, secretKey);
            } catch (GeneralSecurityException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            output.writeObject(message);
            output.flush();
            displayArea.append("\nCLIENT>>> " + string);
        } // process problems sending object
        catch (IOException ioException) {
            displayArea.append("\nError writing object");
            ioException.printStackTrace();
        }
    }

    // execute application
    public static void main(String args[]) {
        final Client application = new Client();
        application.runClient();
    }

}  // end class Client
