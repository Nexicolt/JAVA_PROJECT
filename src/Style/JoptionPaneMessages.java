package Style;

import javax.swing.*;

/**
 * Klasa agreguje statyczne metody, które wyświetlają komunikat informacyjny lub z błędem
 */
public class JoptionPaneMessages {

    /**
     * Funkcja wyświetla komunikat błędu, z podanym w pramaterze tekstem
     */
    public static void showErrorPopup(String errorMessage) {
        JOptionPane.showMessageDialog(null,
                errorMessage,
                "Błąd",
                JOptionPane.ERROR_MESSAGE);
    }
    /**
     * Funkcja wyświetla komunikat informacyjny, z podanym w pramaterze tekstem
     */
    public static void showSuccessPopup(String successMessage) {
        JOptionPane.showMessageDialog(null,
                successMessage,
                "OK",
                JOptionPane.INFORMATION_MESSAGE);
    }

}
