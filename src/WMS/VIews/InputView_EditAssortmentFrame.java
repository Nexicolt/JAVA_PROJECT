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
 * Klasa reprezentuje okno, które wyskakuje po wciśnieciu przycisku edytowania  asrotymentu, na tworzonym przyjęciu
 */
public class InputView_EditAssortmentFrame extends JDialog implements ActionListener {
    private JFiledDialogStyle fromLocationJFlied, assortmentJFlied, totalJFlied;
    private JButtonDialogStyle saveButton, closeButton;

    private final InputView parrentInputPanel;

    /**
     * Konstruktor wyświetla okno i przechywuje referencję do okna rodzica oraz wpisuje przekazane dane asortymentu
     */
    public InputView_EditAssortmentFrame(InputView inputPanelHandler, String assortmentName,
                                          String lokalizationName, float count){
        parrentInputPanel = inputPanelHandler;

        init();

        //Zmiana tekstu w polach dopiero po ich utworzeniu
        assortmentJFlied.setText(assortmentName);
        fromLocationJFlied.setText(lokalizationName);
        totalJFlied.setText("" + count);


    }
    /**
     * Funkcja buduje okno i ustawia je na widoczne
     */
    public void init(){
        setSize(400,400);
        setResizable(false);

        Box verticalDialogBox = Box.createVerticalBox();
        JPanel dialogOptionsJPanel = new JPanel();

        Box verticalJPanelBox = Box.createVerticalBox();
        JLabelStyle fromLocationLabel = new JLabelStyle("Do lokalizacji");
        verticalJPanelBox.add(fromLocationLabel);

        fromLocationJFlied = new JFiledDialogStyle();
        verticalJPanelBox.add(fromLocationJFlied);

        JLabelStyle assortmentLabel = new JLabelStyle("Assortment");
        verticalJPanelBox.add(assortmentLabel);

        assortmentJFlied = new JFiledDialogStyle();
        verticalJPanelBox.add(assortmentJFlied);

        JLabelStyle totalLabel = new JLabelStyle("Ilość");
        verticalJPanelBox.add(totalLabel);

        totalJFlied = new JFiledDialogStyle();
        verticalJPanelBox.add(totalJFlied);

        JPanel buttonGroupPanel = new JPanel();
        buttonGroupPanel.setLayout(new GridLayout(0,2,10,10));

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
        if(source.equals(closeButton)){
            this.dispose();
            parrentInputPanel.setVisible(true);
        }
        //Dla przycisku zapisz -> wywołaj metodę edytowania newgo asortyemnu, na obiekcie JPanelu rodzica
        else if(source.equals(saveButton)){
            //Sprawdź inputy
            if(isAnyInputBlank()){
                JoptionPaneMessages.showErrorPopup("Wszystkie pola muszą być wypełnione");
                return;
            }
            parrentInputPanel.EditAssortent(assortmentJFlied.getText(),
                    fromLocationJFlied.getText(), Float.parseFloat(totalJFlied.getText().replace(',', '.')));
            this.dispose();
            parrentInputPanel.setVisible(true);
        }
    }

    /**
     * Funkcja sprawdza, czy któryś z inputów nie jest pusty
     */
    private boolean isAnyInputBlank(){
        return fromLocationJFlied.getText().isBlank() && assortmentJFlied.getText().isBlank()
                && totalJFlied.getText().isBlank();
    }
}
