package Style;

import javax.swing.*;
import java.awt.*;

//Klasa zawiera zdefiniowany wyglad JFlied wykorzystywana np w JPanelTransfer
public class JFliedTextStyle extends JTextField {
    public  JFliedTextStyle() {
        this.setPreferredSize(new Dimension(500,50));
        this.setFont(new Font("Arial", Font.PLAIN, 30));
    }
}
