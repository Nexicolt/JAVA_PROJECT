package Style;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa reprezentująca styl inputów w transferach. Wszystkie tego typu kontorlki sa jednakowe w aplikacji, więc zosaatła utworozna klasa,
 * która tworzy idnetyczne obiektu i tym samym unikamy powtarzania kodu (w dużym stopniu)
 */
public class JFliedTextStyle extends JTextField {
    public  JFliedTextStyle() {
        this.setPreferredSize(new Dimension(500,50));
        this.setFont(new Font("Arial", Font.PLAIN, 30));
    }
}
