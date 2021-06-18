package WMS.VIews;

import Style.*;
import WMS.Entity.AssortmentEntity;
import WMS.Entity.AssortmentEntityCellView;
import WMS.MainWindowWMS;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Klasa reperezntuajaca GUI okna z wydaniami. Posiada implementacje komunikacji z serwerem
 */
public class OutputView extends JPanel implements ActionListener {
    private MainWindowWMS mainWindowWMS;
    private JFliedTextStyle fromFiled, toFiled;
    private JButtonOptionStyle addButton, editButton, removeButton;
    private JButtonOptionStyle saveButton, closeButton;
    private JList outputList;
    private ArrayList<AssortmentEntity> listOfAssortments = new ArrayList<>();
    JPanel mainContainer;

    /**
     * Konstruktor inicjalizuje referencje do glownego JPanel'u, okna WMS'a
     */
    public OutputView(MainWindowWMS mainWindowWMS, JPanel mainContainer){
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
        verticalSetionBox.add(fromFiled);

        verticalSetionBox.add(Box.createVerticalStrut(10));

        JLabelStyle toLabel = new JLabelStyle("Do:");
        verticalSetionBox.add(toLabel);

        toFiled = new JFliedTextStyle();
        verticalSetionBox.add(toFiled);

        //Joption2 dla opcji z drugiej grupy (lable i 3 buttony obslugujace liste)
        JPanel jPanelOption2 = new JPanel();
        jPanelOption2.setLayout(new FlowLayout());

        JLabel assortmentToOut = new JLabel("Assortymenty do wydania");
        assortmentToOut.setFont(new Font("Arial", Font.PLAIN, 25));
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
        //Dane w liscie
        outputList.setListData(listOfAssortments.toArray());
        //Sposob reprezentacji danych, na podstawie nadpisanej klasy
        outputList.setCellRenderer(new AssortmentEntityCellView());

        outputList.setPreferredSize(new Dimension(500, 300));
        jPanelOption3.add(outputList);

        JScrollPane scrollPane = new JScrollPane(outputList);
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
                if (choice == JOptionPane.YES_OPTION) {
                    setVisible(false);
                    mainContainer.setVisible(true);
                    return;
                }
                setVisible(false);
                mainContainer.setVisible(true);
            }
        //Przycisk edycji asortymentu z listy
        } else if (source.equals(editButton)) {
            //Sprawdz, czy zaznaczono jakis rekord na liscie
            if (outputList.getSelectedIndex() < 0) {
                JoptionPaneMessages.showErrorPopup("Nie wskazano rekordu z listy");
                return;
            }
            AssortmentEntity tmpEntity = listOfAssortments.get(outputList.getSelectedIndex());
            new OutputView_EditAssortmentFrame(this, tmpEntity.getName(),
                                                tmpEntity.getLocalization(), tmpEntity.getCount());
            setVisible(false);

        //Przycisk usuwania asortymentu z listy
        } else if (source.equals(removeButton)) {
            //Sprawdz, czy zaznaczono jakis rekord na liscie
            if (outputList.getSelectedIndex() < 0) {
                JoptionPaneMessages.showErrorPopup("Nie wskazano rekordu z listy");
                return;
            }
            //Usun asortyment i aktualizuj JList
            listOfAssortments.remove(outputList.getSelectedIndex());
            outputList.setListData(listOfAssortments.toArray());

        //Przycisk zapisu *wykonania wydania)
        } else if (source.equals(saveButton)) {
            //Sprawdz, czy inputy nie sa puste
            if(fromFiled.getText().isBlank() || toFiled.getText().isBlank()){
                JoptionPaneMessages.showErrorPopup("Oba pola z dostawca i odbiorca musza być wypelnione");
                return;
            }

            //Sprawdz, czy lista ma chociaz jeden towar
           if(listOfAssortments.size() == 0){
               JoptionPaneMessages.showErrorPopup("Wskaz conajmniej jeden asortyment do wydania");
               return;
           }
            //Wywolaj komende, wysylajaca request sdo serwera
            doOutputCommand();

            //Przycisk dodania nowego asortymentu
        } else if (source.equals(addButton)) {
            setVisible(false);
            new OutputView_AddAssortmentFrame(this);
        }

    }

    /**
     * Funkcja tworzy JSON'a i odpytuje server, o wydanie okreslonych przez uzutkowmnika towarow,
     * na okreslonych lokalizacjach
     */
    private void doOutputCommand() {
        //Glowny JSOn, wysylany do serwera
        JSONObject outputCommandJSON = new JSONObject();
        outputCommandJSON.put("action", "do_output");

        JSONObject data = new JSONObject();
        data.put("from", fromFiled.getText().trim());
        data.put("to", toFiled.getText().trim());

        //Tablica, ktora bedzie zawierala asortymenty do wydania
        JSONArray assortmentsData = new JSONArray();

        //Petla dodajaca wszystkie asortymenty z listy, do JSON'a
        for (AssortmentEntity tmpEntity: listOfAssortments){
            JSONObject tmpJSOnAssortment = new JSONObject();
            tmpJSOnAssortment.put("assortment_name", tmpEntity.getName());
            tmpJSOnAssortment.put("location_name", tmpEntity.getLocalization());
            tmpJSOnAssortment.put("assortment_count", tmpEntity.getCount());

            assortmentsData.put(tmpJSOnAssortment);
        }

        data.put("assortments_data", assortmentsData);
        outputCommandJSON.put("data", data);

        //Wyslij dane do serwera
        mainWindowWMS.GetStreamToServer().println(outputCommandJSON);

        //Czekaj na odpowiedz od serwera
        try {
            while (true) {
                String serverResponse = mainWindowWMS.GetStreamFromServer().readLine();
                if (serverResponse != null) {
                    //Sparsuj odpowiedz do typu JSON
                    JSONObject serverResponseJSON = new JSONObject(serverResponse);

                    //Jesli odeslal success, to wyswietl komunikat i zamknij okno
                    if (serverResponseJSON.getString("status").equals("success")) {
                        JoptionPaneMessages.showSuccessPopup("Wydanie zakonczone poprawnie");
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


    /**
     * Publiczna funkcja, wywolywana z okna dialogowego z wprowadzaniem nowego towaru
     * Dodaje nowy towar do JList i odswieza ja
     */
    public void AddNewAssortment(String assortmentName, String fromLokalization, float count) {
        listOfAssortments.add(new AssortmentEntity(assortmentName, fromLokalization, count));
        outputList.setListData(listOfAssortments.toArray());
    }

    /**
     * Publiczna funkcja, wywolywana z okna dialogowego z wprowadzaniem nowego towaru
     * Dodaje nowy towar do JList i odswieza ja
     */
    public void EditAssortent(String assortmentName, String fromLokalization, float count) {
        listOfAssortments.set(outputList.getSelectedIndex(), new AssortmentEntity(assortmentName, fromLokalization, count));
        outputList.setListData(listOfAssortments.toArray());
    }

    /**
     * Funkcja sprawdza, czy ktorekolwiek pole jest wypelnione
     */
    private boolean isAnyInputField() {
        return !fromFiled.getText().isBlank() || !toFiled.getText().isBlank() || outputList.getModel().getSize() == 0;
    }


}
