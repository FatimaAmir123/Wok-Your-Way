import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import java.awt.*;
import java.sql.*;
import java.time.format.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

class KitchenDisplay extends JFrame {

    static final Color GOLD_SEMI  = new Color(0xFF,0xD7,0x00,210);
    static final Color GOLD_DARK  = new Color(0xC8,0xA8,0x00);
    static final Color SIDEBAR_BG = new Color(0xFF,0xEB,0x7A,200);
    static final Color BROWN      = new Color(0x3D,0x2B,0x1F);
    static final Color BROWN2     = new Color(0x6B,0x4C,0x3B);
    static final Color GREEN_BTN  = new Color(0x2E,0x7D,0x32);
    static final Color ORANGE_BTN = new Color(0xE6,0x5C,0x00);
    static final Color RED_BTN    = new Color(0xC6,0x28,0x28);

    private DefaultTableModel pendingModel, cookingModel;
    private JTable pendingTable, cookingTable;
    private JLabel pendingCount, cookingCount;
    private final java.util.List<Integer> pendingIds=new ArrayList<>(), cookingIds=new ArrayList<>();
    private javax.swing.Timer timer;

    KitchenDisplay() {
        setTitle("WOK YOUR WAY — Kitchen Display");
        setLayout(null); setSize(1200,700); setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel bg = UI.bgLabel();
        setContentPane(bg);

        bg.add(buildSidebar());

        JPanel hdr=UI.header("Kitchen Display",228,6,964);
        JLabel chef=new JLabel("Chef: "+DataStore.loggedInName,SwingConstants.RIGHT);
        chef.setBounds(0,0,940,46); chef.setFont(new Font("Georgia",Font.ITALIC,13)); chef.setForeground(BROWN2);
        hdr.add(chef); bg.add(hdr);

        pendingCount=UI.statCard(bg,"Pending","0",228,56,308,64,new Color(0xFF,0xF3,0xB0,230));
        cookingCount=UI.statCard(bg,"Cooking","0",544,56,308,64,new Color(0xFF,0xD9,0x66,230));
        UI.statCard(bg,"Status","LIVE",860,56,332,64,new Color(0xD1,0xFF,0xBD,230));

        JPanel pp=orderPanel("Pending — Awaiting Cooking",228,128,478,504);
        pendingModel=kitModel();
        pendingTable=UI.table(pendingModel); pendingTable.setDefaultRenderer(Object.class,kitRenderer(new Color(0xFF,0xF3,0xB0,180)));
        pp.add(UI.scroll(pendingTable,8,34,462,420));
        JButton startBtn=kBtn("Start Cooking",ORANGE_BTN,Color.WHITE);
        startBtn.setBounds(8,460,462,36); pp.add(startBtn); bg.add(pp);

        JPanel cp=orderPanel("Cooking — Mark When Ready",714,128,478,504);
        cookingModel=kitModel();
        cookingTable=UI.table(cookingModel); cookingTable.setDefaultRenderer(Object.class,kitRenderer(new Color(0xFF,0xEB,0x7A,180)));
        cp.add(UI.scroll(cookingTable,8,34,462,420));
        JButton readyBtn=kBtn("Mark as Ready",GREEN_BTN,Color.WHITE);
        readyBtn.setBounds(8,460,462,36); cp.add(readyBtn); bg.add(cp);
        setupKitchenTableColumns(pendingTable);
        setupKitchenTableColumns(cookingTable);

        startBtn.addActionListener(e->{
            int row=pendingTable.getSelectedRow();
            if(row<0||row>=pendingIds.size()){JOptionPane.showMessageDialog(this,"Select a pending order to start cooking.","Warning",JOptionPane.WARNING_MESSAGE);return;}
            if(setStatus(pendingIds.get(row),"Cooking")) loadOrders();
            else JOptionPane.showMessageDialog(this,"Update failed.","Error",JOptionPane.ERROR_MESSAGE);
        });
        readyBtn.addActionListener(e->{
            int row=cookingTable.getSelectedRow();
            if(row<0||row>=cookingIds.size()){JOptionPane.showMessageDialog(this,"Select a cooking order.","Warning",JOptionPane.WARNING_MESSAGE);return;}
            int orderId=cookingIds.get(row);
            if(setStatus(orderId,"Ready")){
                // Find the table index from the table number shown in the row and set the ready flag
                String tblStr=cookingModel.getValueAt(row,1).toString(); // e.g. "T3"
                try {
                    int tIdx=Integer.parseInt(tblStr.replace("T",""))-1;
                    if(tIdx>=0 && tIdx<12) WaiterScreen.orderReady[tIdx]=true;
                } catch(NumberFormatException ignored){}
                loadOrders();
            } else JOptionPane.showMessageDialog(this,"Update failed.","Error",JOptionPane.ERROR_MESSAGE);
        });

        loadOrders();
        timer=new javax.swing.Timer(5000,e->loadOrders()); timer.start();
        addWindowListener(new java.awt.event.WindowAdapter(){
            @Override public void windowClosing(java.awt.event.WindowEvent e){if(timer!=null)timer.stop();}});
        setVisible(true);
    }

    private void loadOrders(){
        pendingModel.setRowCount(0); cookingModel.setRowCount(0);
        pendingIds.clear(); cookingIds.clear();
        Connection con=ConnectionClass.getConnection();
        if(con==null){
            // No-DB mode: all sent orders go to Pending; cooking state is DB-only
            String now=java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            for(int i=0;i<12;i++){
                java.util.List<OrderItem> orders=WaiterScreen.tableOrders[i];
                if(orders!=null && !orders.isEmpty() && WaiterScreen.kitchenSent[i]){
                    StringBuilder items=new StringBuilder();
                    for(OrderItem oi:orders){ if(items.length()>0) items.append(", "); items.append(oi.qty+"x "+oi.name); }
                    String tbl="T"+(i+1);
                    pendingModel.addRow(new Object[]{"#"+(1001+i),tbl,items.toString(),now}); pendingIds.add(i);
                }
            }
        } else {
            try (PreparedStatement ps=con.prepareStatement(
                    "SELECT o.order_id, t.table_number, " +
                    "GROUP_CONCAT(CONCAT(oi.quantity,'x ',m.item_name) SEPARATOR ', ') AS items, " +
                    "TIME(o.order_date) AS order_time, o.order_status " +
                    "FROM orders o " +
                    "JOIN restauranttables t ON o.table_id=t.table_id " +
                    "JOIN orderitems oi ON o.order_id=oi.order_id " +
                    "JOIN menuitems m ON oi.item_id=m.item_id " +
                    "WHERE o.order_status IN ('Pending','Cooking') " +
                    "GROUP BY o.order_id, t.table_number, o.order_date, o.order_status " +
                    "ORDER BY o.order_date");
                 ResultSet rs=ps.executeQuery()){
                while(rs.next()){
                    int orderId=rs.getInt("order_id");
                    String tbl=rs.getString("table_number");
                    String items=rs.getString("items");
                    String time=rs.getString("order_time"); if(time!=null&&time.length()>5) time=time.substring(0,5);
                    String status=rs.getString("order_status");
                    if("Pending".equals(status)){ pendingModel.addRow(new Object[]{"#"+orderId,tbl,items,time}); pendingIds.add(orderId); }
                    else { cookingModel.addRow(new Object[]{"#"+orderId,tbl,items,time}); cookingIds.add(orderId); }
                }
            } catch(SQLException e){ System.err.println("[DB] KitchenDisplay.loadOrders: "+e.getMessage()); }
        }
        if(pendingCount!=null) pendingCount.setText(String.valueOf(pendingModel.getRowCount()));
        if(cookingCount!=null) cookingCount.setText(String.valueOf(cookingModel.getRowCount()));
        if(pendingTable!=null) setupKitchenTableColumns(pendingTable);
        if(cookingTable!=null) setupKitchenTableColumns(cookingTable);
    }

    private boolean setStatus(int orderId, String st){
        Connection con=ConnectionClass.getConnection();
        if(con==null){
            if(st.equals("Cooking")||st.equals("Ready")) return true;
            return false;
        }
        try {
            PreparedStatement ps=con.prepareStatement("UPDATE orders SET order_status=? WHERE order_id=?");
            ps.setString(1,st); ps.setInt(2,orderId); ps.executeUpdate();
            if("Ready".equals(st)) DataStore.deductIngredientsForOrder(orderId);
            return true;
        } catch(SQLException e){ System.err.println("[DB] setStatus: "+e.getMessage()); return false; }
    }

    private DefaultTableModel kitModel(){
        return new DefaultTableModel(new String[]{"Order #","Table","Items","Time"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}};}
    private DefaultTableCellRenderer kitRenderer(Color bg){
        return new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                Object display = v;
                if(col==2 && v!=null){
                    // Each item on its own line via HTML
                    String html="<html><body style='padding:3px 4px;'>"
                        +v.toString().replace(", ","<br/>")
                        +"</body></html>";
                    display=html;
                }
                super.getTableCellRendererComponent(t,display,sel,foc,row,col);
                if(!sel){setBackground(bg);setForeground(BROWN);}
                setHorizontalAlignment(col==2 ? SwingConstants.LEFT : SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(2,6,2,6));
                // Auto-expand row height to show all items
                if(col==2 && v!=null){
                    int lines=v.toString().split(", ").length;
                    int needed=Math.max(28, lines*22+8);
                    if(t.getRowHeight(row)!=needed) t.setRowHeight(row,needed);
                }
                return this;
            }
        };
    }
    /** Set fixed column widths; AUTO_RESIZE_OFF lets the user drag any column freely. */
    private void setupKitchenTableColumns(JTable t){
        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        if(t.getColumnCount()<4) return;
        t.getColumnModel().getColumn(0).setPreferredWidth(72);   // Order #
        t.getColumnModel().getColumn(1).setPreferredWidth(56);   // Table
        t.getColumnModel().getColumn(2).setPreferredWidth(280);  // Items (wide, freely draggable)
        t.getColumnModel().getColumn(3).setPreferredWidth(58);   // Time
    }    private JPanel orderPanel(String title,int x,int y,int w,int h){
        JPanel p=new JPanel(null); p.setBounds(x,y,w,h);
        p.setBackground(new Color(255,255,255,170)); p.setBorder(BorderFactory.createLineBorder(GOLD_DARK,1));
        JPanel tb=new JPanel(null); tb.setBounds(0,0,w,40); tb.setBackground(new Color(0xFF,0xD7,0x00,210));
        JLabel tl=new JLabel(title,SwingConstants.CENTER); tl.setBounds(0,0,w,40);
        tl.setFont(new Font("Georgia",Font.BOLD,14)); tl.setForeground(BROWN); tb.add(tl); p.add(tb); return p;}
    private JButton kBtn(String t,Color bg,Color fg){
        JButton b=new JButton(t); b.setFont(new Font("Georgia",Font.BOLD,15)); b.setBackground(bg);
        b.setForeground(fg); b.setFocusPainted(false); b.setBorder(BorderFactory.createLineBorder(GOLD_DARK,1));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;}
    private JPanel buildSidebar(){
        JPanel sb=new JPanel(null); sb.setBounds(0,0,220,670); sb.setBackground(SIDEBAR_BG);
        sb.setBorder(BorderFactory.createMatteBorder(0,0,0,2,GOLD_DARK));
        JLabel brand=new JLabel("Wok Your Way",SwingConstants.CENTER);
        brand.setBounds(8,18,204,36); brand.setFont(UI.brandFont(17)); brand.setForeground(BROWN); sb.add(brand);
        JLabel sub=new JLabel("Kitchen Display",SwingConstants.CENTER);
        sub.setBounds(8,54,204,18); sub.setFont(new Font("Georgia",Font.PLAIN,11)); sub.setForeground(BROWN2); sb.add(sub);
        JSeparator sep=new JSeparator(); sep.setBounds(18,74,184,2); sep.setForeground(GOLD_DARK); sb.add(sep);

        // Status legend — title
        JLabel legendTitle=new JLabel("Order Status Guide", SwingConstants.CENTER);
        legendTitle.setBounds(8,82,204,18); legendTitle.setFont(new Font("Georgia",Font.BOLD,12));
        legendTitle.setForeground(BROWN); sb.add(legendTitle);

        // Three colour-key rows, each with a coloured box + label + description, fully spaced
        String[] keyLabels={"Pending","Cooking","Ready"};
        String[] keyDesc  ={"Not yet started","Being prepared","Done — serve now!"};
        Color[]  keyColors={new Color(0xFF,0xF3,0xB0),new Color(0xFF,0xEB,0x7A),new Color(0xD1,0xFF,0xBD)};
        for(int i=0;i<3;i++){
            int ky=106+i*52;
            JPanel box=new JPanel(); box.setBounds(18,ky,18,18); box.setBackground(keyColors[i]);
            box.setBorder(BorderFactory.createLineBorder(GOLD_DARK)); sb.add(box);
            JLabel kl=new JLabel(keyLabels[i]); kl.setBounds(42,ky,160,18);
            kl.setFont(new Font("Georgia",Font.BOLD,12)); kl.setForeground(BROWN); sb.add(kl);
            JLabel kd=new JLabel(keyDesc[i]); kd.setBounds(42,ky+18,170,16);
            kd.setFont(new Font("Georgia",Font.PLAIN,10)); kd.setForeground(BROWN2); sb.add(kd);
        }
        JButton refBtn=new JButton("Refresh Now"); refBtn.setBounds(18,272,184,38);
        refBtn.setFont(new Font("Georgia",Font.BOLD,12)); refBtn.setBackground(new Color(0xE6,0xAC,0x00)); refBtn.setForeground(BROWN);
        refBtn.setFocusPainted(false); refBtn.setBorder(BorderFactory.createLineBorder(GOLD_DARK,1));
        refBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refBtn.addActionListener(e->loadOrders()); sb.add(refBtn);

        JButton recipeBtn=new JButton("View Recipes"); recipeBtn.setBounds(18,320,184,38);
        recipeBtn.setFont(new Font("Georgia",Font.BOLD,12)); recipeBtn.setBackground(new Color(0xC8,0xA8,0x00)); recipeBtn.setForeground(BROWN);
        recipeBtn.setFocusPainted(false); recipeBtn.setBorder(BorderFactory.createLineBorder(GOLD_DARK,1));
        recipeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        recipeBtn.addActionListener(e->new KitchenRecipeViewer()); sb.add(recipeBtn);

        // Live date/time clock
        JLabel clockLbl=new JLabel("",SwingConstants.CENTER);
        clockLbl.setBounds(8,372,204,48); clockLbl.setFont(new Font("Georgia",Font.BOLD,12));
        clockLbl.setForeground(BROWN); clockLbl.setOpaque(true);
        clockLbl.setBackground(new Color(0xFF,0xD7,0x00,180));
        clockLbl.setBorder(BorderFactory.createLineBorder(GOLD_DARK,1));
        sb.add(clockLbl);
        new javax.swing.Timer(1000, e -> {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String day  = now.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);
            String mon  = now.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);
            clockLbl.setText(String.format("<html><center>%02d:%02d:%02d<br><small>%s %d %s %d</small></center></html>",
                now.getHour(), now.getMinute(), now.getSecond(),
                day, now.getDayOfMonth(), mon, now.getYear()));
        }).start();
        JButton logout=new JButton("Log Out"); logout.setBounds(18,616,184,40);
        logout.setFont(new Font("Georgia",Font.BOLD,14)); logout.setBackground(RED_BTN); logout.setForeground(Color.WHITE);
        logout.setFocusPainted(false); logout.setBorder(BorderFactory.createLineBorder(new Color(0xC0,0x39,0x2B),1));
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e->{if(JOptionPane.showConfirmDialog(this,"Logout?","Logout",JOptionPane.YES_NO_OPTION)==0){if(timer!=null)timer.stop();dispose();new LoginSignUp();}});
        sb.add(logout); return sb;}
    private void addKey(JPanel sb,Color c,String t,int y){
        JPanel d=new JPanel(); d.setBounds(18,y,18,18); d.setBackground(c); d.setBorder(BorderFactory.createLineBorder(GOLD_DARK)); sb.add(d);
        JLabel l=new JLabel(t); l.setBounds(40,y,160,18); l.setFont(new Font("Georgia",Font.PLAIN,12)); l.setForeground(BROWN2); sb.add(l);}
}
