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

/**
 * Klasa reperezntuajaca GUI okna z przyjeciami. Posiada implementacje komunikacji z serwerem
 */
public class InputView extends JPanel implements ActionListener {
    private MainWindowWMS mainWindowWMS;
    private JFliedTextStyle fromFiled;
    private JButtonOptionStyle addButton, editButton, removeButton;
    private JButtonOptionStyle saveButton, closeButton;
    private JList inputList;
    private ArrayList<AssortmentEntity> listOfAssortments = new ArrayList<>();
    JPanel mainContainer;

    /**
     * Konstruktor inicjalizuje referencje do glownego JPanel'u, okna WMS'a
     */
    public InputView(MainWindowWMS mainWindowWMS, JPanel mainContainer) {
        this.mainWindowWMS = mainWindowWMS;
        this.mainContainer = mainContainer;
        init();
    }

    /**
     * Funkcja buduje okno i ustawia je na widoczne
     */
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
        fromFiled.addActionListener(this);
        verticalSetionBox.add(fromFiled);

        verticalSetionBox.add(Box.createVerticalStrut(10));


        //Joption2 dla opcji z drugiej grupy (lable i 3 buttony obslugujace liste)
        JPanel jPanelOption2 = new JPanel();
        jPanelOption2.setLayout(new FlowLayout());

        JLabel assortmentToOut = new JLabel("Assortymenty do przyjecia");
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

        inputList = new JList();
        //Dane w liscie
        inputList.setListData(listOfAssortments.toArray());
        //Sposob reprezentacji danych, na podstawie nadpisanej klasy
        inputList.setCellRenderer(new AssortmentEntityCellView());

        inputList.setPreferredSize(new Dimension(500, 300));
        jPanelOption3.add(inputList);

        JScrollPane scrollPane = new JScrollPane(inputList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //buttonGroupPanel dla przyciskow funkcjnych
        JPanel buttonGroupPanel = new JPanel();
        buttonGroupPanel.setLayout(new GridLayout(0, 2, 10, 10));

        closeButton = new JButtonOptionStyle("Rezygnuj");
        closeButton.addActionListener(this);
        buttonGroupPanel.add(closeButton);

        saveButton = new JButtonOptionStyle("Zapisz");
        saveButton.addActionListener(this);
        buttonGroupPanel.add(saveButton);

        //laczenie calosci
        jPanelOption1.add(verticalSetionBox);
        verticalMainBox.add(jPanelOption1);
        verticalMainBox.add(jPanelOption2);
        verticalMainBox.add(scrollPane);
        verticalMainBox.add(buttonGroupPanel);
        add(verticalMainBox);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        //Akcja dla przycisku "Rezygnuj"
        if (source.equals(closeButton)) {
            //Sprawdz inputy, czy ktorys nie jest wypelniony
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
            //Przycisk edycji asortymentu z listy
        } else if (source.equals(editButton)) {
            //Sprawdz, czy zaznaczono jakis rekord na liscie
            if (inputList.getSelectedIndex() < 0) {
                JoptionPaneMessages.showErrorPopup("Nie wskazano rekordu z listy");
                return;
            }
            AssortmentEntity tmpEntity = listOfAssortments.get(inputList.getSelectedIndex());
            new InputView_EditAssortmentFrame(this, tmpEntity.getName(),
                    tmpEntity.getLocalization(), tmpEntity.getCount());
            setVisible(false);

            //Przycisk usuwania asortymentu z listy
        } else if (source.equals(removeButton)) {
            //Sprawdz, czy zaznaczono jakis rekord na liscie
            if (inputList.getSelectedIndex() < 0) {
                JoptionPaneMessages.showErrorPopup("Nie wskazano rekordu z listy");
                return;
            }
            //Usun asortyment i aktualizuj JList
            listOfAssortments.remove(inputList.getSelectedIndex());
            inputList.setListData(listOfAssortments.toArray());

        } else if (source.equals(saveButton)) {

           if(fromFiled.getText().isBlank()){
                JoptionPaneMessages.showErrorPopup("Pole dostawcy musi być uzupelnione");
                return;
            }
            //Sprawdz, czy lista ma chociaz jeden towar
            if(listOfAssortments.size() == 0){
                JoptionPaneMessages.showErrorPopup("Wskaz conajmniej jeden asortyment do przyjecia");
                return;
            }
            //Wywolaj komende, wysylajaca request sdo serwera
            inputWarehouse();
            //Przycisk dodania nowego asortymentu
        } else if (source.equals(addButton)) {
            setVisible(false);
            new InputView_AddAssortmentFrame(this);
        }else{
            saveButton.doClick();
        }

    }



    /**
     * Publiczna funkcja, wywolywana z okna dialogowego z wprowadzaniem nowego towaru
     * Dodaje nowy towar do JList i odswieza ja
     */
    public void AddNewAssortment(String assortmentName,String toLokalization, float count) {
        listOfAssortments.add(new AssortmentEntity(assortmentName, toLokalization,count));
        inputList.setListData(listOfAssortments.toArray());
    }

    /**
     * Publiczna funkcja, wywolywana z okna dialogowego z wprowadzaniem nowego towaru
     * Dodaje nowy towar do JList i odswieza ja
     */
    public void EditAssortent(String assortmentName, String fromLokalization, float count) {
        listOfAssortments.set(inputList.getSelectedIndex(), new AssortmentEntity(assortmentName, fromLokalization, count));
        inputList.setListData(listOfAssortments.toArray());
    }

    /**
     * Funkcja sprawdza, czy ktorekolwiek pole jest wypelnione
     */
    private boolean isAnyInputField() {
        return !fromFiled.getText().isBlank() || inputList.getModel().getSize() == 0;
    }

    /**
     * Funkcja wywolywana przy kliknieciu przycisku "zapisz". Przygotowuje dane w formacie JSON dla serwera,
     * wysyla komunikat i oczekuje odpowiedzi od serwera, aby pokazać uzytkownikowi kod sukcesu lub bledu
     */
    private void inputWarehouse() {

        JSONObject inputToWarehouseJSON = new JSONObject();
        JSONArray inputToWarehouseDataJSON = new JSONArray();

        inputToWarehouseJSON.put("action", "input_operation");
        inputToWarehouseJSON.put("from_Contractor", fromFiled.getText());

        for (var assortentList : listOfAssortments) {
            JSONObject tmpToWarehouseDataJSON = new JSONObject();
            tmpToWarehouseDataJSON.put("assortment_Name", assortentList.getName());
            tmpToWarehouseDataJSON.put("assortment_location",assortentList.getLocalization());
            tmpToWarehouseDataJSON.put("assortment_Count", assortentList.getCount());

            inputToWarehouseDataJSON.put(tmpToWarehouseDataJSON);
        }
        inputToWarehouseJSON.put("assortments_data", inputToWarehouseDataJSON);
        mainWindowWMS.GetStreamToServer().println(inputToWarehouseJSON);

        //Czekaj na odpowiedz od serwera
        try {
            while (true) {
                String serverResponse = mainWindowWMS.GetStreamFromServer().readLine();
                if (serverResponse != null) {

                    //Sparsuj odpowiedz do typu JSON
                    System.out.println(serverResponse);
                    JSONObject serverResponseJSON = new JSONObject(serverResponse);

                    //Jesli odeslal OK, to uzytkownik utworzony poprawnie
                    if (serverResponseJSON.getString("status").equals("success")) {
                        JoptionPaneMessages.showSuccessPopup("Towar przyjety !!");
                        setVisible(false);
                        mainContainer.setVisible(true);
                    } else {
                        //Jesli nie odeslal ok, to wyswietl zwrocony komunikat bledu
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
