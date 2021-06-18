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

/**
 * Klasa agregujaca wszystkie dzialania zwiazane z baza danych Specjalnie wyekstrahowana do osobnego pliku, by
 * oddzielić dzialania na JSON'ie od wywolan zapytan/procedur SQL
 */
public class SQLHelper {

    static Connection dbConnection = null;

    ///Dane konfigracyjne do polaczenia z MYSQL
    static String DBHOST, DBUSER, DBPASSWORD, DBPORT, DATABASENAME;

    /**
     * Funkcja inicjalizuje polaczenie z baza danych i konczy dzialanie serwera, jesli to sie nie powiedzie
     * (konieczne dzialanie, bo zmienna 'Connection" jest statyczna i inicjalizowana tylko raz)
     */
    public static void InitSqlConnection(){
        //Odczytuje parametry polaczeniu z pliku i konczy pogram, jesli napotka blad (brak koniecznosci try-catch)
        ReadConfiguration();

        //Zainicjalizuj polaczenie z baza danych
        try {
            dbConnection=DriverManager.getConnection("jdbc:mysql://"+DBHOST+":"+DBPORT+"/"+ DATABASENAME,
                    DBUSER, DBPASSWORD);
            //potwierdznie polaczenia z baza danych
            System.out.println("Polaczono z baza danych MySQL");
        }
        catch (SQLException throwables) {
            ClientSession.logger.WriteLog("Blad inicjalizacji polaczenia z MYSQL -> " + throwables.getMessage(), "ERROR");
            System.exit(1);
        }

    }
    /**
     * Funkcja wywoluje procudere, verfyikujaca dane logowania uzytkownika. W przypadku poprawnego przebiegu, czyli poprawnych danych zwrraca 'success'
     * dla napotkanego bledu zwracany jest wyjatek (status == 45000 to blad obsluzony przez programiste)
     */
    public static boolean VerifyLoginData(String login, String password) {

        //Sam sie zamknie
        try(Statement sqlStatement = dbConnection.createStatement()) {

            //Hashuje haslo (bezpieczenstwo)
            password = generateMD5(password);

            ResultSet queryResult = sqlStatement.executeQuery("SELECT VerifyLoginData('"+login+"', '"+password+"') AS Result");

            //Funkcja SQL zwraca true, jesli dane sie zgadzaja
            queryResult.next();
            return queryResult.getBoolean("Result");

        } catch (SQLException throwables) {
            ClientSession.logger.WriteLog("Blad walidacji danych uzytkownika -> " + throwables.getMessage(), "ERROR");
            return false;
        }
    }

    /**
     * Funkcja wywoluje procudere, dodajaca nowgo uzytkownika. W przypadku poprawnego przebiegu nic nie jest zwracane,
     * dla napotkanego bledu zwracany jest wyjatek (status == 45000 to blad obsluzony przez programiste)
     */
    public static void AddNewUser(String login, String password) throws SQLException {

        //Sam sie zamknie
        try(Statement sqlStatement = dbConnection.createStatement()) {

            //Hashuje haslo (bezpieczenstwo)
            password = generateMD5(password);

            //Procedura SQL zwraca wyjatek, przy bledzie
            ResultSet queryResult = sqlStatement.executeQuery("CALL AddNewUser('"+login+"', '"+password+"')");

        } catch (SQLException sqlException) {

            //Kod 45000, to obsluzony w funkcji blad, wiec nie wpisuj wtedy logow
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Blad podczas tworzenia uzytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }
    }

    /**
     * Funkcja wywoluje procudere, dodajaca nowa lokalizacje. W przypadku poprawnego przebiegu nic nie jest zwracane,
     * dla napotkanego bledu zwracany jest wyjatek (status == 45000 to blad obsluzony przez programiste)
     */
    public static void AddNewLocation(String locationName) throws SQLException {

        //Sam sie zamknie
        try(Statement sqlStatement = dbConnection.createStatement()) {

            //Procedura SQL zwraca wyjatek, przy bledzie
            ResultSet queryResult = sqlStatement.executeQuery("CALL AddNewLocation('"+locationName+"')");

        } catch (SQLException sqlException) {

            //Kod 45000, to obsluzony w funkcji blad, wiec nie wpisuj wtedy logow
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Blad podczas tworzenia uzytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }
    }

    /**
     * Funkcja wywoluje procudere, wykonujaca wydanie. W przypadku poprawnego przebiegu nic nie jest zwracane,
     * dla napotkanego bledu zwracany jest wyjatek (status == 45000 to blad obsluzony przez programiste)
     */
    public static String DoInput(String getFrom,  ArrayList<AssortmentEntity> listOfAssortments) throws SQLException {

        Statement sqlStatement = null;
        //Sam sie zamknie
        try {
            sqlStatement = dbConnection.createStatement();

            //Rozpocznij procedure, bo kazdy element na liscie asortymento, to nowe wywolanie procedury SQL
            //GDy cos sie wysypie, to transakcja jest cofana
            sqlStatement.executeQuery("START TRANSACTION ");

            //Procedura SQL zwraca wyjatek, przy bledzie
            for (var assortment: listOfAssortments) {
                ResultSet queryResult = sqlStatement.executeQuery("CALL DoInput('"+getFrom+"', '"+assortment.getLocalization()+"', " +
                        "'"+assortment.getName()+"','"+assortment.getCount()+"')");
            }
            //Zatwierdz transakcje, jesli nie wyrzucil zadnwego wyjatku
            sqlStatement.executeQuery("COMMIT");

            return "success";

        } catch (SQLException sqlException) {

            //Cofnij transakcje, jesli procedura wyrzucila wyjatek
            sqlStatement.executeQuery("ROLLBACK");

            //Kod 45000, to obsluzony w funkcji blad, wiec nie wpisuj wtedy logow
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Blad podczas tworzenia uzytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }finally {
            sqlStatement.close();
        }
    }

    /**
     * Funkcja wywoluje procudere, wykonujaca transfer. W przypadku poprawnego przebiegu nic nie jest zwracane,
     * dla napotkanego bledu zwracany jest wyjatek (status == 45000 to blad obsluzony przez programiste)
     */
    public static String DoTransfer(String fromLocation, String toLocation,String assortmentName, float quantity) throws SQLException {

        Statement sqlStatement = null;
        //Sam sie zamknie
        try {
            sqlStatement = dbConnection.createStatement();

            //Rozpocznij procedure, bo kazdy element na liscie asortymento, to nowe wywolanie procedury SQL
            //GDy cos sie wysypie, to transakcja jest cofana
            sqlStatement.executeQuery("START TRANSACTION ");

            //Procedura SQL zwraca wyjatek, przy bledzie
                ResultSet queryResult = sqlStatement.executeQuery("CALL DoTransfer('"+fromLocation+"', '"+toLocation+"', " +
                        "'"+assortmentName+"','"+quantity+"')");
            //Zatwierdz transakcje, jesli nie wyrzucil zadnwego wyjatku
            sqlStatement.executeQuery("COMMIT");

            return "success";

        } catch (SQLException sqlException) {

            //Cofnij transakcje, jesli procedura wyrzucila wyjatek
            sqlStatement.executeQuery("ROLLBACK");

            //Kod 45000, to obsluzony w funkcji blad, wiec nie wpisuj wtedy logow
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Blad podczas tworzenia uzytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }finally {
            sqlStatement.close();
        }
    }
    /**
     * Funkcja wywoluje procudere, dodajaca nowy towar. W przypadku poprawnego przebiegu nic nie jest zwracane,
     * dla napotkanego bledu zwracany jest wyjatek (status == 45000 to blad obsluzony przez programiste)
     */
    public static void AddNewAssortment(String assortmentName) throws SQLException {

        //Sam sie zamknie
        try(Statement sqlStatement = dbConnection.createStatement()) {

            //Procedura SQL zwraca wyjatek, przy bledzie
            ResultSet queryResult = sqlStatement.executeQuery("CALL AddNewAssortment('"+assortmentName+"')");

        } catch (SQLException sqlException) {

            //Kod 45000, to obsluzony w funkcji blad, wiec nie wpisuj wtedy logow
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Blad podczas tworzenia uzytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }
    }

    /**
     * Funkcja wywoluje procudere, dodajaca nowego klienta. W przypadku poprawnego przebiegu nic nie jest zwracane,
     * dla napotkanego bledu zwracany jest wyjatek (status == 45000 to blad obsluzony przez programiste)
     */
    public static void AddNewContractor(String contractorName) throws SQLException {

        //Sam sie zamknie
        try(Statement sqlStatement = dbConnection.createStatement()) {

            //Procedura SQL zwraca wyjatek, przy bledzie
            ResultSet queryResult = sqlStatement.executeQuery("CALL AddNewContractor('"+contractorName+"')");

        } catch (SQLException sqlException) {

            //Kod 45000, to obsluzony w funkcji blad, wiec nie wpisuj wtedy logow
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Blad podczas tworzenia kontrachenta -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }
    }

    /**
     * Funkcja wyciaga z bazy wszystkie stany magazynowe z odpowiednimi filtrami
     * W przypadku napotkania  bledu, zwraca jego komunikat
     */
    public static String GetStockItem(String assortmentName, String locationName) throws SQLException {

        //Sam sie zamknie
        try(Statement sqlStatement = dbConnection.createStatement()) {

            //Procedura SQL zwraca wyjatek, przy bledzie
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

            //Kod 45000, to obsluzony w funkcji blad, wiec nie wpisuj wtedy logow
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Blad podczas tworzenia uzytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }
    }

    /**
     * Funkcja wywoluje procudere, wykonujaca wydanie. W przypadku poprawnego przebiegu nic nie jest zwracane,
     * dla napotkanego bledu zwracany jest wyjatek (status == 45000 to blad obsluzony przez programiste)
     */
    public static String DoOutput(String sendFrom, String sendTo, ArrayList<AssortmentEntity> listOfAssortments) throws SQLException {

        Statement sqlStatement = null;
        //Sam sie zamknie
        try {
            sqlStatement = dbConnection.createStatement();

            //Rozpocznij procedure, bo kazdy element na liscie asortymento, to nowe wywolanie procedury SQL
            //GDy cos sie wysypie, to transakcja jest cofana
            sqlStatement.executeQuery("START TRANSACTION ");

            //Procedura SQL zwraca wyjatek, przy bledzie
            for (var assortment: listOfAssortments) {
                ResultSet queryResult = sqlStatement.executeQuery("CALL DoOutput('"+sendFrom+"', '"+sendTo+"', " +
                        "'"+assortment.getName()+"', '"+assortment.getLocalization()+"','"+assortment.getCount()+"')");
            }

            //Zatwierdz transakcje, jesli nie wyrzucil zadnwego wyjatku
            sqlStatement.executeQuery("COMMIT");

            return "success";

        } catch (SQLException sqlException) {

            //Cofnij transakcje, jesli procedura wyrzucila wyjatek
            sqlStatement.executeQuery("ROLLBACK");

            //Kod 45000, to obsluzony w funkcji blad, wiec nie wpisuj wtedy logow
            if(!sqlException.getSQLState().equals("45000")){
                ClientSession.logger.WriteLog("Blad podczas tworzenia uzytkownika -> " + sqlException.getMessage(), "ERROR");
            }
            throw sqlException;
        }finally {
            sqlStatement.close();
        }
    }

    /**
     * Funkcja odczytuje parametry polaczenia do bazy MYSQL (dane konfiguracyjne sa w pliku)
     */
    private static void ReadConfiguration(){
        //Odczytaj plik
        try(FileInputStream propertiesFile = new FileInputStream("SQLConfig.properties")){

            //Utworz klase pomocnicza, ktora sparsuje dane z pliku
            DBConfigParser properties = new DBConfigParser();
            properties.load(propertiesFile);

            DBHOST = properties.getProperty("host");
            DBUSER = properties.getProperty("username");
            DBPASSWORD = properties.getProperty("password");
            DBPORT = properties.getProperty("port");
            DATABASENAME = properties.getProperty("database");

            //Sprawdz, czy ktoras wartosc nie jest NULL'em,
            if(DBHOST == null || DBUSER == null || DBPASSWORD == null || DBPORT == null || DATABASENAME == null){
                ClientSession.logger.WriteLog("Bledna struktura pliku konfiguracyjnego bazy danych. Brak jednego z 5 kluczowych parametrow",
                        "ERROR");
                System.exit(1);
            }

        }catch (IOException ioException){
            ClientSession.logger.WriteLog("Blad odczytu konfiguracji dla bazy danych -> " + ioException.getMessage(),
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


