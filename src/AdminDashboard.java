import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

class AdminDashboard {
    AdminDashboard() {
        JFrame f=UI.frame("Wok Your Way — Dashboard");
        JLabel bg = UI.bgWithScroll(f);

        bg.add(UI.sidebar(f,"Overview"));
        bg.add(UI.header("Admin Dashboard",228,6,964));

        String greet=DataStore.loggedInName.isEmpty()?"":"  Welcome, "+DataStore.loggedInName+"!";
        JLabel gl=new JLabel(greet,SwingConstants.LEFT);
        gl.setBounds(240,55,700,18);gl.setFont(new Font("Georgia",Font.ITALIC,12));gl.setForeground(UI.BROWN2);bg.add(gl);

        int cY=76,cW=170,cH=72,cX=228,g=10;
        UI.statCard(bg,"Menu Items",  String.valueOf(DataStore.getTotalMenuItems()),  cX,cY,cW,cH,new Color(0xFF,0xF3,0xB0,230));
        UI.statCard(bg,"Staff",        String.valueOf(DataStore.getActiveStaffCount()),cX+(cW+g),cY,cW,cH,new Color(0xFF,0xEB,0x7A,230));
        UI.statCard(bg,"Reservations", String.valueOf(DataStore.getTotalReservations()),cX+(cW+g)*2,cY,cW,cH,new Color(0xFF,0xD9,0x66,230));
        UI.statCard(bg,"Low Stock",    String.valueOf(DataStore.getLowStockCount()),  cX+(cW+g)*3,cY,cW,cH,
            DataStore.getLowStockCount()>0?new Color(0xFF,0x7F,0x7F,210):new Color(0xD1,0xFF,0xBD,230));
        UI.statCard(bg,"Total Orders", String.valueOf(DataStore.getTotalOrders()),    cX+(cW+g)*4,cY,cW,cH,new Color(0xFF,0xC1,0x07,230));

        JPanel op=UI.content(228,156,592,448); bg.add(op);
        op.add(UI.titleBar("Recent Orders",592,30));
        JTable ot=UI.table(DataStore.orderLogModel); op.add(UI.scroll(ot,8,36,576,400));

        JPanel ap=UI.content(828,156,356,448); bg.add(ap);
        JPanel atb=UI.titleBar("⚠  Low Stock Alerts",356,30);
        atb.setBackground(new Color(0xFF,0x7F,0x7F,200)); ap.add(atb);
        DefaultTableModel lsm=new DefaultTableModel(new String[]{"Ingredient","Qty","Unit","Status"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}};
        for(int i=0;i<DataStore.inventoryModel.getRowCount();i++){
            String st=DataStore.inventoryModel.getValueAt(i,7).toString();
            if(st.equals("Low")||st.equals("Critical"))
                lsm.addRow(new Object[]{DataStore.inventoryModel.getValueAt(i,1),
                    DataStore.inventoryModel.getValueAt(i,3),
                    DataStore.inventoryModel.getValueAt(i,4),st});
        }
        JTable lt=UI.table(lsm);
        lt.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                if(!sel){ String s=lsm.getValueAt(row,3).toString();
                    setBackground(s.equals("Critical")?new Color(0xFF,0x7F,0x7F,180):new Color(0xFF,0xF3,0xB0,180));}
                setHorizontalAlignment(SwingConstants.CENTER); return this;}});
        ap.add(UI.scroll(lt,8,36,340,400));
        if(lsm.getRowCount()==0){JLabel ok=new JLabel("✔  All stock levels OK",SwingConstants.CENTER);
            ok.setBounds(0,150,356,40);ok.setFont(new Font("Georgia",Font.BOLD,14));
            ok.setForeground(new Color(0x2E,0x7D,0x32));ap.add(ok);}

        JPanel rb=UI.content(228,612,964,40); rb.setBackground(new Color(0xFF,0xF3,0xB0,210)); bg.add(rb);
        revLbl(rb,"Total Revenue:  Rs "+DataStore.getTotalRevenue(),8);
        revLbl(rb,"Orders:  "+DataStore.getTotalOrders(),260);
        revLbl(rb,"Staff:  "+DataStore.getActiveStaffCount(),460);
        revLbl(rb,"Confirmed Reservations:  "+DataStore.getConfirmedReservations(),680);

        f.setVisible(true);
    }
    private void revLbl(JPanel p,String t,int x){
        JLabel l=new JLabel(t);l.setBounds(x,0,240,40);
        l.setFont(new Font("Georgia",Font.BOLD,14));l.setForeground(UI.BROWN);p.add(l);}
}
