package WMS;

import Style.JoptionPaneMessages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Klasa odpowiedzialna za asynchroniczne sprawdzanie, czy socket komunikacyjny z serwerem nie zostal zamkniety
 * jesli tak sie stalo, to zamyka bierzaca sesje i wywoluje funkcje inicjalizujaca program i nowe polaczenie
 */
public class SocketChecker  extends Thread{

    protected PrintWriter streamToServer;
    protected BufferedReader streamFromServer;
    private MainWindowWMS mainWindow;

    /**
     * Konstruktor, inizjalizujacy uchwyt do okna glownego i referncje do socketu komunikacyjnego
     */
    public SocketChecker(PrintWriter _streamToServer, BufferedReader _streamFromServer, MainWindowWMS _mainWindowHandler){
       mainWindow = _mainWindowHandler;
        streamToServer = _streamToServer;
        streamFromServer = _streamFromServer;
    }

    /**
     * Metoda "isConnected", wywolywana na sockecie komunikacyjny sie nie sprawdza,
     * bo zwraca false dopeiro, gdy to ja, jako klient rozlacze sie z serwerem
     *
     * Metoda sprawdzania polaczenia, to wysylanie do serwera, co sekunde, komendy "heartbear",
     * na ktora ten odpowiada "pik-pik". Jesli strumien czytajacy zwroci NULL'a, to serwer zamknal polaczenie
     */
    @Override
    public void run() {
        while(true){
            try {
                //Strumienie sa zsynchronizowane, by nie zdarzylo sie tak, ze ten watek wysle 'heartbeat' do serwera,
                // a do strumienie czytajacego podebnie sie glowny watek, po akurat bedzie oczekiwal np. na stany magazynowe
                synchronized(streamToServer ){
                    streamToServer.println("heartbeat");
                    synchronized(streamFromServer){
                        streamFromServer.readLine();
                    }
                }
                Thread.sleep(1000);

            } catch (InterruptedException e) { }
            //Wyjatek zwracany przez strumien czytajacy - oznacza zamkniecie komunikacji z serwerem
            catch (IOException ioException) {
                mainWindow.dispose();
                JoptionPaneMessages.showErrorPopup("Blad komunikacji z serwerem. Konieczna ponowna inizjalizacja aplikacji");
                return;

            }
        }
    }
}
