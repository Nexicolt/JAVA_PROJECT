package Serwer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasa pomocnicza, odpowiedzialna za wpisywanie logow do pliku
 */
public class Logger {

    private static final String logsFileName = "logs.log";

    /**
     * Konstruktor tworzy plik, w ktorym beda przechowywane logi (jesli nie istnieje)
      */
    Logger(){
        try {
            new File(Logger.logsFileName).createNewFile();
        } catch (IOException ioException) {
            System.out.println("Blad tworzenia pliku dla logow serwera. Korzystanie z klasy jest niemozliwe");
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
     * Funkcja pomocnicza, zwracajaca referencje doo strumienia pisania do pliku z logami.
     * (Pozwala uniknać powtarzania kodu)
     */
    private BufferedWriter getOutputStreamToLogFile(){
        try{
           return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Logger.logsFileName, true)));
        }catch (IOException ioException){
            return null;
        }
    }

    /**
     * Funkcja pomocnicza, zwracajaca aktualna date jako tekst
     */
    private String getCurrentDateAsString(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
       return (formatter.format(date));
    }
}
