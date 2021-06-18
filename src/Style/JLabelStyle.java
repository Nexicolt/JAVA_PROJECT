package Style;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa reprezentująca styl dla etykiet. Wszystkie tego typu kontorlki sa jednakowe w aplikacji, więc zosaatła utworozna klasa,
 * która tworzy idnetyczne obiektu i tym samym unikamy powtarzania kodu (w dużym stopniu)
 */
public class JLabelStyle extends JLabel {
    public JLabelStyle(String name){
        super(name);
        setFont(new Font("Arial", Font.PLAIN, 30));
    }
}
