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

/**
 * Klasa reprezentuje okno i logike biznesowa  dla panelu logowania
 */
public class LoginForm extends AbstractJFrame implements ActionListener {

    JTextField loginInputField;
    JPasswordField passwordInputField;
    JButton loginButton;

    /**
     * Konstruktor inizjalizuje tytul okna i przechwytuje refrencje do socketu komunikacji i strumieni
     */
    LoginForm(String windowName, Socket _commSocket, PrintWriter _streamToServer, BufferedReader _streamFromServer) {
        super(windowName, _commSocket, _streamToServer, _streamFromServer);

    }

    /**
     * Funkcja inicjalizujaca layout i pokazujaca okno z wpisaniem danych logowania
     */
    public void init() {

        setSize(400, 200);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        ///////////////////////////
        //// KONTROLKI WYGLaDU ////
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

        //Input do wpisania hasla
        passwordInputField = new JPasswordField(25);
        passwordInputField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        passwordInputField.addActionListener(this);

        JLabel passwordInputLabel = new JLabel("Haslo:");

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
        setLocationRelativeTo(null);
    }

    /**
     * Nasluchiwanie wcisniecia przycisku
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == loginButton) {
            //Sprawdz, czy inputy nie sa puste
            if (loginInputField.getText().isBlank() || passwordInputField.getText().isBlank()) {
                JoptionPaneMessages.showErrorPopup("Oba pola musza być wypelnione");
                return;
            }
            //Wyslij zapytanie do serwera z danymi logowania (zwroci TRUE dla poprawnych danych)
            if (sendLoginData()) {
                MainWindowWMS mainWidnow = new MainWindowWMS("Warehouse Management System",
                                    communicationSocket, streamToServer, streamFromServer, loginInputField.getText() );
                mainWidnow.init();
                dispose();
                new SocketChecker(streamToServer, streamFromServer, mainWidnow).start();
            } else {
                JoptionPaneMessages.showErrorPopup("Bledne dane logowania");
            }
        }else{
            loginButton.doClick();
        }
    }
    /**
     * Wysyla do serwera zapytanie, o poprawnosć danych logowania, wpisanych przez uzytkownika
     * <p>
     * Zwraca true, jesli serwer odpowie 'OK'
     */
    private boolean sendLoginData() {
        JSONObject mainJsonMessage = new JSONObject();

        JSONObject jsonData = new JSONObject();
        jsonData.put("login", loginInputField.getText().trim());
        jsonData.put("password", passwordInputField.getText().trim());

        mainJsonMessage.put("action", "login");
        mainJsonMessage.put("data", jsonData);

        //Wysylka JSON'a do serwera, z danymi logowania
        streamToServer.println(mainJsonMessage);

        //Funckja zwraca true, przy poprawnych danych oraz false, przy niepoprawnych
        return waitForServerResponse();

    }

    /**
     * Funkcja oczekuje na odpowiedz od serwera, w kwestii danych logowania
     * Nie jest osobnym watkiem, bo odebranie danych musi sie odbyć liniowo z ich wyslaniem. Gdyby zrobić odbieranie danych asonchroniczne,
     * to klient moglby zapchać serwer ciaglym wysylaniem zapytan o weryfikacje danych logowania
     */
    private boolean waitForServerResponse() {
        try {
            while (true) {
                String serverResponse = streamFromServer.readLine();
                if (serverResponse != null) {
                    //Serwer odpowiada 'OK', jesli dane logowania sa poprawne
                    if (serverResponse.equals("ok")) return true;
                    else return false;
                }
            }
        } catch (IOException ioException) {
            return false;
        }
    }


}
