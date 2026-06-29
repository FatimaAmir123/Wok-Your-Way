import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

class ReservationManagement {
    ReservationManagement() {
        JFrame f=UI.frame("Wok Your Way — Reservations");
        JLabel bg = UI.bgWithScroll(f);

        bg.add(UI.sidebar(f,"Reservations"));
        bg.add(UI.header("Reservation Management",228,6,964));

        JPanel gp=UI.content(228,58,268,350); bg.add(gp);
        gp.add(UI.titleBar("Table Map",268,28));
        addLegend(gp,10,36,new Color(0xD1,0xFF,0xBD),"Available");
        addLegend(gp,100,36,new Color(0xFF,0xF3,0xB0),"Reserved");
        addLegend(gp,188,36,new Color(0xFF,0x7F,0x7F),"Occupied");
        JButton[] tblBtns=new JButton[12];
        for(int i=0;i<12;i++){
            int col=i%3,row=i/3;
            JButton btn=new JButton("T"+(i+1));
            btn.setBounds(8+col*84,60+row*62,72,52);
            btn.setFont(new Font("Georgia",Font.BOLD,15));
            btn.setBackground(new Color(0xD1,0xFF,0xBD)); btn.setForeground(UI.BROWN);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(UI.GOLD_DARK));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final int tno=i+1; btn.addActionListener(e->showTableInfo(f,tno)); gp.add(btn);
            tblBtns[i]=btn;
        }
        updateTableColors(tblBtns);

        JPanel lp=UI.content(504,58,680,590); bg.add(lp);
        lp.add(UI.titleBar("All Reservations",680,28));
        JTable rt=UI.table(DataStore.reservationModel);
        rt.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                if(!sel&&row<DataStore.reservationModel.getRowCount()){
                    String st=DataStore.reservationModel.getValueAt(row,7).toString();
                    if(st.equals("Confirmed")) setBackground(new Color(0xD1,0xFF,0xBD,180));
                    else if(st.equals("Pending")) setBackground(new Color(0xFF,0xF3,0xB0,180));
                    else setBackground(new Color(0xFF,0x7F,0x7F,160));}
                setHorizontalAlignment(SwingConstants.CENTER);return this;}});
        lp.add(UI.scroll(rt,8,34,664,504));

        int bY=546,bW=120,bH=36,bX=8,bg2=8;
        JButton addB=UI.button("Add",UI.GOLD_NAV);addB.setBounds(bX,bY,bW,bH);
        JButton updB=UI.button("Update",new Color(0xFF,0xEB,0x7A));updB.setBounds(bX+(bW+bg2),bY,bW,bH);
        JButton conB=UI.button("Confirm",new Color(0xD1,0xFF,0xBD));conB.setBounds(bX+(bW+bg2)*2,bY,bW,bH);
        JButton canB=UI.button("Cancel",new Color(0xFF,0xF3,0xB0));canB.setBounds(bX+(bW+bg2)*3,bY,bW,bH);
        JButton delB=UI.deleteBtn("Delete");delB.setBounds(bX+(bW+bg2)*4,bY,bW,bH);
        lp.add(addB);lp.add(updB);lp.add(conB);lp.add(canB);lp.add(delB);

        addB.addActionListener(e->{
            JTextField cu=tf(),ph=tf(DataStore.randomPhone()),tbl=tf("T1"),dt=tf("2026-06-01 19:00:00"),gs=tf("2");
            JComboBox<String> stBox=new JComboBox<>(new String[]{"Pending","Confirmed"});stBox.setFont(new Font("Georgia",Font.PLAIN,14));
            if(JOptionPane.showConfirmDialog(f,new Object[]{"Customer Full Name:",cu,"Phone:",ph,"Table (e.g. T1):",tbl,"Date & Time (YYYY-MM-DD HH:MM:SS):",dt,"Guests:",gs,"Status:",stBox},"Add Reservation",JOptionPane.OK_CANCEL_OPTION)==0){
                int gn;try{gn=Integer.parseInt(gs.getText().trim());}catch(NumberFormatException ex){gn=1;}
                if(DataStore.addReservation(cu.getText().trim(),ph.getText().trim(),tbl.getText().trim(),dt.getText().trim(),gn,stBox.getSelectedItem().toString()))
                    {rt.setModel(DataStore.reservationModel); updateTableColors(tblBtns);}
                else JOptionPane.showMessageDialog(f,"Failed.","Error",JOptionPane.ERROR_MESSAGE);
            }
        });
        updB.addActionListener(e->{
            int row=rt.getSelectedRow();if(row<0){warn(f,"Select a reservation to update.");return;}
            JTextField cu=tf(DataStore.reservationModel.getValueAt(row,1).toString());
            JTextField ph=tf(DataStore.reservationModel.getValueAt(row,2).toString());
            JTextField tbl=tf(DataStore.reservationModel.getValueAt(row,3).toString());
            JTextField dt=tf(DataStore.reservationModel.getValueAt(row,4).toString()+" "+DataStore.reservationModel.getValueAt(row,5).toString());
            JTextField gs=tf(DataStore.reservationModel.getValueAt(row,6).toString());
            JComboBox<String> stBox=new JComboBox<>(new String[]{"Pending","Confirmed","Cancelled"});
            stBox.setSelectedItem(DataStore.reservationModel.getValueAt(row,7).toString());
            stBox.setFont(new Font("Georgia",Font.PLAIN,14));
            if(JOptionPane.showConfirmDialog(f,new Object[]{"Customer Full Name:",cu,"Phone:",ph,"Table (e.g. T1):",tbl,"Date & Time (YYYY-MM-DD HH:MM:SS):",dt,"Guests:",gs,"Status:",stBox},"Update Reservation",JOptionPane.OK_CANCEL_OPTION)==0){
                int gn;try{gn=Integer.parseInt(gs.getText().trim());}catch(NumberFormatException ex){gn=1;}
                if(!DataStore.updateReservation(row,cu.getText().trim(),ph.getText().trim(),tbl.getText().trim(),dt.getText().trim(),gn,stBox.getSelectedItem().toString()))
                    JOptionPane.showMessageDialog(f,"Update failed.","Error",JOptionPane.ERROR_MESSAGE);
                else updateTableColors(tblBtns);
            }
        });
        conB.addActionListener(e->{updateStatus(f,rt,"Confirmed"); updateTableColors(tblBtns);});
        canB.addActionListener(e->{updateStatus(f,rt,"Cancelled"); updateTableColors(tblBtns);});
        delB.addActionListener(e->{
            int row=rt.getSelectedRow();if(row<0){warn(f,"Select a reservation.");return;}
            String cu=DataStore.reservationModel.getValueAt(row,1).toString();
            if(JOptionPane.showConfirmDialog(f,"Delete reservation for "+cu+"?","Confirm",JOptionPane.YES_NO_OPTION)==0)
                if(DataStore.deleteReservation(row)) {rt.setModel(DataStore.reservationModel); updateTableColors(tblBtns);}
        });
        f.setVisible(true);
    }
    private void updateStatus(JFrame f,JTable t,String s){
        int row=t.getSelectedRow();if(row<0){warn(f,"Select a reservation first.");return;}
        if(DataStore.updateReservationStatus(row,s)) t.setModel(DataStore.reservationModel);
        else JOptionPane.showMessageDialog(f,"Update failed.","Error",JOptionPane.ERROR_MESSAGE);
    }
    private void updateTableColors(JButton[] btns){
        for(int i=0;i<btns.length;i++){
            int tno=i+1; Color c=new Color(0xD1,0xFF,0xBD);
            for(int j=0;j<DataStore.reservationModel.getRowCount();j++){
                String tbl="T"+tno;
                if(!tbl.equals(DataStore.reservationModel.getValueAt(j,3).toString())) continue;
                String st=DataStore.reservationModel.getValueAt(j,7).toString();
                if(st.equals("Confirmed")){c=new Color(0xFF,0x7F,0x7F); break;}
                if(st.equals("Pending")&&!c.equals(new Color(0xFF,0x7F,0x7F))) c=new Color(0xFF,0xF3,0xB0);
            }
            btns[i].setBackground(c);
            btns[i].setText("T"+tno);
        }
    }
    private void showTableInfo(JFrame f,int tno){
        StringBuilder sb=new StringBuilder("Reservations for Table T"+tno+":\n\n"); boolean found=false;
        for(int i=0;i<DataStore.reservationModel.getRowCount();i++){
            if(("T"+tno).equals(DataStore.reservationModel.getValueAt(i,3).toString())||
           ("Table "+tno).equals(DataStore.reservationModel.getValueAt(i,3).toString())){
                sb.append("Customer: ").append(DataStore.reservationModel.getValueAt(i,1))
                  .append("\nPhone: ").append(DataStore.reservationModel.getValueAt(i,2))
                  .append("\nDate: ").append(DataStore.reservationModel.getValueAt(i,4))
                  .append("  Time: ").append(DataStore.reservationModel.getValueAt(i,5))
                  .append("\nGuests: ").append(DataStore.reservationModel.getValueAt(i,6))
                  .append("\nStatus: ").append(DataStore.reservationModel.getValueAt(i,7)).append("\n\n");
                found=true;}}
        if(!found) sb.append("No reservations found.");
        JOptionPane.showMessageDialog(f,sb.toString(),"Table T"+tno,JOptionPane.INFORMATION_MESSAGE);
    }
    private void addLegend(JPanel p,int x,int y,Color c,String t){
        JLabel d=new JLabel("●");d.setBounds(x,y,20,20);d.setForeground(c);d.setFont(new Font("Georgia",Font.BOLD,16));p.add(d);
        JLabel l=new JLabel(t);l.setBounds(x+22,y,90,20);l.setFont(new Font("Georgia",Font.PLAIN,12));l.setForeground(UI.BROWN);p.add(l);}
    private JTextField tf(){return tf("");}
    private JTextField tf(String v){JTextField x=new JTextField(v);x.setFont(new Font("Georgia",Font.PLAIN,14));return x;}
    private void warn(JFrame f,String m){JOptionPane.showMessageDialog(f,m,"Warning",JOptionPane.WARNING_MESSAGE);}
}
