package unl.cse;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.sql.Connection;

import java.sql.Statement;

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
			conn = DriverManager.getConnection("jdbc:mysql://cse.unl.edu:3306/askinner", "askinner", "x");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		System.out.println("Connected successfully");
		
		Statement statement = null;
		
		try {
			statement = conn.createStatement();
			statement.execute("INSERT INTO Hall (name) VALUES ('Selleck')");
			System.out.println("Statement executed");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
