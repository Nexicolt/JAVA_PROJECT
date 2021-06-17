package Serwer;

import WMS.Entity.AssortmentEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;


public class SQLHelper {

    static Connection dbConnection = null;

    ///Dane konfigracyjne do połączenia z MYSQL
    static String DBHOST, DBUSER, DBPASSWORD, DBPORT, DATABASENAME;


    public static void InitSqlConnection(){
        //Odczytuje parametry połączeniu z pliku i kończy pogram, jeśli napotka błąd (brak konieczności try-catch)
        ReadConfiguration();

        //Zainicjalizuj połączenie z bazą danych
        try {
            dbConnection=DriverManager.getConnection("jdbc:mysql://"+DBHOST+":"+DBPORT+"/"+ DATABASENAME,
                    DBUSER, DBPASSWORD);
            //potwierdznie połączenia z bazą danych
            System.out.println("Połączono z bazą danych MySQL");
        }
        catch (SQLException throwables) {
            ClientSession.logger.WriteLog("Błąd inicjalizacji połączenia z MYSQL -> " + throwables.getMessage(), "ERROR");
            System.exit(1);
        }

    }
    /**
     * Funkcja odpytuje baze danych i sprawdza ilośc rekordów w tabeli 'Users', dla podanego loginu i hasła
     */
    //TODO przerobic
    public static boolean VerifyLoginData(String login, String password) {

        //Sam się zamknie
        try(Statement sqlStatement = dbConnection.createStatement()) {

            //Hashuje hasło (bezpieczeństwo)
            password = generateMD5(password);

            ResultSet queryResult = sqlStatement.executeQuery("SELECT VerifyLoginData('"+login+"', '"+password+"') AS Result");

            //Funkcja SQL zwraca true, jeśli dane się zgadzają
            queryResult.next();
            return queryResult.getBoolean("Result");

        } catch (SQLException throwables) {
            ClientSession.logger.WriteLog("Błąd walidacji danych użytkownika -> " + throwables.getMessage(), "ERROR");
            return false;
        }
    }

    /**
     * Funkcja wywołuje procuderę, dodającą nowrgo użytkownika i zwraca ok, w przypadku powodzenia
     * W przypadku napotkania  błędu, zwraca jego komunikat
     */
    public static void AddNewUser(String login, String password) throws SQLException {

        //Sam się zamknie
        try(Statement sqlStatement = dbConnection.createStatement()) {

            //Hashuje hasło (bezpieczeństwo)
            password = generateMD5(password);

            //Procedura SQL zwraca wyjątek, przy błędzie
            ResultSet queryResult = sqlStatement.executeQuery("CALL AddNewUser('"+login+"', '"+password+"')");

        } catch (SQLException sqlException) {

            //Kod 45000, to obsłużony w funkcji błąd, więc nie wpisuj wtedy logów
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Błąd podczas tworzenia użytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }
    }

    /**
     * Funkcja wywołuje procuderę, dodającą nowrgo użytkownika i zwraca ok, w przypadku powodzenia
     * W przypadku napotkania  błędu, zwraca jego komunikat
     */
    public static void AddNewLocation(String locationName) throws SQLException {

        //Sam się zamknie
        try(Statement sqlStatement = dbConnection.createStatement()) {

            //Procedura SQL zwraca wyjątek, przy błędzie
            ResultSet queryResult = sqlStatement.executeQuery("CALL AddNewLocation('"+locationName+"')");

        } catch (SQLException sqlException) {

            //Kod 45000, to obsłużony w funkcji błąd, więc nie wpisuj wtedy logów
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Błąd podczas tworzenia użytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }
    }

    public static String DoInput(String getFrom,  ArrayList<AssortmentEntity> listOfAssortments) throws SQLException {

        Statement sqlStatement = null;
        //Sam się zamknie
        try {
            sqlStatement = dbConnection.createStatement();

            //Rozpocznij procedurę, bo każdy element na liście asortymentó, to nowe wywołanie procedury SQL
            //GDy coś się wysypie, to transakcja jest cofana
            sqlStatement.executeQuery("START TRANSACTION ");

            //Procedura SQL zwraca wyjątek, przy błędzie
            for (var assortment: listOfAssortments) {
                ResultSet queryResult = sqlStatement.executeQuery("CALL DoInput('"+getFrom+"', '"+assortment.getLocalization()+"', " +
                        "'"+assortment.getName()+"','"+assortment.getCount()+"')");
            }
            //Zatwierdź transakcję, jeśli nie wyrzucił żadnwego wyjątku
            sqlStatement.executeQuery("COMMIT");

            return "success";

        } catch (SQLException sqlException) {

            //Cofnij transakcję, jeśli procedura wyrzuciła wyjątek
            sqlStatement.executeQuery("ROLLBACK");

            //Kod 45000, to obsłużony w funkcji błąd, więc nie wpisuj wtedy logów
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Błąd podczas tworzenia użytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }finally {
            sqlStatement.close();
        }
    }

    public static String DoTransfer(String fromLocation, String toLocation,String assortmentName, float quantity) throws SQLException {

        Statement sqlStatement = null;
        //Sam się zamknie
        try {
            sqlStatement = dbConnection.createStatement();

            //Rozpocznij procedurę, bo każdy element na liście asortymentó, to nowe wywołanie procedury SQL
            //GDy coś się wysypie, to transakcja jest cofana
            sqlStatement.executeQuery("START TRANSACTION ");

            //Procedura SQL zwraca wyjątek, przy błędzie
                ResultSet queryResult = sqlStatement.executeQuery("CALL DoTransfer('"+fromLocation+"', '"+toLocation+"', " +
                        "'"+assortmentName+"','"+quantity+"')");
            //Zatwierdź transakcję, jeśli nie wyrzucił żadnwego wyjątku
            sqlStatement.executeQuery("COMMIT");

            return "success";

        } catch (SQLException sqlException) {

            //Cofnij transakcję, jeśli procedura wyrzuciła wyjątek
            sqlStatement.executeQuery("ROLLBACK");

            //Kod 45000, to obsłużony w funkcji błąd, więc nie wpisuj wtedy logów
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Błąd podczas tworzenia użytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }finally {
            sqlStatement.close();
        }
    }
    /**
     * Funkcja wywołuje procuderę, dodającą nowy towar i zwraca ok, w przypadku powodzenia
     * W przypadku napotkania  błędu, zwraca jego komunikat
     */
    public static void AddNewAssortment(String assortmentName) throws SQLException {

        //Sam się zamknie
        try(Statement sqlStatement = dbConnection.createStatement()) {

            //Procedura SQL zwraca wyjątek, przy błędzie
            ResultSet queryResult = sqlStatement.executeQuery("CALL AddNewAssortment('"+assortmentName+"')");

        } catch (SQLException sqlException) {

            //Kod 45000, to obsłużony w funkcji błąd, więc nie wpisuj wtedy logów
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Błąd podczas tworzenia użytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }
    }

    public static void AddNewContractor(String contractorName) throws SQLException {

        //Sam się zamknie
        try(Statement sqlStatement = dbConnection.createStatement()) {

            //Procedura SQL zwraca wyjątek, przy błędzie
            ResultSet queryResult = sqlStatement.executeQuery("CALL AddNewContractor('"+contractorName+"')");

        } catch (SQLException sqlException) {

            //Kod 45000, to obsłużony w funkcji błąd, więc nie wpisuj wtedy logów
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Błąd podczas tworzenia kontrachenta -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }
    }

    /**
     * Funkcja wyciąga z bazy wszystkie stany magazynowe z odpowiednimi filtrami
     * W przypadku napotkania  błędu, zwraca jego komunikat
     */
    public static String GetStockItem(String assortmentName, String locationName) throws SQLException {

        //Sam się zamknie
        try(Statement sqlStatement = dbConnection.createStatement()) {

            //Procedura SQL zwraca wyjątek, przy błędzie
            ResultSet queryResult = sqlStatement.executeQuery("CALL GetStockItem('"+assortmentName+"', '"+locationName+"')");

            JSONArray stockItemReturnJSon = new JSONArray();

            //Iteruj po odczytanych wierszach i pakuj je do tablicy JSONA
            while (queryResult.next()){
                JSONObject tmpStockItem = new JSONObject();
                tmpStockItem.put("assortment_name", queryResult.getString("Assortment"));
                tmpStockItem.put("location_name", queryResult.getString("Location"));
                tmpStockItem.put("stock_level", queryResult.getString("Count"));

                stockItemReturnJSon.put(tmpStockItem);
            }
            return  stockItemReturnJSon.toString();

        } catch (SQLException sqlException) {

            //Kod 45000, to obsłużony w funkcji błąd, więc nie wpisuj wtedy logów
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Błąd podczas tworzenia użytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }
    }

    /**
     * Funkcja
     */
    public static String DoOutput(String sendFrom, String sendTo, ArrayList<AssortmentEntity> listOfAssortments) throws SQLException {

        Statement sqlStatement = null;
        //Sam się zamknie
        try {
            sqlStatement = dbConnection.createStatement();

            //Rozpocznij procedurę, bo każdy element na liście asortymentó, to nowe wywołanie procedury SQL
            //GDy coś się wysypie, to transakcja jest cofana
            sqlStatement.executeQuery("START TRANSACTION ");

            //Procedura SQL zwraca wyjątek, przy błędzie
            for (var assortment: listOfAssortments) {
                ResultSet queryResult = sqlStatement.executeQuery("CALL DoOutput('"+sendFrom+"', '"+sendTo+"', " +
                        "'"+assortment.getName()+"', '"+assortment.getLocalization()+"','"+assortment.getCount()+"')");
            }

            //Zatwierdź transakcję, jeśli nie wyrzucił żadnwego wyjątku
            sqlStatement.executeQuery("COMMIT");

            return "success";

        } catch (SQLException sqlException) {

            //Cofnij transakcję, jeśli procedura wyrzuciła wyjątek
            sqlStatement.executeQuery("ROLLBACK");

            //Kod 45000, to obsłużony w funkcji błąd, więc nie wpisuj wtedy logów
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Błąd podczas tworzenia użytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }finally {
            sqlStatement.close();
        }
    }

    /**
     * Funkcja odczytuje parametry połączenia do bazy MYSQL (dane konfiguracyjne są w pliku)
     */
    private static void ReadConfiguration(){
        //Odczytaj plik
        try(FileInputStream propertiesFile = new FileInputStream("SQLConfig.properties")){

            //Utwórz klasę pomocniczą, która sparsuje dane z pliku
            DBConfigParser properties = new DBConfigParser();
            properties.load(propertiesFile);

            DBHOST = properties.getProperty("host");
            DBUSER = properties.getProperty("username");
            DBPASSWORD = properties.getProperty("password");
            DBPORT = properties.getProperty("port");
            DATABASENAME = properties.getProperty("database");

            //Sprawdż, czy któraś wartośc nie jest NULL'em,
            if(DBHOST == null || DBUSER == null || DBPASSWORD == null || DBPORT == null || DATABASENAME == null){
                ClientSession.logger.WriteLog("Błędna struktura pliku konfiguracyjnego bazy danych. Brak jednego z 5 kluczowych parametrów",
                        "ERROR");
                System.exit(1);
            }

        }catch (IOException ioException){
            ClientSession.logger.WriteLog("Błąd odczytu konfiguracji dla bazy danych -> " + ioException.getMessage(),
                                            "ERROR");
            System.exit(1);
        }
    }

    /**
     * Funkcja generuje hash MD5 z podanego w parametrze stringa
     */
    private static String generateMD5(String stringToHash){
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignored) {}

        md5.update(StandardCharsets.UTF_8.encode(stringToHash));
        return String.format("%032x", new BigInteger(1, md5.digest()));
    }
}


