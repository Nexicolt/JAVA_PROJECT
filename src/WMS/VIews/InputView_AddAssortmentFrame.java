package WMS.VIews;

import Style.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * Klasa reprezentuje okno, ktore wyskakuje po wcisnieciu przycisku dodawania nowego asrotymentu, na tworzonym przyjeciu
 */
public class InputView_AddAssortmentFrame extends JDialog implements ActionListener {
    private JFiledDialogStyle toLocationJFlied, assortmentJFlied, totalJFlied;
    private JButtonDialogStyle saveButton, closeButton;

    private final InputView parrentInputPanel;

    /**
     * Konstruktor wyswietla okno i przechywuje referencje do okna rodzica
     */
    public InputView_AddAssortmentFrame(InputView inputPanelHandler){
        parrentInputPanel = inputPanelHandler;
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

        JLabelStyle fromLocationLabel = new JLabelStyle("Do lokalizacji");
        verticalJPanelBox.add(fromLocationLabel);

        toLocationJFlied = new JFiledDialogStyle();
        toLocationJFlied.addActionListener(this);
        verticalJPanelBox.add(toLocationJFlied);

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
            parrentInputPanel.setVisible(true);
        }
        //Dla przycisku zapisz -> wywolaj metode dodania newgo asortymentnu, na obiekcie JPanelu rodzica
        else if(source.equals(saveButton)){
            //Sprawdz inputy
            if(isAnyInputBlank()){
                JoptionPaneMessages.showErrorPopup("Wszystkie pola musza być wypelnione");
                return;
            }
            parrentInputPanel.AddNewAssortment(assortmentJFlied.getText(),toLocationJFlied.getText()
                    , Float.parseFloat(totalJFlied.getText().replace(',', '.')));
            this.dispose();
            parrentInputPanel.setVisible(true);
        }else{
            saveButton.doClick();
        }
    }
    /**
     * Funkcja sprawdza, czy ktorys z inputow nie jest pusty
     */
    private boolean isAnyInputBlank(){
        return assortmentJFlied.getText().isBlank() && totalJFlied.getText().isBlank();
    }
}
