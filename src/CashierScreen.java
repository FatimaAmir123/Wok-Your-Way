import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import java.awt.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

class CashierScreen extends JFrame {

    static final Color GOLD_SEMI  = new Color(0xFF,0xD7,0x00,210);
    static final Color GOLD_DARK  = new Color(0xC8,0xA8,0x00);
    static final Color SIDEBAR_BG = new Color(0xFF,0xEB,0x7A,200);
    static final Color BROWN      = new Color(0x3D,0x2B,0x1F);
    static final Color BROWN2     = new Color(0x6B,0x4C,0x3B);
    static final Color GREEN_BTN  = new Color(0x2E,0x7D,0x32);
    static final Color RED_BTN    = new Color(0xC6,0x28,0x28);

    private DefaultTableModel txModel;
    private JLabel totalLbl, cashLbl, cardLbl;
    private double grandTotal=0, totalCash=0, totalCard=0, totalDigital=0;
    private int txCount=0;

    CashierScreen() {
        setTitle("WOK YOUR WAY — Cashier");
        setSize(1200,700); setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root=new JPanel(new BorderLayout()){
            @Override protected void paintComponent(Graphics g){
                g.setColor(new Color(0xFF,0xF8,0xDC)); g.fillRect(0,0,getWidth(),getHeight());}};
        root.setOpaque(false); setContentPane(root);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(),    BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel buildSidebar(){
        JPanel sb=new JPanel(new BorderLayout()){
            @Override protected void paintComponent(Graphics g){g.setColor(SIDEBAR_BG);g.fillRect(0,0,getWidth(),getHeight());}};
        sb.setOpaque(false); sb.setPreferredSize(new Dimension(200,0));
        sb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,0,2,GOLD_DARK),BorderFactory.createEmptyBorder(10,10,10,10)));
        JPanel top=new JPanel(); top.setLayout(new BoxLayout(top,BoxLayout.Y_AXIS)); top.setOpaque(false);
        top.add(Box.createVerticalStrut(4));
        JLabel brand=new JLabel("Wok Your Way",SwingConstants.CENTER);
        brand.setFont(UI.brandFont(16)); brand.setForeground(BROWN); brand.setAlignmentX(Component.CENTER_ALIGNMENT); top.add(brand);
        top.add(Box.createVerticalStrut(2));
        JLabel sub=new JLabel("Cashier Interface",SwingConstants.CENTER);
        sub.setFont(new Font("Georgia",Font.PLAIN,11)); sub.setForeground(BROWN2); sub.setAlignmentX(Component.CENTER_ALIGNMENT); top.add(sub);
        top.add(Box.createVerticalStrut(8));
        JSeparator sep=new JSeparator(); sep.setForeground(GOLD_DARK); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,2)); top.add(sep);
        top.add(Box.createVerticalStrut(8));
        JLabel hint=new JLabel("<html><center>Click a table<br>to open its bill</center></html>",SwingConstants.CENTER);
        hint.setFont(new Font("Georgia",Font.PLAIN,11)); hint.setForeground(BROWN2); hint.setAlignmentX(Component.CENTER_ALIGNMENT); top.add(hint);
        top.add(Box.createVerticalStrut(10));
        // Live date/time clock
        JLabel cashierClock=new JLabel("",SwingConstants.CENTER);
        cashierClock.setFont(new Font("Georgia",Font.BOLD,12));
        cashierClock.setForeground(BROWN); cashierClock.setOpaque(true);
        cashierClock.setBackground(new Color(0xFF,0xD7,0x00,180));
        cashierClock.setBorder(BorderFactory.createLineBorder(GOLD_DARK,1));
        cashierClock.setAlignmentX(Component.CENTER_ALIGNMENT);
        cashierClock.setMaximumSize(new Dimension(Integer.MAX_VALUE,52));
        cashierClock.setPreferredSize(new Dimension(180,52));
        top.add(cashierClock);
        new javax.swing.Timer(1000, e -> {
            java.time.LocalDateTime now=java.time.LocalDateTime.now();
            String day=now.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT,java.util.Locale.ENGLISH);
            String mon=now.getMonth().getDisplayName(java.time.format.TextStyle.SHORT,java.util.Locale.ENGLISH);
            cashierClock.setText(String.format("<html><center>%02d:%02d:%02d<br>%s %d %s %d</center></html>",
                now.getHour(),now.getMinute(),now.getSecond(),day,now.getDayOfMonth(),mon,now.getYear()));
        }).start();
        sb.add(top,BorderLayout.NORTH);
        JButton logout=mkBtn("Logout",RED_BTN,Color.WHITE,13);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        logout.addActionListener(e->{if(JOptionPane.showConfirmDialog(this,"Logout?","Logout",JOptionPane.YES_NO_OPTION)==0){dispose();new LoginSignUp();}});
        JPanel bw=new JPanel(new BorderLayout()); bw.setOpaque(false); bw.setBorder(BorderFactory.createEmptyBorder(6,0,0,0)); bw.add(logout,BorderLayout.CENTER);
        sb.add(bw,BorderLayout.SOUTH); return sb;
    }

    private JPanel buildMain(){
        JPanel main=new JPanel(new BorderLayout(10,10)); main.setOpaque(false); main.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));

        JPanel stats=new JPanel(new GridLayout(1,3,10,0)); stats.setOpaque(false);
        JPanel p1=statPanel("Transactions",new Color(0xFF,0xF3,0xB0,230));
        JPanel p2=statPanel("Today Revenue",new Color(0xD1,0xFF,0xBD,230));
        JPanel p3=statPanel("Avg Bill",new Color(0xFF,0xEB,0x7A,230));
        stats.add(p1); stats.add(p2); stats.add(p3);
        main.add(stats, BorderLayout.NORTH);

        JPanel centre=new JPanel(new GridLayout(1,2,10,0)); centre.setOpaque(false);

        JPanel tPanel=new JPanel(new BorderLayout(0,8));
        tPanel.setBackground(new Color(255,255,255,180));
        tPanel.setBorder(BorderFactory.createLineBorder(GOLD_DARK));
        JLabel tTitle=new JLabel("Select Table",SwingConstants.CENTER);
        tTitle.setFont(new Font("Georgia",Font.BOLD,14)); tTitle.setForeground(BROWN);
        tTitle.setBackground(GOLD_SEMI); tTitle.setOpaque(true);
        tTitle.setBorder(BorderFactory.createEmptyBorder(6,0,6,0)); tPanel.add(tTitle,BorderLayout.NORTH);
        JPanel grid=new JPanel(new GridLayout(3,4,8,8)); grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        for(int i=0;i<12;i++){final int tno=i+1;
            JButton btn=mkBtn("T"+tno,new Color(0xFF,0xF3,0xB0),BROWN,13);
            btn.addActionListener(e->openBill(tno)); grid.add(btn);}
        tPanel.add(grid,BorderLayout.CENTER); centre.add(tPanel);

        JPanel txPanel=new JPanel(new BorderLayout(0,8));
        txPanel.setBackground(new Color(255,255,255,180));
        txPanel.setBorder(BorderFactory.createLineBorder(GOLD_DARK));
        JLabel txTitle=new JLabel("Transaction History",SwingConstants.CENTER);
        txTitle.setFont(new Font("Georgia",Font.BOLD,14)); txTitle.setForeground(BROWN);
        txTitle.setBackground(GOLD_SEMI); txTitle.setOpaque(true);
        txTitle.setBorder(BorderFactory.createEmptyBorder(6,0,6,0)); txPanel.add(txTitle,BorderLayout.NORTH);
        txModel=new DefaultTableModel(new String[]{"TXN","Table","Items","Total","Method","Time"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}};
        JTable txTbl=UI.table(txModel);
        JScrollPane txScroll=new JScrollPane(txTbl);
        txScroll.getViewport().setBackground(new Color(255,255,255,200)); txPanel.add(txScroll,BorderLayout.CENTER);
        JPanel sumBar=new JPanel(new FlowLayout(FlowLayout.LEFT,16,6));
        sumBar.setBackground(new Color(0xFF,0xF3,0xB0,200));
        sumBar.setBorder(BorderFactory.createMatteBorder(1,0,0,0,GOLD_DARK));
        totalLbl=sLbl("Total: Rs 0"); cashLbl=sLbl("Cash: Rs 0"); cardLbl=sLbl("Card/Digital: Rs 0");
        sumBar.add(sLbl("Summary |")); sumBar.add(totalLbl); sumBar.add(cashLbl); sumBar.add(cardLbl);
        txPanel.add(sumBar,BorderLayout.SOUTH); centre.add(txPanel);
        main.add(centre,BorderLayout.CENTER); return main;
    }

    private void openBill(int tno){
        List<OrderItem> items=new ArrayList<>();
        if(tno<=12 && WaiterScreen.tableOrders[tno-1]!=null)
            items.addAll(WaiterScreen.tableOrders[tno-1]);

        if(items.isEmpty()){JOptionPane.showMessageDialog(this,"No pending order for Table T"+tno+".\nAsk the waiter to enter the order first.","No Order",JOptionPane.INFORMATION_MESSAGE);return;}

        JDialog dlg=new JDialog(this,"Bill — Table "+tno,true);
        dlg.setSize(460,520); dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(8,8));
        JLabel title=new JLabel("  Bill for Table "+tno,SwingConstants.LEFT);
        title.setFont(new Font("Georgia",Font.BOLD,16)); title.setForeground(BROWN);
        title.setBackground(GOLD_SEMI); title.setOpaque(true);
        title.setBorder(BorderFactory.createEmptyBorder(8,10,8,0)); dlg.add(title,BorderLayout.NORTH);

        DefaultTableModel bm=new DefaultTableModel(new String[]{"Item","Qty","Price","Total"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}};
        double sub=0;
        for(OrderItem oi:items){double lt=oi.lineTotal();sub+=lt;
            bm.addRow(new Object[]{oi.name,oi.qty,"Rs"+(int)oi.price,"Rs"+(int)lt});}
        dlg.add(new JScrollPane(UI.table(bm)),BorderLayout.CENTER);

        double tax=sub*0.10, total=sub+tax;
        JPanel sum=new JPanel(); sum.setLayout(new BoxLayout(sum,BoxLayout.Y_AXIS));
        sum.setBackground(new Color(0xFF,0xF8,0xDC)); sum.setBorder(BorderFactory.createEmptyBorder(10,14,10,14));
        sum.add(sumLine("Subtotal:","Rs"+(int)sub)); sum.add(sumLine("Tax (10%):","Rs"+(int)tax));
        sum.add(sumLine("TOTAL:","Rs"+(int)total));
        String[] methods={"CASH","CARD","DIGITAL"};
        JComboBox<String> mBox=new JComboBox<>(methods); mBox.setFont(new Font("Georgia",Font.PLAIN,13));
        JPanel mRow=new JPanel(new FlowLayout(FlowLayout.LEFT)); mRow.setOpaque(false);
        mRow.add(new JLabel("Payment:"){{setFont(new Font("Georgia",Font.PLAIN,13));}}); mRow.add(mBox);
        sum.add(mRow);
        JButton procBtn=mkBtn("✔ Process Payment",GREEN_BTN,Color.WHITE,13);
        procBtn.setAlignmentX(Component.CENTER_ALIGNMENT); procBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        sum.add(Box.createVerticalStrut(8)); sum.add(procBtn); dlg.add(sum,BorderLayout.SOUTH);

        final double ft=total;
        final List<OrderItem> fi=items;
        procBtn.addActionListener(e->{
            String method=mBox.getSelectedItem().toString();
            grandTotal+=ft; txCount++;
            switch(method){
                case "CASH": totalCash+=ft; break;
                case "CARD": totalCard+=ft; break;
                default:     totalDigital+=ft; break;
            }
            DataStore.markOrderPaid(
                (tno <= 12 ? WaiterScreen.kitchenOrderId[tno - 1] : -1),
                "T" + tno, DataStore.loggedInUsername, fi);
            txModel.addRow(new Object[]{"TXN"+txCount,"T"+tno,fi.size()+" items","Rs"+(int)ft,method,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))});
            totalLbl.setText("Total: Rs"+(int)grandTotal); cashLbl.setText("Cash: Rs"+(int)totalCash);
            cardLbl.setText("Card/Digital: Rs"+(int)(totalCard+totalDigital));
            if(tno<=12){
                WaiterScreen.tableOrders[tno-1].clear();
                WaiterScreen.tableStatus[tno-1]="EMPTY";
                WaiterScreen.kitchenSent[tno-1]=false;
                WaiterScreen.kitchenOrderId[tno-1]=-1;
                WaiterScreen.orderReady[tno-1]=false;
            }
            dlg.dispose();
            JOptionPane.showMessageDialog(this,"Payment of Rs"+(int)ft+" via "+method+" processed!","Success",JOptionPane.INFORMATION_MESSAGE);
        });
        dlg.setVisible(true);
    }

    private JPanel statPanel(String label,Color bg){
        JPanel p=new JPanel(new BorderLayout());p.setBackground(bg);p.setBorder(BorderFactory.createLineBorder(GOLD_DARK));
        JLabel n=new JLabel("0",SwingConstants.CENTER);n.setFont(new Font("Georgia",Font.BOLD,28));n.setForeground(BROWN);
        JLabel l=new JLabel(label,SwingConstants.CENTER);l.setFont(new Font("Georgia",Font.PLAIN,11));l.setForeground(BROWN2);
        l.setBorder(BorderFactory.createEmptyBorder(0,0,6,0));p.add(n,BorderLayout.CENTER);p.add(l,BorderLayout.SOUTH);return p;}
    private JLabel sLbl(String t){JLabel l=new JLabel(t);l.setFont(new Font("Georgia",Font.PLAIN,12));l.setForeground(BROWN);return l;}
    private JComponent sumLine(String label,String val){
        JPanel row=new JPanel(new BorderLayout());row.setOpaque(false);
        JLabel l=new JLabel(label);l.setFont(new Font("Georgia",Font.BOLD,13));l.setForeground(BROWN);
        JLabel v=new JLabel(val,SwingConstants.RIGHT);v.setFont(new Font("Georgia",Font.PLAIN,13));v.setForeground(BROWN);
        row.add(l,BorderLayout.WEST);row.add(v,BorderLayout.EAST);return row;}
    private JButton mkBtn(String t,Color bg,Color fg,int sz){
        JButton b=new JButton(t){@Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed()?bg.darker():getModel().isRollover()?bg.brighter():bg);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
            g2.setColor(GOLD_DARK);g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
            g2.setColor(fg);g2.setFont(getFont());FontMetrics fm=g2.getFontMetrics();
            g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()-fm.getHeight())/2+fm.getAscent());g2.dispose();}
            @Override protected void paintBorder(Graphics g){}@Override public boolean isOpaque(){return false;}};
        b.setFont(new Font("Georgia",Font.BOLD,sz));b.setFocusPainted(false);b.setContentAreaFilled(false);b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return b;}
}
