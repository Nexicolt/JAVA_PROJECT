package Style;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa reprezentująca inputu, do wpisywania tekstu przycisku. Wszystkie tego typu kontorlki sa jednakowe w aplikacji, więc zosaatła utworozna klasa,
 * która tworzy idnetyczne obiektu i tym samym unikamy powtarzania kodu (w dużym stopniu)
 */
public class JFiledDialogStyle extends JTextField {
    public JFiledDialogStyle(){
        this.setPreferredSize(new Dimension(200,50));
        this.setFont(new Font("Arial", Font.PLAIN, 20));
    }
}
