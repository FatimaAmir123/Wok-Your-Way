import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DataStore.loadAll();
        SwingUtilities.invokeLater(() -> new LoginSignUp());
    }
}
