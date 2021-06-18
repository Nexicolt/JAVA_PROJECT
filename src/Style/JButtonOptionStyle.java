package Style;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa reprezentująca styl przycisków funkcyjnych (zapisz/Anuluj). Wszystkie tego typu kontorlki sa jednakowe w aplikacji, więc zosaatła utworozna klasa,
 * która tworzy idnetyczne obiektu i tym samym unikamy powtarzania kodu (w dużym stopniu)
 */
public class JButtonOptionStyle extends JButton {
    public JButtonOptionStyle(String name){
        super(name);
        this.setPreferredSize(new Dimension(50,40));
        this.setFont(new Font("Arial", Font.PLAIN, 25));
        this.setBackground(Color.lightGray);
    }
}
