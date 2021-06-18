package Serwer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasa pomocnicza, odpowiedzialna za wpisywanie logów do pliku
 */
public class Logger {

    private static final String logsFileName = "logs.log";

    /**
     * Konstruktor tworzy plik, w którym będą przechowywane logi (jeśli nie istnieje)
      */
    Logger(){
        try {
            new File(Logger.logsFileName).createNewFile();
        } catch (IOException ioException) {
            System.out.println("Błąd tworzenia pliku dla logów serwera. Korzystanie z klasy jest niemożliwe");
        }
    }

    /**
     * Funckja wypisuje informacje debugowania w logach
     */
    public void WriteLog(String logMessage, String logLevel) {
        try(BufferedWriter toFileStream = getOutputStreamToLogFile()) {
            toFileStream.write("[" + getCurrentDateAsString() + "]\t[" + logLevel + "]\t" + logMessage + "\n");
        } catch (IOException ioException) {
            return;
        }
    }



    /**
     * Funkcja pomocnicza, zwracająca referencję doo strumienia pisania do pliku z logami.
     * (Pozwala uniknąć powtarzania kodu)
     */
    private BufferedWriter getOutputStreamToLogFile(){
        try{
           return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Logger.logsFileName, true)));
        }catch (IOException ioException){
            return null;
        }
    }

    /**
     * Funkcja pomocnicza, zwracająca aktualną datę jako tekst
     */
    private String getCurrentDateAsString(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
       return (formatter.format(date));
    }
}
