package WMS;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Klasa abstrakcyjna dla okien, typu JFrame, która agreguje kontruktor i metodę, zamykającą połączenie z serwerem,
 * w przypadku zamknięcia palikacji. Model obiektowy - tym sposobem unikam redundancji kodu i implemetowania metod w każdej
 * klasie z osobna
 */
public abstract class AbstractJFrame extends JFrame implements WindowListener {

    protected Socket communicationSocket;
    protected PrintWriter streamToServer;
    protected BufferedReader streamFromServer;

    /**
     * Konstruktor inizjalizuje tytuł okna i przechwytuje refrencję do socketu komunikacji i strumieni
     */
    AbstractJFrame(String windowName, Socket _commSocket, PrintWriter _streamToServer, BufferedReader _streamFromServer) {
        super(windowName);
        communicationSocket = _commSocket;
        streamToServer = _streamToServer;
        streamFromServer = _streamFromServer;
    }

    /**
     * Zwraca referencję do strumienia komunikacyjnego z serwerem
     */
    public PrintWriter GetStreamToServer(){
        return streamToServer;
    }

    /**
     * Zwraca referencję do strumienia komunikacyjnego z serwerem
     */
    public BufferedReader GetStreamFromServer(){
        return streamFromServer;
    }

    /**
     * Zamykanie połączenia z serwerem, poprzez wciśnięcie "X"
     */
    @Override
    public void windowClosing(WindowEvent e) {
        try {
            streamFromServer.close();
            streamToServer.close();
            communicationSocket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

}
