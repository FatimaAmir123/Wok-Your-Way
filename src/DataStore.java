import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.table.*;

class DataStore {

    public static int    loggedInUserId   = -1;
    public static String loggedInName     = "";
    public static String loggedInUsername = "";
    public static String loggedInRole     = "";

    public static final Color SIDEBAR_BG  = new Color(0xFF,0xEB,0x7A,210);
    public static final Color HEADER_BG   = new Color(0xFF,0xD7,0x00,180);
    public static final Color TABLE_HDR   = new Color(0xFF,0xD7,0x00,220);
    public static final Color ROW_ALT     = new Color(255,253,220,200);
    public static final Color TEXT_DARK   = new Color(0x3D2B1F);
    public static final Color TEXT_MED    = new Color(0x6B4C3B);
    public static final Color BORDER_CLR  = new Color(0xC8A800);
    public static final Color CLR_OK      = new Color(0xD1FFBD);
    public static final Color CLR_ALERT   = new Color(0xFF7F7F);
    public static final Color CLR_PENDING = new Color(0xFFF3B0);
    public static final Color BTN_NAV     = new Color(0xE6AC00);
    public static final Color BTN_DELETE  = new Color(0xFF7F7F);
    public static final Color BTN_LOGOUT  = new Color(0xD9534F);
    public static final Color SELECTION   = new Color(0xFFD700);

    public static final Font FONT_TITLE   = new Font("Georgia",Font.BOLD,22);
    public static final Font FONT_HEADING = new Font("Georgia",Font.BOLD,15);
    public static final Font FONT_BODY    = new Font("Georgia",Font.PLAIN,14);
    public static final Font FONT_BTN     = new Font("Georgia",Font.BOLD,15);
    public static final Font FONT_TBL_HDR = new Font("Georgia",Font.BOLD,14);
    public static final Font FONT_TBL_ROW = new Font("Georgia",Font.PLAIN,13);
    public static final Font FONT_SMALL   = new Font("Georgia",Font.PLAIN,12);
    public static final Font FONT_CARD_NUM= new Font("Georgia",Font.BOLD,34);
    public static final Font FONT_CARD_LBL= new Font("Georgia",Font.PLAIN,13);

    public static DefaultTableModel menuModel        = emptyMenuModel();
    public static DefaultTableModel staffModel       = emptyStaffModel();
    public static DefaultTableModel reservationModel = emptyReservationModel();
    public static DefaultTableModel inventoryModel   = emptyInventoryModel();
    public static DefaultTableModel orderLogModel    = emptyOrderLogModel();

    private static DefaultTableModel emptyMenuModel() {
        return new DefaultTableModel(new String[]{"#","Item Name","Category","Price"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}};
    }
    private static DefaultTableModel emptyStaffModel() {
        return new DefaultTableModel(new String[]{"#","Full Name","Username","Role","Phone","Password","Status"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}};
    }
    private static DefaultTableModel emptyReservationModel() {
        return new DefaultTableModel(new String[]{"#","Customer Name","Phone","Table No","Date","Time","Guests","Status"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}};
    }
    private static DefaultTableModel emptyInventoryModel() {
        return new DefaultTableModel(new String[]{"#","Item Name","Category","Quantity","Unit","Min Level","Supplier","Status"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}};
    }
    private static DefaultTableModel emptyOrderLogModel() {
        return new DefaultTableModel(new String[]{"Order #","Item Ordered","Qty","Total Price","Date"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}};
    }

    public static void loadMenu() {
        menuModel.setRowCount(0);
        Connection con = ConnectionClass.getConnection();
        if (con == null) return;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT m.item_id, m.item_name, c.category_name, m.price " +
                 "FROM menuitems m JOIN categories c ON m.category_id=c.category_id " +
                 "ORDER BY m.item_id")) {
            while (rs.next())
                menuModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3), "Rs "+rs.getInt(4)});
        } catch (SQLException e) { System.err.println("[DB] loadMenu: "+e.getMessage()); }
    }

    public static void loadStaff() {
        staffModel.setRowCount(0);
        Connection con = ConnectionClass.getConnection();
        if (con == null) return;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT user_id, full_name, username, role, phone, password, COALESCE(status,'Active') AS status " +
                 "FROM users ORDER BY user_id")) {
            while (rs.next())
                staffModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5),
                    rs.getString(6), rs.getString(7)});
        } catch (SQLException e) { System.err.println("[DB] loadStaff: "+e.getMessage()); }
    }

    public static void loadReservations() {
        reservationModel.setRowCount(0);
        Connection con = ConnectionClass.getConnection();
        if (con == null) return;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT r.reservation_id, r.customer_name, COALESCE(r.phone,'') AS phone, " +
                 "       t.table_number, DATE(r.reservation_time), TIME(r.reservation_time), " +
                 "       r.number_of_guests, r.status " +
                 "FROM reservations r " +
                 "JOIN restauranttables t ON r.table_id=t.table_id " +
                 "ORDER BY r.reservation_id")) {
            while (rs.next())
                reservationModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6),
                    rs.getInt(7), rs.getString(8)});
        } catch (SQLException e) { System.err.println("[DB] loadReservations: "+e.getMessage()); }
    }

    public static void loadInventory() {
        inventoryModel.setRowCount(0);
        Connection con = ConnectionClass.getConnection();
        if (con == null) return;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT item_id, ingredient_name, 'General' AS category, " +
                 "       quantity, unit, 1 AS min_level, 'N/A' AS supplier, " +
                 "       CASE WHEN quantity<=0 THEN 'Critical' " +
                 "            WHEN quantity<=1 THEN 'Low' ELSE 'OK' END AS status " +
                 "FROM inventory ORDER BY item_id")) {
            while (rs.next())
                inventoryModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    (double)rs.getInt(4), rs.getString(5),
                    (double)rs.getInt(6), rs.getString(7), rs.getString(8)});
        } catch (SQLException e) { System.err.println("[DB] loadInventory: "+e.getMessage()); }
    }

    public static void loadOrderLog() {
        orderLogModel.setRowCount(0);
        Connection con = ConnectionClass.getConnection();
        if (con == null) return;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT o.order_id, m.item_name, oi.quantity, oi.subtotal, DATE(o.order_date) " +
                 "FROM orders o " +
                 "JOIN orderitems oi ON o.order_id=oi.order_id " +
                 "JOIN menuitems m  ON oi.item_id=m.item_id " +
                 "ORDER BY o.order_id DESC")) {
            while (rs.next())
                orderLogModel.addRow(new Object[]{
                    "#"+rs.getInt(1), rs.getString(2), String.valueOf(rs.getInt(3)),
                    "Rs "+rs.getInt(4), rs.getString(5)});
        } catch (SQLException e) { System.err.println("[DB] loadOrderLog: "+e.getMessage()); }
    }

    public static void loadAll() {
        seedPhoneNumbers();
        cleanupStaleKitchenOrders();
        createMenuIngredientsTable();
        seedInventoryAndRecipes();
        loadMenu(); loadStaff(); loadReservations(); loadInventory(); loadOrderLog();
    }

    /** On fresh startup, any leftover Pending/Cooking orders from a previous run
     *  are stale — no waiter has them open. Mark them Cancelled so the kitchen
     *  starts clean. */
    public static void cleanupStaleKitchenOrders() {
        Connection con = ConnectionClass.getConnection(); if (con == null) return;
        try {
            // Delete orderitems first (foreign key), then the stale orders themselves
            con.createStatement().executeUpdate(
                "DELETE oi FROM orderitems oi " +
                "JOIN orders o ON oi.order_id = o.order_id " +
                "WHERE o.order_status IN ('Pending','Cooking')");
            con.createStatement().executeUpdate(
                "DELETE FROM orders WHERE order_status IN ('Pending','Cooking')");
            System.out.println("[DB] Stale kitchen orders deleted on startup.");
        } catch (SQLException e) {
            System.err.println("[DB] cleanupStaleKitchenOrders: " + e.getMessage());
        }
    }

    public static String deductIngredients(String itemName, int servings) {
        return "Order placed for "+servings+" x "+itemName+". Inventory updated.";
    }

    /** Seed all required inventory ingredients (skip if already present),
     *  then seed recipes for every menu item (skip if that item already has ingredients). */
    public static void seedInventoryAndRecipes() {
        Connection con = ConnectionClass.getConnection(); if (con == null) return;
        try {
            // ── 1. Seed ingredients into inventory ─────────────────────────────
            // Format: {name, qty, unit}
            Object[][] ingredients = {
                // Proteins
                {"Chicken Breast",     50,  "kg"},
                {"Beef Steak",         30,  "kg"},
                {"Fish Fillet",        25,  "kg"},
                {"Minced Beef",        20,  "kg"},
                // Dairy
                {"Mozzarella Cheese",  15,  "kg"},
                {"Parmesan Cheese",    10,  "kg"},
                {"Cheddar Cheese",     12,  "kg"},
                {"Cream",              20,  "litre"},
                {"Butter",             10,  "kg"},
                {"Ice Cream",          15,  "kg"},
                // Pasta & Bread
                {"Pasta (Fettuccine)", 15,  "kg"},
                {"Pasta (Penne)",      15,  "kg"},
                {"Macaroni",           15,  "kg"},
                {"Bread Bun",          80,  "pcs"},
                {"Sandwich Bread",     60,  "pcs"},
                {"Garlic Bread Loaf",  30,  "pcs"},
                // Vegetables & Produce
                {"Garlic",             5,   "kg"},
                {"Mushroom",           10,  "kg"},
                {"Chilli",             5,   "kg"},
                {"Lettuce",            8,   "kg"},
                {"Tomato",             10,  "kg"},
                {"Potato",             25,  "kg"},
                {"Cashew Nuts",        5,   "kg"},
                // Oils & Sauces
                {"Olive Oil",          10,  "litre"},
                {"Tomato Sauce",       15,  "litre"},
                {"Alfredo Sauce",      10,  "litre"},
                {"Pink Sauce",         10,  "litre"},
                {"Barbecue Sauce",     8,   "litre"},
                {"Tarragon Sauce",     8,   "litre"},
                {"Moroccan Spice Mix", 3,   "kg"},
                // Drinks & Mixers
                {"Lemonade",           30,  "litre"},
                {"Coconut Milk",       20,  "litre"},
                {"Pineapple Juice",    20,  "litre"},
                {"Coffee",             5,   "kg"},
                {"Milk",               20,  "litre"},
                {"Mint Leaves",        2,   "kg"},
                {"Blue Syrup",         5,   "litre"},
                {"Sugar Syrup",        10,  "litre"},
                // Coating & Misc
                {"Breadcrumbs",        8,   "kg"},
                {"Egg",                100, "pcs"},
                {"Flour",              20,  "kg"},
            };
            PreparedStatement chkIng = con.prepareStatement(
                "SELECT COUNT(*) FROM inventory WHERE ingredient_name=?");
            PreparedStatement insIng = con.prepareStatement(
                "INSERT INTO inventory(ingredient_name,quantity,unit) VALUES(?,?,?)");
            for (Object[] row : ingredients) {
                chkIng.setString(1, (String)row[0]); ResultSet r = chkIng.executeQuery(); r.next();
                if (r.getInt(1) > 0) continue;
                insIng.setString(1,(String)row[0]); insIng.setInt(2,(int)row[1]); insIng.setString(3,(String)row[2]);
                insIng.addBatch();
            }
            insIng.executeBatch();
            System.out.println("[Seed] Inventory ingredients seeded.");

            // ── 2. Helper: look up IDs by name ─────────────────────────────────
            // Build ingredient name → id map
            java.util.Map<String,Integer> ingMap = new java.util.HashMap<>();
            ResultSet ir = con.createStatement().executeQuery(
                "SELECT item_id, ingredient_name FROM inventory");
            while (ir.next()) ingMap.put(ir.getString(2), ir.getInt(1));

            // Build menu item name → id map
            java.util.Map<String,Integer> menuMap = new java.util.HashMap<>();
            ResultSet mr = con.createStatement().executeQuery(
                "SELECT item_id, item_name FROM menuitems");
            while (mr.next()) menuMap.put(mr.getString(2), mr.getInt(1));

            // ── 3. Define recipes: {menuItemName, {ingName, qtyPerServing}, ...} ──
            // qty = realistic amount used per ONE serving of that dish
            Object[][][] recipes = {
                // Starters (category 8)
                {{"Stuffed Chilli Bites"},
                    {"Chilli",0.1}, {"Mozzarella Cheese",0.08}, {"Breadcrumbs",0.05},
                    {"Egg",1.0}, {"Flour",0.05}, {"Olive Oil",0.03}},
                {{"Chicken Crunch Strips"},
                    {"Chicken Breast",0.2}, {"Breadcrumbs",0.06}, {"Egg",1.0},
                    {"Flour",0.05}, {"Olive Oil",0.04}},
                {{"Garlic Cheese Bread"},
                    {"Garlic Bread Loaf",0.5}, {"Garlic",0.02}, {"Butter",0.04},
                    {"Mozzarella Cheese",0.08}, {"Parmesan Cheese",0.03}},
                {{"Finger Fish"},
                    {"Fish Fillet",0.2}, {"Breadcrumbs",0.06}, {"Egg",1.0},
                    {"Flour",0.05}, {"Olive Oil",0.04}},
                // Mains — Chicken (category 9)
                {{"Chicken Tarragon"},
                    {"Chicken Breast",0.25}, {"Tarragon Sauce",0.1}, {"Cream",0.08},
                    {"Butter",0.03}, {"Garlic",0.01}, {"Olive Oil",0.03}},
                {{"Chicken Cashew Nut"},
                    {"Chicken Breast",0.25}, {"Cashew Nuts",0.06}, {"Olive Oil",0.04},
                    {"Garlic",0.01}, {"Tomato Sauce",0.05}},
                {{"Moroccan Chicken"},
                    {"Chicken Breast",0.25}, {"Moroccan Spice Mix",0.04}, {"Tomato",0.1},
                    {"Olive Oil",0.04}, {"Garlic",0.01}},
                {{"Fish and Chips"},
                    {"Fish Fillet",0.25}, {"Potato",0.3}, {"Flour",0.06},
                    {"Egg",1.0}, {"Breadcrumbs",0.06}, {"Olive Oil",0.05}},
                {{"Chicken Parmesan"},
                    {"Chicken Breast",0.25}, {"Parmesan Cheese",0.07}, {"Mozzarella Cheese",0.06},
                    {"Tomato Sauce",0.1}, {"Breadcrumbs",0.05}, {"Egg",1.0}, {"Olive Oil",0.04}},
                // Pasta (category 11)
                {{"Fettuccine Alfredo"},
                    {"Pasta (Fettuccine)",0.15}, {"Alfredo Sauce",0.12}, {"Parmesan Cheese",0.06},
                    {"Cream",0.08}, {"Butter",0.04}, {"Garlic",0.01}},
                {{"Pink Sauce Penne"},
                    {"Pasta (Penne)",0.15}, {"Pink Sauce",0.12}, {"Parmesan Cheese",0.05},
                    {"Cream",0.06}, {"Tomato Sauce",0.05}, {"Garlic",0.01}},
                {{"Mac and Cheese"},
                    {"Macaroni",0.15}, {"Cheddar Cheese",0.1}, {"Cream",0.1},
                    {"Butter",0.04}, {"Flour",0.03}, {"Milk",0.1}},
                // Burgers & Sandwiches (category 12)
                {{"Grilled Chicken Sandwich"},
                    {"Chicken Breast",0.2}, {"Sandwich Bread",2.0}, {"Lettuce",0.04},
                    {"Tomato",0.05}, {"Butter",0.02}, {"Olive Oil",0.02}},
                {{"Mushroom Swiss Burger"},
                    {"Minced Beef",0.2}, {"Bread Bun",1.0}, {"Mushroom",0.08},
                    {"Cheddar Cheese",0.05}, {"Lettuce",0.03}, {"Tomato",0.04},
                    {"Barbecue Sauce",0.04}},
                {{"Steak Sandwich"},
                    {"Beef Steak",0.25}, {"Sandwich Bread",2.0}, {"Mushroom",0.06},
                    {"Lettuce",0.03}, {"Tomato",0.04}, {"Barbecue Sauce",0.04},
                    {"Butter",0.02}},
                // Drinks (category 10)
                {{"Blue Lagoon"},
                    {"Blue Syrup",0.04}, {"Lemonade",0.25}, {"Sugar Syrup",0.03}},
                {{"Pina Colada"},
                    {"Coconut Milk",0.15}, {"Pineapple Juice",0.15}, {"Sugar Syrup",0.03}},
                {{"Cold Coffee with Ice Cream"},
                    {"Coffee",0.02}, {"Milk",0.2}, {"Ice Cream",0.1}, {"Sugar Syrup",0.03}},
                {{"Mint Margarita"},
                    {"Mint Leaves",0.01}, {"Lemonade",0.25}, {"Sugar Syrup",0.03}},
            };

            PreparedStatement chkRec = con.prepareStatement(
                "SELECT COUNT(*) FROM menu_ingredients WHERE menu_item_id=?");
            PreparedStatement insRec = con.prepareStatement(
                "INSERT IGNORE INTO menu_ingredients(menu_item_id,inventory_item_id,qty_per_serving) VALUES(?,?,?)");

            for (Object[][] recipe : recipes) {
                String dishName = (String) recipe[0][0];
                Integer menuId  = menuMap.get(dishName);
                if (menuId == null) { System.err.println("[Seed] Menu item not found: "+dishName); continue; }
                // Skip if this dish already has ingredients saved
                chkRec.setInt(1, menuId); ResultSet rr = chkRec.executeQuery(); rr.next();
                if (rr.getInt(1) > 0) continue;
                for (int i = 1; i < recipe.length; i++) {
                    String ingName = (String) recipe[i][0];
                    double qty     = (Double) recipe[i][1];
                    Integer invId  = ingMap.get(ingName);
                    if (invId == null) { System.err.println("[Seed] Ingredient not found: "+ingName); continue; }
                    insRec.setInt(1, menuId); insRec.setInt(2, invId); insRec.setDouble(3, qty);
                    insRec.addBatch();
                }
                insRec.executeBatch();
            }
            System.out.println("[Seed] Recipes seeded.");
        } catch (SQLException e) { System.err.println("[Seed] seedInventoryAndRecipes: "+e.getMessage()); }
    }

    public static void createMenuIngredientsTable() {
        Connection con = ConnectionClass.getConnection(); if (con == null) return;
        try {
            con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS menu_ingredients (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  menu_item_id INT NOT NULL," +
                "  inventory_item_id INT NOT NULL," +
                "  qty_per_serving DOUBLE NOT NULL DEFAULT 1," +
                "  UNIQUE KEY uq_mi (menu_item_id, inventory_item_id)" +
                ")");
        } catch (SQLException e) { System.err.println("[DB] createMenuIngredientsTable: "+e.getMessage()); }
    }

    /** Deduct ingredients from inventory for a given order (called when order marked Ready). */
    public static void deductIngredientsForOrder(int orderId) {
        Connection con = ConnectionClass.getConnection(); if (con == null) return;
        try {
            // Get each item in the order with its quantity
            PreparedStatement ps = con.prepareStatement(
                "SELECT oi.item_id, oi.quantity FROM orderitems oi WHERE oi.order_id=?");
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int menuItemId = rs.getInt("item_id");
                int orderQty   = rs.getInt("quantity");
                // Get all ingredients for this menu item
                PreparedStatement ip = con.prepareStatement(
                    "SELECT inventory_item_id, qty_per_serving FROM menu_ingredients WHERE menu_item_id=?");
                ip.setInt(1, menuItemId);
                ResultSet ir = ip.executeQuery();
                while (ir.next()) {
                    int invId    = ir.getInt("inventory_item_id");
                    double deduct = ir.getDouble("qty_per_serving") * orderQty;
                    PreparedStatement up = con.prepareStatement(
                        "UPDATE inventory SET quantity=GREATEST(0, quantity-?) WHERE item_id=?");
                    up.setDouble(1, deduct); up.setInt(2, invId); up.executeUpdate();
                }
            }
            loadInventory(); // refresh model after deduction
            System.out.println("[DB] Ingredients deducted for order #"+orderId);
        } catch (SQLException e) { System.err.println("[DB] deductIngredientsForOrder: "+e.getMessage()); }
    }

    /** Save a recipe (full replace: delete old rows, insert new). */
    public static boolean saveRecipe(int menuItemId, int[] invIds, double[] qtys) {
        Connection con = ConnectionClass.getConnection(); if (con == null) return false;
        try {
            PreparedStatement del = con.prepareStatement(
                "DELETE FROM menu_ingredients WHERE menu_item_id=?");
            del.setInt(1, menuItemId); del.executeUpdate();
            PreparedStatement ins = con.prepareStatement(
                "INSERT INTO menu_ingredients(menu_item_id,inventory_item_id,qty_per_serving) VALUES(?,?,?)");
            for (int i = 0; i < invIds.length; i++) {
                ins.setInt(1, menuItemId); ins.setInt(2, invIds[i]); ins.setDouble(3, qtys[i]);
                ins.addBatch();
            }
            ins.executeBatch(); return true;
        } catch (SQLException e) { System.err.println("[DB] saveRecipe: "+e.getMessage()); return false; }
    }

    /** Load recipe rows for a menu item: returns list of {inv_id, ingredient_name, qty_per_serving, unit} */
    public static java.util.List<Object[]> loadRecipe(int menuItemId) {
        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        Connection con = ConnectionClass.getConnection(); if (con == null) return rows;
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT mi.inventory_item_id, i.ingredient_name, mi.qty_per_serving, i.unit " +
                "FROM menu_ingredients mi JOIN inventory i ON mi.inventory_item_id=i.item_id " +
                "WHERE mi.menu_item_id=? ORDER BY i.ingredient_name");
            ps.setInt(1, menuItemId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                rows.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getString(4)});
        } catch (SQLException e) { System.err.println("[DB] loadRecipe: "+e.getMessage()); }
        return rows;
    }

    public static void refreshInventoryStatus() {
        loadInventory();
    }

    public static int getTotalMenuItems()    {
        Connection con=ConnectionClass.getConnection(); if(con==null) return menuModel.getRowCount();
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery("SELECT COUNT(*) FROM menuitems")){
            if(rs.next()) return rs.getInt(1);}catch(SQLException e){e.printStackTrace();}
        return menuModel.getRowCount();
    }
    public static int getTotalReservations() {
        Connection con=ConnectionClass.getConnection(); if(con==null) return reservationModel.getRowCount();
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery("SELECT COUNT(*) FROM reservations")){
            if(rs.next()) return rs.getInt(1);}catch(SQLException e){e.printStackTrace();}
        return reservationModel.getRowCount();
    }
    public static int getTotalOrders() {
        Connection con=ConnectionClass.getConnection(); if(con==null) return orderLogModel.getRowCount();
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery("SELECT COUNT(*) FROM orders")){
            if(rs.next()) return rs.getInt(1);}catch(SQLException e){e.printStackTrace();}
        return orderLogModel.getRowCount();
    }

    public static int getActiveStaffCount() {
        Connection con=ConnectionClass.getConnection(); if(con==null){
            int c=0;for(int i=0;i<staffModel.getRowCount();i++)if("Active".equals(staffModel.getValueAt(i,6)))c++;return c;}
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery("SELECT COUNT(*) FROM users WHERE COALESCE(status,'Active')='Active'")){
            if(rs.next()) return rs.getInt(1);}catch(SQLException e){e.printStackTrace();}
        return staffModel.getRowCount();
    }

    public static int getConfirmedReservations() {
        Connection con=ConnectionClass.getConnection(); if(con==null){
            int c=0;for(int i=0;i<reservationModel.getRowCount();i++)if("Confirmed".equals(reservationModel.getValueAt(i,7)))c++;return c;}
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery("SELECT COUNT(*) FROM reservations WHERE status='Confirmed'")){
            if(rs.next()) return rs.getInt(1);}catch(SQLException e){e.printStackTrace();}
        return 0;
    }

    public static int getLowStockCount() {
        Connection con=ConnectionClass.getConnection(); if(con==null){
            int c=0;for(int i=0;i<inventoryModel.getRowCount();i++){String s=inventoryModel.getValueAt(i,7).toString();if(s.equals("Low")||s.equals("Critical"))c++;}return c;}
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery("SELECT COUNT(*) FROM inventory WHERE quantity<=1")){
            if(rs.next()) return rs.getInt(1);}catch(SQLException e){e.printStackTrace();}
        return 0;
    }

    public static int getCriticalStockCount() {
        Connection con=ConnectionClass.getConnection(); if(con==null){
            int c=0;for(int i=0;i<inventoryModel.getRowCount();i++)if("Critical".equals(inventoryModel.getValueAt(i,7)))c++;return c;}
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery("SELECT COUNT(*) FROM inventory WHERE quantity<=0")){
            if(rs.next()) return rs.getInt(1);}catch(SQLException e){e.printStackTrace();}
        return 0;
    }

    public static int getTotalRevenue() {
        Connection con=ConnectionClass.getConnection(); if(con==null){
            int total=0;for(int i=0;i<orderLogModel.getRowCount();i++){try{total+=Integer.parseInt(orderLogModel.getValueAt(i,3).toString().replace("Rs ","").replace(",","").trim());}catch(NumberFormatException ignored){}}return total;}
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery("SELECT COALESCE(SUM(total_amount),0) FROM orders")){
            if(rs.next()) return rs.getInt(1);}catch(SQLException e){e.printStackTrace();}
        return 0;
    }

    public static String[] getCategoryNames() {
        return new String[]{"Starter","Main Course","Fast Food","Drinks","Dessert","Other"};
    }

    public static boolean addMenuItem(String name, String cat, int price) {
        Connection con=ConnectionClass.getConnection(); if(con==null) return false;
        try {
            int catId = getCategoryId(con, cat);
            PreparedStatement ps=con.prepareStatement(
                "INSERT INTO menuitems(item_name,category_id,price,is_available) VALUES(?,?,?,1)");
            ps.setString(1,name); ps.setInt(2,catId); ps.setInt(3,price); ps.executeUpdate();
            loadMenu(); return true;
        } catch(SQLException e){System.err.println("[DB] addMenuItem: "+e.getMessage()); return false;}
    }
    public static boolean updateMenuItem(int row, String name, String cat, int price) {
        Connection con=ConnectionClass.getConnection(); if(con==null) return false;
        try {
            int itemId=(int)menuModel.getValueAt(row,0);
            int catId=getCategoryId(con,cat);
            PreparedStatement ps=con.prepareStatement(
                "UPDATE menuitems SET item_name=?,category_id=?,price=? WHERE item_id=?");
            ps.setString(1,name); ps.setInt(2,catId); ps.setInt(3,price); ps.setInt(4,itemId);
            ps.executeUpdate(); loadMenu(); return true;
        } catch(SQLException e){System.err.println("[DB] updateMenuItem: "+e.getMessage()); return false;}
    }
    public static boolean deleteMenuItem(int row) {
        Connection con=ConnectionClass.getConnection(); if(con==null) return false;
        try {
            int itemId=(int)menuModel.getValueAt(row,0);
            PreparedStatement ps=con.prepareStatement("DELETE FROM menuitems WHERE item_id=?");
            ps.setInt(1,itemId); ps.executeUpdate(); loadMenu(); return true;
        } catch(SQLException e){System.err.println("[DB] deleteMenuItem: "+e.getMessage()); return false;}
    }
    private static int getCategoryId(Connection con, String catName) throws SQLException {
        java.util.Map<String,String> map=new java.util.LinkedHashMap<>();
        map.put("Starter","Appetizers"); map.put("Main Course","Main Course");
        map.put("Fast Food","Burgers & Sandwiches"); map.put("Drinks","Beverages");
        map.put("Pasta","Pasta"); map.put("Dessert","Dessert"); map.put("Other","Other");
        String dbName=map.getOrDefault(catName,catName);
        PreparedStatement ps=con.prepareStatement("SELECT category_id FROM categories WHERE category_name=?");
        ps.setString(1,dbName); ResultSet rs=ps.executeQuery();
        if(rs.next()) return rs.getInt(1);
        ResultSet all=con.createStatement().executeQuery("SELECT category_id FROM categories LIMIT 1");
        if(all.next()) return all.getInt(1);
        return 8;
    }

    public static boolean addInventoryItem(String name, int qty, String unit) {
        Connection con=ConnectionClass.getConnection(); if(con==null) return false;
        try {
            PreparedStatement ps=con.prepareStatement(
                "INSERT INTO inventory(ingredient_name,quantity,unit) VALUES(?,?,?)");
            ps.setString(1,name); ps.setInt(2,qty); ps.setString(3,unit); ps.executeUpdate();
            loadInventory(); return true;
        } catch(SQLException e){System.err.println("[DB] addInventory: "+e.getMessage()); return false;}
    }
    public static boolean updateInventoryQty(int row, int qty) {
        Connection con=ConnectionClass.getConnection(); if(con==null) return false;
        try {
            int itemId=(int)((Number)inventoryModel.getValueAt(row,0)).intValue();
            PreparedStatement ps=con.prepareStatement("UPDATE inventory SET quantity=? WHERE item_id=?");
            ps.setInt(1,qty); ps.setInt(2,itemId); ps.executeUpdate(); loadInventory(); return true;
        } catch(SQLException e){System.err.println("[DB] updateInventoryQty: "+e.getMessage()); return false;}
    }
    public static boolean deleteInventoryItem(int row) {
        Connection con=ConnectionClass.getConnection(); if(con==null) return false;
        try {
            int itemId=(int)((Number)inventoryModel.getValueAt(row,0)).intValue();
            PreparedStatement ps=con.prepareStatement("DELETE FROM inventory WHERE item_id=?");
            ps.setInt(1,itemId); ps.executeUpdate(); loadInventory(); return true;
        } catch(SQLException e){System.err.println("[DB] deleteInventory: "+e.getMessage()); return false;}
    }

    public static boolean addReservation(String customerName, String phone, String tableNo, String datetime, int guests, String status) {
        Connection con=ConnectionClass.getConnection(); if(con==null) return false;
        try {
            String tNum=tableNo.replace("Table ","T");
            PreparedStatement tps=con.prepareStatement("SELECT table_id FROM restauranttables WHERE table_number=?");
            tps.setString(1,tNum); ResultSet trs=tps.executeQuery();
            if(!trs.next()){System.err.println("[DB] Table not found: "+tNum); return false;}
            int tableId=trs.getInt(1);
            String ph=(phone==null||phone.trim().isEmpty())?randomPhone():phone.trim();
            PreparedStatement ps=con.prepareStatement(
                "INSERT INTO reservations(customer_name,phone,table_id,reservation_time,number_of_guests,status) VALUES(?,?,?,?,?,?)");
            ps.setString(1,customerName); ps.setString(2,ph); ps.setInt(3,tableId);
            ps.setString(4,datetime); ps.setInt(5,guests); ps.setString(6,status);
            ps.executeUpdate(); loadReservations(); return true;
        } catch(SQLException e){System.err.println("[DB] addReservation: "+e.getMessage()); return false;}
    }
    public static boolean updateReservation(int row, String customerName, String phone, String tableNo, String datetime, int guests, String status) {
        Connection con=ConnectionClass.getConnection(); if(con==null) return false;
        try {
            int resId=(int)reservationModel.getValueAt(row,0);
            String tNum=tableNo.replace("Table ","T");
            PreparedStatement tps=con.prepareStatement("SELECT table_id FROM restauranttables WHERE table_number=?");
            tps.setString(1,tNum); ResultSet trs=tps.executeQuery();
            if(!trs.next()){System.err.println("[DB] Table not found: "+tNum); return false;}
            int tableId=trs.getInt(1);
            String ph=(phone==null||phone.trim().isEmpty())?randomPhone():phone.trim();
            PreparedStatement ps=con.prepareStatement(
                "UPDATE reservations SET customer_name=?,phone=?,table_id=?,reservation_time=?,number_of_guests=?,status=? WHERE reservation_id=?");
            ps.setString(1,customerName); ps.setString(2,ph); ps.setInt(3,tableId);
            ps.setString(4,datetime); ps.setInt(5,guests); ps.setString(6,status); ps.setInt(7,resId);
            ps.executeUpdate(); loadReservations(); return true;
        } catch(SQLException e){System.err.println("[DB] updateReservation: "+e.getMessage()); return false;}
    }
    public static boolean updateReservationStatus(int row, String status) {
        Connection con=ConnectionClass.getConnection(); if(con==null) return false;
        try {
            int resId=(int)reservationModel.getValueAt(row,0);
            PreparedStatement ps=con.prepareStatement("UPDATE reservations SET status=? WHERE reservation_id=?");
            ps.setString(1,status); ps.setInt(2,resId); ps.executeUpdate(); loadReservations(); return true;
        } catch(SQLException e){System.err.println("[DB] updateReservationStatus: "+e.getMessage()); return false;}
    }
    public static boolean deleteReservation(int row) {
        Connection con=ConnectionClass.getConnection(); if(con==null) return false;
        try {
            int resId=(int)reservationModel.getValueAt(row,0);
            PreparedStatement ps=con.prepareStatement("DELETE FROM reservations WHERE reservation_id=?");
            ps.setInt(1,resId); ps.executeUpdate(); loadReservations(); return true;
        } catch(SQLException e){System.err.println("[DB] deleteReservation: "+e.getMessage()); return false;}
    }

    // ── Phone helpers ──────────────────────────────────────────────
    public static String randomPhone() {
        java.util.Random rng = new java.util.Random();
        int[] prefixes = {300,301,306,310,311,312,313,314,315,316,317,320,321,330,331,332,333,334,335,336,340,345};
        String prefix = String.valueOf(prefixes[rng.nextInt(prefixes.length)]);
        return "0" + prefix + String.format("%07d", rng.nextInt(10000000));
    }
    public static void seedPhoneNumbers() {
        Connection con=ConnectionClass.getConnection(); if(con==null) return;
        try {
            try { con.createStatement().executeUpdate("ALTER TABLE reservations ADD COLUMN phone VARCHAR(20)"); }
            catch(SQLException ignored) {}
            ResultSet rs=con.createStatement().executeQuery(
                "SELECT reservation_id FROM reservations WHERE phone IS NULL OR phone=''");
            PreparedStatement ps=con.prepareStatement("UPDATE reservations SET phone=? WHERE reservation_id=?");
            while(rs.next()){ ps.setString(1,randomPhone()); ps.setInt(2,rs.getInt(1)); ps.addBatch(); }
            ps.executeBatch();
        } catch(SQLException e){System.err.println("[DB] seedPhoneNumbers: "+e.getMessage());}
    }

    // ── Kitchen / cashier order flow ───────────────────────────────
    /** Insert order with status 'Pending' so kitchen sees it immediately. */
    public static int sendOrderToKitchen(String tableNo, java.util.List<OrderItem> items) {
        Connection con = ConnectionClass.getConnection();
        if (con == null) return -1;
        try {
            String tNum = tableNo.startsWith("T") ? tableNo : tableNo.replace("Table ","T");
            PreparedStatement tps = con.prepareStatement(
                "SELECT table_id FROM restauranttables WHERE table_number=?");
            tps.setString(1, tNum); ResultSet trs = tps.executeQuery();
            int tableId = 1; if (trs.next()) tableId = trs.getInt(1);

            int total = 0; for (OrderItem oi : items) total += (int) oi.lineTotal();

            PreparedStatement ops = con.prepareStatement(
                "INSERT INTO orders(table_id,waiter_id,cashier_id,order_status,total_amount,order_date)" +
                " VALUES(?,?,?,?,?,NOW())",
                Statement.RETURN_GENERATED_KEYS);
            ops.setInt(1, tableId);
            ops.setInt(2, loggedInUserId > 0 ? loggedInUserId : 1);
            ops.setInt(3, 1);
            ops.setString(4, "Pending");
            ops.setInt(5, total);
            ops.executeUpdate();
            ResultSet keys = ops.getGeneratedKeys(); keys.next();
            int orderId = keys.getInt(1);

            PreparedStatement ips = con.prepareStatement(
                "INSERT INTO orderitems(order_id,item_id,quantity,subtotal) VALUES(?,?,?,?)");
            for (OrderItem oi : items) {
                PreparedStatement mps = con.prepareStatement(
                    "SELECT item_id FROM menuitems WHERE item_name=?");
                mps.setString(1, oi.name); ResultSet mrs = mps.executeQuery();
                int itemId = 1; if (mrs.next()) itemId = mrs.getInt(1);
                ips.setInt(1, orderId); ips.setInt(2, itemId);
                ips.setInt(3, oi.qty); ips.setInt(4, (int) oi.lineTotal());
                ips.addBatch();
            }
            ips.executeBatch();
            System.out.println("[DB] Order #" + orderId + " sent to kitchen (Pending).");
            return orderId;
        } catch (SQLException e) {
            System.err.println("[DB] sendOrderToKitchen: " + e.getMessage()); return -1;
        }
    }

    /** Mark existing kitchen order as Paid at cashier time; falls back to saveOrder if needed. */
    public static int markOrderPaid(int existingOrderId, String tableNo,
                                     String waiterUsername, java.util.List<OrderItem> items) {
        Connection con = ConnectionClass.getConnection();
        if (con == null) return saveOrder(tableNo, waiterUsername, items);
        if (existingOrderId > 0) {
            try {
                PreparedStatement ps = con.prepareStatement(
                    "UPDATE orders SET order_status='Paid', cashier_id=? WHERE order_id=?");
                ps.setInt(1, loggedInUserId > 0 ? loggedInUserId : 1);
                ps.setInt(2, existingOrderId);
                ps.executeUpdate();
                // Free the table
                String tNum = tableNo.replace("Table ","T");
                PreparedStatement tid = con.prepareStatement(
                    "SELECT table_id FROM restauranttables WHERE table_number=?");
                tid.setString(1, tNum); ResultSet trs = tid.executeQuery();
                if (trs.next()) {
                    PreparedStatement ups = con.prepareStatement(
                        "UPDATE restauranttables SET status='Available' WHERE table_id=?");
                    ups.setInt(1, trs.getInt(1)); ups.executeUpdate();
                }
                loadOrderLog();
                System.out.println("[DB] Order #" + existingOrderId + " marked Paid.");
                return existingOrderId;
            } catch (SQLException e) {
                System.err.println("[DB] markOrderPaid: " + e.getMessage());
            }
        }
        return saveOrder(tableNo, waiterUsername, items);
    }

    public static int saveOrder(String tableNo, String waiterUsername, java.util.List<OrderItem> items) {
        Connection con=ConnectionClass.getConnection();
        if(con==null){
            int orderId=orderLogModel.getRowCount()+1001;
            for(OrderItem oi:items)
                orderLogModel.addRow(new Object[]{"#"+orderId,oi.name,String.valueOf(oi.qty),"Rs "+(int)oi.lineTotal(),java.time.LocalDate.now().toString()});
            return orderId;
        }
        try {
            String tNum=tableNo.replace("Table ","T");
            PreparedStatement tps=con.prepareStatement("SELECT table_id FROM restauranttables WHERE table_number=?");
            tps.setString(1,tNum); ResultSet trs=tps.executeQuery();
            int tableId=1; if(trs.next()) tableId=trs.getInt(1);

            PreparedStatement wps=con.prepareStatement("SELECT user_id FROM users WHERE username=?");
            wps.setString(1,waiterUsername); ResultSet wrs=wps.executeQuery();
            int waiterId=loggedInUserId>0?loggedInUserId:1; if(wrs.next()) waiterId=wrs.getInt(1);

            int total=0; for(OrderItem oi:items) total+=(int)oi.lineTotal();

            PreparedStatement ops=con.prepareStatement(
                "INSERT INTO orders(table_id,waiter_id,cashier_id,order_status,total_amount) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ops.setInt(1,tableId); ops.setInt(2,waiterId); ops.setInt(3,loggedInUserId);
            ops.setString(4,"Paid"); ops.setInt(5,total); ops.executeUpdate();
            ResultSet keys=ops.getGeneratedKeys(); keys.next();
            int orderId=keys.getInt(1);

            PreparedStatement ips=con.prepareStatement(
                "INSERT INTO orderitems(order_id,item_id,quantity,subtotal) VALUES(?,?,?,?)");
            for(OrderItem oi:items){
                PreparedStatement mps=con.prepareStatement("SELECT item_id FROM menuitems WHERE item_name=?");
                mps.setString(1,oi.name); ResultSet mrs=mps.executeQuery();
                int itemId=1; if(mrs.next()) itemId=mrs.getInt(1);
                ips.setInt(1,orderId); ips.setInt(2,itemId);
                ips.setInt(3,oi.qty); ips.setInt(4,(int)oi.lineTotal()); ips.addBatch();
            }
            ips.executeBatch();

            PreparedStatement ups=con.prepareStatement("UPDATE restauranttables SET status='Available' WHERE table_id=?");
            ups.setInt(1,tableId); ups.executeUpdate();

            loadOrderLog(); return orderId;
        } catch(SQLException e){System.err.println("[DB] saveOrder: "+e.getMessage()); return -1;}
    }
} // end DataStore
