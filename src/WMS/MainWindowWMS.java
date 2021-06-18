package WMS;

import Style.*;
import WMS.VIews.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
/**
 * Klasa reperezntuje główno okno aplikacji, po poprawnym zalogowaniu. To ona wywołuje JPanela, stanowiące osobne funkcjonalności.
 * Jest swego rodzaju kontenrem całego GUI
 */
public class MainWindowWMS extends AbstractJFrame implements ActionListener, ComponentListener {

    //Osobna Klasa JButtonMainStyle dziedziczy po JButton zawiera gotowe style dla przyciskow menu Glownego
    private JButtonMainStyle stockLevelsButton, outPutDocumentButton, inputDocumentButton, transferButton, settingButton;
    private JButton logoutButton;
    private JPanel mainWindowPanel;
    String userName;
    JPanel transferPanel;

    //Konstruktor MainWindowWMS - FI
    public MainWindowWMS(String windowName, Socket _commSocket, PrintWriter _streamToServer, BufferedReader _streamFromServer, String userName){
        super(windowName, _commSocket, _streamToServer, _streamFromServer);
        this.userName = userName;
    }

    /**
     *     Funkcja wywolania widoku menu głównego aplikacji
     */
    public void init(){
        setSize(1100,550);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Panel Górny zawierajacy nazwe zalogowanego uzytkownika oraz przycisk wyloguj
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());
        JLabel zalogowanyUzytkownika = new JLabel("Zalogowano: "+userName);
        northPanel.add(zalogowanyUzytkownika);

        logoutButton = new JButton("Wyloguj");
        logoutButton.addActionListener(this);
        logoutButton.setBackground(Color.lightGray);
        northPanel.add(logoutButton);
        add(northPanel,BorderLayout.NORTH);


        //Panel grupujacy wszytkie przyciski
        mainWindowPanel = new JPanel();
        mainWindowPanel.setLayout(new FlowLayout());
        mainWindowPanel.addComponentListener(this);

        //Stany Magazynowe Button
        stockLevelsButton = new JButtonMainStyle("Stany Magazynowe");
        stockLevelsButton.addActionListener(this);
        mainWindowPanel.add(stockLevelsButton);

        //Transfer Button
        transferButton = new JButtonMainStyle("Transfer");
        transferButton.addActionListener(this);
        mainWindowPanel.add(transferButton);

        //Przyjecia Button
        inputDocumentButton = new JButtonMainStyle("Przyjęcia");
        inputDocumentButton.addActionListener(this);
        mainWindowPanel.add(inputDocumentButton);

        //Wydania Button
        outPutDocumentButton = new JButtonMainStyle("Wydania");
        outPutDocumentButton.addActionListener(this);
        mainWindowPanel.add(outPutDocumentButton);

        //Ustawienia Button
        settingButton = new JButtonMainStyle("Ustawienia");
        settingButton.addActionListener(this);
        mainWindowPanel.add(settingButton);

        add(mainWindowPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source.equals(stockLevelsButton)){
            //TODO
            mainWindowPanel.setVisible(false);
            StockItemView stockItemView = new StockItemView(this, mainWindowPanel);
            add(stockItemView);
            pack();
        }
        else if(source.equals(transferButton)){
            mainWindowPanel.setVisible(false);
            transferPanel = new TransferView(this, mainWindowPanel);
            add(transferPanel);
            pack();
        }
        else if(source.equals(inputDocumentButton)){
            mainWindowPanel.setVisible(false);
            InputView inputView = new InputView(this, mainWindowPanel);
            add(inputView);
            pack();
        }
        else if(source.equals(outPutDocumentButton)){
            //TODO Dodanie nowego Jpanelu wydania - wersja testowa
            mainWindowPanel.setVisible(false);
            OutputView outputJPanel = new OutputView(this, mainWindowPanel);
            add(outputJPanel);
            pack();
        }
        else if(source.equals(settingButton)){
            //TODO
            mainWindowPanel.setVisible(false);
            SettingView settingView = new SettingView(this, mainWindowPanel);
            add(settingView);
        }
        else if(source.equals(logoutButton)){
            new LoginForm("Logowanie", communicationSocket, streamToServer, streamFromServer).init();
            dispose();
        }

        //Pakowanie zawartości frame i środkowanie na ekranie
        setLocationRelativeTo(null);
    }

    @Override
    public void componentResized(ComponentEvent e) { }

    @Override
    public void componentMoved(ComponentEvent e) { }

    /**
     * Funkcja centruje okno aplikacji bo jej uwidocznieniu. Konieczne do immplementacji, bo poprawna realizacja procedu magazynowego,
     * pwooduje ponowne uwidocznienie frame'a, po tym jak został on schowany po kliknięciu w określoną funkcjnalność
     */
    @Override
    public void componentShown(ComponentEvent e) {
        if(e.getComponent() == mainWindowPanel){
            setSize(1100,550);
            setLocationRelativeTo(null);
        }
    }

    @Override
    public void componentHidden(ComponentEvent e) { }
}
