package WMS.VIews;

import Style.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//// TODO: 09.06.2021 opisz Ci jutro
public class InputView_AddAssortmentFrame extends JDialog implements ActionListener {
    private JFiledDialogStyle fromLocationJFlied, assortmentJFlied, totalJFlied;
    private JButtonDialogStyle saveButton, closeButton;

    private final InputView parrentInputPanel;

    /**
     * Konstruktor wyświetla okno i przechywuje referencję do okna rodzica
     */
    public InputView_AddAssortmentFrame(InputView inputPanelHandler){
        parrentInputPanel = inputPanelHandler;
        init();
    }
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
            parrentInputPanel.setVisible(true);
            this.dispose();
        }
        //Dla przycisku zapisz -> wywołaj metodę dodania newgo asortyemnu, na obiekcie JPanelu rodzica
        else if(source.equals(saveButton)){
            //Sprawdź inputy
            if(isAnyInputBlank()){
                JoptionPaneMessages.showErrorPopup("Wszystkie pola muszą być wypełnione");
                return;
            }
            parrentInputPanel.AddNewAssortment(assortmentJFlied.getText(),
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
