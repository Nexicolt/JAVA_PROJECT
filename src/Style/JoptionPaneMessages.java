package Style;

import javax.swing.*;

/**
 * Klasa agreguje statyczne metody, ktore wyswietlaja komunikat informacyjny lub z bledem
 */
public class JoptionPaneMessages {

    /**
     * Funkcja wyswietla komunikat bledu, z podanym w pramaterze tekstem
     */
    public static void showErrorPopup(String errorMessage) {
        JOptionPane.showMessageDialog(null,
                errorMessage,
                "Blad",
                JOptionPane.ERROR_MESSAGE);
    }
    /**
     * Funkcja wyswietla komunikat informacyjny, z podanym w pramaterze tekstem
     */
    public static void showSuccessPopup(String successMessage) {
        JOptionPane.showMessageDialog(null,
                successMessage,
                "OK",
                JOptionPane.INFORMATION_MESSAGE);
    }

}
