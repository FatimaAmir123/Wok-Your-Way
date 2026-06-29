import java.awt.*;
import javax.swing.*;

class MenuManagement {
    MenuManagement() {
        JFrame f=UI.frame("Wok Your Way — Menu Management");
        JLabel bg = UI.bgWithScroll(f);

        bg.add(UI.sidebar(f,"Menu"));
        bg.add(UI.header("Menu Management",228,6,964));

        JLabel totalL=UI.statCard(bg,"Total Items","0",228,56,222,64,new Color(0xFF,0xF3,0xB0,230));
        JLabel startsL=UI.statCard(bg,"Starters/Apps","0",460,56,222,64,new Color(0xFF,0xEB,0x7A,230));
        JLabel mainL=UI.statCard(bg,"Main Course","0",692,56,222,64,new Color(0xFF,0xD9,0x66,230));
        JLabel drinksL=UI.statCard(bg,"Beverages","0",924,56,222,64,new Color(0xFF,0xC1,0x07,230));

        JPanel c=UI.content(228,128,964,488); bg.add(c);
        c.add(UI.titleBar("Menu Items",964,28));
        JTable t=UI.table(DataStore.menuModel); c.add(UI.scroll(t,8,34,948,404));

        int bY=446,bW=294,bH=36;
        JButton addB=UI.button("Add Item",UI.GOLD_NAV); addB.setBounds(8,bY,bW,bH);
        JButton edB=UI.button("Edit Item",UI.GOLD_NAV);  edB.setBounds(334,bY,bW,bH);
        JButton delB=UI.deleteBtn("Delete Item");        delB.setBounds(660,bY,bW,bH);
        c.add(addB);c.add(edB);c.add(delB);

        Runnable refresh=()->{
            t.setModel(DataStore.menuModel);
            int tot=0,sta=0,main=0,drk=0;
            for(int i=0;i<DataStore.menuModel.getRowCount();i++){tot++;
                String cat=DataStore.menuModel.getValueAt(i,2).toString().toLowerCase();
                if(cat.contains("starter")||cat.contains("app")) sta++;
                else if(cat.contains("main")) main++;
                else if(cat.contains("drink")||cat.contains("bev")||cat.contains("juice")) drk++;
            }
            totalL.setText(String.valueOf(tot));startsL.setText(String.valueOf(sta));
            mainL.setText(String.valueOf(main));drinksL.setText(String.valueOf(drk));
        };
        refresh.run();

        addB.addActionListener(e->{
            JTextField nm=tf();JTextField pr=tf();JComboBox<String> cat=cb(DataStore.getCategoryNames());
            if(dialog(f,new Object[]{"Item Name:",nm,"Category:",cat,"Price (Rs):",pr},"Add Menu Item")){
                int px; try{px=Integer.parseInt(pr.getText().trim());}catch(NumberFormatException ex){err(f,"Price must be a number.");return;}
                if(nm.getText().trim().isEmpty()){err(f,"Name required.");return;}
                if(DataStore.addMenuItem(nm.getText().trim(),cat.getSelectedItem().toString(),px)) refresh.run();
                else err(f,"Failed. Name may already exist.");
            }
        });
        edB.addActionListener(e->{
            int row=t.getSelectedRow(); if(row<0){warn(f,"Select an item to edit.");return;}
            JTextField nm=tf(DataStore.menuModel.getValueAt(row,1).toString());
            JTextField pr=tf(DataStore.menuModel.getValueAt(row,3).toString().replace("Rs ","").trim());
            JComboBox<String> cat=cb(DataStore.getCategoryNames());
            cat.setSelectedItem(DataStore.menuModel.getValueAt(row,2).toString());
            if(dialog(f,new Object[]{"Item Name:",nm,"Category:",cat,"Price (Rs):",pr},"Edit Menu Item")){
                int px; try{px=Integer.parseInt(pr.getText().trim());}catch(NumberFormatException ex){err(f,"Price must be a number.");return;}
                if(DataStore.updateMenuItem(row,nm.getText().trim(),cat.getSelectedItem().toString(),px)) refresh.run();
                else err(f,"Update failed.");
            }
        });
        delB.addActionListener(e->{
            int row=t.getSelectedRow(); if(row<0){warn(f,"Select an item to delete.");return;}
            String nm=DataStore.menuModel.getValueAt(row,1).toString();
            if(JOptionPane.showConfirmDialog(f,"Delete \""+nm+"\"?","Confirm",JOptionPane.YES_NO_OPTION)==0)
                if(DataStore.deleteMenuItem(row)) refresh.run(); else err(f,"Delete failed.");
        });
        f.setVisible(true);
    }
    private JTextField tf(){return tf("");}
    private JTextField tf(String v){JTextField x=new JTextField(v);x.setFont(new Font("Georgia",Font.PLAIN,14));return x;}
    private JComboBox<String> cb(String[] items){JComboBox<String> x=new JComboBox<>(items);x.setFont(new Font("Georgia",Font.PLAIN,14));return x;}
    private boolean dialog(JFrame f,Object[] msg,String title){return JOptionPane.showConfirmDialog(f,msg,title,JOptionPane.OK_CANCEL_OPTION)==0;}
    private void err(JFrame f,String m){JOptionPane.showMessageDialog(f,m,"Error",JOptionPane.ERROR_MESSAGE);}
    private void warn(JFrame f,String m){JOptionPane.showMessageDialog(f,m,"Warning",JOptionPane.WARNING_MESSAGE);}
}
