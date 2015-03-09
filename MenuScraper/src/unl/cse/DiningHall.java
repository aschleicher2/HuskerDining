package unl.cse;

import java.util.ArrayList;

public class DiningHall {
	private String name;
	private ArrayList<Menu> menus;
	
	public DiningHall(String name) {
		this.name = name;
		menus = new ArrayList<Menu>();
	}
	
	public void addMenu(Menu menu) {
		menus.add(menu);
	}
}
