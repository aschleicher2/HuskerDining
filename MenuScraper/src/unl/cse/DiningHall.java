package unl.cse;

import java.util.ArrayList;

public class DiningHall {
	private String name;
	private ArrayList<Menu> menus;
	
	public DiningHall(String name) {
		this.name = name;
		menus = new ArrayList<Menu>();
	}
	
	public String getName() {
		return name;
	}
	
	public void addMenu(Menu menu) {
		if(!menu.isEmpty()){
			menus.add(menu);
		}
		
	}
	
	public ArrayList<Menu> getMenus() {
		return menus;
	}
}
