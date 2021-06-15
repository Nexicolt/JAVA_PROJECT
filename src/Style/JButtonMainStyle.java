package Style;

import javax.swing.*;
import java.awt.*;

//Klasa zawiera zdefiniowany wyglad przyciskow menu glownego

public class JButtonMainStyle extends JButton {
    public JButtonMainStyle(String name){
        super(name);
        this.setPreferredSize(new Dimension(300,200));
        this.setFont(new Font("Arial", Font.PLAIN, 30));
        this.setBackground(Color.lightGray);
    }
}
