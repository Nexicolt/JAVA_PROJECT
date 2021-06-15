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

//TODO Do poprawy wyswietlanie przyciskow (FILIP)
public class SettingView extends JPanel implements ActionListener {
    private MainWindowWMS mainWindowWMS;
    private JButtonMainStyle addUserButton, addLoactionButton, addAssortmentButton, backButton;
    JPanel mainContainer;

    /**
     * Konstruktor inicjalizuje referencje do głownego JPanel'u, okna WMS'a
     */
    public SettingView(MainWindowWMS mainWindowWMS, JPanel mainContainer){
        this.mainWindowWMS = mainWindowWMS;
        this.mainContainer = mainContainer;
        init();
    }

    public void init() {
        setSize(new Dimension(500, 200));
        //box glowny do pozycjnonwania w pionie
        Box verticalMainBox = Box.createVerticalBox();

        verticalMainBox.add(Box.createVerticalStrut(20));
        addUserButton = new JButtonMainStyle("Dodaj użytkownika");
        addUserButton.addActionListener(this);
        verticalMainBox.add(addUserButton);

        verticalMainBox.add(Box.createVerticalStrut(20));
        addLoactionButton = new JButtonMainStyle("Dodaj lokalizację");
        addLoactionButton.addActionListener(this);
        verticalMainBox.add(addLoactionButton);

        verticalMainBox.add(Box.createVerticalStrut(20));
        addAssortmentButton = new JButtonMainStyle("Dodaj asortyment");
        addAssortmentButton.addActionListener(this);
        verticalMainBox.add(addAssortmentButton);

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
        }
    }

    /**
     * Funkcja wyświetla pole do wpisania nazwy użytkownika i zwraca komunikat z serwera, z wynikiem operacji dodawania usera
     */
    private void addNewUserCommand(){

        //JoptionPane z dwoma polami (na login i hasło)
        JTextField loginInput = new JTextField(10);
        JTextField passwordInput = new JTextField(10);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Login:"));
        myPanel.add(loginInput);
        myPanel.add(Box.createHorizontalStrut(15)); // przestrzeń między inputami
        myPanel.add(new JLabel("Hasło:"));
        myPanel.add(passwordInput);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Wprowadź dane dla nowego użytkownika", JOptionPane.OK_CANCEL_OPTION);

        //Wyślij dane do serwera, jeśli kliknięto "OK"
        if (result == JOptionPane.OK_OPTION) {

            //Sprawdź, czy pola zostały wypełnione
            if(loginInput.getText().isBlank() || passwordInput.getText().isBlank()){
                JoptionPaneMessages.showErrorPopup("Oba pola muszą zostać wyepłnione");
                return;
            }

            //Utwórz JSON'a, do wysyłki
            JSONObject createUserJSON = new JSONObject();

            JSONObject jsonData = new JSONObject();
            jsonData.put("login", loginInput.getText().trim());
            jsonData.put("password", passwordInput.getText());

            createUserJSON.put("action", "create_user");
            createUserJSON.put("data", jsonData);

            //Wysyłka JSON'a do serwera, z danymi nowego użytkownika
            mainWindowWMS.GetStreamToServer().println(createUserJSON);

            //Czekaj na odpowiedź od serwera
            try {
                while (true) {
                    String serverResponse = mainWindowWMS.GetStreamFromServer().readLine();
                    if (serverResponse != null) {
                        //Sparsuj odpowiedź do typu JSON
                        System.out.println(serverResponse);
                        JSONObject serverResponseJSON = new JSONObject(serverResponse);

                        //Jeśli odesłał OK, to użytkownik utworzony poprawnie
                        if(serverResponseJSON.getString("status").equals("success")){
                            JoptionPaneMessages.showSuccessPopup("Poprawnie dodano użytkownika");
                        }else{
                            //Jeśli nie odesłał ok, to wyświetl zwrócony komunikat błędu
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
     * Funkcja wyświetla pole do wpisania anzwy, dla nowej lokalizacji i wysyła request do serwera
     */
    private void addNewLocationCommand(){

        String locationName=JOptionPane.showInputDialog(null,"Wprowadź nazwę lokalizacji");


        //Wyślij dane do serwera, jeśliwporwadzona nazwa nie jest pusta
        if (locationName != null && !locationName.isBlank()) {


            //Utwórz JSON'a, do wysyłki
            JSONObject createLocationJSON = new JSONObject();

            JSONObject jsonData = new JSONObject();
            jsonData.put("location_name", locationName.trim());

            createLocationJSON.put("action", "create_location");
            createLocationJSON.put("data", jsonData);

            //Wysyłka JSON'a do serwera, z danymi nowej lokalizacji
            mainWindowWMS.GetStreamToServer().println(createLocationJSON);

            //Czekaj na odpowiedź od serwera
            try {
                while (true) {
                    String serverResponse = mainWindowWMS.GetStreamFromServer().readLine();
                    if (serverResponse != null) {
                        //Sparsuj odpowiedź do typu JSON
                        JSONObject serverResponseJSON = new JSONObject(serverResponse);

                        //Jeśli odesłał OK, to lokalizacja dodana poprawnie
                        if(serverResponseJSON.getString("status").equals("success")){
                            JoptionPaneMessages.showSuccessPopup("Poprawnie dodano lokalizację");
                        }else{
                            //Jeśli nie odesłał ok, to wyświetl zwrócony komunikat błędu
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
     * Funkcja wyświetla pole do wpisania anzwy, dla nowego asortymentu i wysyła request do serwera
     */
    private void addNewAssortment(){

        String assortmentName=JOptionPane.showInputDialog(null,"Wprowadź nazwę asortymentu");


        //Wyślij dane do serwera, jeśliwporwadzona nazwa nie jest pusta
        if (assortmentName != null && !assortmentName.isBlank()) {

            //Utwórz JSON'a, do wysyłki
            JSONObject createAssortmentJSON = new JSONObject();

            JSONObject jsonData = new JSONObject();
            jsonData.put("assortment_name", assortmentName.trim());

            createAssortmentJSON.put("action", "create_assortment");
            createAssortmentJSON.put("data", jsonData);

            //Wysyłka JSON'a do serwera, z danymi nowej lokalizacji
            mainWindowWMS.GetStreamToServer().println(createAssortmentJSON);

            //Czekaj na odpowiedź od serwera
            try {
                while (true) {
                    String serverResponse = mainWindowWMS.GetStreamFromServer().readLine();
                    if (serverResponse != null) {
                        //Sparsuj odpowiedź do typu JSON
                        JSONObject serverResponseJSON = new JSONObject(serverResponse);

                        //Jeśli odesłał OK, to lokalizacja dodana poprawnie
                        if(serverResponseJSON.getString("status").equals("success")){
                            JoptionPaneMessages.showSuccessPopup("Poprawnie dodano asortyment");
                        }else{
                            //Jeśli nie odesłał ok, to wyświetl zwrócony komunikat błędu
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