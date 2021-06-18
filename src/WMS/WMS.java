package WMS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static Style.JoptionPaneMessages.showErrorPopup;

/**
 * Klasa rozpoczynajaca inicjalizacje modulu klienta
 */
public class WMS{
    public static int connectionPort = 14432;
    public static String connectionAddress = "Localhost";

    //Dane do polaczenia z serwerem
    private static Socket communicationSocket;
    private static PrintWriter streamToServer;
    private static BufferedReader streamFromServer;

    /**
     * Glowna funkcja, inizjalizujaca aplikacje klienta
     */
    public static void main(String[] args)  {
        //Utworz socket polaczeniowy z serwerem i przekazuj go przez refrencje, by uniknać redundancji
        // i tworzenia socketu w kazdej klasie, pochodnej od JFrame
        try {
            communicationSocket = new Socket(connectionAddress, connectionPort);
            streamToServer = new PrintWriter(communicationSocket.getOutputStream(), true);
            streamFromServer = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));
        } catch (IOException e) {
            showErrorPopup("Blad nawiazywania polaczenia z serwerem!");
            System.exit(0);
        }

        new LoginForm("Logowanie", communicationSocket, streamToServer, streamFromServer).init();
        //new MainWindowWMS("Okno glowne", communicationSocket, streamToServer, streamFromServer).init();

    }
}
