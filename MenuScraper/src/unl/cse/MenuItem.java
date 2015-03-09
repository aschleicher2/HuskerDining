package unl.cse;

public class MenuItem {
	private String name;
	private String category;
	
	public MenuItem(String name, String category) {
		this.name = name;
		this.category = category;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCategory() {
		return category;
	}
}
