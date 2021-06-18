package WMS;

import Style.JoptionPaneMessages;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginForm extends AbstractJFrame implements ActionListener {

    JTextField loginInputField;
    JPasswordField passwordInputField;
    JButton loginButton;

    /**
     * Konstruktor inizjalizuje tytuł okna i przechwytuje refrencję do socketu komunikacji i strumieni
     */
    LoginForm(String windowName, Socket _commSocket, PrintWriter _streamToServer, BufferedReader _streamFromServer) {
        super(windowName, _commSocket, _streamToServer, _streamFromServer);

    }

    /**
     * Funkcja inicjalizująca layout i pokazująca okno z wpisaniem danych logowania
     */
    public void init() {

        setSize(400, 200);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        ///////////////////////////
        //// KONTROLKI WYGLĄDU ////
        ///////////////////////////

        setLayout(new FlowLayout());

        //Input do wpisania loginu
        loginInputField = new JTextField(25);
        loginInputField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        loginInputField.addActionListener(this);

        JLabel loginInputLabel = new JLabel("Login:");

        JPanel loginInputPanel = new JPanel();
        loginInputPanel.add(loginInputLabel);
        loginInputPanel.add(loginInputField);

        //Input do wpisania hasła
        passwordInputField = new JPasswordField(25);
        passwordInputField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        passwordInputField.addActionListener(this);

        JLabel passwordInputLabel = new JLabel("Hasło:");

        JPanel passwordInputPanel = new JPanel();
        passwordInputPanel.add(passwordInputLabel);
        passwordInputPanel.add(passwordInputField);


        //Przycisk 'Zaloguj'
        loginButton = new JButton("Zaloguj");
        loginButton.addActionListener(this);

        add(loginInputPanel);
        add(passwordInputPanel);
        add(loginButton);

        setVisible(true);
    }

    /**
     * Nasłuchiwanie wciśnięcia przycisku
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == loginButton) {
            //Sprawdź, czy inputy nie są puste
            if (loginInputField.getText().isBlank() || passwordInputField.getText().isBlank()) {
                JoptionPaneMessages.showErrorPopup("Oba pola muszą być wypełnione");
                return;
            }
            //Wyślij zapytanie do serwera z danymi logowania (zwróci TRUE dla poprawnych danych)
            if (sendLoginData()) {
                MainWindowWMS mainWidnow = new MainWindowWMS("Warehouse Management System",
                                    communicationSocket, streamToServer, streamFromServer, loginInputField.getText() );
                mainWidnow.init();
                dispose();
                new SocketChecker(streamToServer, streamFromServer, mainWidnow).start();
            } else {
                JoptionPaneMessages.showErrorPopup("Błędne dane logowania");
            }
        }else{
            loginButton.doClick();
        }
    }
    /**
     * Wysyła do serwera zapytanie, o poprawność danych logowania, wpisanych przez użytkownika
     * <p>
     * Zwraca true, jeśli serwer odpowie 'OK'
     */
    private boolean sendLoginData() {
        JSONObject mainJsonMessage = new JSONObject();

        JSONObject jsonData = new JSONObject();
        jsonData.put("login", loginInputField.getText().trim());
        jsonData.put("password", passwordInputField.getText().trim());

        mainJsonMessage.put("action", "login");
        mainJsonMessage.put("data", jsonData);

        //Wysyłka JSON'a do serwera, z danymi logowania
        streamToServer.println(mainJsonMessage);

        //Funckja zwraca true, przy poprawnych danych oraz false, przy niepoprawnych
        return waitForServerResponse();

    }

    /**
     * Funkcja oczekuje na odpowiedź od serwera, w kwestii danych logowania
     * <p>
     * Nie jest osobnym wątkiem, bo odebranie danych musi sie odbyć liniowo z ich wysłaniem. Gdyby zrobić odbieranie danych asonchroniczne,
     * to klient mógłby zapchać serwer ciągłym wysyłaniem zapytań o weryfikację danych logowania
     */
    private boolean waitForServerResponse() {
        try {
            while (true) {
                String serverResponse = streamFromServer.readLine();
                if (serverResponse != null) {
                    //Serwer odpowiada 'OK', jeśli dane logowania są poprawne
                    if (serverResponse.equals("ok")) return true;
                    else return false;
                }
            }
        } catch (IOException ioException) {
            return false;
        }
    }


}
