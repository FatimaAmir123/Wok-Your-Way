import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

class StaffManagement {
    StaffManagement() {
        JFrame f=UI.frame("Wok Your Way — Staff Management");
        JLabel bg = UI.bgWithScroll(f);

        bg.add(UI.sidebar(f,"Staff"));
        bg.add(UI.header("Staff Management",228,6,964));

        JPanel c=UI.content(228,58,964,592); bg.add(c);
        c.add(UI.titleBar("Staff Accounts",964,28));

        JTable table=new JTable(DataStore.staffModel);
        table.setRowHeight(26);
        table.setFont(new Font("Georgia",Font.PLAIN,12));
        table.setForeground(new Color(0x3D2B1F));
        table.setGridColor(new Color(0xE8C84A));
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(0xFFD700));
        table.setSelectionForeground(new Color(0x3D2B1F));
        table.setFillsViewportHeight(true);
        table.setOpaque(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(35);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t,Object v,boolean sel,boolean foc,int row,int col) {
                Object display = (col==5) ? "••••••••" : v;
                super.getTableCellRendererComponent(t,display,sel,foc,row,col);
                if (!sel) {
                    String status=DataStore.staffModel.getValueAt(row,6).toString();
                    if (status.equals("Active"))
                        setBackground(new Color(0xD1,0xFF,0xBD,200));
                    else
                        setBackground(new Color(0xFF,0x7F,0x7F,160));
                    setForeground(new Color(0x3D2B1F));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
                return this;
            }
        });

        JTableHeader hdr=table.getTableHeader();
        hdr.setFont(new Font("Georgia",Font.BOLD,14));
        hdr.setBackground(new Color(0xFF,0xD7,0x00,220));
        hdr.setForeground(new Color(0x3D2B1F));
        hdr.setPreferredSize(new Dimension(0,38));
        hdr.setReorderingAllowed(false);
        ((DefaultTableCellRenderer)hdr.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scroll=new JScrollPane(table);
        scroll.setBounds(10,34,944,506);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xC8A800),1));
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        c.add(scroll);

        int bY=548,bW=222,bH=36,bX=8,bG=12;
        JButton addB  =bld("Add Staff",      new Color(0xFF,0xEB,0x7A,255));
        JButton edB   =bld("Edit Staff",     new Color(0xFF,0xEB,0x7A,255));
        JButton pwB   =bld("Reset Password", new Color(0xFF,0xEB,0x7A,255));
        JButton delB  =bld("Delete Staff",   new Color(0xFF,0x7F,0x7F,255));
        addB.setBounds(bX,           bY,bW,bH);
        edB.setBounds(bX+(bW+bG),   bY,bW,bH);
        pwB.setBounds(bX+(bW+bG)*2, bY,bW,bH);
        delB.setBounds(bX+(bW+bG)*3,bY,bW,bH);
        c.add(addB);c.add(edB);c.add(pwB);c.add(delB);

        addB.addActionListener(e -> {
            JTextField nmF=tf(),unF=tf(),phF=tf(); JPasswordField pwF=new JPasswordField();
            pwF.setFont(new Font("Georgia",Font.PLAIN,14));
            String[] roles={"Waiter","Cashier","Admin","Chef"};
            JComboBox<String> rb=new JComboBox<>(roles); rb.setFont(new Font("Georgia",Font.PLAIN,14));
            String[] statuses={"Active","Inactive"};
            JComboBox<String> sb=new JComboBox<>(statuses); sb.setFont(new Font("Georgia",Font.PLAIN,14));
            int res=JOptionPane.showConfirmDialog(f,
                new Object[]{"Full Name:",nmF,"Username:",unF,"Phone:",phF,"Role:",rb,"Status:",sb,"Password:",pwF},
                "Add New Staff Member",JOptionPane.OK_CANCEL_OPTION);
            if (res==JOptionPane.OK_OPTION) {
                String nm=nmF.getText().trim(), un=unF.getText().trim();
                String pw=new String(pwF.getPassword()).trim();
                if(nm.isEmpty()||un.isEmpty()){JOptionPane.showMessageDialog(f,"Name and Username required.","Error",JOptionPane.ERROR_MESSAGE);return;}
                if(pw.isEmpty()){JOptionPane.showMessageDialog(f,"Password required.","Error",JOptionPane.ERROR_MESSAGE);return;}
                Connection con=ConnectionClass.getConnection();
                if(con==null){JOptionPane.showMessageDialog(f,"Database unavailable.","Error",JOptionPane.ERROR_MESSAGE);return;}
                try {
                    PreparedStatement ps=con.prepareStatement(
                        "INSERT INTO users(full_name,username,password,role,status) VALUES(?,?,?,?,?)");
                    ps.setString(1,nm); ps.setString(2,un); ps.setString(3,pw);
                    ps.setString(4,rb.getSelectedItem().toString());
                    ps.setString(5,sb.getSelectedItem().toString()); ps.executeUpdate();
                    DataStore.loadStaff(); table.setModel(DataStore.staffModel);
                    JOptionPane.showMessageDialog(f,"Staff member added!","Success",JOptionPane.INFORMATION_MESSAGE);
                } catch(SQLException ex){JOptionPane.showMessageDialog(f,"Error: "+ex.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);}
            }
        });

        edB.addActionListener(e -> {
            int row=table.getSelectedRow();
            if(row<0){JOptionPane.showMessageDialog(f,"Select a staff member to edit.","Warning",JOptionPane.WARNING_MESSAGE);return;}
            JTextField nmF=tf(DataStore.staffModel.getValueAt(row,1).toString());
            JTextField phF=tf(DataStore.staffModel.getValueAt(row,4).toString());
            String[] roles={"Waiter","Cashier","Admin","Chef"};
            JComboBox<String> rb=new JComboBox<>(roles); rb.setFont(new Font("Georgia",Font.PLAIN,14));
            rb.setSelectedItem(DataStore.staffModel.getValueAt(row,3).toString());
            String[] statuses={"Active","Inactive"};
            JComboBox<String> sb=new JComboBox<>(statuses); sb.setFont(new Font("Georgia",Font.PLAIN,14));
            sb.setSelectedItem(DataStore.staffModel.getValueAt(row,6).toString());
            int res=JOptionPane.showConfirmDialog(f,
                new Object[]{"Full Name:",nmF,"Phone:",phF,"Role:",rb,"Status:",sb},
                "Edit Staff Member",JOptionPane.OK_CANCEL_OPTION);
            if(res==JOptionPane.OK_OPTION){
                int userId=(int)DataStore.staffModel.getValueAt(row,0);
                Connection con=ConnectionClass.getConnection();
                if(con==null){JOptionPane.showMessageDialog(f,"Database unavailable.","Error",JOptionPane.ERROR_MESSAGE);return;}
                try {
                    PreparedStatement ps=con.prepareStatement(
                        "UPDATE users SET full_name=?,role=?,status=? WHERE user_id=?");
                    ps.setString(1,nmF.getText().trim());
                    ps.setString(2,rb.getSelectedItem().toString());
                    ps.setString(3,sb.getSelectedItem().toString());
                    ps.setInt(4,userId); ps.executeUpdate();
                    DataStore.loadStaff(); table.setModel(DataStore.staffModel);
                    JOptionPane.showMessageDialog(f,"Staff member updated!","Success",JOptionPane.INFORMATION_MESSAGE);
                } catch(SQLException ex){JOptionPane.showMessageDialog(f,"Error: "+ex.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);}
            }
        });

        pwB.addActionListener(e -> {
            int row=table.getSelectedRow();
            if(row<0){JOptionPane.showMessageDialog(f,"Select a staff member first.","Warning",JOptionPane.WARNING_MESSAGE);return;}
            String nm=DataStore.staffModel.getValueAt(row,1).toString();
            JPasswordField p1=new JPasswordField(),p2=new JPasswordField();
            p1.setFont(new Font("Georgia",Font.PLAIN,14)); p2.setFont(new Font("Georgia",Font.PLAIN,14));
            int res=JOptionPane.showConfirmDialog(f,
                new Object[]{"New password for "+nm+":",p1,"Confirm:",p2},
                "Reset Password",JOptionPane.OK_CANCEL_OPTION);
            if(res==JOptionPane.OK_OPTION){
                String pw1=new String(p1.getPassword()),pw2=new String(p2.getPassword());
                if(pw1.isEmpty()){JOptionPane.showMessageDialog(f,"Password cannot be empty.","Error",JOptionPane.ERROR_MESSAGE);return;}
                if(!pw1.equals(pw2)){JOptionPane.showMessageDialog(f,"Passwords do not match.","Error",JOptionPane.ERROR_MESSAGE);return;}
                int userId=(int)DataStore.staffModel.getValueAt(row,0);
                Connection con=ConnectionClass.getConnection();
                if(con==null){JOptionPane.showMessageDialog(f,"Database unavailable.","Error",JOptionPane.ERROR_MESSAGE);return;}
                try {
                    PreparedStatement ps=con.prepareStatement("UPDATE users SET password=? WHERE user_id=?");
                    ps.setString(1,pw1); ps.setInt(2,userId); ps.executeUpdate();
                    DataStore.loadStaff(); table.setModel(DataStore.staffModel);
                    JOptionPane.showMessageDialog(f,"Password for "+nm+" reset!","Success",JOptionPane.INFORMATION_MESSAGE);
                } catch(SQLException ex){JOptionPane.showMessageDialog(f,"Error: "+ex.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);}
            }
        });

        delB.addActionListener(e -> {
            int row=table.getSelectedRow();
            if(row<0){JOptionPane.showMessageDialog(f,"Select a staff member to delete.","Warning",JOptionPane.WARNING_MESSAGE);return;}
            if("Admin".equals(DataStore.staffModel.getValueAt(row,3).toString())||
               "Owner".equals(DataStore.staffModel.getValueAt(row,3).toString())){
                JOptionPane.showMessageDialog(f,"Admin/Owner account cannot be deleted.","Restricted",JOptionPane.WARNING_MESSAGE);return;}
            String nm=DataStore.staffModel.getValueAt(row,1).toString();
            if(JOptionPane.showConfirmDialog(f,"Delete "+nm+"? This cannot be undone.",
                "Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION){
                int userId=(int)DataStore.staffModel.getValueAt(row,0);
                Connection con=ConnectionClass.getConnection();
                if(con==null){JOptionPane.showMessageDialog(f,"Database unavailable.","Error",JOptionPane.ERROR_MESSAGE);return;}
                try {
                    PreparedStatement ps=con.prepareStatement("DELETE FROM users WHERE user_id=?");
                    ps.setInt(1,userId); ps.executeUpdate();
                    DataStore.loadStaff(); table.setModel(DataStore.staffModel);
                    JOptionPane.showMessageDialog(f,nm+" removed.","Deleted",JOptionPane.INFORMATION_MESSAGE);
                } catch(SQLException ex){JOptionPane.showMessageDialog(f,"Error: "+ex.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);}
            }
        });

        f.setVisible(true);
    }
    private JTextField tf(){return tf("");}
    private JTextField tf(String v){JTextField x=new JTextField(v);x.setFont(new Font("Georgia",Font.PLAIN,14));return x;}
    private JButton bld(String t,Color bg){
        JButton b=new JButton(t);b.setFont(new Font("Georgia",Font.BOLD,16));
        b.setBackground(bg);b.setForeground(new Color(0x3D2B1F));b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(0xC8A800),1));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setHorizontalAlignment(SwingConstants.CENTER);return b;}
}
