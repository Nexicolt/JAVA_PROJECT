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

public class StockItemView extends JPanel implements ActionListener {
    private MainWindowWMS mainWindowWMS;
    private JButtonOptionStyle filterButton, backButton;
    private JFliedTextStyle assortmentShearTextField, locationShearTextField;
    private JList stockItemList;
    private ArrayList<AssortmentEntity> listOfAssortments = new ArrayList<>();
    JPanel mainContainer;

    /**
     * Konstruktor inicjalizuje referencje do głownego JPanel'u, okna WMS'a
     */
    public StockItemView(MainWindowWMS mainWindowWMS, JPanel mainContainer) {
        this.mainWindowWMS = mainWindowWMS;
        this.mainContainer = mainContainer;
        init();
        LoadStockItemList();
    }

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
        stockListPanel.add(stockItemList);

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
     * Funkcja odpytuje serwer, o listę wszystkich dostępnych asortymentów, a nastepnie ją sortuje,
     * jeśli został ustawiony jakiś filtr
     */
    private void LoadStockItemList() {

        //Główny request do servera
        JSONObject requestToServer = new JSONObject();
        requestToServer.put("action", "get_stock_item");

        //Filtry
        JSONObject filtersJSN = new JSONObject();
        filtersJSN.put("assortment_name", assortmentShearTextField.getText().trim());
        filtersJSN.put("location_name", locationShearTextField.getText().trim());

        requestToServer.put("filters", filtersJSN);

        //Wysyłka JSON'a do serwera, z danymi nowej lokalizacji
        mainWindowWMS.GetStreamToServer().println(requestToServer);

        //Czekaj na odpowiedź od serwera
        try {
            while (true) {
                String serverResponse = mainWindowWMS.GetStreamFromServer().readLine();
                if (serverResponse != null) {
                    //Sparsuj odpowiedź do typu JSON
                    JSONObject serverResponseJSON = new JSONObject(serverResponse);

                    //Jeśli odesłał success, to wywołaj funkcję aktualizującą listę
                    if (serverResponseJSON.getString("status").equals("success")) {
                        UpdateJlist(serverResponseJSON.getJSONArray("stock_items"));
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

    /**
     * Funkcja aktualizuje listę, na podstawie otrzymanej tablicy
     */
    private void UpdateJlist(JSONArray stockItemArray) {
        //Wyczyść listę
        stockItemList.removeAll();
        //Wyzeruj kolekcje, która stanowi dane dla listy
        listOfAssortments = new ArrayList<>();

        //Iteruj po otrzymanej od serwera tablicy stockItemów
        for (Object tmpObject : stockItemArray) {
            //Konwertuj do JSONObject (nie wiem czemu nie można zadeklarować typu JSONObject w pętli)
            JSONObject obj = (JSONObject) tmpObject;

            //Dodaj do listy z danymi, nowy obiekt z odczytanymi wartościami
            listOfAssortments.add(new AssortmentEntity(obj.getString("assortment_name"),
                    obj.getString("location_name"),
                    Float.parseFloat(obj.getString("stock_level").replace(',', '.'))));
        }

        //Ustaw dane z kolekcji, do wyświetlenia na liście
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
