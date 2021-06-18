package Style;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa reprezentujaca styl przyciskow w menu glownym (duze przyciski). Wszystkie tego typu kontorlki sa jednakowe w aplikacji, wiec zosaatla utworozna klasa,
 * ktora tworzy idnetyczne obiektu i tym samym unikamy powtarzania kodu (w duzym stopniu)
 */

public class JButtonMainStyle extends JButton {
    public JButtonMainStyle(String name){
        super(name);
        this.setPreferredSize(new Dimension(300,200));
        this.setFont(new Font("Arial", Font.PLAIN, 30));
        this.setBackground(Color.lightGray);
    }
}
