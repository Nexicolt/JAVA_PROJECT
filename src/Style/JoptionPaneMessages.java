package Style;

import javax.swing.*;

public class JoptionPaneMessages {

    public static void showErrorPopup(String errorMessage) {
        JOptionPane.showMessageDialog(null,
                errorMessage,
                "Błąd",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccessPopup(String successMessage) {
        JOptionPane.showMessageDialog(null,
                successMessage,
                "OK",
                JOptionPane.INFORMATION_MESSAGE);
    }

}
