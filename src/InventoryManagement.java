import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

class InventoryManagement {
    InventoryManagement() {
        JFrame f=UI.frame("Wok Your Way — Inventory");
        JLabel bg = UI.bgWithScroll(f);

        bg.add(UI.sidebar(f,"Inventory"));
        bg.add(UI.header("Inventory Management",228,6,964));

        JPanel c=UI.content(228,58,964,592); bg.add(c);
        c.add(UI.titleBar("Stock Items",964,28));

        JTable t=UI.table(DataStore.inventoryModel);
        t.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable tb,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(tb,v,sel,foc,row,col);
                if(!sel&&row<DataStore.inventoryModel.getRowCount()){
                    String st=DataStore.inventoryModel.getValueAt(row,7).toString();
                    if(st.equals("Critical")) setBackground(new Color(0xFF,0x7F,0x7F,180));
                    else if(st.equals("Low")) setBackground(new Color(0xFF,0xF3,0xB0,180));
                    else setBackground(new Color(0xD1,0xFF,0xBD,150));}
                setHorizontalAlignment(SwingConstants.CENTER);setBorder(BorderFactory.createEmptyBorder(0,6,0,6));return this;}});
        c.add(UI.scroll(t,8,34,948,502));

        String[] units={"kg","litre","pcs","bottle","box","packet","g","ml"};
        int bY=544,bW=178,bH=36,bX=8,bg2=10;
        JButton addB=UI.button("Add Item",UI.GOLD_NAV);addB.setBounds(bX,bY,bW,bH);
        JButton updB=UI.button("Update Qty",UI.GOLD_NAV);updB.setBounds(bX+(bW+bg2),bY,bW,bH);
        JButton edB=UI.button("Edit Item",UI.GOLD_NAV);edB.setBounds(bX+(bW+bg2)*2,bY,bW,bH);
        JButton filB=UI.button("Show Low Stock",UI.GOLD_NAV);filB.setBounds(bX+(bW+bg2)*3,bY,bW,bH);
        JButton delB=UI.deleteBtn("Delete Item");delB.setBounds(bX+(bW+bg2)*4,bY,bW,bH);
        c.add(addB);c.add(updB);c.add(edB);c.add(filB);c.add(delB);

        addB.addActionListener(e->{
            JTextField nm=tf();JTextField qty=tf();JComboBox<String> ub=new JComboBox<>(units);ub.setFont(new Font("Georgia",Font.PLAIN,14));
            if(JOptionPane.showConfirmDialog(f,new Object[]{"Ingredient Name:",nm,"Quantity:",qty,"Unit:",ub},"Add Item",JOptionPane.OK_CANCEL_OPTION)==0){
                int q;try{q=Integer.parseInt(qty.getText().trim());}catch(NumberFormatException ex){JOptionPane.showMessageDialog(f,"Quantity must be a number.","Error",JOptionPane.ERROR_MESSAGE);return;}
                if(DataStore.addInventoryItem(nm.getText().trim(),q,ub.getSelectedItem().toString())) t.setModel(DataStore.inventoryModel);
                else JOptionPane.showMessageDialog(f,"Add failed.","Error",JOptionPane.ERROR_MESSAGE);}
        });
        updB.addActionListener(e->{
            int row=t.getSelectedRow();if(row<0){JOptionPane.showMessageDialog(f,"Select an item.","Warning",JOptionPane.WARNING_MESSAGE);return;}
            String nm=DataStore.inventoryModel.getValueAt(row,1).toString();
            String cur=DataStore.inventoryModel.getValueAt(row,3).toString().replace(".0","");
            JTextField qty=tf(cur);
            if(JOptionPane.showConfirmDialog(f,new Object[]{"New quantity for "+nm+":",qty},"Update Qty",JOptionPane.OK_CANCEL_OPTION)==0){
                int q;try{q=Integer.parseInt(qty.getText().trim());}catch(NumberFormatException ex){JOptionPane.showMessageDialog(f,"Number required.","Error",JOptionPane.ERROR_MESSAGE);return;}
                if(DataStore.updateInventoryQty(row,q)) t.setModel(DataStore.inventoryModel);}
        });
        edB.addActionListener(e->{
            int row=t.getSelectedRow();if(row<0){JOptionPane.showMessageDialog(f,"Select an item.","Warning",JOptionPane.WARNING_MESSAGE);return;}
            JTextField nm=tf(DataStore.inventoryModel.getValueAt(row,1).toString());
            JComboBox<String> ub=new JComboBox<>(units);ub.setFont(new Font("Georgia",Font.PLAIN,14));
            ub.setSelectedItem(DataStore.inventoryModel.getValueAt(row,4).toString());
            if(JOptionPane.showConfirmDialog(f,new Object[]{"Name:",nm,"Unit:",ub},"Edit Item",JOptionPane.OK_CANCEL_OPTION)==0){
                DataStore.inventoryModel.setValueAt(nm.getText().trim(),row,1);
                DataStore.inventoryModel.setValueAt(ub.getSelectedItem().toString(),row,4);
                t.repaint();
                JOptionPane.showMessageDialog(f,"Item updated!","Success",JOptionPane.INFORMATION_MESSAGE);
            }
        });
        final boolean[] showAll={true};
        filB.addActionListener(e->{
            if(showAll[0]){
                TableRowSorter<DefaultTableModel> sorter=new TableRowSorter<>(DataStore.inventoryModel);
                sorter.setRowFilter(RowFilter.regexFilter("Low|Critical",7));
                t.setRowSorter(sorter); filB.setText("Show All"); showAll[0]=false;
            }else{ t.setRowSorter(null); filB.setText("Show Low Stock"); showAll[0]=true; }
        });
        delB.addActionListener(e->{
            int row=t.getSelectedRow();if(row<0){JOptionPane.showMessageDialog(f,"Select an item.","Warning",JOptionPane.WARNING_MESSAGE);return;}
            int mr=t.getRowSorter()!=null?t.convertRowIndexToModel(row):row;
            String nm=DataStore.inventoryModel.getValueAt(mr,1).toString();
            if(JOptionPane.showConfirmDialog(f,"Delete "+nm+"?","Confirm",JOptionPane.YES_NO_OPTION)==0)
                if(DataStore.deleteInventoryItem(mr)){ t.setRowSorter(null); t.setModel(DataStore.inventoryModel); showAll[0]=true; filB.setText("Show Low Stock");}
        });
        f.setVisible(true);
    }
    private JTextField tf(){return tf("");}
    private JTextField tf(String v){JTextField x=new JTextField(v);x.setFont(new Font("Georgia",Font.PLAIN,14));return x;}
}
