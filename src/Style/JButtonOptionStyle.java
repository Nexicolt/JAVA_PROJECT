package Style;

import javax.swing.*;
import java.awt.*;

//Klasa zawiera zdefiniowany wyglad przyciskow funkcyjnych np zapisz/ rezygnuj
//mozna ja wykorzystac w dowolny oknie
public class JButtonOptionStyle extends JButton {
    public JButtonOptionStyle(String name){
        super(name);
        this.setPreferredSize(new Dimension(50,40));
        this.setFont(new Font("Arial", Font.PLAIN, 25));
        this.setBackground(Color.lightGray);
    }
}
