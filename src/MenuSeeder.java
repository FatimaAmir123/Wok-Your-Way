import java.sql.*;

class MenuSeeder {
    static void seed() {
        Object[][] items = {
            {"Stuffed Chilli Bites",8,450},{"Chicken Crunch Strips",8,650},
            {"Garlic Cheese Bread",8,350},{"Finger Fish",8,850},
            {"Chicken Tarragon",9,1200},{"Chicken Cashew Nut",9,1150},
            {"Moroccan Chicken",9,1250},{"Fish and Chips",9,1100},
            {"Chicken Parmesan",9,1350},{"Fettuccine Alfredo",11,950},
            {"Pink Sauce Penne",11,900},{"Mac and Cheese",11,800},
            {"Grilled Chicken Sandwich",12,750},{"Mushroom Swiss Burger",12,850},
            {"Steak Sandwich",12,950},{"Blue Lagoon",10,350},
            {"Pina Colada",10,450},{"Cold Coffee with Ice Cream",10,500},
            {"Mint Margarita",10,300}
        };
        try {
            Connection con=ConnectionClass.getConnection();
            if(con==null){System.err.println("[Seeder] No DB connection.");return;}
            PreparedStatement chk=con.prepareStatement("SELECT COUNT(*) FROM menuitems WHERE item_name=?");
            PreparedStatement ins=con.prepareStatement("INSERT INTO menuitems(item_name,category_id,price,is_available) VALUES(?,?,?,1)");
            int added=0,skipped=0;
            for(Object[] row:items){
                chk.setString(1,(String)row[0]); ResultSet rs=chk.executeQuery(); rs.next();
                if(rs.getInt(1)>0){skipped++;continue;}
                ins.setString(1,(String)row[0]);ins.setInt(2,(int)row[1]);ins.setInt(3,(int)row[2]);ins.addBatch();added++;
            }
            ins.executeBatch();
            System.out.println("[Seeder] Done. Added: "+added+", Skipped: "+skipped);
        }catch(SQLException e){System.err.println("[Seeder] "+e.getMessage());}
    }
}
