package Serwer;

import java.io.*;
import java.util.HashMap;

public class DBConfigParser {

    HashMap<String, String> fileKeys = new HashMap<String, String>();

    /**
     * ładuje config z pliku
     */
    public void load(FileInputStream pathToFile){
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pathToFile))){
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                var splittedLine = line.split(":");

                fileKeys.put(splittedLine[0],(splittedLine.length == 1) ? "" : splittedLine[1]);
            }
        } catch (FileNotFoundException notFoundException){
            System.out.println("Brak pliku o podanej nazwie");
        }catch (IOException ioException){
            System.out.println("Błąd czytania z pliku");
        }catch(Exception exception){
            System.out.println("Nieobsłużony błąd" + exception.getMessage());
        }
    }

    /**
     * Zwraca klucz z wcześniej odczytanego cxonfigu
     * @param keyName
     * @return
     */
    public String getProperty(String keyName){
        return fileKeys.get(keyName);
    }
}
