package Serwer;

import WMS.Entity.AssortmentEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Klasa reprezentujaca pojedynczego klienta. Kazdy nowo polaczony klient, to nowy obiekt klasy.
 * Odpowiada za nasluchiwanie przychodzacych komunikatow i odsylanie komunikatow zwrotnych
 */
class ClientSession extends Thread {
    private final Socket clientCommunicatorSocket;
    private BufferedReader inputStream;
    private PrintWriter outputStream;

    //Obiekt klasy 'Logger', pozwalajacy na wpisywanie logow do pliku
    public static Logger logger = new Logger();

    /**
     * Konstruktor, inizjalizujacy socket oraz strumienie wyjscia/wejscia
     */
    public ClientSession(Socket _socket) {
        clientCommunicatorSocket = _socket;
        try {
            inputStream = new BufferedReader(new InputStreamReader(clientCommunicatorSocket.getInputStream()));
            outputStream = new PrintWriter(new OutputStreamWriter(clientCommunicatorSocket.getOutputStream()), true);
        } catch (Exception e) {
            logger.WriteLog("Blad podczas inizjalizacji polaczenia z klientem -> " + e.getMessage(), "ERROR");
        }
    }

    /**
     * Glowna funckja watku, odpowiedzialna za nasluchiwanie i wywolanie odpowiednich metod, odpowiadajacych klientowi
     */
    public void run() {
        String jsonMessageFromClient;
        try {
            while ((jsonMessageFromClient = inputStream.readLine()) != null) {

                //W przypadku 'heartbeat', czyli watku sprawdzajacego, czy socket komunikacyjny nie zostal zamkniety
                //wtedy wysylamny jest po prostu tekst, bez JSON'a
                if (jsonMessageFromClient.equals("heartbeat")){
                    outputStream.println("pik-pik");
                    continue;
                }

                //Serializacja JSON'a (tekstu), otrzymane od klienta, do formy obiektu
                JSONObject jsonObject = new JSONObject(jsonMessageFromClient);

                //Akcja mowi o tym, czego klient oczekuje od serwera
                String actionPerformed = jsonObject.getString("action");
                //Obsluga logowania
                if (actionPerformed.equals("login")) {
                    //Zwroć do klienta informacje, zwrocona przez funkcje
                    outputStream.println(verifyLoginData(jsonObject));
                } else if (actionPerformed.equals("create_user")) {
                    //Zwroć do klienta informacje, zwrocona przez funkcje
                    outputStream.println(addNewUser(jsonObject));
                } else if (actionPerformed.equals("create_location")) {
                    //Zwroć do klienta informacje, zwrocona przez funkcje
                    outputStream.println(addNewLocation(jsonObject));
                } else if (actionPerformed.equals("create_assortment")) {
                    //Zwroć do klienta informacje, zwrocona przez funkcje
                    outputStream.println(addNewAssortment(jsonObject));
                } else if (actionPerformed.equals("get_stock_item")) {
                    //Zwroć do klienta informacje, zwrocona przez funkcje
                    outputStream.println(getStockItem(jsonObject));
                } else if (actionPerformed.equals("do_output")) {
                    //Zwroć do klienta informacje, zwrocona przez funkcje
                    System.out.println("zlapalem polecenie wydania");
                    outputStream.println(doOutput(jsonObject));
                } else if (actionPerformed.equals("create_contractor")) {
                    //Zwroć do klienta informacje, zwrocona przez funkcje
                    outputStream.println(addNewContractor(jsonObject));
                } else if (actionPerformed.equals("input_operation")) {
                    //Zwroć do klienta informacje, zwrocona przez funkcje
                    outputStream.println(doInput(jsonObject));
                } else if (actionPerformed.equals("transfer_operation")) {
                    outputStream.println(doTransfer(jsonObject));
                }
            }
            inputStream.close();
            clientCommunicatorSocket.close();
        } catch (Exception e) {
            logger.WriteLog("Blad podczas komunikacji z klientem -> " + e.getMessage(), "ERROR");
        }
    }

    /**
     * Funkcja weryfikuje dane logowania i odsyla 'ok', w przypadku poprawnych danych
     */
    private String verifyLoginData(JSONObject JSONMessage) {

        //Dane logowania z przeslanego JSON'a
        String login = JSONMessage.getJSONObject("data").getString("login");
        String password = JSONMessage.getJSONObject("data").getString("password");

        //Werfyikacja danych
        String returnCode = SQLHelper.VerifyLoginData(login, password) ? "ok" : "wrong data";

        return returnCode;
    }

    /**
     * Funkcja wywuluje procedure SQL-owa, ktora dodaje nowego uzytkownika z podanych danych
     * Jesli napotka blad w trakcie realizacji procedury, to go zwroci wraz ze statusem "error"
     * Jesli wszystko ok, to status = success,
     */
    private JSONObject addNewUser(JSONObject JSONMessage) {

        //Dane logowania z przeslanego JSON'a
        String login = JSONMessage.getJSONObject("data").getString("login");
        String password = JSONMessage.getJSONObject("data").getString("password");

        //Utworz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywolanie funkcji dodajacej nowego uzytkownika (zwraca wyjatek, przy napotkaniu bledu)
        try {
            SQLHelper.AddNewUser(login, password);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie utworzono uzytkownika");
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsluzony w funkcji, wiec wyswietl go uzytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany blad");
            }
        }
        return serwerResponseJson;
    }

    /**
     * Funkcjaa wywolywana przy wykonywaniu wydania. Parsuje otrzymanego JSON'a, wywoluje funkcje z SQLHelpera i na
     * podstawie jej rezultatow wysyla zwrotke do klienta
     */
    private JSONObject doInput(JSONObject JSONDataFromClient) {

        //Wyciagnij obiorce i nadawce z JSONA
        String getfrom = JSONDataFromClient.getString("from_Contractor");

        //Przygotuj liste asortymentow, do przekazania funkcji w SQLHelperze
        ArrayList<AssortmentEntity> listOfAssortments = new ArrayList<>();

        JSONArray assortmentsDataArray = JSONDataFromClient.getJSONArray("assortments_data");
        for (Object tmpAssortment : assortmentsDataArray) {
            JSONObject tmpAssortmentJSON = (JSONObject) tmpAssortment;

            AssortmentEntity tmpAssortmentEntity = new AssortmentEntity(
                    tmpAssortmentJSON.getString("assortment_Name"),
                    tmpAssortmentJSON.getString("assortment_location"),
                    tmpAssortmentJSON.getFloat("assortment_Count"));

            listOfAssortments.add(tmpAssortmentEntity);
        }

        //Utworz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywolanie funkcji pobierajacej stany magazynowe (zwraca wyjatek, przy napotkaniu bledu)
        try {
            String responseStockItemJSOnArray = SQLHelper.DoInput(getfrom, listOfAssortments);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie zakonczono przyjecie");
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsluzony w funkcji, wiec wyswietl go uzytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany blad");
            }
        }
        return serwerResponseJson;
    }

    /**
     * Funkcjaa wywolywana przy wykonywaniu transferu. Parsuje otrzymanego JSON'a, wywoluje funkcje z SQLHelpera i na
     * podstawie jej rezultatow wysyla zwrotke do klienta
     */
    private JSONObject doTransfer(JSONObject JSONDataFromClient) {

        //Wyciagnij obiorce i nadawce z JSONA
        String fromLocation = JSONDataFromClient.getString("from_location");
        String assortmentName = JSONDataFromClient.getString("Asortment");
        String toLocation = JSONDataFromClient.getString("to_location");
        float assortmentQuantity = JSONDataFromClient.getFloat("AssortmentQuantity");

        //Utworz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywolanie funkcji pobierajacej stany magazynowe (zwraca wyjatek, przy napotkaniu bledu)
        try {
            String responseStockItemJSOnArray = SQLHelper.DoTransfer(fromLocation, toLocation, assortmentName, assortmentQuantity);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie zakonczono przyjecie");
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsluzony w funkcji, wiec wyswietl go uzytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany blad");
            }
        }
        return serwerResponseJson;
    }

    /**
     * Funkcja wywuluje procedure SQL-owa, ktora dodaje nowa lokalizacje z podanych danych
     * Jesli napotka blad w trakcie realizacji procedury, to go zwroci wraz ze statusem "error"
     * Jesli wszystko ok, to status = success,
     */
    private JSONObject addNewLocation(JSONObject JSONMessage) {

        //nazwa nowej lokalizacji
        String locationName = JSONMessage.getJSONObject("data").getString("location_name");

        //Utworz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywolanie funkcji dodajacej nowego uzytkownika (zwraca wyjatek, przy napotkaniu bledu)
        try {
            SQLHelper.AddNewLocation(locationName);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie utworzono lokalizacje");
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsluzony w funkcji, wiec wyswietl go uzytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany blad");
            }
        }
        return serwerResponseJson;
    }

    /**
     * Funkcja wywuluje procedure SQL-owa, ktora dodaje nowego asortymentu z podanych danych
     * Jesli napotka blad w trakcie realizacji procedury, to go zwroci wraz ze statusem "error"
     * Jesli wszystko ok, to status = success,
     */
    private JSONObject addNewAssortment(JSONObject JSONMessage) {

        //nazwa nowej lokalizacji
        String assortmentName = JSONMessage.getJSONObject("data").getString("assortment_name");

        //Utworz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywolanie funkcji dodajacej nowego uzytkownika (zwraca wyjatek, przy napotkaniu bledu)
        try {
            SQLHelper.AddNewAssortment(assortmentName);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie utworzono asortyment");
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsluzony w funkcji, wiec wyswietl go uzytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany blad");
            }
        }
        return serwerResponseJson;
    }

    /**
     * Funkcja wyciaga z bazy wszystkie asortymenty o podanych parametrach
     * Jesli napotka blad w trakcie realizacji procedury, to go zwroci wraz ze statusem "error"
     * Jesli wszystko ok, to status = success,
     */
    private JSONObject getStockItem(JSONObject JSONMessage) {

        //nazwa szukanego asortymentu
        String assortmentName = JSONMessage.getJSONObject("filters").getString("assortment_name");

        //nazwa lukalizacji, w ktorej jest szuane
        String locationName = JSONMessage.getJSONObject("filters").getString("location_name");

        //Utworz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywolanie funkcji pobierajacej stany magazynowe (zwraca wyjatek, przy napotkaniu bledu)
        try {
            String responseStockItemJSOnArray = SQLHelper.GetStockItem(assortmentName, locationName);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie odczytano stany magazynowe");
            serwerResponseJson.put("stock_items", new JSONArray(responseStockItemJSOnArray));
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsluzony w funkcji, wiec wyswietl go uzytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany blad");
            }
        }
        return serwerResponseJson;

    }

    /**
     * Funckja parsuje dane z asortymentami, ktore przekaal jej klient i wywoluje procedure SQL
     */
    private JSONObject addNewContractor(JSONObject JSONMessage) {

        //nazwa nowej lokalizacji
        String contractorName = JSONMessage.getJSONObject("data").getString("contractor_name");

        //Utworz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywolanie funkcji dodajacej nowego uzytkownika (zwraca wyjatek, przy napotkaniu bledu)
        try {
            SQLHelper.AddNewContractor(contractorName);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie utworzono kontrachenta");
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsluzony w funkcji, wiec wyswietl go uzytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany blad");
            }
        }
        return serwerResponseJson;
    }

    /**
     * Funckja wyciaga dane z JSON'a i wywoluje procedure z SQL Helpera
     */
    private JSONObject doOutput(JSONObject JSONDataFromClient) {

        //Wyciagnij obiorce i nadawce z JSONA
        String sendFrom = JSONDataFromClient.getJSONObject("data").getString("from");
        String sendTo = JSONDataFromClient.getJSONObject("data").getString("to");

        //Przygotuj liste asortymentow, do przekazania funkcji w SQLHelperze
        ArrayList<AssortmentEntity> listOfAssortments = new ArrayList<>();

        JSONArray assortmentsDataArray = JSONDataFromClient.getJSONObject("data").getJSONArray("assortments_data");
        for (Object tmpAssortment : assortmentsDataArray) {
            JSONObject tmpAssortmentJSON = (JSONObject) tmpAssortment;

            AssortmentEntity tmpAssortmentEntity = new AssortmentEntity(
                    tmpAssortmentJSON.getString("assortment_name"),
                    tmpAssortmentJSON.getString("location_name"),
                    tmpAssortmentJSON.getFloat("assortment_count"));

            listOfAssortments.add(tmpAssortmentEntity);
        }

        //Utworz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywolanie funkcji wykonujacej wydanie (zwraca wyjatek, przy napotkaniu bledu)
        try {
            SQLHelper.DoOutput(sendFrom, sendTo, listOfAssortments);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie zakonczono wydanie");

            createWZOutputFile(JSONDataFromClient);
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsluzony w funkcji, wiec wyswietl go uzytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany blad");
            }
        }
        return serwerResponseJson;
    }

    /**
     * Funkcja wywolywana przy poprawnym zakonczeniu wydania.
     * Tworzy dokument WZ, dla ERP'a
     */
    private void createWZOutputFile(JSONObject jsonString) {

        //Formatowanie daty, zeby doklejać ja do pliku
        SimpleDateFormat dateFormater = new SimpleDateFormat("dd_MM_yyyy__HH_mm_ss");
        String WZFileName = dateFormater.format(new Date());

        //Edycja JSON'a, do pliku wyjsciowego
        jsonString.remove("action");
        jsonString.put("document_type", "WZ");
        jsonString.put("document_numer", WZFileName);
        jsonString.put("ship_from", jsonString.getJSONObject("data").getString("from"));
        jsonString.put("ship_to", jsonString.getJSONObject("data").getString("to"));

        jsonString.getJSONObject("data").remove("to");
        jsonString.getJSONObject("data").remove("from");

        //Sciezka do pliku
        String path = "output_WZ/WZ_" + WZFileName + ".json";
        createFileIfNotExists(path);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)))) {

            bufferedWriter.write(jsonString.toString(4));

        } catch(FileNotFoundException notFoundException) {
            System.out.println("Brak pliku o podanej nazwie");
        } catch (IOException ioException) {
            System.out.println("Blad czytania z pliku");
        } catch (Exception exception) {
            System.out.println("Nieobsluzony blad");
        }
    }

    /**
     * Funckja pomocnicza -> towrzy podane w parametrze plik, jesli ten nie istnieje
     */
    public static void createFileIfNotExists(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } else {
                FileOutputStream writer = new FileOutputStream(path);
                writer.write(("").getBytes());
                writer.close();
            }
        } catch (IOException e) {
           logger.WriteLog("Blad tworzenia pliku ( " + path + ") -> " + e.getMessage(), "ERROR");
        }
    }
}