package Style;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa reprezentujaca styl dla etykiet. Wszystkie tego typu kontorlki sa jednakowe w aplikacji, wiec zosaatla utworozna klasa,
 * ktora tworzy idnetyczne obiektu i tym samym unikamy powtarzania kodu (w duzym stopniu)
 */
public class JLabelStyle extends JLabel {
    public JLabelStyle(String name){
        super(name);
        setFont(new Font("Arial", Font.PLAIN, 30));
    }
}
