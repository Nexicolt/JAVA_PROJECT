package Serwer;

import java.io.*;
import java.util.HashMap;

/**
 * Prosta klasa pomocniczna, odpowiedzialna za parsowanie pliku konfiguracyjnego z danymi polaczeniowymi do bazy MYSQL
 */
public class DBConfigParser {

    HashMap<String, String> fileKeys = new HashMap<String, String>();

    /**
     * laduje config z pliku
     */
    public void load(FileInputStream pathToFile){
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pathToFile))){
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                var splittedLine = line.split(":");

                //kontynyuj petle, jesli linia byla pusta, lub nie posiadala znaku ':'
                if(splittedLine.length == 0) continue;

                fileKeys.put(splittedLine[0],(splittedLine.length == 1) ? "" : splittedLine[1]);
            }
        } catch (FileNotFoundException notFoundException){
            System.out.println("Brak pliku o podanej nazwie");
        }catch (IOException ioException){
            System.out.println("Blad czytania z pliku");
        }catch(Exception exception){
            System.out.println("Nieobsluzony blad" + exception.getMessage());
        }
    }

    /**
     * Zwraca klucz z wczesniej odczytanego cxonfigu
     */
    public String getProperty(String keyName){
        return fileKeys.get(keyName);
    }
}
