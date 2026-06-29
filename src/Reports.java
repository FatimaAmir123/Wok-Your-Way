import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

class Reports {
    Reports() {
        JFrame f=UI.frame("Wok Your Way — Reports");
        JLabel bg = UI.bgWithScroll(f);

        bg.add(UI.sidebar(f,"Reports"));
        bg.add(UI.header("Reports & Analytics",228,6,964));

        JPanel sb=UI.content(228,58,964,40); sb.setBackground(new Color(0xFF,0xF3,0xB0,190)); bg.add(sb);
        sLbl(sb,"Total Orders: "+DataStore.getTotalOrders(),8);
        sLbl(sb,"Revenue: Rs "+DataStore.getTotalRevenue(),248);
        sLbl(sb,"Staff: "+DataStore.getActiveStaffCount(),480);
        sLbl(sb,"Low Stock: "+DataStore.getLowStockCount(),680);

        JPanel c=UI.content(228,104,964,548); bg.add(c);
        int tw=220,th=36,tg=8;
        JButton[] tabs=new JButton[4];
        String[] tabNames={"Order Report","Staff Report","Inventory Report","Reservation Report"};
        for(int i=0;i<4;i++){tabs[i]=UI.button(tabNames[i],UI.GOLD_NAV);tabs[i].setBounds(10+i*(tw+tg),10,tw,th);}
        for(JButton tb:tabs) c.add(tb);
        tabs[0].setBackground(UI.GOLD_DARK);

        JLabel activeTitle=new JLabel("  Order Report",SwingConstants.CENTER);
        activeTitle.setBounds(0,50,964,28);activeTitle.setFont(new Font("Georgia",Font.BOLD,13));
        activeTitle.setForeground(UI.BROWN);activeTitle.setOpaque(true);
        activeTitle.setBackground(new Color(0xFF,0xD7,0x00,180));c.add(activeTitle);

        JScrollPane[] panes=new JScrollPane[4];
        panes[0]=rPane(DataStore.orderLogModel,null);
        panes[1]=rPane(buildStaffReport(),3);
        panes[2]=rPane(buildInvReport(),6);
        panes[3]=rPane(buildResReport(),7);
        for(JScrollPane p:panes){p.setBounds(10,86,944,452);p.setVisible(false);c.add(p);}
        panes[0].setVisible(true);

        for(int i=0;i<4;i++){final int idx=i;tabs[i].addActionListener(e->{
            for(JScrollPane p:panes) p.setVisible(false);
            for(JButton tb:tabs) tb.setBackground(UI.GOLD_NAV);
            panes[idx].setVisible(true); tabs[idx].setBackground(UI.GOLD_DARK);
            activeTitle.setText("  "+tabNames[idx]);});}
        f.setVisible(true);
    }
    private DefaultTableModel buildStaffReport(){
        DefaultTableModel m=new DefaultTableModel(new String[]{"#","Full Name","Username","Role","Phone"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
        for(int i=0;i<DataStore.staffModel.getRowCount();i++)
            m.addRow(new Object[]{DataStore.staffModel.getValueAt(i,0),DataStore.staffModel.getValueAt(i,1),DataStore.staffModel.getValueAt(i,2),DataStore.staffModel.getValueAt(i,3),DataStore.staffModel.getValueAt(i,4)});
        return m;
    }
    private DefaultTableModel buildInvReport(){
        DefaultTableModel m=new DefaultTableModel(new String[]{"Item Name","Category","Qty","Unit","Min Level","Supplier","Status"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
        for(int i=0;i<DataStore.inventoryModel.getRowCount();i++)
            m.addRow(new Object[]{DataStore.inventoryModel.getValueAt(i,1),DataStore.inventoryModel.getValueAt(i,2),DataStore.inventoryModel.getValueAt(i,3),DataStore.inventoryModel.getValueAt(i,4),DataStore.inventoryModel.getValueAt(i,5),DataStore.inventoryModel.getValueAt(i,6),DataStore.inventoryModel.getValueAt(i,7)});
        return m;
    }
    private DefaultTableModel buildResReport(){
        DefaultTableModel m=new DefaultTableModel(new String[]{"#","Customer","Phone","Table","Date","Time","Guests","Status"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
        for(int i=0;i<DataStore.reservationModel.getRowCount();i++)
            m.addRow(new Object[]{DataStore.reservationModel.getValueAt(i,0),DataStore.reservationModel.getValueAt(i,1),DataStore.reservationModel.getValueAt(i,2),DataStore.reservationModel.getValueAt(i,3),DataStore.reservationModel.getValueAt(i,4),DataStore.reservationModel.getValueAt(i,5),DataStore.reservationModel.getValueAt(i,6),DataStore.reservationModel.getValueAt(i,7)});
        return m;
    }
    private JScrollPane rPane(DefaultTableModel m,Integer statusCol){
        JTable t=UI.table(m);
        if(statusCol!=null){final int sc=statusCol;
            t.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
                @Override public Component getTableCellRendererComponent(JTable tb,Object v,boolean sel,boolean foc,int row,int col){
                    super.getTableCellRendererComponent(tb,v,sel,foc,row,col);
                    if(!sel&&row<m.getRowCount()){String st=m.getValueAt(row,sc).toString();
                        if(st.equals("Critical")||st.equals("Cancelled")) setBackground(new Color(0xFF,0x7F,0x7F,180));
                        else if(st.equals("Low")||st.equals("Pending")) setBackground(new Color(0xFF,0xF3,0xB0,180));
                        else setBackground(new Color(0xD1,0xFF,0xBD,180));}
                    setHorizontalAlignment(SwingConstants.CENTER);return this;}});}
        JScrollPane p=new JScrollPane(t);p.setOpaque(false);p.getViewport().setOpaque(false);
        p.setBorder(BorderFactory.createLineBorder(UI.GOLD_DARK,1));return p;
    }
    private void sLbl(JPanel p,String t,int x){JLabel l=new JLabel(t);l.setBounds(x,0,240,50);l.setFont(new Font("Georgia",Font.BOLD,14));l.setForeground(UI.BROWN);p.add(l);}
}
