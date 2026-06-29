import static java.awt.Component.CENTER_ALIGNMENT;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import java.awt.*;
import java.awt.event.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

class WaiterScreen extends JFrame {

    static final Color WS_GOLD_SEMI  = new Color(0xFF, 0xD7, 0x00, 210);
    static final Color WS_GOLD_DARK  = new Color(0xC8, 0xA8, 0x00);
    static final Color WS_GOLD_NAV   = new Color(0xE6, 0xAC, 0x00);
    static final Color WS_SIDEBAR_BG = new Color(0xFF, 0xEB, 0x7A, 200);
    static final Color WS_BROWN      = new Color(0x3D, 0x2B, 0x1F);
    static final Color WS_BROWN2     = new Color(0x6B, 0x4C, 0x3B);
    static final Color WS_RED_BTN    = new Color(0xC6, 0x28, 0x28);
    static final Color WS_GREEN_BTN  = new Color(0x2E, 0x7D, 0x32);
    static final Color WS_AMBER_BTN  = new Color(0xF5, 0x7F, 0x17);
    static final Color WS_TEAL_BTN   = new Color(0x00, 0x69, 0x6E);
    static final Color WS_T_EMPTY    = new Color(0xFF, 0xFF, 0xFF, 200);
    static final Color WS_T_RESERVED = new Color(0xFF, 0xEB, 0x3B, 200);
    static final Color WS_T_OCCUPIED = new Color(0xFF, 0x5C, 0x5C, 200);

    static class WsBgPanel extends JPanel {
        private final ImageIcon gif;
        WsBgPanel(LayoutManager lm) {
            super(lm);
            ImageIcon icon = null;
            try {
                java.net.URL u = WsBgPanel.class.getResource("yellow.gif");
                java.io.File file = new java.io.File("yellow.gif");
                icon = (u != null) ? new ImageIcon(u)
                     : file.exists() ? new ImageIcon(file.getAbsolutePath())
                     : null;
                if (icon != null) icon.setImageObserver(this);
            } catch (Exception ignored) {}
            gif = icon;
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0xFF, 0xF8, 0xDC));
            g2.fillRect(0, 0, getWidth(), getHeight());
            if (gif != null) {
                g2.drawImage(gif.getImage(), 0, 0, getWidth(), getHeight(), this);
                g2.setColor(new Color(0xFF, 0xF0, 0x80, 80));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.dispose();
        }
    }

    static String[] tableStatus = new String[12];
    @SuppressWarnings("unchecked")
    static List<OrderItem>[] tableOrders = new List[12];
    static boolean[] kitchenSent    = new boolean[12];
    static int[]     kitchenOrderId = new int[12]; // DB order_id created when sent to kitchen
    static boolean[] orderReady     = new boolean[12]; // set true by kitchen when order is ready
    static {
        for (int i = 0; i < 12; i++) {
            tableStatus[i]    = "EMPTY";
            tableOrders[i]    = new ArrayList<>();
            kitchenSent[i]    = false;
            kitchenOrderId[i] = -1;
            orderReady[i]     = false;
        }
    }

    private JLabel statEmpty, statReserved, statOccupied;
    private JPanel tableGrid;

    WaiterScreen() {
        setTitle("WOK YOUR WAY — Waiter Interface");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 700));
        setSize(1200, 700);
        setLocationRelativeTo(null);

        WsBgPanel root = new WsBgPanel(new BorderLayout());
        setContentPane(root);
        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);

        refreshStatCards();
        for (int i = 0; i < 12; i++) {
            if (findReservationRow(i + 1) >= 0)
                tableStatus[i] = "RESERVED";
        }
        setVisible(true);
    }

    private JPanel buildSidebar() {
        JPanel sb = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(WS_SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sb.setOpaque(false);
        sb.setPreferredSize(new Dimension(200, 0));
        sb.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 2, WS_GOLD_DARK),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);

        top.add(wsVgap(4));
        top.add(wsCenteredLabel("WOK YOUR WAY",    new Font("Segoe Script", Font.BOLD, 16), WS_BROWN));
        top.add(wsVgap(2));
        top.add(wsCenteredLabel("Waiter Interface", new Font("Georgia", Font.PLAIN, 11),    WS_BROWN2));
        top.add(wsVgap(8));
        top.add(wsMakeSep());
        top.add(wsVgap(8));

        JLabel lh = new JLabel("TABLE STATUS");
        lh.setFont(new Font("Georgia", Font.BOLD, 10));
        lh.setForeground(WS_BROWN2);
        lh.setAlignmentX(CENTER_ALIGNMENT);
        top.add(lh);
        top.add(wsVgap(6));
        top.add(wsDotRow(new Color(0xBB, 0xBB, 0xBB), "Empty"));
        top.add(wsVgap(2));
        top.add(wsDotRow(new Color(0xFF, 0xEB, 0x3B), "Reserved"));
        top.add(wsVgap(2));
        top.add(wsDotRow(new Color(0xFF, 0x5C, 0x5C), "Occupied"));
        top.add(wsVgap(8));
        top.add(wsMakeSep());
        top.add(wsVgap(8));

        for (String t : new String[]{"Tap any table to", "open its order.", "", "12 tables total"}) {
            JLabel l = new JLabel(t.isEmpty() ? " " : t);
            l.setFont(new Font("Georgia", Font.PLAIN, 11));
            l.setForeground(WS_BROWN2);
            l.setAlignmentX(CENTER_ALIGNMENT);
            top.add(l);
            top.add(wsVgap(2));
        }
        sb.add(top, BorderLayout.NORTH);

        // Live date/time clock in the middle of the sidebar
        JLabel waiterClock=new JLabel("",SwingConstants.CENTER);
        waiterClock.setFont(new Font("Georgia",Font.BOLD,12));
        waiterClock.setForeground(WS_BROWN); waiterClock.setOpaque(true);
        waiterClock.setBackground(new Color(0xFF,0xD7,0x00,180));
        waiterClock.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(WS_GOLD_DARK,1),
            BorderFactory.createEmptyBorder(4,4,4,4)));
        new javax.swing.Timer(1000, e -> {
            java.time.LocalDateTime now=java.time.LocalDateTime.now();
            String day=now.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT,java.util.Locale.ENGLISH);
            String mon=now.getMonth().getDisplayName(java.time.format.TextStyle.SHORT,java.util.Locale.ENGLISH);
            waiterClock.setText(String.format("<html><center>%02d:%02d:%02d<br>%s %d %s %d</center></html>",
                now.getHour(),now.getMinute(),now.getSecond(),day,now.getDayOfMonth(),mon,now.getYear()));
        }).start();
        JPanel clockWrap=new JPanel(new BorderLayout());
        clockWrap.setOpaque(false);
        clockWrap.setBorder(BorderFactory.createEmptyBorder(8,0,8,0));
        clockWrap.add(waiterClock,BorderLayout.CENTER);
        sb.add(clockWrap,BorderLayout.CENTER);

        JButton logout = wsMakeBtn("Logout", WS_RED_BTN, Color.WHITE, 13);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Logout?", "Logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose();
                new LoginSignUp();
            }
        });
        JPanel bw = new JPanel(new BorderLayout());
        bw.setOpaque(false);
        bw.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        bw.add(logout, BorderLayout.CENTER);
        sb.add(bw, BorderLayout.SOUTH);
        return sb;
    }

    private JPanel wsDotRow(Color c, String lbl) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);
        JLabel dot = new JLabel("\u25cf");
        dot.setFont(new Font("Arial", Font.PLAIN, 14));
        dot.setForeground(c);
        JLabel txt = new JLabel(lbl);
        txt.setFont(new Font("Georgia", Font.PLAIN, 12));
        txt.setForeground(WS_BROWN);
        row.add(dot); row.add(txt);
        return row;
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout(0, 4));
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 6));

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(WS_GOLD_SEMI);
        hdr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(WS_GOLD_DARK),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JLabel title = new JLabel("Table Management");
        title.setFont(new Font("Georgia", Font.BOLD, 17));
        title.setForeground(WS_BROWN);
        JLabel clock = new JLabel("", SwingConstants.RIGHT);
        clock.setFont(new Font("Georgia", Font.BOLD, 12));
        clock.setForeground(WS_BROWN);
        new javax.swing.Timer(1000, e -> wsTick(clock)).start();
        wsTick(clock);
        hdr.add(title, BorderLayout.WEST);
        hdr.add(clock, BorderLayout.EAST);
        main.add(hdr, BorderLayout.NORTH);

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 6, 0));
        statsRow.setOpaque(false);
        statsRow.setPreferredSize(new Dimension(0, 85));
        statsRow.add(wsStatPanel("Total Tables", new Color(0xFF, 0xF3, 0xB0, 210), "12", false));
        JPanel ep = wsStatPanel("Empty",    new Color(0xDD, 0xDD, 0xDD, 210), "0", true);
        JPanel rp = wsStatPanel("Reserved", new Color(0xFF, 0xEB, 0x3B, 210), "0", true);
        JPanel op = wsStatPanel("Occupied", new Color(0xFF, 0x5C, 0x5C, 210), "0", true);
        statEmpty    = (JLabel) ep.getClientProperty("num");
        statReserved = (JLabel) rp.getClientProperty("num");
        statOccupied = (JLabel) op.getClientProperty("num");
        statsRow.add(ep); statsRow.add(rp); statsRow.add(op);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(255, 255, 255, 80));
        outer.setBorder(BorderFactory.createLineBorder(WS_GOLD_DARK));

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(WS_GOLD_SEMI);
        bar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        JLabel bl = new JLabel("Table Overview");
        bl.setFont(new Font("Georgia", Font.BOLD, 13));
        bl.setForeground(WS_BROWN);
        bar.add(bl, BorderLayout.WEST);
        outer.add(bar, BorderLayout.NORTH);

        tableGrid = new JPanel(new GridLayout(3, 4, 8, 8));
        tableGrid.setOpaque(false);
        tableGrid.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        for (int i = 0; i < 12; i++) {
            final int n = i + 1;
            JButton b = wsTableBtn(n);
            b.addActionListener(e -> openOrder(n));
            tableGrid.add(b);
        }
        outer.add(tableGrid, BorderLayout.CENTER);

        JPanel centre = new JPanel(new BorderLayout(0, 4));
        centre.setOpaque(false);
        centre.add(statsRow, BorderLayout.NORTH);
        centre.add(outer,    BorderLayout.CENTER);
        main.add(centre, BorderLayout.CENTER);
        return main;
    }

    private JPanel wsStatPanel(String label, Color bg, String val, boolean storeRef) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setBackground(bg);
        p.setBorder(BorderFactory.createLineBorder(WS_GOLD_DARK));
        JLabel num = new JLabel(val, SwingConstants.CENTER);
        num.setFont(new Font("Georgia", Font.BOLD, 30));
        num.setForeground(WS_BROWN);
        JLabel txt = new JLabel(label, SwingConstants.CENTER);
        txt.setFont(new Font("Georgia", Font.PLAIN, 11));
        txt.setForeground(WS_BROWN2);
        txt.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        p.add(num, BorderLayout.CENTER);
        p.add(txt, BorderLayout.SOUTH);
        if (storeRef) p.putClientProperty("num", num);
        return p;
    }

    private JButton wsTableBtn(int n) {
        String st  = tableStatus[n - 1];
        Color rsv = reservationColor(n);
        Color bg  = rsv != null ? rsv : wsStColor(st);
        String displaySt = rsv != null ? reservationColorText(n) : st;
        String em  = wsStEmoji(displaySt);

        JButton b = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed()  ? bg.darker()
                        : getModel().isRollover() ? bg.brighter()
                        : bg;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(WS_GOLD_DARK);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
            @Override public boolean isOpaque() { return false; }
        };
        b.setLayout(new GridBagLayout());
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx   = 0;
        gbc.gridy   = GridBagConstraints.RELATIVE;
        gbc.weightx = 1;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.insets  = new Insets(1, 4, 1, 4);

        if (!em.isEmpty()) {
            JLabel ic = new JLabel(em, SwingConstants.CENTER);
            ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            b.add(ic, gbc);
        }
        JLabel nl = new JLabel("TABLE " + n, SwingConstants.CENTER);
        nl.setFont(new Font("Georgia", Font.BOLD, 14));
        nl.setForeground(WS_BROWN);
        b.add(nl, gbc);

        if (!displaySt.equals("EMPTY")) {
            JLabel sl = new JLabel(displaySt, SwingConstants.CENTER);
            sl.setFont(new Font("Georgia", Font.ITALIC, 11));
            sl.setForeground(WS_BROWN);
            b.add(sl, gbc);
        }

        if (st.equals("OCCUPIED") && !tableOrders[n - 1].isEmpty()) {
            JLabel badge = new JLabel(tableOrders[n - 1].size() + " items", SwingConstants.CENTER);
            badge.setFont(new Font("Georgia", Font.PLAIN, 10));
            badge.setForeground(new Color(0x8B, 0x25, 0x00));
            b.add(badge, gbc);
        }
        if (orderReady[n - 1]) {
            JLabel readyBadge = new JLabel("✔ ORDER READY", SwingConstants.CENTER);
            readyBadge.setFont(new Font("Georgia", Font.BOLD, 10));
            readyBadge.setForeground(new Color(0x1B, 0x5E, 0x20));
            b.add(readyBadge, gbc);
        }
        return b;
    }

    private Color wsStColor(String s) {
        if (s.equals("RESERVED")) return WS_T_RESERVED;
        if (s.equals("OCCUPIED")) return WS_T_OCCUPIED;
        return WS_T_EMPTY;
    }

    private int findReservationRow(int tno) {
        String tbl = "T" + tno;
        for (int i = 0; i < DataStore.reservationModel.getRowCount(); i++) {
            String rtbl = DataStore.reservationModel.getValueAt(i, 3).toString();
            if (tbl.equals(rtbl) && !DataStore.reservationModel.getValueAt(i, 7).toString().equals("Cancelled"))
                return i;
        }
        return -1;
    }

    private Color reservationColor(int tno) {
        for (int j = 0; j < DataStore.reservationModel.getRowCount(); j++) {
            String tbl = "T" + tno;
            if (!tbl.equals(DataStore.reservationModel.getValueAt(j, 3).toString())) continue;
            String st = DataStore.reservationModel.getValueAt(j, 7).toString();
            if (st.equals("Confirmed")) return new Color(0xFF, 0x7F, 0x7F);
            if (st.equals("Pending"))   return new Color(0xFF, 0xF3, 0xB0);
        }
        return null;
    }
    private String reservationColorText(int tno) {
        for (int j = 0; j < DataStore.reservationModel.getRowCount(); j++) {
            String tbl = "T" + tno;
            if (!tbl.equals(DataStore.reservationModel.getValueAt(j, 3).toString())) continue;
            String st = DataStore.reservationModel.getValueAt(j, 7).toString();
            if (st.equals("Confirmed")) return "OCCUPIED";
            if (st.equals("Pending"))   return "RESERVED";
        }
        return "EMPTY";
    }
    private String wsStEmoji(String s) {
        if (s.equals("RESERVED")) return "\uD83D\uDD16";
        if (s.equals("OCCUPIED")) return "\uD83C\uDF7D";
        return "";
    }

    public void refreshTables() {
        DataStore.loadReservations();
        for (int i = 0; i < 12; i++) {
            if (tableStatus[i].equals("OCCUPIED")) continue;
            tableStatus[i] = findReservationRow(i + 1) >= 0 ? "RESERVED" : "EMPTY";
        }
        tableGrid.removeAll();
        for (int i = 0; i < 12; i++) {
            final int n = i + 1;
            JButton b = wsTableBtn(n);
            b.addActionListener(e -> openOrder(n));
            tableGrid.add(b);
        }
        tableGrid.revalidate();
        tableGrid.repaint();
        refreshStatCards();
    }

    private void refreshStatCards() {
        int e = 0, r = 0, o = 0;
        for (int i = 0; i < 12; i++) {
            String effective = reservationColorText(i + 1);
            if (effective.equals("EMPTY")) {
                if      (tableStatus[i].equals("EMPTY"))    e++;
                else if (tableStatus[i].equals("RESERVED")) r++;
                else                                        o++;
            } else if (effective.equals("RESERVED")) r++;
            else                                    o++;
        }
        if (statEmpty    != null) statEmpty.setText(String.valueOf(e));
        if (statReserved != null) statReserved.setText(String.valueOf(r));
        if (statOccupied != null) statOccupied.setText(String.valueOf(o));
    }

    private void wsTick(JLabel l) {
        java.time.LocalDateTime n = java.time.LocalDateTime.now();
        String day = n.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);
        String mon = n.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);
        l.setText(String.format("%02d:%02d:%02d  |  %s %d %s %d",
            n.getHour(), n.getMinute(), n.getSecond(),
            day, n.getDayOfMonth(), mon, n.getYear()));
    }

    private static double wsParsePrice(String s) {
        try { return Double.parseDouble(s.replace("Rs", "").trim()); }
        catch (Exception e) { return 0; }
    }

    private void openOrder(int tNo) {
        setVisible(false);

        JFrame of = new JFrame("WOK YOUR WAY — Table " + tNo);
        of.setMinimumSize(new Dimension(960, 580));
        of.setSize(1200, 700);
        of.setLocationRelativeTo(null);
        of.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        WsBgPanel root = new WsBgPanel(new BorderLayout());
        of.setContentPane(root);

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(WS_GOLD_SEMI);
        hdr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, WS_GOLD_DARK),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        JButton backBtn = wsMakeBtn("\u2190 Back", WS_BROWN, Color.WHITE, 12);
        backBtn.setPreferredSize(new Dimension(90, 36));
        JLabel titleLbl = new JLabel("TABLE " + tNo + "  \u2014  ORDER MANAGEMENT", SwingConstants.CENTER);
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 16));
        titleLbl.setForeground(WS_BROWN);
        JLabel statusLbl = new JLabel("Status: " + tableStatus[tNo - 1], SwingConstants.RIGHT);
        statusLbl.setFont(new Font("Georgia", Font.BOLD, 12));
        statusLbl.setForeground(WS_BROWN);
        statusLbl.setPreferredSize(new Dimension(160, 36));
        hdr.add(backBtn,  BorderLayout.WEST);
        hdr.add(titleLbl, BorderLayout.CENTER);
        hdr.add(statusLbl, BorderLayout.EAST);
        root.add(hdr, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerSize(4);
        split.setDividerLocation(0.55);
        split.setResizeWeight(0.55);
        split.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        split.setOpaque(false);
        root.add(split, BorderLayout.CENTER);

        List<OrderItem> orderList = tableOrders[tNo - 1];
        DefaultTableModel orderModel = new DefaultTableModel(
            new String[]{"Item", "Category", "Price", "Qty", "Total"}, 0
        ) { public boolean isCellEditable(int r, int c) { return false; } };
        for (OrderItem it : orderList)
            orderModel.addRow(new Object[]{it.name, it.category, "Rs " + (int) it.price, it.qty, "Rs " + (int)(it.price * it.qty)});

        DefaultTableModel menuDisplay = new DefaultTableModel(
            new String[]{"#", "Item Name", "Category", "Price"}, 0
        ) { public boolean isCellEditable(int r, int c) { return false; } };
        for (int i = 0; i < DataStore.menuModel.getRowCount(); i++)
            menuDisplay.addRow(new Object[]{
                DataStore.menuModel.getValueAt(i, 0),
                DataStore.menuModel.getValueAt(i, 1),
                DataStore.menuModel.getValueAt(i, 2),
                DataStore.menuModel.getValueAt(i, 3)});

        JTable menuTable = wsStyledTable(menuDisplay);
        menuTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        menuTable.getColumnModel().getColumn(0).setMaxWidth(40);
        menuTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        menuTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        menuTable.getColumnModel().getColumn(3).setPreferredWidth(80);

        JScrollPane menuScroll = new JScrollPane(menuTable,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.setBorder(BorderFactory.createEmptyBorder());
        menuScroll.setOpaque(false);
        menuScroll.getViewport().setOpaque(false);

        JComboBox<String> catBox = new JComboBox<>(
            new String[]{"All", "Starter", "Main Course", "Fast Food", "Drinks"});
        catBox.setFont(new Font("Georgia", Font.PLAIN, 11));
        catBox.setBackground(Color.WHITE);
        catBox.addActionListener(e -> {
            String sel = (String) catBox.getSelectedItem();
            menuDisplay.setRowCount(0);
            for (int i = 0; i < DataStore.menuModel.getRowCount(); i++) {
                String cat = (String) DataStore.menuModel.getValueAt(i, 2);
                if ("All".equals(sel) || cat.equals(sel))
                    menuDisplay.addRow(new Object[]{
                        DataStore.menuModel.getValueAt(i, 0),
                        DataStore.menuModel.getValueAt(i, 1),
                        DataStore.menuModel.getValueAt(i, 2),
                        DataStore.menuModel.getValueAt(i, 3)});
            }
        });
        JLabel fl = new JLabel("Filter:");
        fl.setFont(new Font("Georgia", Font.BOLD, 11));
        fl.setForeground(WS_BROWN);
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        filterBar.setBackground(new Color(0xFF, 0xF3, 0xB0, 220));
        filterBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, WS_GOLD_DARK));
        filterBar.add(fl); filterBar.add(catBox);

        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        qtySpinner.setFont(new Font("Georgia", Font.PLAIN, 12));
        qtySpinner.setPreferredSize(new Dimension(60, 30));
        JButton addToOrderBtn = wsMakeBtn("Add Selected to Order", WS_GOLD_NAV, WS_BROWN, 12);
        JLabel ql = new JLabel("Qty:");
        ql.setFont(new Font("Georgia", Font.BOLD, 12));
        ql.setForeground(WS_BROWN);
        JPanel qr = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        qr.setOpaque(false);
        qr.add(ql); qr.add(qtySpinner);
        JPanel addBar = new JPanel(new BorderLayout(6, 0));
        addBar.setBackground(new Color(0xFF, 0xF3, 0xB0, 220));
        addBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, WS_GOLD_DARK),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        addBar.add(qr, BorderLayout.WEST);
        addBar.add(addToOrderBtn, BorderLayout.CENTER);

        JPanel mp = new JPanel(new BorderLayout());
        mp.setBackground(new Color(255, 255, 255, 170));
        mp.setBorder(BorderFactory.createLineBorder(WS_GOLD_DARK));
        mp.add(wsTitBar("  Menu Items"), BorderLayout.NORTH);
        JPanel mc = new JPanel(new BorderLayout());
        mc.setOpaque(false);
        mc.add(filterBar,  BorderLayout.NORTH);
        mc.add(menuScroll, BorderLayout.CENTER);
        mc.add(addBar,     BorderLayout.SOUTH);
        mp.add(mc, BorderLayout.CENTER);
        split.setLeftComponent(mp);

        JTable oTable = wsStyledTable(orderModel);
        int[] colWidths = {140, 90, 70, 40, 80};
        for (int i = 0; i < colWidths.length; i++)
            oTable.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);

        JScrollPane oScroll = new JScrollPane(oTable,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        oScroll.setBorder(BorderFactory.createLineBorder(WS_GOLD_DARK));
        oScroll.setOpaque(false);
        oScroll.getViewport().setOpaque(false);

        JLabel kitchenStatusLbl = new JLabel(
            kitchenSent[tNo - 1] ? "  \u2714 Sent to kitchen" : "  Not sent to kitchen yet",
            SwingConstants.LEFT);
        kitchenStatusLbl.setFont(new Font("Georgia", Font.ITALIC, 11));
        kitchenStatusLbl.setForeground(kitchenSent[tNo - 1] ? new Color(0x2E, 0x7D, 0x32) : WS_BROWN2);

        // Banner shown when the kitchen marks the order as Ready
        JLabel readyBanner = new JLabel(
            "  \u2705 ORDER READY — Please serve Table " + tNo + "!",
            SwingConstants.CENTER);
        readyBanner.setFont(new Font("Georgia", Font.BOLD, 13));
        readyBanner.setForeground(new Color(0x1B, 0x5E, 0x20));
        readyBanner.setOpaque(true);
        readyBanner.setBackground(new Color(0xD1, 0xFF, 0xBD));
        readyBanner.setBorder(BorderFactory.createLineBorder(new Color(0x2E, 0x7D, 0x32), 2));
        readyBanner.setVisible(orderReady[tNo - 1]);

        JButton removeBtn   = wsMakeBtn("Remove Item",           new Color(0xE5, 0x73, 0x73), Color.WHITE, 11);
        JButton clearBtn    = wsMakeBtn("Clear Table",           new Color(0xB7, 0x1C, 0x1C), Color.WHITE, 11);
        JButton confirmBtn  = wsMakeBtn("Confirm Order",         WS_GREEN_BTN,                Color.WHITE, 11);
        JButton kitchenBtn  = wsMakeBtn("Send to Kitchen",       WS_TEAL_BTN,                 Color.WHITE, 11);
        JButton cashierBtn  = wsMakeBtn("Send to Cashier",       new Color(0x1B, 0x5E, 0x20), Color.WHITE, 11);
        JButton reservedBtn = wsMakeBtn("Mark Table as Reserved",WS_AMBER_BTN,                Color.WHITE, 11);

        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        btnPanel.add(wsBtnRow(removeBtn, clearBtn, confirmBtn));
        btnPanel.add(wsBtnRow(kitchenBtn, cashierBtn));
        btnPanel.add(wsBtnRow(reservedBtn));

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 2));
        bottomPanel.setOpaque(false);
        bottomPanel.add(readyBanner,      BorderLayout.NORTH);
        bottomPanel.add(kitchenStatusLbl, BorderLayout.CENTER);
        bottomPanel.add(btnPanel,         BorderLayout.SOUTH);

        JPanel rp = new JPanel(new BorderLayout());
        rp.setBackground(new Color(255, 255, 255, 170));
        rp.setBorder(BorderFactory.createLineBorder(WS_GOLD_DARK));
        rp.add(wsTitBar("  Order — Table " + tNo), BorderLayout.NORTH);
        rp.add(oScroll,     BorderLayout.CENTER);
        rp.add(bottomPanel, BorderLayout.SOUTH);
        split.setRightComponent(rp);

        of.setVisible(true);

        // Poll every 3 seconds to check if kitchen has marked this table's order as Ready
        javax.swing.Timer readyPoller = new javax.swing.Timer(3000, null);
        readyPoller.addActionListener(ev -> {
            if (orderReady[tNo - 1] && !readyBanner.isVisible()) {
                readyBanner.setVisible(true);
                of.revalidate(); of.repaint();
                JOptionPane.showMessageDialog(of,
                    "Table " + tNo + " order is READY — please serve the customer!",
                    "\u2705 Order Ready", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        readyPoller.start();

        Runnable goBack = () -> { readyPoller.stop(); refreshTables(); setVisible(true); of.dispose(); };
        backBtn.addActionListener(e -> goBack.run());
        of.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { goBack.run(); }
        });

        addToOrderBtn.addActionListener(e -> {
            int row = menuTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(of, "Please select an item from the menu first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String iName  = (String) menuDisplay.getValueAt(row, 1);
            String iCat   = (String) menuDisplay.getValueAt(row, 2);
            String pStr   = (String) menuDisplay.getValueAt(row, 3);
            double iPrice = wsParsePrice(pStr);
            int    qty    = (int) qtySpinner.getValue();
            boolean found = false;
            for (int i = 0; i < orderList.size(); i++) {
                if (orderList.get(i).name.equals(iName)) {
                    orderList.get(i).qty += qty;
                    orderModel.setValueAt(orderList.get(i).qty, i, 3);
                    orderModel.setValueAt("Rs " + (int)(orderList.get(i).price * orderList.get(i).qty), i, 4);
                    found = true; break;
                }
            }
            if (!found) {
                orderModel.addRow(new Object[]{iName, iCat, pStr, qty, "Rs " + (int)(iPrice * qty)});
                orderList.add(new OrderItem(iName, iCat, iPrice, qty));
            }
        });

        removeBtn.addActionListener(e -> {
            int row = oTable.getSelectedRow();
            if (row >= 0) { orderModel.removeRow(row); orderList.remove(row); }
            else JOptionPane.showMessageDialog(of, "Select an item to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
        });

        clearBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(of, "Clear all orders for Table " + tNo + "?",
                    "Clear", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int rrow = findReservationRow(tNo);
                if (rrow >= 0) {
                    DataStore.updateReservationStatus(rrow, "Cancelled");
                    DataStore.loadReservations();
                }
                orderModel.setRowCount(0);
                orderList.clear();
                kitchenSent[tNo - 1]    = false;
                kitchenOrderId[tNo - 1] = -1;
                orderReady[tNo - 1]     = false;
                tableStatus[tNo - 1] = "EMPTY";
                statusLbl.setText("Status: EMPTY");
                refreshTables(); setVisible(true); of.dispose();
            }
        });

        confirmBtn.addActionListener(e -> {
            if (orderList.isEmpty()) {
                JOptionPane.showMessageDialog(of, "No items added yet!", "Empty Order", JOptionPane.WARNING_MESSAGE);
                return;
            }
            tableStatus[tNo - 1] = "OCCUPIED";
            statusLbl.setText("Status: OCCUPIED");
            JOptionPane.showMessageDialog(of, "Order confirmed for Table " + tNo + "!", "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
            refreshTables();
        });

        kitchenBtn.addActionListener(e -> {
            if (orderList.isEmpty()) {
                JOptionPane.showMessageDialog(of, "No items to send to kitchen!", "Empty Order", JOptionPane.WARNING_MESSAGE);
                return;
            }
            StringBuilder sb = new StringBuilder(
                "==============================\n        KITCHEN ORDER\n==============================\nTable: ")
                .append(tNo).append("\n\n");
            for (String cat : new String[]{"Starter", "Main Course", "Fast Food", "Drinks"}) {
                boolean hp = false;
                for (OrderItem it : orderList) {
                    if (it.category.equals(cat)) {
                        if (!hp) { sb.append("[ ").append(cat.toUpperCase()).append(" ]\n"); hp = true; }
                        sb.append(String.format("  %-26s x%d\n", it.name, it.qty));
                    }
                }
            }
            sb.append("\n==============================");
            JTextArea ta = new JTextArea(sb.toString());
            ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
            ta.setEditable(false);
            ta.setBackground(new Color(0xE0, 0xF2, 0xF1));
            JScrollPane sc = new JScrollPane(ta);
            sc.setPreferredSize(new Dimension(360, 280));
            JOptionPane.showMessageDialog(of, sc, "Kitchen — Table " + tNo, JOptionPane.INFORMATION_MESSAGE);
            int oid = DataStore.sendOrderToKitchen("T" + tNo, orderList);
            kitchenOrderId[tNo - 1] = oid;
            kitchenSent[tNo - 1]    = true;
            orderReady[tNo - 1]     = false; // reset — new order just sent
            kitchenStatusLbl.setText("  \u2714 Sent to kitchen");
            kitchenStatusLbl.setForeground(new Color(0x2E, 0x7D, 0x32));
            readyBanner.setVisible(false);
            if (!tableStatus[tNo - 1].equals("OCCUPIED")) {
                tableStatus[tNo - 1] = "OCCUPIED";
                statusLbl.setText("Status: OCCUPIED");
                refreshTables();
            }
        });

        cashierBtn.addActionListener(e -> {
            if (orderList.isEmpty()) {
                JOptionPane.showMessageDialog(of, "No order to send!", "Empty Order", JOptionPane.WARNING_MESSAGE);
                return;
            }
            StringBuilder sb = new StringBuilder(
                "==============================\n        CASHIER COPY\n==============================\nTable: ")
                .append(tNo).append("\n\n");
            double sub = 0;
            for (OrderItem it : orderList) {
                double t = it.price * it.qty;
                sb.append(String.format("  %-22s x%d = Rs %.0f\n", it.name, it.qty, t));
                sub += t;
            }
            double tax = sub * 0.10;
            sb.append(String.format("\n----------------------------\nSubtotal : Rs %.0f\nTax (10%%): Rs %.0f\nTOTAL    : Rs %.0f\n==============================",
                sub, tax, sub + tax));
            JTextArea ta = new JTextArea(sb.toString());
            ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
            ta.setEditable(false);
            ta.setBackground(new Color(0xFF, 0xF3, 0xB0));
            JScrollPane sc = new JScrollPane(ta);
            sc.setPreferredSize(new Dimension(360, 280));
            JOptionPane.showMessageDialog(of, sc, "Cashier — Table " + tNo, JOptionPane.INFORMATION_MESSAGE);
        });

        reservedBtn.addActionListener(e -> {
            int rrow = findReservationRow(tNo);
            if (rrow >= 0) {
                if (JOptionPane.showConfirmDialog(of, "Cancel reservation for Table " + tNo + "?",
                        "Cancel Reservation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    DataStore.updateReservationStatus(rrow, "Cancelled");
                    DataStore.loadReservations();
                    tableStatus[tNo - 1] = "EMPTY";
                    statusLbl.setText("Status: EMPTY");
                    JOptionPane.showMessageDialog(of, "Reservation cancelled.");
                    refreshTables();
                }
            } else {
                JTextField cuF = new JTextField("Walk-in");
                JComboBox<String> stBox = new JComboBox<>(new String[]{"Pending", "Confirmed"});
                stBox.setFont(new Font("Georgia", Font.PLAIN, 14));
                int r = JOptionPane.showConfirmDialog(of,
                    new Object[]{"Customer Name:", cuF, "Table:", "T" + tNo, "Status:", stBox},
                    "Reserve Table " + tNo, JOptionPane.OK_CANCEL_OPTION);
                if (r == JOptionPane.OK_OPTION) {
                    String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    DataStore.addReservation(cuF.getText().trim(), "N/A", "T" + tNo, now, 2, stBox.getSelectedItem().toString());
                    DataStore.loadReservations();
                    tableStatus[tNo - 1] = "RESERVED";
                    statusLbl.setText("Status: RESERVED");
                    JOptionPane.showMessageDialog(of, "Table " + tNo + " reserved!", "Reserved", JOptionPane.INFORMATION_MESSAGE);
                    refreshTables();
                }
            }
        });
    }

    private JPanel wsBtnRow(JButton... btns) {
        JPanel p = new JPanel(new GridLayout(1, btns.length, 6, 0));
        p.setOpaque(false);
        for (JButton b : btns) p.add(b);
        return p;
    }

    private JPanel wsTitBar(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WS_GOLD_SEMI);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, WS_GOLD_DARK),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Georgia", Font.BOLD, 13));
        l.setForeground(WS_BROWN);
        p.add(l, BorderLayout.WEST);
        return p;
    }

    private JButton wsMakeBtn(String text, Color bg, Color fg, int size) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed()  ? bg.darker()
                        : getModel().isRollover() ? bg.brighter()
                        : bg;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(WS_GOLD_DARK);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()  - fm.stringWidth(getText())) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
            @Override public boolean isOpaque() { return false; }
        };
        b.setFont(new Font("Georgia", Font.BOLD, size));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTable wsStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(28);
        t.setFont(new Font("Georgia", Font.PLAIN, 12));
        t.setForeground(WS_BROWN);
        t.setGridColor(new Color(0xE8, 0xC8, 0x4A));
        t.setShowGrid(true);
        t.setOpaque(false);
        t.setFillsViewportHeight(true);
        t.setSelectionBackground(new Color(0xFF, 0xD7, 0x00, 200));
        t.setSelectionForeground(WS_BROWN);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tt, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tt, v, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0
                        ? new Color(255, 255, 255, 180)
                        : new Color(255, 248, 200, 180));
                    setForeground(WS_BROWN);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
                return this;
            }
        });
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Georgia", Font.BOLD, 12));
        h.setBackground(new Color(0xFF, 0xD7, 0x00, 230));
        h.setForeground(WS_BROWN);
        h.setPreferredSize(new Dimension(0, 30));
        h.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) h.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        return t;
    }

    private JLabel wsCenteredLabel(String text, Font font, Color fg) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(font); l.setForeground(fg);
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    private JSeparator wsMakeSep() {
        JSeparator s = new JSeparator();
        s.setForeground(WS_GOLD_DARK);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        return s;
    }

    private Component wsVgap(int h) { return Box.createVerticalStrut(h); }
}
