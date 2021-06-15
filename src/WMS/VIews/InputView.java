package WMS.VIews;

import Style.*;
import WMS.Entity.AssortmentEntity;
import WMS.Entity.AssortmentEntityCellView;
import org.json.JSONArray;
import org.json.JSONObject;
import WMS.MainWindowWMS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

//JPanel wyswietalacy przyjecia wywolywany po kliknieciu przycisku przyjmij
public class InputView extends JPanel implements ActionListener {
    private MainWindowWMS mainWindowWMS;
    private JFliedTextStyle fromFiled;
    private JButtonOptionStyle addButton, editButton, removeButton;
    private JButtonOptionStyle saveButton, closeButton;
    private JList outputList;
    private ArrayList<AssortmentEntity> listOfAssortments = new ArrayList<>();
    JPanel mainContainer;

    /**
     * Konstruktor inicjalizuje referencje do głownego JPanel'u, okna WMS'a
     */
    public InputView(MainWindowWMS mainWindowWMS, JPanel mainContainer) {
        this.mainWindowWMS = mainWindowWMS;
        this.mainContainer = mainContainer;
        init();
    }

    public void init() {
        setSize(new Dimension(500, 500));
        //box glowny do pozycjnonwania w pionie
        Box verticalMainBox = Box.createVerticalBox();

        verticalMainBox.add(Box.createVerticalStrut(20));

        //Joption1 dla opcji z pierwszej grupy (od, do)
        JPanel jPanelOption1 = new JPanel();
        Box verticalSetionBox = Box.createVerticalBox();

        JLabelStyle fromLabel = new JLabelStyle("Od:");
        verticalSetionBox.add(fromLabel);

        fromFiled = new JFliedTextStyle();
        verticalSetionBox.add(fromFiled);

        verticalSetionBox.add(Box.createVerticalStrut(10));


        //Joption2 dla opcji z drugiej grupy (lable i 3 buttony obslugujace liste)
        JPanel jPanelOption2 = new JPanel();
        jPanelOption2.setLayout(new FlowLayout());

        JLabel assortmentToOut = new JLabel("Assortymenty do przyjęcia");
        assortmentToOut.setFont(new Font("Arial", Font.PLAIN, 20));
        jPanelOption2.add(assortmentToOut);

        addButton = new JButtonOptionStyle("+");
        addButton.addActionListener(this);
        jPanelOption2.add(addButton);

        editButton = new JButtonOptionStyle("e");
        editButton.addActionListener(this);
        jPanelOption2.add(editButton);

        removeButton = new JButtonOptionStyle("-");
        removeButton.addActionListener(this);
        jPanelOption2.add(removeButton);

        //Joption3 dla ulozenia listy
        JPanel jPanelOption3 = new JPanel();

        outputList = new JList();
        //Dane w liście
        outputList.setListData(listOfAssortments.toArray());
        //Sposób reprezentacji danych, na podstawie nadpisanej klasy
        outputList.setCellRenderer(new AssortmentEntityCellView());

        outputList.setPreferredSize(new Dimension(500, 300));
        jPanelOption3.add(outputList);


        //buttonGroupPanel dla przyciskow funkcjnych
        JPanel buttonGroupPanel = new JPanel();
        buttonGroupPanel.setLayout(new GridLayout(0, 2, 10, 10));

        closeButton = new JButtonOptionStyle("Rezygnuj");
        closeButton.addActionListener(this);
        buttonGroupPanel.add(closeButton);

        saveButton = new JButtonOptionStyle("Zapisz");
        saveButton.addActionListener(this);
        buttonGroupPanel.add(saveButton);

        //Łączenie calosci
        jPanelOption1.add(verticalSetionBox);
        verticalMainBox.add(jPanelOption1);
        verticalMainBox.add(jPanelOption2);
        verticalMainBox.add(jPanelOption3);
        verticalMainBox.add(buttonGroupPanel);
        add(verticalMainBox);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        //Akcja dla przycisku "Rezygnuj"
        if (source.equals(closeButton)) {
            //Sprawdź inputy, czy któryś nie jest wypełniony
            if (isAnyInputField()) {
                int choice = JOptionPane.showConfirmDialog(null,
                        "Wprowadzone dane nie zostaną zapisane. Czy na pewno chcesz wyjść?",
                        "Uwaga!",
                        JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            setVisible(false);
            mainWindowWMS.setVisible(true);
            //Przycisk edycji asortymentu z listy
        } else if (source.equals(editButton)) {
            //Sprawdź, czy zaznaczono jakiś rekord na liście
            if (outputList.getSelectedIndex() < 0) {
                JoptionPaneMessages.showErrorPopup("Nie wskazano rekordu z listy");
                return;
            }
            AssortmentEntity tmpEntity = listOfAssortments.get(outputList.getSelectedIndex());
            new InputView_EditAssortmentFrame(this, tmpEntity.getName(),
                    tmpEntity.getLocalization(), tmpEntity.getCount());
            setVisible(false);

            //Przycisk usuwania asortymentu z listy
        } else if (source.equals(removeButton)) {
            //Sprawdź, czy zaznaczono jakiś rekord na liście
            if (outputList.getSelectedIndex() < 0) {
                JoptionPaneMessages.showErrorPopup("Nie wskazano rekordu z listy");
                return;
            }
            //Usuń asortyment i aktualizuj JList
            listOfAssortments.remove(outputList.getSelectedIndex());
            outputList.setListData(listOfAssortments.toArray());

        } else if (source.equals(saveButton)) {

            /*this.setVisible(false);
            mainWindowWMS.setVisible(true);*/

            //Przycisk dodania nowego asortymentu
        } else if (source.equals(addButton)) {
            setVisible(false);
            new InputView_AddAssortmentFrame(this);
        }

    }



    /**
     * Publiczna funkcja, wywoływana z okna dialogowego z wprowadzaniem nowego towaru
     * Dodaje nowy towar do JList i odświeża ją
     */
    public void AddNewAssortment(String assortmentName, String fromLokalization, float count) {
        listOfAssortments.add(new AssortmentEntity(assortmentName, fromLokalization, count));
        outputList.setListData(listOfAssortments.toArray());
    }

    /**
     * Publiczna funkcja, wywoływana z okna dialogowego z wprowadzaniem nowego towaru
     * Dodaje nowy towar do JList i odświeża ją
     */
    public void EditAssortent(String assortmentName, String fromLokalization, float count) {
        listOfAssortments.set(outputList.getSelectedIndex(), new AssortmentEntity(assortmentName, fromLokalization, count));
        outputList.setListData(listOfAssortments.toArray());
    }

    /**
     * Funkcja sprawdza, czy którekolwiek pole jest wypełnione
     */
    private boolean isAnyInputField() {
        return !fromFiled.getText().isBlank() || outputList.getModel().getSize() == 0;
    }

    private void InputToWarehouse() {

        JSONObject inputToWarehouseJSON = new JSONObject();
        JSONArray inputToWarehouseDataJSON = new JSONArray();

        inputToWarehouseJSON.put("action", "input_operation");
        inputToWarehouseJSON.put("from_Contractor", fromFiled.getText());

        for (var assortentList : listOfAssortments) {
            JSONObject tmpToWarehouseDataJSON = new JSONObject();
            tmpToWarehouseDataJSON.put("assortment_Name", assortentList.getName());
            tmpToWarehouseDataJSON.put("assortment_Count", assortentList.getCount());
            inputToWarehouseDataJSON.put(tmpToWarehouseDataJSON);
        }
        inputToWarehouseJSON.put("data", inputToWarehouseDataJSON);
        mainWindowWMS.GetStreamToServer().println(inputToWarehouseJSON);

        //Czekaj na odpowiedź od serwera
        try {
            while (true) {
                String serverResponse = mainWindowWMS.GetStreamFromServer().readLine();
                if (serverResponse != null) {

                    //Sparsuj odpowiedź do typu JSON
                    System.out.println(serverResponse);
                    JSONObject serverResponseJSON = new JSONObject(serverResponse);

                    //Jeśli odesłał OK, to użytkownik utworzony poprawnie
                    if (serverResponseJSON.getString("status").equals("success")) {
                        JoptionPaneMessages.showSuccessPopup("Towar przyjęty !!");
                    } else {
                        //Jeśli nie odesłał ok, to wyświetl zwrócony komunikat błędu
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
