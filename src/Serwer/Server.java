package Serwer;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Glowna klasa serwera, odpowiedzialna za uruchomienie instancji serwerowej i tworzenie nowych instancji polaczen z klientami (socketow)
 */
public class Server {

    public static int port = 14432;
    ServerSocket communicationSocket;

    //Obiekt klasy 'Logger', pozwalajacy na wpisywanie logow do pliku
    private static Logger logger = new Logger();

    /**
     * Konstruktor inicjalizujacy socket komunikacyjny
     */
    public Server() {
        try{
            communicationSocket = new ServerSocket(port);
            //blok try catch zbedny, funkcja wylaczy serwer przy wyjatku
            SQLHelper.InitSqlConnection();
            logger.WriteLog("Poprawnie uruchomiono serwer", "DEBUG");
        }catch (Exception exception){
            logger.WriteLog("Blad podczas urchamiania serwera  -> " + exception.getMessage(), "ERROR");
            System.exit(1);
        }
    }

    /**
     * Funkcja uruchamia serwer i nasluchuje komunikatow
     */
    void StartSever() throws Exception {
        while(true) {
            Socket socket = communicationSocket.accept();
            new ClientSession(socket).start();
        }
    }

    /**
     * Glowna funkcja serwera, to ona uruchamia caly serwer
     */
    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.StartSever();
            server.communicationSocket.close();
        } catch (Exception exception) {
            logger.WriteLog("Blad podczas uruchamiania serwera -> " + exception.getMessage(), "ERROR");
            System.exit(1);
        }

    }
}
