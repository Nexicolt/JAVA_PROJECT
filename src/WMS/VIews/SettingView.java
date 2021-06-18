package WMS.VIews;

import Style.JButtonMainStyle;
import Style.JoptionPaneMessages;
import WMS.AbstractJFrame;
import WMS.MainWindowWMS;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Klasa reprezntuje panel z ustawienia, na ktorym mozna dodawać lokalizacje, uzytkownikow, asortymenty...
 */
public class SettingView extends JPanel implements ActionListener {
    private MainWindowWMS mainWindowWMS;
    private JButtonMainStyle addUserButton, addLoactionButton,addContractorButton, addAssortmentButton, backButton;
    JPanel mainContainer;

    /**
     * Konstruktor inicjalizuje referencje do glownego JPanel'u, okna WMS'a
     */
    public SettingView(MainWindowWMS mainWindowWMS, JPanel mainContainer){
        this.mainWindowWMS = mainWindowWMS;
        this.mainContainer = mainContainer;
        init();
    }
    /**
     * Funkcja buduje okno i ustawia je na widoczne
     */
    public void init() {
        //box glowny do pozycjnonwania w pionie
        Box verticalMainBox = Box.createVerticalBox();

        verticalMainBox.add(Box.createVerticalStrut(20));
        addUserButton = new JButtonMainStyle("Dodaj uzytkownika");
        addUserButton.addActionListener(this);
        verticalMainBox.add(addUserButton);

        verticalMainBox.add(Box.createVerticalStrut(20));
        addLoactionButton = new JButtonMainStyle("Dodaj lokalizacje");
        addLoactionButton.addActionListener(this);
        verticalMainBox.add(addLoactionButton);

        verticalMainBox.add(Box.createVerticalStrut(20));
        addAssortmentButton = new JButtonMainStyle("Dodaj asortyment");
        addAssortmentButton.addActionListener(this);
        verticalMainBox.add(addAssortmentButton);

        verticalMainBox.add(Box.createVerticalStrut(20));
        addContractorButton = new JButtonMainStyle("Dodaj kontrachenta");
        addContractorButton.addActionListener(this);
        verticalMainBox.add(addContractorButton);

        verticalMainBox.add(Box.createVerticalStrut(60));
        backButton = new JButtonMainStyle("<- Wstecz");
        backButton.addActionListener(this);

        verticalMainBox.add(backButton);
        add(verticalMainBox);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        //Akcja dla przycisku "Rezygnuj"
        if (source == backButton) {
            setVisible(false);
            mainContainer.setVisible(true);
        }else if(source == addUserButton){
            addNewUserCommand();
        }else if(source == addLoactionButton){
            addNewLocationCommand();
        }else if(source == addAssortmentButton){
            addNewAssortment();
        }else if(source.equals(addContractorButton)){
            addNewContractor();
        }
    }

    /**
     * Funkcja wyswietla pole do wpisania nazwy uzytkownika i zwraca komunikat z serwera, z wynikiem operacji dodawania usera
     */
    private void addNewUserCommand(){

        //JoptionPane z dwoma polami (na login i haslo)
        JTextField loginInput = new JTextField(10);
        JTextField passwordInput = new JTextField(10);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Login:"));
        myPanel.add(loginInput);
        myPanel.add(Box.createHorizontalStrut(15)); // przestrzen miedzy inputami
        myPanel.add(new JLabel("Haslo:"));
        myPanel.add(passwordInput);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Wprowadz dane dla nowego uzytkownika", JOptionPane.OK_CANCEL_OPTION);

        //Wyslij dane do serwera, jesli kliknieto "OK"
        if (result == JOptionPane.OK_OPTION) {

            //Sprawdz, czy pola zostaly wypelnione
            if(loginInput.getText().isBlank() || passwordInput.getText().isBlank()){
                JoptionPaneMessages.showErrorPopup("Oba pola musza zostać wyeplnione");
                return;
            }

            //Utworz JSON'a, do wysylki
            JSONObject createUserJSON = new JSONObject();

            JSONObject jsonData = new JSONObject();
            jsonData.put("login", loginInput.getText().trim());
            jsonData.put("password", passwordInput.getText());

            createUserJSON.put("action", "create_user");
            createUserJSON.put("data", jsonData);

            //Wysylka JSON'a do serwera, z danymi nowego uzytkownika
            mainWindowWMS.GetStreamToServer().println(createUserJSON);

            //Czekaj na odpowiedz od serwera
            try {
                while (true) {
                    String serverResponse = mainWindowWMS.GetStreamFromServer().readLine();
                    if (serverResponse != null) {
                        //Sparsuj odpowiedz do typu JSON
                        System.out.println(serverResponse);
                        JSONObject serverResponseJSON = new JSONObject(serverResponse);

                        //Jesli odeslal OK, to uzytkownik utworzony poprawnie
                        if(serverResponseJSON.getString("status").equals("success")){
                            JoptionPaneMessages.showSuccessPopup("Poprawnie dodano uzytkownika");
                        }else{
                            //Jesli nie odeslal ok, to wyswietl zwrocony komunikat bledu
                            String erroMessage = serverResponseJSON.getString("message");
                            JoptionPaneMessages.showErrorPopup(erroMessage);
                        }
                        break;
                    }
                }
            } catch (IOException ignored) { }
        }
    }

    /**
     * Funkcja wyswietla pole do wpisania nazwy, dla nowej lokalizacji i wysyla request do serwera
     */
    private void addNewLocationCommand(){

        String locationName=JOptionPane.showInputDialog(null,"Wprowadz nazwe lokalizacji");


        //Wyslij dane do serwera, jesliwporwadzona nazwa nie jest pusta
        if (locationName != null && !locationName.isBlank()) {


            //Utworz JSON'a, do wysylki
            JSONObject createLocationJSON = new JSONObject();

            JSONObject jsonData = new JSONObject();
            jsonData.put("location_name", locationName.trim());

            createLocationJSON.put("action", "create_location");
            createLocationJSON.put("data", jsonData);

            //Wysylka JSON'a do serwera, z danymi nowej lokalizacji
            mainWindowWMS.GetStreamToServer().println(createLocationJSON);

            //Czekaj na odpowiedz od serwera
            try {
                while (true) {
                    String serverResponse = mainWindowWMS.GetStreamFromServer().readLine();
                    if (serverResponse != null) {
                        //Sparsuj odpowiedz do typu JSON
                        JSONObject serverResponseJSON = new JSONObject(serverResponse);

                        //Jesli odeslal OK, to lokalizacja dodana poprawnie
                        if(serverResponseJSON.getString("status").equals("success")){
                            JoptionPaneMessages.showSuccessPopup("Poprawnie dodano lokalizacje");
                        }else{
                            //Jesli nie odeslal ok, to wyswietl zwrocony komunikat bledu
                            String erroMessage = serverResponseJSON.getString("message");
                            JoptionPaneMessages.showErrorPopup(erroMessage);
                        }
                        break;
                    }
                }
            } catch (IOException ignored) { }
        }
    }
    /**
     * Funkcja wyswietla pole do wpisania anzwy, dla nowego asortymentu i wysyla request do serwera
     */
    private void addNewAssortment(){

        String assortmentName=JOptionPane.showInputDialog(null,"Wprowadz nazwe asortymentu");


        //Wyslij dane do serwera, jesliwporwadzona nazwa nie jest pusta
        if (assortmentName != null && !assortmentName.isBlank()) {

            //Utworz JSON'a, do wysylki
            JSONObject createAssortmentJSON = new JSONObject();

            JSONObject jsonData = new JSONObject();
            jsonData.put("assortment_name", assortmentName.trim());

            createAssortmentJSON.put("action", "create_assortment");
            createAssortmentJSON.put("data", jsonData);

            //Wysylka JSON'a do serwera, z danymi nowej lokalizacji
            mainWindowWMS.GetStreamToServer().println(createAssortmentJSON);

            //Czekaj na odpowiedz od serwera
            try {
                while (true) {
                    String serverResponse = mainWindowWMS.GetStreamFromServer().readLine();
                    if (serverResponse != null) {
                        //Sparsuj odpowiedz do typu JSON
                        JSONObject serverResponseJSON = new JSONObject(serverResponse);

                        //Jesli odeslal OK, to lokalizacja dodana poprawnie
                        if(serverResponseJSON.getString("status").equals("success")){
                            JoptionPaneMessages.showSuccessPopup("Poprawnie dodano asortyment");
                        }else{
                            //Jesli nie odeslal ok, to wyswietl zwrocony komunikat bledu
                            String erroMessage = serverResponseJSON.getString("message");
                            JoptionPaneMessages.showErrorPopup(erroMessage);
                        }
                        break;
                    }
                }
            } catch (IOException ignored) { }
        }
    }

    /**
     * Funkcja wyswietla pole do wpisania nazwy, dla nowego klienta i wysyla request do serwera
     */
    private void addNewContractor(){

        String contractorName=JOptionPane.showInputDialog(null,"Wprowadz nazwe kontrachenta");


        //Wyslij dane do serwera, jesli wporwadzona nazwa nie jest pusta
        if (contractorName != null && !contractorName.isBlank()) {

            //Utworz JSON'a, do wysylki
            JSONObject createContractorJSON = new JSONObject();

            JSONObject jsonData = new JSONObject();
            jsonData.put("contractor_name", contractorName.trim());

            createContractorJSON.put("action", "create_contractor");
            createContractorJSON.put("data", jsonData);

            //Wysylka JSON'a do serwera, z danymi nowego klienta
            mainWindowWMS.GetStreamToServer().println(createContractorJSON);

            //Czekaj na odpowiedz od serwera
            try {
                while (true) {
                    String serverResponse = mainWindowWMS.GetStreamFromServer().readLine();
                    if (serverResponse != null) {
                        //Sparsuj odpowiedz do typu JSON
                        JSONObject serverResponseJSON = new JSONObject(serverResponse);

                        //Jesli odeslal OK, to klient dodany poprawnie
                        if(serverResponseJSON.getString("status").equals("success")){
                            JoptionPaneMessages.showSuccessPopup("Poprawnie dodano kontrachenta");
                        }else{
                            //Jesli nie odeslal ok, to wyswietl zwrocony komunikat bledu
                            String erroMessage = serverResponseJSON.getString("message");
                            JoptionPaneMessages.showErrorPopup(erroMessage);
                        }
                        break;
                    }
                }
            } catch (IOException ignored) { }
        }
    }
}