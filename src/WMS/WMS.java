package WMS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static Style.JoptionPaneMessages.showErrorPopup;

public class WMS{
    public static int connectionPort = 14432;
    public static String connectionAddress = "Localhost";

    //Dane do połączenia z serwerem
    private static Socket communicationSocket;
    private static PrintWriter streamToServer;
    private static BufferedReader streamFromServer;


    public static void main(String[] args)  {
        //Utwórz socket połączeniowy z serwerem i przekazuj go przez refrencję, by uniknąć redundancji
        // i tworzenia socketu w każdej klasie, pochodnej od JFrame
        try {
            communicationSocket = new Socket(connectionAddress, connectionPort);
            streamToServer = new PrintWriter(communicationSocket.getOutputStream(), true);
            streamFromServer = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));
        } catch (IOException e) {
            showErrorPopup("Błąd nawiązywania połączenia z serwerem!");
            System.exit(0);
        }

        //new LoginForm("Logowanie", communicationSocket, streamToServer, streamFromServer).init();
        new MainWindowWMS("Okno głowne", communicationSocket, streamToServer, streamFromServer).init();
    }
}
