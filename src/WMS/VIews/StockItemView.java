package WMS.VIews;

import Style.JButtonOptionStyle;
import Style.JFliedTextStyle;
import Style.JLabelStyle;
import Style.JoptionPaneMessages;
import WMS.Entity.AssortmentEntity;
import WMS.Entity.AssortmentEntityCellView;
import WMS.MainWindowWMS;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 *  Klasa reperezntuje widok stanow magazynowych i obsluguje interakcje uzytkownika, zwiazane z filtrowaniem wynikow
 */
public class StockItemView extends JPanel implements ActionListener {
    private MainWindowWMS mainWindowWMS;
    private JButtonOptionStyle filterButton, backButton;
    private JFliedTextStyle assortmentShearTextField, locationShearTextField;
    private JList stockItemList;
    private ArrayList<AssortmentEntity> listOfAssortments = new ArrayList<>();
    JPanel mainContainer;

    /**
     * Konstruktor inicjalizuje referencje do glownego JPanel'u, okna WMS'a
     */
    public StockItemView(MainWindowWMS mainWindowWMS, JPanel mainContainer) {
        this.mainWindowWMS = mainWindowWMS;
        this.mainContainer = mainContainer;
        init();
        LoadStockItemList();
    }

    /**
     * Funkcja buduje okno i ustawia je na widoczne
     */
    public void init() {
        setSize(new Dimension(500, 500));
        //box glowny do pozycjnonwania w pionie
        Box verticalMainBox = Box.createVerticalBox();

        Border stockFilterBorder = BorderFactory.createTitledBorder("Kryteria selekcji");

        JPanel filtrPanel = new JPanel();

        Box verticalfiltrPanelBox = Box.createVerticalBox();
        JLabelStyle assortmentShearTextJLabel = new JLabelStyle("Assortyment");
        verticalfiltrPanelBox.add(assortmentShearTextJLabel);

        assortmentShearTextField = new JFliedTextStyle();
        verticalfiltrPanelBox.add(assortmentShearTextField);

        verticalfiltrPanelBox.add(Box.createVerticalStrut(20));
        JLabelStyle locationShearTextLabel = new JLabelStyle("Lokalizacja");
        verticalfiltrPanelBox.add(locationShearTextLabel);

        verticalfiltrPanelBox.add(Box.createVerticalStrut(20));
        locationShearTextField = new JFliedTextStyle();
        verticalfiltrPanelBox.add(locationShearTextField);

        verticalfiltrPanelBox.add(Box.createVerticalStrut(20));
        filterButton = new JButtonOptionStyle("Filtruj");
        filterButton.addActionListener(this);
        verticalfiltrPanelBox.add(filterButton);

        filtrPanel.add(verticalfiltrPanelBox);

        JPanel stockListPanel = new JPanel();
        stockItemList = new JList();
        stockItemList.setPreferredSize(new Dimension(800, 300));
        stockItemList.setCellRenderer(new AssortmentEntityCellView());

        JScrollPane scrollPane = new JScrollPane(stockItemList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        stockListPanel.add(scrollPane);

        backButton = new JButtonOptionStyle("Wstecz");
        backButton.addActionListener(this);


        filtrPanel.setBorder(stockFilterBorder);
        verticalMainBox.add(filtrPanel);
        verticalMainBox.add(stockListPanel);
        verticalMainBox.add(backButton);
        add(verticalMainBox);
        setVisible(true);
    }

    /**
     * Funkcja odpytuje serwer, o liste wszystkich dostepnych asortymentow, a nastepnie ja sortuje,
     * jesli zostal ustawiony jakis filtr
     */
    private void LoadStockItemList() {

        //Glowny request do servera
        JSONObject requestToServer = new JSONObject();
        requestToServer.put("action", "get_stock_item");

        //Filtry
        JSONObject filtersJSN = new JSONObject();
        filtersJSN.put("assortment_name", assortmentShearTextField.getText().trim());
        filtersJSN.put("location_name", locationShearTextField.getText().trim());

        requestToServer.put("filters", filtersJSN);

        //Wysylka JSON'a do serwera, z danymi nowej lokalizacji
        mainWindowWMS.GetStreamToServer().println(requestToServer);

        //Czekaj na odpowiedz od serwera
        try {
            while (true) {
                String serverResponse = mainWindowWMS.GetStreamFromServer().readLine();
                if (serverResponse != null) {
                    //Sparsuj odpowiedz do typu JSON
                    JSONObject serverResponseJSON = new JSONObject(serverResponse);

                    //Jesli odeslal success, to wywolaj funkcje aktualizujaca liste
                    if (serverResponseJSON.getString("status").equals("success")) {
                        UpdateJlist(serverResponseJSON.getJSONArray("stock_items"));
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

    /**
     * Funkcja aktualizuje liste, na podstawie otrzymanej tablicy
     */
    private void UpdateJlist(JSONArray stockItemArray) {
        //Wyczysć liste
        stockItemList.removeAll();
        //Wyzeruj kolekcje, ktora stanowi dane dla listy
        listOfAssortments = new ArrayList<>();

        //Iteruj po otrzymanej od serwera tablicy stockItemow
        for (Object tmpObject : stockItemArray) {
            //Konwertuj do JSONObject (nie wiem czemu nie mozna zadeklarować typu JSONObject w petli)
            JSONObject obj = (JSONObject) tmpObject;

            //Dodaj do listy z danymi, nowy obiekt z odczytanymi wartosciami
            listOfAssortments.add(new AssortmentEntity(obj.getString("assortment_name"),
                    obj.getString("location_name"),
                    Float.parseFloat(obj.getString("stock_level").replace(',', '.'))));
        }

        //Ustaw dane z kolekcji, do wyswietlenia na liscie
        stockItemList.setListData(listOfAssortments.toArray());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        //Akcja dla przycisku "Rezygnuj"
        if (source.equals(backButton)) {
            setVisible(false);
            mainContainer.setVisible(true);
        } else if (source == filterButton) {
            LoadStockItemList();
        }
    }
}
