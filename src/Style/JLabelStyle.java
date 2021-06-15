package Style;

import javax.swing.*;
import java.awt.*;

//Klasa zawiera zdefiniowany wyglad Labelow - wykorzystywana np w JPanelTransfer
public class JLabelStyle extends JLabel {
    public JLabelStyle(String name){
        super(name);
        setFont(new Font("Arial", Font.PLAIN, 30));
    }
}
