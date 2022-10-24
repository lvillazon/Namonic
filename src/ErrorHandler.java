import javax.swing.*;

public class ErrorHandler {
    public static void ModalMessage(String message) {
        JOptionPane.showMessageDialog(null,
                message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}