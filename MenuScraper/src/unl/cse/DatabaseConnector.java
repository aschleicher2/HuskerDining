package unl.cse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.sql.Connection;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

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
		
		// Load DB connection info from config file
		Properties prop = new Properties();
		InputStream input = null;
		
		String dbURL = null;
		String dbUser = null;
		String dbPass = null;
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
			dbURL = prop.getProperty("dbURL");
			dbUser = prop.getProperty("dbUser");
			dbPass = prop.getProperty("dbPass");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("Huskers");
		dbPass = encryptor.decrypt(dbPass);
		try {
			conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
			System.out.println("Connected to database successfully");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		
		// Initialize a statement that we will be using to insert and view data
		PreparedStatement statement = null;	
		
		
		try {
			// Add the tables if they don't exist
			System.out.println("Adding tables into database if they don't exist");
			
			// Hall table
			statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS Hall ("
					+ "id INT NOT NULL AUTO_INCREMENT,"
					+ "name VARCHAR(45) NOT NULL,"
					+ "address VARCHAR(45),"
					+ "phone VARCHAR(45),"
					+ "manager VARCHAR(45),"
					+ "PRIMARY KEY (id))");
			statement.executeUpdate();
			
			// Menu table
			statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS Menu ("
					+ "id INT NOT NULL AUTO_INCREMENT,"
					+ "meal VARCHAR(45) NOT NULL,"
					+ "date LONG NOT NULL,"
					+ "fromTime VARCHAR(45),"
					+ "toTime VARCHAR(45),"
					+ "hall_id INT NOT NULL,"
					+ "FOREIGN KEY (hall_id)"
					+ "REFERENCES Hall(id),"
					+ "PRIMARY KEY (id))");
			statement.executeUpdate();
			
			// MenuItem table
			statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS MenuItem ("
					+ "id INT NOT NULL AUTO_INCREMENT,"
					+ "name VARCHAR(45) NOT NULL,"
					+ "category VARCHAR(45),"
					+ "PRIMARY KEY (id))");
			statement.executeUpdate();
			
			// ItemOnMenu table
			statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS ItemOnMenu ("
					+ "menu_id INT NOT NULL,"
					+ "item_id INT NOT NULL,"
					+ "FOREIGN KEY (menu_id)"
					+ "REFERENCES Menu(id),"
					+ "FOREIGN KEY (item_id)"
					+ "REFERENCES MenuItem(id))");
			statement.executeUpdate();
			
			System.out.println("Tables ready");
			
			// Initialize ResultSet for use in the for loops
			ResultSet rs;
			
			// Loop through each DiningHall that is available
			
			for (DiningHall diningHall : halls) {
				
				
				int hallID;
				// Check to see if the DiningHall already exists in the database
				statement = conn.prepareStatement("SELECT id FROM Hall WHERE name = ?");
				statement.setString(1, diningHall.getName());
				rs = statement.executeQuery();
				
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
				
				double count = 0;
				// Loop through each Menu that is in the current DiningHall
				for (Menu menu : diningHall.getMenus()) {
					double percentDone = (count/diningHall.getMenus().size())*100;
					
					// Print the percentage done for user of program
					System.out.printf("Inserting data for %s: %.2f%%\n",diningHall.getName(),percentDone);
					
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
					
					count++;					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("Finished inserting data into the database");
		
		// Close the connection to the database
		try {
			conn.close();
			System.out.println("Connection closed successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return true;
	}
}
