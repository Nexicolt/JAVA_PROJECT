package WMS.Entity;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa przekazywana do listy, wyświetlającej asortymentu uwzględnione w wydaniu
 * Posiada nadpisana funckję wyświetlającą rekord w JList, by wyśweitlić dane obiektu, wedlu założeń programisty
 */
public class AssortmentEntityCellView extends JLabel implements ListCellRenderer {
    //Kolor nanoszony na wiersz, w momencie jak zostanie zaznaczony
    private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

    public AssortmentEntityCellView() {
        setOpaque(true);
        setIconTextGap(12);
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        AssortmentEntity entry = (AssortmentEntity) value;
        setText("PRODUKT: " + entry.getName() + "     LOKALIZACJA: " + entry.getLocalization() + "     ILOŚĆ: " + entry.getCount());
        if (isSelected) {
            setBackground(HIGHLIGHT_COLOR);
            setForeground(Color.white);
        } else {
            setBackground(Color.white);
            setForeground(Color.black);
        }
        return this;
    }
}
