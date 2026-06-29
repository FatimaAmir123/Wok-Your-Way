import java.awt.*;
import java.awt.geom.*;
import java.sql.*;
import javax.swing.*;

class LoginSignUp {
    private JFrame frame;

    LoginSignUp() {
        frame = new JFrame("Wok Your Way — Login");
        frame.setSize(520,620); frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        showLogin(); frame.setVisible(true);
    }

    private JPanel bgPanel() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                int w=getWidth(),h=getHeight();
                g2.setColor(new Color(250,248,238)); g2.fillRect(0,0,w,h);
                g2.setColor(new Color(255,200,0));
                Path2D.Double p=new Path2D.Double();
                p.moveTo(0,0); p.lineTo(w,0); p.lineTo(w,h*0.25);
                p.curveTo(w*0.7,h*0.42,w*0.3,h*0.1,0,h*0.32); p.closePath(); g2.fill(p);
                g2.setColor(new Color(0,0,0,25));
                for(int i=0;i<4;i++) for(int j=0;j<4;j++) g2.fillOval(w-85+i*12,45+j*12,3,3);
                for(int i=0;i<4;i++) for(int j=0;j<6;j++) g2.fillOval(28+i*12,h-125+j*12,3,3);
            }
        };
    }

    private JPanel cardPanel(int w,int h) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,245)); g2.fillRoundRect(0,0,getWidth(),getHeight(),24,24);
            }
        };
        card.setOpaque(false); card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(w,h)); return card;
    }

    private JButton goldBtn(String text) {
    JButton button = new JButton(text) {
        @Override 
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // golden Log In button
            g2.setColor(getModel().isRollover() ? new Color(230,175,0) : new Color(255,193,7));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            
            // 2. Manually draw the text inside the button to force it to be WHITE
            g2.setColor(Color.WHITE);
            g2.setFont(getFont());
            
            // This math perfectly centers your text both horizontally and vertically
            FontMetrics fm = g2.getFontMetrics();
            int stringWidth = fm.stringWidth(getText());
            int stringHeight = fm.getAscent();
            int x = (getWidth() - stringWidth) / 2;
            int y = (getHeight() + stringHeight) / 2 - 2; // -2 balances visual baseline
            
            g2.drawString(getText(), x, y);
        }
        @Override 
        protected void paintBorder(Graphics g) {}
    };
    
    button.setFont(new Font("Georgia", Font.BOLD, 15));
    button.setContentAreaFilled(false);
    button.setFocusPainted(false);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.setPreferredSize(new Dimension(0, 48));
    return button;
}

    private void row(JPanel card,GridBagConstraints gc,int y,Component c,int t,int b){
        gc.gridy=y; gc.insets=new Insets(t,36,b,36); card.add(c,gc);
    }
    private void labelRow(JPanel card,GridBagConstraints gc,int y,String text){
        JLabel l=new JLabel(text); l.setFont(new Font("SansSerif",Font.BOLD,13));
        l.setForeground(new Color(80,70,40)); gc.gridy=y; gc.insets=new Insets(0,36,5,36); card.add(l,gc);
    }

    void showLogin() {
        JPanel bg=bgPanel(); bg.setLayout(new GridBagLayout());
        JPanel card=cardPanel(400,520);
        GridBagConstraints gc=new GridBagConstraints();
        gc.gridx=0; gc.fill=GridBagConstraints.HORIZONTAL;

        JLabel brand=new JLabel("Wok Your Way",SwingConstants.CENTER);
        brand.setFont(UI.brandFont(22)); brand.setForeground(new Color(50,30,5));
        row(card,gc,0,brand,30,2);
        JLabel title=new JLabel("Welcome Back",SwingConstants.CENTER);
        title.setFont(new Font("SansSerif",Font.BOLD,22)); title.setForeground(new Color(50,40,10));
        row(card,gc,1,title,2,4);
        JLabel sub=new JLabel("Sign in to continue",SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif",Font.PLAIN,13)); sub.setForeground(new Color(130,120,100));
        row(card,gc,2,sub,0,18);
        JSeparator sep=new JSeparator(); sep.setForeground(new Color(245,200,0));
        row(card,gc,3,sep,0,18);

        labelRow(card,gc,4,"Username");
        JTextField userF=UI.styledField(false); row(card,gc,5,userF,0,16);
        labelRow(card,gc,6,"Password");
        JPasswordField passF=(JPasswordField)UI.styledField(true); row(card,gc,7,passF,0,8);

        JLabel err=new JLabel("",SwingConstants.CENTER);
        err.setFont(new Font("SansSerif",Font.PLAIN,12)); err.setForeground(new Color(180,30,30));
        row(card,gc,8,err,0,6);

        JButton loginBtn=goldBtn("Log In"); row(card,gc,9,loginBtn,0,10);
        JButton regLink=new JButton("No account? Sign Up");
        regLink.setFont(new Font("SansSerif",Font.PLAIN,12)); regLink.setForeground(new Color(100,80,20));
        regLink.setBorderPainted(false); regLink.setContentAreaFilled(false);
        regLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row(card,gc,10,regLink,0,28);

        loginBtn.addActionListener(e->doLogin(userF.getText().trim(),
            new String(passF.getPassword()).trim(),err,passF));
        passF.addActionListener(e->loginBtn.doClick());
        userF.addActionListener(e->passF.requestFocus());
        regLink.addActionListener(e->{frame.getContentPane().removeAll();showSignUp();
            frame.revalidate();frame.repaint();});

        wrap(bg,card); frame.setContentPane(bg); frame.revalidate(); frame.repaint();
    }

    void showSignUp() {
        JPanel bg=bgPanel(); bg.setLayout(new GridBagLayout());
        JPanel card=cardPanel(400,530);
        GridBagConstraints gc=new GridBagConstraints();
        gc.gridx=0; gc.fill=GridBagConstraints.HORIZONTAL;

        JLabel title=new JLabel("Create Account",SwingConstants.CENTER);
        title.setFont(new Font("SansSerif",Font.BOLD,22)); title.setForeground(new Color(50,40,10));
        row(card,gc,0,title,28,4);
        JSeparator sep=new JSeparator(); sep.setForeground(new Color(245,200,0));
        row(card,gc,1,sep,0,16);
        labelRow(card,gc,2,"Full Name"); JTextField nameF=UI.styledField(false); row(card,gc,3,nameF,0,12);
        labelRow(card,gc,4,"Username"); JTextField userF=UI.styledField(false); row(card,gc,5,userF,0,12);
        labelRow(card,gc,6,"Password"); JPasswordField passF=(JPasswordField)UI.styledField(true); row(card,gc,7,passF,0,8);
        JLabel msg=new JLabel("",SwingConstants.CENTER);
        msg.setFont(new Font("SansSerif",Font.PLAIN,12)); row(card,gc,8,msg,0,6);
        JButton regBtn=goldBtn("Create Account"); row(card,gc,9,regBtn,0,10);
        JButton back=new JButton("← Back to Login");
        back.setFont(new Font("SansSerif",Font.PLAIN,12)); back.setForeground(new Color(100,80,20));
        back.setBorderPainted(false); back.setContentAreaFilled(false);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row(card,gc,10,back,0,28);

        regBtn.addActionListener(e->{
            String fn=nameF.getText().trim(),un=userF.getText().trim(),
                   pw=new String(passF.getPassword()).trim();
            if(fn.isEmpty()||un.isEmpty()||pw.isEmpty()){msg.setForeground(new Color(180,30,30));msg.setText("All fields required.");return;}
            if(pw.length()<4){msg.setForeground(new Color(180,30,30));msg.setText("Password min 4 chars.");return;}
            Connection con=ConnectionClass.getConnection();
            if(con==null){msg.setForeground(new Color(180,30,30));msg.setText("Database unavailable.");return;}
            try {
                PreparedStatement chk=con.prepareStatement("SELECT COUNT(*) FROM users WHERE username=?");
                chk.setString(1,un); ResultSet rs=chk.executeQuery(); rs.next();
                if(rs.getInt(1)>0){msg.setForeground(new Color(180,30,30));msg.setText("Username taken. Try another.");return;}
                PreparedStatement ins=con.prepareStatement(
                    "INSERT INTO users(full_name,username,password,role) VALUES(?,?,?,'Waiter')");
                ins.setString(1,fn); ins.setString(2,un); ins.setString(3,pw); ins.executeUpdate();
                DataStore.loadStaff();
                msg.setForeground(new Color(30,120,30));msg.setText("Account created! You can now log in.");
                nameF.setText("");userF.setText("");passF.setText("");
            } catch(SQLException ex){msg.setForeground(new Color(180,30,30));msg.setText("Error: "+ex.getMessage());}
        });
        back.addActionListener(e->{frame.getContentPane().removeAll();showLogin();frame.revalidate();frame.repaint();});
        wrap(bg,card); frame.setContentPane(bg); frame.revalidate(); frame.repaint();
    }

    void doLogin(String un, String pw, JLabel err, JPasswordField passF) {
        if (un.isEmpty() || pw.isEmpty()) { err.setText("Enter username and password."); return; }
        Connection con = ConnectionClass.getConnection();
        if (con == null) {
            err.setText("Database unavailable. Check connection."); return;
        }
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT user_id, full_name, username, role, password FROM users WHERE username=?");
            ps.setString(1, un);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedPw = rs.getString("password");
                if (storedPw.equals(pw)) {
                    DataStore.loggedInUserId   = rs.getInt("user_id");
                    DataStore.loggedInName     = rs.getString("full_name");
                    DataStore.loggedInUsername = rs.getString("username");
                    DataStore.loggedInRole     = rs.getString("role");
                    frame.dispose();
                    route(DataStore.loggedInRole);
                } else {
                    err.setText("Incorrect username or password.");
                    passF.setText("");
                }
            } else {
                err.setText("Incorrect username or password.");
                passF.setText("");
            }
        } catch (SQLException e) {
            err.setText("Login error: " + e.getMessage());
            System.err.println("[DB] doLogin: " + e.getMessage());
        }
    }

    void route(String role){
        switch(role){
            case "Admin": case "Owner": case "Manager": new AdminDashboard(); break;
            case "Waiter":  new WaiterScreen();  break;
            case "Cashier": new CashierScreen(); break;
            case "Chef":    new KitchenDisplay();break;
            default:        new AdminDashboard(); break;
        }
    }

    void wrap(JPanel bg,JPanel card){
        JPanel w=new JPanel(new GridBagLayout()); w.setOpaque(false); card.setOpaque(false);
        GridBagConstraints gc=new GridBagConstraints();
        gc.fill=GridBagConstraints.NONE; gc.anchor=GridBagConstraints.CENTER; w.add(card,gc);
        GridBagConstraints bgc=new GridBagConstraints();
        bgc.fill=GridBagConstraints.NONE; bgc.anchor=GridBagConstraints.CENTER;
        bgc.weightx=1;bgc.weighty=1; bg.add(w,bgc);
    }
}
