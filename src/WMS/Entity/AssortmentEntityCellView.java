package WMS.Entity;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa przekazywana do listy, wyswietlajacej asortymentu uwzglednione w wydaniu
 * Posiada nadpisana funckje wyswietlajaca rekord w JList, by wysweitlić dane obiektu, wedlu zalozen programisty
 */
public class AssortmentEntityCellView extends JLabel implements ListCellRenderer {
    //Kolor nanoszony na wiersz, w momencie jak zostanie zaznaczony
    private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

    public AssortmentEntityCellView() {
        setOpaque(true);
        setIconTextGap(12);
    }

    /**
     * Zaimplementowana z intrfejsu funkcja, Jest ona wywolywana przy wyswietlaniu rekordow w JList
     * To ona zwraca String'a, wyswietlanego w pojedynczym rekordzie. Nadpisana, by wyswietlala nazwe asortymntu, lokalziacje oraz ilosć
     */
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        AssortmentEntity entry = (AssortmentEntity) value;
        setText("PRODUKT: " + entry.getName() + "     LOKALIZACJA: " + entry.getLocalization() + "     ILOsĆ: " + entry.getCount());
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
