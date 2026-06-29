import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

class UI {
    static javax.swing.Timer adminClockTimer = null;
    static final Color GOLD       = new Color(0xFF,0xD7,0x00,210);
    static final Color GOLD_DARK  = new Color(0xC8,0xA8,0x00);
    static final Color GOLD_NAV   = new Color(0xE6,0xAC,0x00);
    static final Color SIDEBAR    = new Color(0xFF,0xEB,0x7A,215);
    static final Color BROWN      = new Color(0x3D,0x2B,0x1F);
    static final Color BROWN2     = new Color(0x6B,0x4C,0x3B);
    static final Color RED_BTN    = new Color(0xC6,0x28,0x28);
    static final Color GREEN_BTN  = new Color(0x2E,0x7D,0x32);
    static final Color ORANGE_BTN = new Color(0xE6,0x5C,0x00);
    static final Color TEAL_BTN   = new Color(0x00,0x69,0x6E);
    static final Color ROW_EVEN   = new Color(255,255,255,180);
    static final Color ROW_ODD    = new Color(255,248,200,180);

    static Font brandFont(int size) {
        String[] preferred = {"Palatino Linotype","Palatino","Book Antiqua","Garamond","Georgia"};
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Set<String> available = new HashSet<>(Arrays.asList(ge.getAvailableFontFamilyNames()));
        for (String f : preferred)
            if (available.contains(f)) return new Font(f, Font.BOLD, size);
        return new Font("Serif", Font.BOLD, size);
    }

    // ── FIX: bgLabel now reports its preferred size so JScrollPane can scroll correctly ──
    static JLabel bgLabel() {
        ImageIcon gif = new ImageIcon(UI.class.getResource("/yellow.gif"));
        try {
            java.io.File f = new java.io.File("yellow.gif");
            if (f.exists()) gif = new ImageIcon(f.getAbsolutePath());
        } catch (Exception ignored) {}
        JLabel bg = gif != null ? new JLabel(gif) {
            @Override public Dimension getPreferredSize() { return new Dimension(1200, 700); }
        } : new JLabel() {
            @Override public Dimension getPreferredSize() { return new Dimension(1200, 700); }
        };
        bg.setBackground(new Color(0xFF,0xF8,0xDC));
        bg.setOpaque(gif == null);
        bg.setBounds(0,0,1200,700);
        bg.setLayout(null);
        return bg;
    }

    static JFrame frame(String title) {
        JFrame f = new JFrame(title);
        f.setLayout(null);
        f.setSize(1200,700);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return f;
    }

    static JPanel header(String title, int x, int y, int w) {
        JPanel p = new JPanel(null);
        p.setBounds(x,y,w,46);
        p.setBackground(new Color(0xFF,0xD7,0x00,185));
        p.setBorder(BorderFactory.createLineBorder(GOLD_DARK,1));
        JLabel l = new JLabel(title, SwingConstants.CENTER);
        l.setBounds(0,0,w,46); l.setFont(new Font("Georgia",Font.BOLD,19));
        l.setForeground(BROWN); p.add(l); return p;
    }

    static JPanel content(int x,int y,int w,int h) {
        JPanel p = new JPanel(null);
        p.setBounds(x,y,w,h);
        p.setBackground(new Color(255,255,255,165));
        p.setBorder(BorderFactory.createLineBorder(GOLD_DARK,1));
        return p;
    }

    static JPanel titleBar(String title, int w, int h) {
        JPanel p = new JPanel(null);
        p.setBounds(0,0,w,h);
        p.setBackground(new Color(0xFF,0xD7,0x00,210));
        JLabel l = new JLabel(title,SwingConstants.CENTER);
        l.setBounds(0,0,w,h); l.setFont(new Font("Georgia",Font.BOLD,13));
        l.setForeground(BROWN); p.add(l); return p;
    }

    static JTable table(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(26); t.setFont(new Font("Georgia",Font.PLAIN,12));
        t.setForeground(BROWN); t.setGridColor(new Color(0xE8,0xC8,0x4A));
        t.setShowGrid(true); t.setOpaque(false); t.setFillsViewportHeight(true);
        t.setSelectionBackground(new Color(0xFF,0xD7,0x00,200));
        t.setSelectionForeground(BROWN);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(
                    JTable tb,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(tb,v,sel,foc,row,col);
                if(!sel){ setBackground(row%2==0?ROW_EVEN:ROW_ODD); setForeground(BROWN); }
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0,6,0,6)); return this;
            }
        });
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Georgia",Font.BOLD,14));
        h.setBackground(new Color(0xFF,0xD7,0x00,220)); h.setForeground(BROWN);
        h.setPreferredSize(new Dimension(0,40)); h.setReorderingAllowed(false);
        ((DefaultTableCellRenderer)h.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.CENTER);
        return t;
    }

    static JScrollPane scroll(JTable t, int x,int y,int w,int h) {
        JScrollPane s = new JScrollPane(t);
        s.setBounds(x,y,w,h);
        s.setBorder(BorderFactory.createLineBorder(GOLD_DARK,1));
        s.setOpaque(false); s.getViewport().setOpaque(false);
        return s;
    }

    static JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Georgia",Font.BOLD,15));
        b.setBackground(bg); b.setForeground(BROWN);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(GOLD_DARK,1));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    static JButton deleteBtn(String text) { return button(text,new Color(0xFF,0x7F,0x7F)); }

    static JLabel statCard(JLabel bg,String label,String value,int x,int y,int w,int h,Color color) {
        JPanel card = new JPanel(null);
        card.setBounds(x,y,w,h); card.setBackground(color);
        card.setBorder(BorderFactory.createLineBorder(GOLD_DARK,1));
        JLabel num = new JLabel(value,SwingConstants.CENTER);
        num.setBounds(0,4,w,38); num.setFont(new Font("Georgia",Font.BOLD,24)); num.setForeground(BROWN);
        JLabel lbl = new JLabel(label,SwingConstants.CENTER);
        lbl.setBounds(0,42,w,22); lbl.setFont(new Font("Georgia",Font.PLAIN,12)); lbl.setForeground(DataStore.TEXT_MED);
        card.add(num); card.add(lbl); bg.add(card); return num;
    }

    static JPanel sidebar(JFrame frame, String activeScreen) {
        JPanel sb = new JPanel(null);
        sb.setBounds(0,0,220,670); sb.setBackground(SIDEBAR);
        sb.setBorder(BorderFactory.createMatteBorder(0,0,0,2,GOLD_DARK));

        JLabel brand = new JLabel("Wok Your Way", SwingConstants.CENTER);
        brand.setBounds(8,14,204,38); brand.setFont(brandFont(20));
        brand.setForeground(BROWN); sb.add(brand);

        JLabel sub = new JLabel("Restaurant Management", SwingConstants.CENTER);
        sub.setBounds(8,52,204,18); sub.setFont(new Font("Georgia",Font.PLAIN,11));
        sub.setForeground(BROWN2); sb.add(sub);

        if (!DataStore.loggedInRole.isEmpty()) {
            JLabel role = new JLabel("[ "+DataStore.loggedInRole+" ]",SwingConstants.CENTER);
            role.setBounds(8,70,204,16); role.setFont(new Font("Georgia",Font.ITALIC,10));
            role.setForeground(BROWN2); sb.add(role);
        }

        JSeparator sep = new JSeparator();
        sep.setBounds(18,88,184,2); sep.setForeground(GOLD_DARK); sb.add(sep);

        String[] names={"Overview","Menu","Staff","Reservations","Inventory","Reports","Recipes"};
        String[] screens={"Overview","Menu","Staff","Reservations","Inventory","Reports","Recipes"};
        for (int i=0;i<names.length;i++){
            JButton btn = new JButton(names[i]);
            btn.setBounds(20,96+i*56,180,40);
            btn.setFont(new Font("Georgia",Font.BOLD,14));
            btn.setBackground(names[i].equals(activeScreen)?GOLD_DARK:GOLD_NAV);
            btn.setForeground(BROWN); btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(GOLD_DARK,1));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final String s=screens[i];
            btn.addActionListener(e->{frame.dispose(); navigate(s);});
            sb.add(btn);
        }

        JButton logout = new JButton("Log Out");
        logout.setBounds(20,614,180,40);
        logout.setFont(new Font("Georgia",Font.BOLD,14));
        logout.setBackground(new Color(0xD9,0x53,0x4F));
        logout.setForeground(Color.WHITE); logout.setFocusPainted(false);
        logout.setBorder(BorderFactory.createLineBorder(new Color(0xC0,0x39,0x2B),1));
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e->{
            if(JOptionPane.showConfirmDialog(frame,"Log out?","Logout",JOptionPane.YES_NO_OPTION)==0){
                frame.dispose(); new LoginSignUp();
            }
        });
        sb.add(logout);

        // Live date/time clock
        JLabel adminClock=new JLabel("",SwingConstants.CENTER);
        adminClock.setBounds(20,508,180,52); adminClock.setFont(new Font("Georgia",Font.BOLD,12));
        adminClock.setForeground(BROWN); adminClock.setOpaque(true);
        adminClock.setBackground(new Color(0xFF,0xD7,0x00,180));
        adminClock.setBorder(BorderFactory.createLineBorder(GOLD_DARK,1));
        sb.add(adminClock);
        if (adminClockTimer != null) adminClockTimer.stop();
        adminClockTimer = new javax.swing.Timer(1000, e -> {
            java.time.LocalDateTime now=java.time.LocalDateTime.now();
            String day=now.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT,java.util.Locale.ENGLISH);
            String mon=now.getMonth().getDisplayName(java.time.format.TextStyle.SHORT,java.util.Locale.ENGLISH);
            adminClock.setText(String.format("<html><center>%02d:%02d:%02d<br>%s %d %s %d</center></html>",
                now.getHour(),now.getMinute(),now.getSecond(),day,now.getDayOfMonth(),mon,now.getYear()));
        });
        adminClockTimer.start();

        return sb;
    }

    static void navigate(String s) {
        switch(s){
            case "Overview":     new AdminDashboard();       break;
            case "Menu":         new MenuManagement();        break;
            case "Staff":        new StaffManagement();       break;
            case "Reservations": new ReservationManagement(); break;
            case "Inventory":    new InventoryManagement();   break;
            case "Reports":      new Reports();               break;
            case "Recipes":      new RecipeManagement();      break;
        }
    }

    static JTextField styledField(boolean isPassword) {
        JTextField f = isPassword ? new JPasswordField() : new JTextField();
        f.setFont(new Font("SansSerif",Font.PLAIN,14));
        f.setForeground(BROWN); f.setBackground(new Color(250,248,240));
        f.setBorder(new CompoundBorder(
            new LineBorder(new Color(245,200,0),1,true),new EmptyBorder(8,12,8,12)));
        f.setPreferredSize(new Dimension(0,44)); return f;
    }

    // Direct content pane — no scroll bars
    static JLabel bgWithScroll(JFrame f) {
        JLabel bg = bgLabel();
        f.setContentPane(bg);
        return bg;
    }
}
