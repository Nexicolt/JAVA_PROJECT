package Style;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa reprezentujaca styl przycisku. Wszystkie tego typu kontorlki sa jednakowe w aplikacji, wiec zosaatla utworozna klasa,
 * ktora tworzy idnetyczne obiektu i tym samym unikamy powtarzania kodu (w duzym stopniu)
 */
public class JButtonDialogStyle extends JButton {
    public JButtonDialogStyle(String name){
        super(name);
        this.setPreferredSize(new Dimension(20,40));
        this.setFont(new Font("Arial", Font.PLAIN, 25));
        this.setBackground(Color.lightGray);
    }
}
