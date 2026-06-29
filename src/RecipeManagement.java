import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

class RecipeManagement {
    RecipeManagement() {
        JFrame f = UI.frame("Wok Your Way — Recipes");
        JLabel bg = UI.bgWithScroll(f);
        bg.add(UI.sidebar(f, "Recipes"));
        bg.add(UI.header("Recipe & Ingredient Management", 228, 6, 964));

        // ── Left panel: menu item list ────────────────────────────
        JPanel left = UI.content(228, 58, 310, 592); bg.add(left);
        left.add(UI.titleBar("Menu Items", 310, 28));

        DefaultTableModel menuTm = new DefaultTableModel(new String[]{"#","Item Name"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }};
        for (int i = 0; i < DataStore.menuModel.getRowCount(); i++)
            menuTm.addRow(new Object[]{
                DataStore.menuModel.getValueAt(i, 0),
                DataStore.menuModel.getValueAt(i, 1)});
        JTable menuTable = UI.table(menuTm);
        left.add(UI.scroll(menuTable, 8, 34, 294, 546));

        // ── Right panel: recipe for selected item ─────────────────
        JPanel right = UI.content(546, 58, 646, 592); bg.add(right);
        right.add(UI.titleBar("Recipe (Ingredients per Serving)", 646, 28));

        JLabel selLabel = new JLabel("← Select a menu item", SwingConstants.CENTER);
        selLabel.setBounds(8, 34, 630, 24); selLabel.setFont(new Font("Georgia", Font.ITALIC, 13));
        selLabel.setForeground(new Color(0x6B, 0x4C, 0x3B)); right.add(selLabel);

        DefaultTableModel recipeTm = new DefaultTableModel(
            new String[]{"Ingredient", "Qty per Serving", "Unit"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }};
        JTable recipeTable = UI.table(recipeTm);
        right.add(UI.scroll(recipeTable, 8, 64, 630, 410));

        // Buttons row
        int bY = 482, bW = 150, bH = 36, gap = 10;
        JButton addIngBtn  = UI.button("Add Ingredient",  UI.GOLD_NAV); addIngBtn.setBounds(8,     bY, bW, bH);
        JButton remIngBtn  = UI.button("Remove Selected", UI.GOLD_NAV); remIngBtn.setBounds(8+bW+gap, bY, bW, bH);
        JButton saveBtn    = UI.button("Save Recipe",     new Color(0x2E,0x7D,0x32)); saveBtn.setBounds(8+(bW+gap)*2, bY, bW, bH);
        saveBtn.setForeground(Color.WHITE);
        JButton clearBtn   = UI.deleteBtn("Clear Recipe"); clearBtn.setBounds(8+(bW+gap)*3, bY, bW, bH);
        right.add(addIngBtn); right.add(remIngBtn); right.add(saveBtn); right.add(clearBtn);

        // Note label
        JLabel note = new JLabel("<html><i>Quantities are deducted per serving when order marked Ready in kitchen.</i></html>",
            SwingConstants.LEFT);
        note.setBounds(8, 526, 630, 28); note.setFont(new Font("Georgia", Font.PLAIN, 11));
        note.setForeground(new Color(0x6B,0x4C,0x3B)); right.add(note);

        // ── State ──────────────────────────────────────────────────
        int[] selectedMenuId  = {-1};
        String[] selectedName = {""};
        // Each row's inv id stored parallel to table rows
        java.util.List<Integer> rowInvIds = new java.util.ArrayList<>();
        java.util.List<Double>  rowQtys   = new java.util.ArrayList<>();

        Runnable refreshRecipeTable = () -> {
            recipeTm.setRowCount(0); rowInvIds.clear(); rowQtys.clear();
            if (selectedMenuId[0] < 0) return;
            java.util.List<Object[]> rows = DataStore.loadRecipe(selectedMenuId[0]);
            for (Object[] r : rows) {
                rowInvIds.add((Integer) r[0]);
                rowQtys.add((Double) r[2]);
                recipeTm.addRow(new Object[]{r[1], r[2], r[3]});
            }
        };

        menuTable.getSelectionModel().addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting()) return;
            int row = menuTable.getSelectedRow(); if (row < 0) return;
            selectedMenuId[0] = (Integer) menuTm.getValueAt(row, 0);
            selectedName[0]   = menuTm.getValueAt(row, 1).toString();
            selLabel.setText("Recipe for: " + selectedName[0]);
            refreshRecipeTable.run();
        });

        addIngBtn.addActionListener(e -> {
            if (selectedMenuId[0] < 0) {
                JOptionPane.showMessageDialog(f, "Select a menu item first.", "Warning", JOptionPane.WARNING_MESSAGE); return;
            }
            int invRows = DataStore.inventoryModel.getRowCount();
            if (invRows == 0) { JOptionPane.showMessageDialog(f, "No inventory items found. Add inventory items first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
            String[] ingNames = new String[invRows];
            int[]    ingIds   = new int[invRows];
            for (int i = 0; i < invRows; i++) {
                ingIds[i]   = (int)((Number)DataStore.inventoryModel.getValueAt(i, 0)).intValue();
                ingNames[i] = DataStore.inventoryModel.getValueAt(i, 1).toString()
                            + " (" + DataStore.inventoryModel.getValueAt(i, 4) + ")";
            }
            JComboBox<String> ingBox = new JComboBox<>(ingNames);
            ingBox.setFont(new Font("Georgia", Font.PLAIN, 14));
            JTextField qtyField = new JTextField("1"); qtyField.setFont(new Font("Georgia", Font.PLAIN, 14));
            if (JOptionPane.showConfirmDialog(f, new Object[]{"Ingredient:", ingBox, "Qty per serving:", qtyField},
                    "Add Ingredient", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                double qty;
                try { qty = Double.parseDouble(qtyField.getText().trim()); }
                catch (NumberFormatException ex) { JOptionPane.showMessageDialog(f, "Enter a valid number for qty.", "Error", JOptionPane.ERROR_MESSAGE); return; }
                int idx = ingBox.getSelectedIndex();
                int invId = ingIds[idx];
                if (rowInvIds.contains(invId)) { JOptionPane.showMessageDialog(f, "That ingredient is already in the recipe.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
                String unit = DataStore.inventoryModel.getValueAt(idx, 4).toString();
                rowInvIds.add(invId); rowQtys.add(qty);
                recipeTm.addRow(new Object[]{DataStore.inventoryModel.getValueAt(idx, 1), qty, unit});
            }
        });

        remIngBtn.addActionListener(e -> {
            int row = recipeTable.getSelectedRow(); if (row < 0) {
                JOptionPane.showMessageDialog(f, "Select an ingredient row to remove.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            rowInvIds.remove(row); rowQtys.remove(row); recipeTm.removeRow(row);
        });

        saveBtn.addActionListener(e -> {
            if (selectedMenuId[0] < 0) { JOptionPane.showMessageDialog(f, "Select a menu item first.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            int[] ids = rowInvIds.stream().mapToInt(Integer::intValue).toArray();
            double[] qtys = new double[rowQtys.size()];
            for (int i = 0; i < rowQtys.size(); i++) qtys[i] = rowQtys.get(i);
            if (DataStore.saveRecipe(selectedMenuId[0], ids, qtys))
                JOptionPane.showMessageDialog(f, "Recipe saved for " + selectedName[0] + "!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            else
                JOptionPane.showMessageDialog(f, "Save failed.", "Error", JOptionPane.ERROR_MESSAGE);
        });

        clearBtn.addActionListener(e -> {
            if (selectedMenuId[0] < 0) return;
            if (JOptionPane.showConfirmDialog(f, "Clear all ingredients for " + selectedName[0] + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                DataStore.saveRecipe(selectedMenuId[0], new int[0], new double[0]);
                refreshRecipeTable.run();
            }
        });

        f.setVisible(true);
    }
}
