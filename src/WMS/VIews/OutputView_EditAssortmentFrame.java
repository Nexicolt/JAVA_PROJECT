package WMS.VIews;

import Style.JButtonDialogStyle;
import Style.JFiledDialogStyle;
import Style.JLabelStyle;
import Style.JoptionPaneMessages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Klasa reprezentuje okno, ktore wyskakuje po wcisnieciu przycisku edycji asrotymentu, na tworzonym wydaniu
 */
public class OutputView_EditAssortmentFrame extends JDialog implements ActionListener {
    private JFiledDialogStyle fromLocationJFlied, assortmentJFlied, totalJFlied;
    private JButtonDialogStyle saveButton, closeButton;

    private final OutputView parrentOutputPanel;

    /**
     * Konstruktor wyswietla okno i przechywuje referencje do okna rodzica oraz wpisuje przekazane dane asortymentu
     */
    public OutputView_EditAssortmentFrame(OutputView outputPanelHandler, String assortmentName,
                                          String lokalizationName, float count) {
        parrentOutputPanel = outputPanelHandler;

        init();

        //Zmiana tekstu w polach dopiero po ich utworzeniu
        assortmentJFlied.setText(assortmentName);
        fromLocationJFlied.setText(lokalizationName);
        totalJFlied.setText("" + count);


    }

    /**
     * Funkcja buduje okno i ustawia je na widoczne
     */
    public void init() {
        setSize(400, 400);
        setResizable(false);

        Box verticalDialogBox = Box.createVerticalBox();
        JPanel dialogOptionsJPanel = new JPanel();

        Box verticalJPanelBox = Box.createVerticalBox();
        JLabelStyle fromLocationLabel = new JLabelStyle("Z lokalizacji");
        verticalJPanelBox.add(fromLocationLabel);

        fromLocationJFlied = new JFiledDialogStyle();
        fromLocationJFlied.addActionListener(this);
        verticalJPanelBox.add(fromLocationJFlied);

        JLabelStyle assortmentLabel = new JLabelStyle("Assortment");
        verticalJPanelBox.add(assortmentLabel);

        assortmentJFlied = new JFiledDialogStyle();
        assortmentJFlied.addActionListener(this);
        verticalJPanelBox.add(assortmentJFlied);

        JLabelStyle totalLabel = new JLabelStyle("Ilosć");
        verticalJPanelBox.add(totalLabel);

        totalJFlied = new JFiledDialogStyle();
        totalJFlied.addActionListener(this);
        verticalJPanelBox.add(totalJFlied);

        JPanel buttonGroupPanel = new JPanel();
        buttonGroupPanel.setLayout(new GridLayout(0, 2, 10, 10));

        closeButton = new JButtonDialogStyle("Rezygnuj");
        closeButton.addActionListener(this);
        buttonGroupPanel.add(closeButton);

        saveButton = new JButtonDialogStyle("Zapisz");
        saveButton.addActionListener(this);
        buttonGroupPanel.add(saveButton);

        dialogOptionsJPanel.add(verticalJPanelBox);
        verticalDialogBox.add(dialogOptionsJPanel);
        verticalDialogBox.add(buttonGroupPanel);
        add(verticalDialogBox);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(closeButton)) {
            this.dispose();
            parrentOutputPanel.setVisible(true);
        }
        //Dla przycisku zapisz -> wywolaj metode edytowania newgo asortyemnu, na obiekcie JPanelu rodzica
        else if (source.equals(saveButton)) {
            //Sprawdz inputy
            if (isAnyInputBlank()) {
                JoptionPaneMessages.showErrorPopup("Wszystkie pola musza być wypelnione");
                return;
            }
            parrentOutputPanel.EditAssortent(assortmentJFlied.getText(),
                    fromLocationJFlied.getText(), Float.parseFloat(totalJFlied.getText().replace(',', '.')));
            this.dispose();
            parrentOutputPanel.setVisible(true);
        }else{
            saveButton.doClick();
        }
    }

    /**
     * Funkcja sprawdza, czy ktorys z inputow nie jest pusty
     */
    private boolean isAnyInputBlank() {
        return fromLocationJFlied.getText().isBlank() && assortmentJFlied.getText().isBlank()
                && totalJFlied.getText().isBlank();
    }
}
