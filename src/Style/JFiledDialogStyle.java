package Style;

import javax.swing.*;
import java.awt.*;

public class JFiledDialogStyle extends JTextField {
    public JFiledDialogStyle(){
        this.setPreferredSize(new Dimension(200,50));
        this.setFont(new Font("Arial", Font.PLAIN, 20));
    }
}
