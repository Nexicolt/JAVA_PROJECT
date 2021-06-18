package Style;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa reprezentująca styl przycisków w menu głównym (duże przyciski). Wszystkie tego typu kontorlki sa jednakowe w aplikacji, więc zosaatła utworozna klasa,
 * która tworzy idnetyczne obiektu i tym samym unikamy powtarzania kodu (w dużym stopniu)
 */

public class JButtonMainStyle extends JButton {
    public JButtonMainStyle(String name){
        super(name);
        this.setPreferredSize(new Dimension(300,200));
        this.setFont(new Font("Arial", Font.PLAIN, 30));
        this.setBackground(Color.lightGray);
    }
}
