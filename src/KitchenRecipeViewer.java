import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

class KitchenRecipeViewer extends JFrame {
    static final Color GOLD_DARK = new Color(0xC8,0xA8,0x00);
    static final Color BROWN     = new Color(0x3D,0x2B,0x1F);
    static final Color BROWN2    = new Color(0x6B,0x4C,0x3B);

    KitchenRecipeViewer() {
        setTitle("Wok Your Way — Recipe Viewer");
        setSize(820, 620); setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel bg = UI.bgLabel(); setContentPane(bg);

        JPanel hdr = UI.header("Recipe & Ingredient Guide", 0, 0, 820);
        bg.add(hdr);

        // Left: menu item list
        JPanel left = UI.content(8, 58, 260, 530); bg.add(left);
        left.add(UI.titleBar("Menu Items", 260, 28));

        DefaultTableModel menuTm = new DefaultTableModel(new String[]{"Item"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }};
        int[] menuIds = new int[DataStore.menuModel.getRowCount()];
        for (int i = 0; i < DataStore.menuModel.getRowCount(); i++) {
            menuIds[i] = (int)((Number)DataStore.menuModel.getValueAt(i, 0)).intValue();
            menuTm.addRow(new Object[]{DataStore.menuModel.getValueAt(i, 1)});
        }
        JTable menuTable = UI.table(menuTm);
        left.add(UI.scroll(menuTable, 8, 34, 244, 488));

        // Right: ingredient list for selected item
        JPanel right = UI.content(276, 58, 536, 530); bg.add(right);
        right.add(UI.titleBar("Ingredients per Serving", 536, 28));

        JLabel selLabel = new JLabel("← Select a menu item", SwingConstants.CENTER);
        selLabel.setBounds(8, 34, 520, 24); selLabel.setFont(new Font("Georgia", Font.ITALIC, 13));
        selLabel.setForeground(BROWN2); right.add(selLabel);

        DefaultTableModel ingTm = new DefaultTableModel(
            new String[]{"Ingredient", "Qty per Serving", "Unit"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }};
        JTable ingTable = UI.table(ingTm);
        right.add(UI.scroll(ingTable, 8, 64, 520, 458));

        menuTable.getSelectionModel().addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting()) return;
            int row = menuTable.getSelectedRow(); if (row < 0) return;
            selLabel.setText("Ingredients for: " + menuTm.getValueAt(row, 0));
            ingTm.setRowCount(0);
            java.util.List<Object[]> rows = DataStore.loadRecipe(menuIds[row]);
            if (rows.isEmpty())
                ingTm.addRow(new Object[]{"No ingredients set", "-", "-"});
            else
                for (Object[] r : rows) ingTm.addRow(new Object[]{r[1], r[2], r[3]});
        });

        JButton closeBtn = UI.button("Close", new Color(0xD9,0x53,0x4F)); closeBtn.setForeground(Color.WHITE);
        closeBtn.setBounds(336, 596, 140, 36); bg.add(closeBtn);
        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }
}
