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
		// Make sure that the driver is loaded
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("No MySQL Driver found");
			e.printStackTrace();
			return false;
		}
		
		// Establish a connection to the database
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://cse.unl.edu:3306/askinner", "askinner", "x");
			System.out.println("Connected to database successfully");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		
		// Initialize a statement that we will be using to insert and view data
		PreparedStatement statement = null;
		
		try {
			// Loop through each DiningHall that is available
			for (DiningHall diningHall : halls) {
				int hallID;
				
				// Check to see if the DiningHall already exists in the database
				statement = conn.prepareStatement("SELECT id FROM Hall WHERE name = ?");
				statement.setString(1, diningHall.getName());
				ResultSet rs = statement.executeQuery();
				
				// If it does exist, get the id
				// If it doesn't exist, insert the hall and get the id
				if(rs.next()){
					hallID = rs.getInt("id");
				} else {
					statement = conn.prepareStatement("INSERT INTO Hall (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
					statement.setString(1, diningHall.getName());
					statement.executeUpdate();
					rs = statement.getGeneratedKeys();
					if(rs.next()){
						hallID = rs.getInt(1);
					} else {
						return false;
					}
				}
				
				// Loop through each Menu that is in the current DiningHall
				for (Menu menu : diningHall.getMenus()) {
					// Check to see if this menu already exists in the database
					statement = conn.prepareStatement("SELECT id FROM Menu WHERE hall_id = ? AND meal = ? AND date = ?");
					statement.setInt(1, hallID);
					statement.setString(2, menu.getMeal());
					statement.setLong(3, menu.getDate().getTime());
					rs = statement.executeQuery();
					
					// If it doesn't exist, add the menu and get the id
					// If it does exist, that means the menu is already in the database and we don't need to add it again
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
							System.out.println("Created menu in db with id: " + menuID);
						} else {
							System.out.println("Failed to generate id for menu");
							return false;
						}
						
						// Loop through each MenuItem that is in the current Menu
						for (MenuItem menuItem : menu.getMenuItems()) {
							int menuItemID;
							// Check to see if this MenuItem is already in the database
							statement = conn.prepareStatement("SELECT id FROM MenuItem WHERE name = ?");
							statement.setString(1, menuItem.getName());
							rs = statement.executeQuery();
							
							// If it isn't, insert it and get the id
							// If it is, just get the id
							if(!rs.next()){
								statement = conn.prepareStatement("INSERT INTO MenuItem (name, category) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
								statement.setString(1, menuItem.getName());
								statement.setString(2, menuItem.getCategory());
								statement.executeUpdate();
								rs = statement.getGeneratedKeys();
								if(rs.next()){
									menuItemID = rs.getInt(1);
									System.out.println("Created menu item in db with id: " + menuItemID);
								} else {
									System.out.println("Failed to generate id for menu item");
									return false;
								}
							} else {
								menuItemID = rs.getInt(1);
							}
							
							// Using the id, insert a new ItemOnMenu item to the database which creates a relationship
							// between the item and menu, so that we can use the same item on multiple menus
							statement = conn.prepareStatement("INSERT INTO ItemOnMenu (menu_id,item_id) VALUES (?,?)");
							statement.setInt(1, menuID);
							statement.setInt(2, menuItemID);
							statement.executeUpdate();
						}
					}
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// Close the connection to the database
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
