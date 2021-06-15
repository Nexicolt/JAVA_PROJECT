package Style;

import javax.swing.*;
import java.awt.*;

public class JButtonDialogStyle extends JButton {
    public JButtonDialogStyle(String name){
        super(name);
        this.setPreferredSize(new Dimension(20,40));
        this.setFont(new Font("Arial", Font.PLAIN, 25));
        this.setBackground(Color.lightGray);
    }
}
