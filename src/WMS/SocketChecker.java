package WMS;

import Style.JoptionPaneMessages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Klasa odpowiedzialna za asynchroniczne sprawdzanie, czy socket komunikacyjny z serwerem nie został zamknięty
 * jeśli tak się stało, to zamyka bierzącą sesję i wywołuję funkcję inicjalizującą program i nowe połączenie
 */
public class SocketChecker  extends Thread{

    protected PrintWriter streamToServer;
    protected BufferedReader streamFromServer;
    private MainWindowWMS mainWindow;

    /**
     * Konstruktor, inizjalizujący uchwyt do okna głównego i referncję do socketu komunikacyjnego
     */
    public SocketChecker(PrintWriter _streamToServer, BufferedReader _streamFromServer, MainWindowWMS _mainWindowHandler){
       mainWindow = _mainWindowHandler;
        streamToServer = _streamToServer;
        streamFromServer = _streamFromServer;
    }

    /**
     * Metoda 'isConnected", wywoływana na sockecie komunikacyjny się nie sprawdza,
     * bo zwraca false dopeiro, gdy to ja, jako klient rozłącze się z serwerem
     *
     * Metoda sprawdzania połączenia, to wysyłanie do serwera, co sekundę, komendy "heartbear",
     * na która ten odpowiada "pik-pik". Jeśli strumień czytający zwróci NULL'a, to serwer zamknął połączenie
     */
    @Override
    public void run() {
        while(true){
            try {
                //Strumienie są zsynchronizowane, by nie zdarzyło się tak, że ten watek wyśle 'heartbeat' do serwera,
                // a do strumienie czytającego podebnie sie główny wątek, po akurat będzie oczekiwał np. na stany magazynowe
                synchronized(streamToServer ){
                    streamToServer.println("heartbeat");
                    synchronized(streamFromServer){
                        streamFromServer.readLine();
                    }
                }
                Thread.sleep(1000);

            } catch (InterruptedException e) { }
            //Wyjątek zwracany przez strumień czytający - oznacza zamknięcie komunikacji z serwerem
            catch (IOException ioException) {
                mainWindow.dispose();
                JoptionPaneMessages.showErrorPopup("Błąd komunikacji z serwerem. Konieczna ponowna inizjalizacja aplikacji");
                return;

            }
        }
    }
}
