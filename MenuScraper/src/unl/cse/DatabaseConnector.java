package unl.cse;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;

import com.mysql.jdbc.Statement;

public class DatabaseConnector {
	public static boolean uploadMenuData(ArrayList<DiningHall> halls) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("No MySQL Driver found");
			e.printStackTrace();
			return false;
		}
		
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://cse.unl.edu:3306/askinner", "askinner", "skc94fan");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		System.out.println("Connected successfully");
		
		PreparedStatement statement = null;
		
		try {
			for (DiningHall diningHall : halls) {
				int hallID;
				statement = conn.prepareStatement("SELECT id FROM Hall WHERE name = ?");
				statement.setString(1, diningHall.getName());
				ResultSet rs = statement.executeQuery();
				if(rs.next()){
					hallID = rs.getInt("id");
					System.out.println(diningHall.getName() + " already found in db with id of "+ hallID);
				} else {
					statement = conn.prepareStatement("INSERT INTO Hall (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
					statement.setString(1, diningHall.getName());
					statement.executeUpdate();
					rs = statement.getGeneratedKeys();
					if(rs.next()){
						hallID = rs.getInt(1);
						System.out.println("Created hall in db with id: " + hallID);
					} else {
						System.out.println("Failed to generate id for hall");
						return false;
					}
				}
				for (Menu menu : diningHall.getMenus()) {
					statement = conn.prepareStatement("SELECT id FROM Menu WHERE hall_id = ? AND meal = ? AND date = ?");
					statement.setInt(1, hallID);
					statement.setString(2, menu.getMeal());
					statement.setLong(3, menu.getDate().getTime());
					rs = statement.executeQuery();
					if(!rs.next()){
						int menuID;
						statement = conn.prepareStatement("INSERT INTO Menu (meal, date, fromTime, toTime, hall_id) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
						statement.setString(1, menu.getMeal());
						statement.setLong(2, menu.getDate().getTime());
						statement.setString(3, menu.getFromTime());
						statement.setString(4, menu.getToTime());
						statement.setInt(5, hallID);
						statement.executeUpdate();
						rs = statement.getGeneratedKeys();
						if(rs.next()){
							menuID = rs.getInt(1);
							System.out.println("Create menu in db with id: " + menuID);
						} else {
							System.out.println("Failed to generate id for menu");
							return false;
						}
						for (MenuItem menuItem : menu.getMenuItems()) {
							int menuItemID;
							statement = conn.prepareStatement("INSERT INTO MenuItem (name, category, menu_id) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
							statement.setString(1, menuItem.getName());
							statement.setString(2, menuItem.getCategory());
							statement.setInt(3, menuID);
							statement.executeUpdate();
							rs = statement.getGeneratedKeys();
							if(rs.next()){
								menuItemID = rs.getInt(1);
								System.out.println("Create menu item in db with id: " + menuItemID);
							} else {
								System.out.println("Failed to generate id for menu item");
								return false;
							}
						}
					} else {
						System.out.println("Meal already exists");
					}
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
