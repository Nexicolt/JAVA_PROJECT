package Style;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa reprezentujaca styl inputow w transferach. Wszystkie tego typu kontorlki sa jednakowe w aplikacji, wiec zosaatla utworozna klasa,
 * ktora tworzy idnetyczne obiektu i tym samym unikamy powtarzania kodu (w duzym stopniu)
 */
public class JFliedTextStyle extends JTextField {
    public  JFliedTextStyle() {
        this.setPreferredSize(new Dimension(500,50));
        this.setFont(new Font("Arial", Font.PLAIN, 30));
    }
}
