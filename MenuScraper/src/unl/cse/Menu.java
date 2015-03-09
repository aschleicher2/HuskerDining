package unl.cse;

import java.util.Date;
import java.util.ArrayList;

public class Menu {
	private Date date;
	private String meal;
	private String fromTime;
	private String toTime;
	private ArrayList<MenuItem> menuItems;
	
	
	public Menu(Date date, String meal, String fromTime, String toTime) {
		this.date = date;
		this.meal = meal;
		this.fromTime = fromTime;
		this.toTime = toTime;
		
		menuItems = new ArrayList<MenuItem>();
	}
	
	public void addMenuItem(MenuItem menuItem) {
		menuItems.add(menuItem);
	}
	
	public ArrayList<MenuItem> getMenuItems() {
		return menuItems;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getFromTime() {
		return fromTime;
	}
	
	public String getToTime() {
		return toTime;
	}
	
	public String getMeal() {
		return meal;
	}
}
