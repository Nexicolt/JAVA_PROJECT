package WMS.VIews;

import Style.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Klasa reprezentuje okno, które wyskakuje po wciśnieciu przycisku dodawania nowego asrotymentu, na tworzonym wydaniu
 */
public class OutputView_AddAssortmentFrame extends JDialog implements ActionListener {
    private JFiledDialogStyle fromLocationJFlied, assortmentJFlied, totalJFlied;
    private JButtonDialogStyle saveButton, closeButton;

    private final OutputView parrentOutputPanel;

    /**
     * Konstruktor wyświetla okno i przechywuje referencję do okna rodzica
     */
    public OutputView_AddAssortmentFrame(OutputView outputPanelHandler){
        parrentOutputPanel = outputPanelHandler;
        init();
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
        JLabelStyle fromLocationLabel = new JLabelStyle("Z Lokalizacji");
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
            parrentOutputPanel.setVisible(true);
            this.dispose();
        }
        //Dla przycisku zapisz -> wywołaj metodę dodania newgo asortyemnu, na obiekcie JPanelu rodzica
        else if(source.equals(saveButton)){
            //Sprawdź inputy
            if(isAnyInputBlank()){
                JoptionPaneMessages.showErrorPopup("Wszystkie pola muszą być wypełnione");
                return;
            }
            parrentOutputPanel.AddNewAssortment(assortmentJFlied.getText(),
                                        fromLocationJFlied.getText(), Float.parseFloat(totalJFlied.getText().replace(',', '.')));
            this.dispose();
            parrentOutputPanel.setVisible(true);
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
