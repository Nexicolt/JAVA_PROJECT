package Serwer;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Główna klasa serwera, odpowiedzialna za uruchomienie instancji serwerowej i tworzenie nowych instancji połączeń z klientami (socketów)
 */
public class Server {

    public static int port = 14432;
    ServerSocket communicationSocket;

    //Obiekt klasy 'Logger', pozwalający na wpisywanie logów do pliku
    private static Logger logger = new Logger();

    /**
     * Konstruktor inicjalizujący socket komunikacyjny
     */
    public Server() {
        try{
            communicationSocket = new ServerSocket(port);
            //blok try catch zbedny, funkcja wylaczy serwer przy wyjatku
            SQLHelper.InitSqlConnection();
            logger.WriteLog("Poprawnie uruchomiono serwer", "DEBUG");
        }catch (Exception exception){
            logger.WriteLog("Błąd podczas urchamiania serwera  -> " + exception.getMessage(), "ERROR");
            System.exit(1);
        }
    }

    /**
     * Funkcja uruchamia serwer i nasłuchuje komunikatów
     */
    void StartSever() throws Exception {
        while(true) {
            Socket socket = communicationSocket.accept();
            new ClientSession(socket).start();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.StartSever();
            server.communicationSocket.close();
        } catch (Exception exception) {
            logger.WriteLog("Błąd podczas uruchamiania serwera -> " + exception.getMessage(), "ERROR");
            System.exit(1);
        }

    }
}
