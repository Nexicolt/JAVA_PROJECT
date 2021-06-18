package Style;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa reprezentująca styl przycisku. Wszystkie tego typu kontorlki sa jednakowe w aplikacji, więc zosaatła utworozna klasa,
 * która tworzy idnetyczne obiektu i tym samym unikamy powtarzania kodu (w dużym stopniu)
 */
public class JButtonDialogStyle extends JButton {
    public JButtonDialogStyle(String name){
        super(name);
        this.setPreferredSize(new Dimension(20,40));
        this.setFont(new Font("Arial", Font.PLAIN, 25));
        this.setBackground(Color.lightGray);
    }
}
