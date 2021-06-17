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

class ClientSession extends Thread {
    private final Socket clientCommunicatorSocket;
    private BufferedReader inputStream;
    private PrintWriter outputStream;

    //Obiekt klasy 'Logger', pozwalający na wpisywanie logów do pliku
    public static Logger logger = new Logger();

    /**
     * Konstruktor, inizjalizujący socket oraz strumienie wyjścia/wejścia
     *
     * @param _socket
     */
    public ClientSession(Socket _socket) {
        clientCommunicatorSocket = _socket;
        try {
            inputStream = new BufferedReader(new InputStreamReader(clientCommunicatorSocket.getInputStream()));
            outputStream = new PrintWriter(new OutputStreamWriter(clientCommunicatorSocket.getOutputStream()), true);
        } catch (Exception e) {
            logger.WriteLog("Błąd podczas inizjalizacji połączenia z klientem -> " + e.getMessage(), "ERROR");
        }
    }

    /**
     * Główna funckja wątku, odpowiedzialna za nasłuchiwanie
     */
    public void run() {
        String jsonMessageFromClient;
        try {
            while ((jsonMessageFromClient = inputStream.readLine()) != null) {

                //Serializacja JSON'a (tekstu), otrzymane od klienta, do formy obiektu
                JSONObject jsonObject = new JSONObject(jsonMessageFromClient);

                //Akcja mówi o tym, czego klient oczekuje od serwera
                String actionPerformed = jsonObject.getString("action");
                //Obsługa logowania
                if (actionPerformed.equals("login")) {
                    //Zwróć do klienta informację, zwróconą przez funkcję
                    outputStream.println(verifyLoginData(jsonObject));
                } else if (actionPerformed.equals("create_user")) {
                    //Zwróć do klienta informację, zwróconą przez funkcję
                    outputStream.println(addNewUser(jsonObject));
                } else if (actionPerformed.equals("create_location")) {
                    //Zwróć do klienta informację, zwróconą przez funkcję
                    outputStream.println(addNewLocation(jsonObject));
                } else if (actionPerformed.equals("create_assortment")) {
                    //Zwróć do klienta informację, zwróconą przez funkcję
                    outputStream.println(addNewAssortment(jsonObject));
                } else if (actionPerformed.equals("get_stock_item")) {
                    //Zwróć do klienta informację, zwróconą przez funkcję
                    outputStream.println(getStockItem(jsonObject));
                } else if (actionPerformed.equals("do_output")) {
                    //Zwróć do klienta informację, zwróconą przez funkcję
                    System.out.println("złapałem polecenie wydania");
                    outputStream.println(doOutput(jsonObject));
                } else if (actionPerformed.equals("create_contractor")) {
                    //Zwróć do klienta informację, zwróconą przez funkcję
                    outputStream.println(addNewContractor(jsonObject));
                } else if (actionPerformed.equals("input_operation")) {
                    //Zwróć do klienta informację, zwróconą przez funkcję
                    outputStream.println(doInput(jsonObject));
                } else if (actionPerformed.equals("transfer_operation")) {
                    outputStream.println(doTransfer(jsonObject));
                }
            }
            inputStream.close();
            clientCommunicatorSocket.close();
        } catch (Exception e) {
            logger.WriteLog("Błąd podczas komunikacji z klientem -> " + e.getMessage(), "ERROR");
        }
    }

    /**
     * Funkcja weryfikuje dane logowania i odsyła 'ok', w przypadku poprawnych danych
     */
    private String verifyLoginData(JSONObject JSONMessage) {

        //Dane logowania z przesłanego JSON'a
        String login = JSONMessage.getJSONObject("data").getString("login");
        String password = JSONMessage.getJSONObject("data").getString("password");

        //Werfyikacja danych
        String returnCode = SQLHelper.VerifyLoginData(login, password) ? "ok" : "wrong data";

        return returnCode;
    }

    /**
     * Funkcja wywułuje procedurę SQL-ową, która dodaje nowego użytkownika z podanych danych
     * Jeśli napotka błąd w trakcie realizacji procedury, to go zwróci wraz ze statusem "error"
     * Jesli wszystko ok, to status = success,
     */
    private JSONObject addNewUser(JSONObject JSONMessage) {

        //Dane logowania z przesłanego JSON'a
        String login = JSONMessage.getJSONObject("data").getString("login");
        String password = JSONMessage.getJSONObject("data").getString("password");

        //Utwórz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywołanie funkcji dodającej nowego użytkownika (zwraca wyjątek, przy napotkaniu błędu)
        try {
            SQLHelper.AddNewUser(login, password);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie utworzono użytkownika");
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsłużony w funkcji, więc wyświetl go użytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany błąd");
            }
        }
        return serwerResponseJson;
    }

    private JSONObject doInput(JSONObject JSONDataFromClient) {

        //Wyciągnij obiorcę i nadawcę z JSONA
        String getfrom = JSONDataFromClient.getString("from_Contractor");

        //Przygotuj listę asortymentów, do przekazania funkcji w SQLHelperze
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

        //Utwórz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywołanie funkcji pobierającej stany magazynowe (zwraca wyjątek, przy napotkaniu błędu)
        try {
            String responseStockItemJSOnArray = SQLHelper.DoInput(getfrom, listOfAssortments);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie zakończono przyjęcie");
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsłużony w funkcji, więc wyświetl go użytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany błąd");
            }
        }
        return serwerResponseJson;
    }

    private JSONObject doTransfer(JSONObject JSONDataFromClient) {

        //Wyciągnij obiorcę i nadawcę z JSONA
        String fromLocation = JSONDataFromClient.getString("from_location");
        String assortmentName = JSONDataFromClient.getString("Asortment");
        String toLocation = JSONDataFromClient.getString("to_location");
        float assortmentQuantity = JSONDataFromClient.getFloat("AssortmentQuantity");

        //Utwórz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywołanie funkcji pobierającej stany magazynowe (zwraca wyjątek, przy napotkaniu błędu)
        try {
            String responseStockItemJSOnArray = SQLHelper.DoTransfer(fromLocation, toLocation, assortmentName, assortmentQuantity);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie zakończono przyjęcie");
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsłużony w funkcji, więc wyświetl go użytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany błąd");
            }
        }
        return serwerResponseJson;
    }

    /**
     * Funkcja wywułuje procedurę SQL-ową, która dodaje nową lokalizację z podanych danych
     * Jeśli napotka błąd w trakcie realizacji procedury, to go zwróci wraz ze statusem "error"
     * Jesli wszystko ok, to status = success,
     */
    private JSONObject addNewLocation(JSONObject JSONMessage) {

        //nazwa nowej lokalizacji
        String locationName = JSONMessage.getJSONObject("data").getString("location_name");

        //Utwórz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywołanie funkcji dodającej nowego użytkownika (zwraca wyjątek, przy napotkaniu błędu)
        try {
            SQLHelper.AddNewLocation(locationName);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie utworzono lokalizację");
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsłużony w funkcji, więc wyświetl go użytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany błąd");
            }
        }
        return serwerResponseJson;
    }

    /**
     * Funkcja wywułuje procedurę SQL-ową, która dodaje nowego asortymentu z podanych danych
     * Jeśli napotka błąd w trakcie realizacji procedury, to go zwróci wraz ze statusem "error"
     * Jesli wszystko ok, to status = success,
     */
    private JSONObject addNewAssortment(JSONObject JSONMessage) {

        //nazwa nowej lokalizacji
        String assortmentName = JSONMessage.getJSONObject("data").getString("assortment_name");

        //Utwórz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywołanie funkcji dodającej nowego użytkownika (zwraca wyjątek, przy napotkaniu błędu)
        try {
            SQLHelper.AddNewAssortment(assortmentName);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie utworzono asortyment");
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsłużony w funkcji, więc wyświetl go użytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany błąd");
            }
        }
        return serwerResponseJson;
    }

    /**
     * Funkcja wyciąga z bazy wszystkie asortymenty o podanych parametrach
     * Jeśli napotka błąd w trakcie realizacji procedury, to go zwróci wraz ze statusem "error"
     * Jesli wszystko ok, to status = success,
     */
    private JSONObject getStockItem(JSONObject JSONMessage) {

        //nazwa szukanego asortymentu
        String assortmentName = JSONMessage.getJSONObject("filters").getString("assortment_name");

        //nazwa lukalizacji, w której jest szuane
        String locationName = JSONMessage.getJSONObject("filters").getString("location_name");

        //Utwórz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywołanie funkcji pobierającej stany magazynowe (zwraca wyjątek, przy napotkaniu błędu)
        try {
            String responseStockItemJSOnArray = SQLHelper.GetStockItem(assortmentName, locationName);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie odczytano stany magazynowe");
            serwerResponseJson.put("stock_items", new JSONArray(responseStockItemJSOnArray));
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsłużony w funkcji, więc wyświetl go użytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany błąd");
            }
        }
        return serwerResponseJson;

    }

    /**
     * Funckja parsuje dane z asortymentami, które przekaał jej klient i wywołuje procedurę SQL
     */
    private JSONObject addNewContractor(JSONObject JSONMessage) {

        //nazwa nowej lokalizacji
        String contractorName = JSONMessage.getJSONObject("data").getString("contractor_name");

        //Utwórz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywołanie funkcji dodającej nowego użytkownika (zwraca wyjątek, przy napotkaniu błędu)
        try {
            SQLHelper.AddNewContractor(contractorName);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie utworzono kontrachenta");
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsłużony w funkcji, więc wyświetl go użytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany błąd");
            }
        }
        return serwerResponseJson;
    }

    /**
     * Funckja wyciąga dane z JSON'a i wywołuję procedure z SQL Helpera
     */
    private JSONObject doOutput(JSONObject JSONDataFromClient) {

        //Wyciągnij obiorcę i nadawcę z JSONA
        String sendFrom = JSONDataFromClient.getJSONObject("data").getString("from");
        String sendTo = JSONDataFromClient.getJSONObject("data").getString("to");

        //Przygotuj listę asortymentów, do przekazania funkcji w SQLHelperze
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

        //Utwórz JSON'a zwrotnego
        JSONObject serwerResponseJson = new JSONObject();

        //Wywołanie funkcji wykonującej wydanie (zwraca wyjątek, przy napotkaniu błędu)
        try {
            SQLHelper.DoOutput(sendFrom, sendTo, listOfAssortments);
            serwerResponseJson.put("status", "success");
            serwerResponseJson.put("message", "Poprawnie zakończono wydanie");

            createWZOutputFile(JSONDataFromClient);
        } catch (SQLException sqlException) {
            serwerResponseJson.put("status", "error");

            //Kod 4500, to obsłużony w funkcji, więc wyświetl go użytkownikowi
            if (sqlException.getSQLState().equals("45000")) {
                serwerResponseJson.put("message", sqlException.getMessage());
            } else {
                serwerResponseJson.put("message", "Nieoczekiwany błąd");
            }
        }
        return serwerResponseJson;
    }

    /**
     * Funkcja wywoływana przy poprawnym zakończeniu wydania.
     * Tworzy dokument WZ, dla ERP'a
     */
    private void createWZOutputFile(JSONObject jsonString) {

        //Formatowanie daty, żeby doklejać ja do pliku
        SimpleDateFormat dateFormater = new SimpleDateFormat("dd_MM_yyyy__HH_mm_ss");
        String WZFileName = dateFormater.format(new Date());

        //Edycja JSON'a, do pliku wyjściowego
        jsonString.remove("action");
        jsonString.put("document_type", "WZ");
        jsonString.put("document_numer", WZFileName);
        jsonString.put("ship_from", jsonString.getJSONObject("data").getString("from"));
        jsonString.put("ship_to", jsonString.getJSONObject("data").getString("to"));

        jsonString.getJSONObject("data").remove("to");
        jsonString.getJSONObject("data").remove("from");

        //Scieżka do pliku
        String path = "output_WZ/WZ_" + WZFileName + ".json";
        createFileIfNotExists(path);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)))) {

            bufferedWriter.write(jsonString.toString(4));

        } catch(FileNotFoundException notFoundException) {
            System.out.println("Brak pliku o podanej nazwie");
        } catch (IOException ioException) {
            System.out.println("Błąd czytania z pliku");
        } catch (Exception exception) {
            System.out.println("Nieobsłużony błąd");
        }
    }

    /**
     * Create file if not exist.
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
            e.printStackTrace();
        }
    }
}