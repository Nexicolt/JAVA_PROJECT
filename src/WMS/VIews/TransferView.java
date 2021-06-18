package WMS.VIews;

import Style.*;
import WMS.MainWindowWMS;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Klas obsluguje sekcje Transfery - pojawia sie po wyborze przycisku z menu glownego
 */
public class TransferView extends JPanel implements ActionListener {

    //Uchwyty do inputow i przyciskow
    JFliedTextStyle fromLocationJTextField, assortmentJTextField, tolocationJTextField, totalJTextField;
    JButtonOptionStyle saveButton, closeButton;
    MainWindowWMS mainWindowWMS;
    JPanel mainContainer;

    /**
     * Konstruktor inicjalizuje referencje do glownego JPanel'u, okna WMS'a
     */
    public TransferView(MainWindowWMS mainWindowWMS, JPanel mainContainer){
        this.mainWindowWMS = mainWindowWMS;
        this.mainContainer = mainContainer;
        init();
    }

    public void init()
    {
        setSize(new Dimension(400,200));

        //uzywany Layout Box pozwalajazy na ulozenie elementow w pionie
        //verticalMainBox glowny box dla panelu
        Box verticalMainBox = Box.createVerticalBox();

        //acctonSetionJPanel grupa Jpaneli grupy pierwzej (okna do wypelnienia)
        JPanel acctonSetionJPanel = new JPanel();
        //verticalSetionBox box layot pozwalajacy na dobre ulozenie okien opcji
        Box verticalSetionBox = Box.createVerticalBox();

        verticalSetionBox.add(Box.createVerticalStrut(50));
        JLabelStyle fromLocationLabel = new JLabelStyle("Z lokalizacji:");
        verticalSetionBox.add(fromLocationLabel);
        fromLocationJTextField = new JFliedTextStyle();
        verticalSetionBox.add(fromLocationJTextField);

        verticalSetionBox.add(Box.createVerticalStrut(20));

        JLabelStyle assortmentLabel = new JLabelStyle("Assortyment:");
        verticalSetionBox.add(assortmentLabel);
        assortmentJTextField = new JFliedTextStyle();
        verticalSetionBox.add(assortmentJTextField);

        verticalSetionBox.add(Box.createVerticalStrut(20));

        JLabelStyle toLocationLabel = new JLabelStyle("Do Lokalizacji:");
        verticalSetionBox.add(toLocationLabel);
        tolocationJTextField = new JFliedTextStyle();
        verticalSetionBox.add(tolocationJTextField);

        verticalSetionBox.add(Box.createVerticalStrut(20));

        JLabelStyle totalLabel = new JLabelStyle("Ilosć:");
        verticalSetionBox.add(totalLabel);
        totalJTextField = new JFliedTextStyle();
        verticalSetionBox.add(totalJTextField);

        verticalSetionBox.add(Box.createVerticalStrut(20));

        //Sekcja buttonow zawierajaca
        //JPanel buttonGroupPanel
        JPanel buttonGroupPanel = new JPanel();
        buttonGroupPanel.setLayout(new GridLayout(0,2,10,10));

        closeButton = new JButtonOptionStyle("Rezygnuj");
        closeButton.addActionListener(this);
        buttonGroupPanel.add(closeButton);

        saveButton = new JButtonOptionStyle("Zapisz");
        saveButton.addActionListener(this);
        buttonGroupPanel.add(saveButton);

        //Dodanie calosci do wspolnego JPanelu
        acctonSetionJPanel.add(verticalSetionBox);
        verticalMainBox.add(acctonSetionJPanel);
        verticalMainBox.add(buttonGroupPanel);
        add(verticalMainBox);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source == closeButton) {
            //Sprawdz buttony i wyswietl komunikat ostrzegawczy, jesli ktorys jest wypelniony
            if (isAnyInputField()) {
                int choice = JOptionPane.showConfirmDialog(null,
                        "Wprowadzone dane nie zostana zapisane. Czy na pewno chcesz wyjsć?",
                        "Uwaga!",
                        JOptionPane.YES_NO_OPTION);
                if(choice == JOptionPane.YES_OPTION) {
                    setVisible(false);
                    mainContainer.setVisible(true);
                    return;
                }
            }
            setVisible(false);
            mainContainer.setVisible(true);
        }
        else if(source == saveButton){
            //Sprawdz inputy, czy ktorys nie jest pusty
            ///fromLocationJTextField, assortmentJTextField, tolocationJTextField, totalJTextField;
            if(isAnyInputBlank()){
                JoptionPaneMessages.showErrorPopup("Wszystkie pola musza być wypelnione");
                return;
            }
            doTransferCommand();
        }
    }

    /**
     * Funkcja sprawdza, czy ktorys z inputow nie jest pusty
     */
    private boolean isAnyInputBlank(){
        return fromLocationJTextField.getText().isBlank() || assortmentJTextField.getText().isBlank() ||
                tolocationJTextField.getText().isBlank() || totalJTextField.getText().isBlank() ;
    }

    /**
     * Funkcja sprawdza, czy ktorykolwiek z inputow jest wypelniony
     */
    private boolean isAnyInputField(){
        return !totalJTextField.getText().isBlank() || !assortmentJTextField.getText().isBlank()
                || !fromLocationJTextField.getText().isBlank() || !tolocationJTextField.getText().isBlank();
    }

    /**
     * Glowna funkcja, odpowiedzialna z wykonanaie transferu. Waliduje wprowadzone dane, wysyla request do serwera i wyswietla otrzymana odpowiedz
     */
    private void doTransferCommand() {
        //Glowny JSOn, wysylany do serwera
        JSONObject transferCommandJSON = new JSONObject();
        transferCommandJSON.put("action", "transfer_operation");

        transferCommandJSON.put("from_location", fromLocationJTextField.getText().trim());
        transferCommandJSON.put("to_location", tolocationJTextField.getText().trim());
        transferCommandJSON.put("Asortment", assortmentJTextField.getText().trim());
        transferCommandJSON.put("AssortmentQuantity", totalJTextField.getText().trim());

        //Wyslij dane do serwera
        mainWindowWMS.GetStreamToServer().println(transferCommandJSON);

        //Czekaj na odpowiedz od serwera
        try {
            while (true) {
                String serverResponse = mainWindowWMS.GetStreamFromServer().readLine();
                if (serverResponse != null) {
                    //Sparsuj odpowiedz do typu JSON
                    JSONObject serverResponseJSON = new JSONObject(serverResponse);

                    //Jesli odeslal success, to wyswietl komunikat i zamknij okno
                    if (serverResponseJSON.getString("status").equals("success")) {
                        JoptionPaneMessages.showSuccessPopup("Transfer zakonczony poprawnie");
                        setVisible(false);
                        mainContainer.setVisible(true);
                    } else {
                        //Jesli nie odeslal success, to wyswietl zwrocony komunikat bledu
                        String erroMessage = serverResponseJSON.getString("message");
                        JoptionPaneMessages.showErrorPopup(erroMessage);
                    }
                    break;
                }
            }
        } catch (IOException ignored) {
        }
    }
}
