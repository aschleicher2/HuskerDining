package unl.cse;


import java.util.Date;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

public class MenuScraper {
	private static String menuURL = "http://menu.unl.edu";
	
	public static void main(String[] args) {
		MenuScraper ms = new MenuScraper();
	}
	
	public MenuScraper() {
		connectToMenu();
		loadMenu();
	}

	private void connectToMenu() {
		
		// Silence javascript errors
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		
		// Initialize a new web client acting like Chrome
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
		try {
			// Get the page from the provided menu URL
			HtmlPage page = webClient.getPage(menuURL);
			
			// Select the drop-down for the list of dining halls
			HtmlSelect select = (HtmlSelect) page.getElementById("cphpageTitle_gComplex");
			
			// For now, just select Selleck which is value 23. Future phase will get all halls.
			HtmlOption option = select.getOptionByValue("23");
			select.setSelectedAttribute(option, true);
			
			// Simulate pressing the GO button
			HtmlElement button = (HtmlElement) page.getElementById("cphpageTitle_btnSubmit");
			button.click();
			ScriptResult result = page.executeJavaScript("cphpageTitle_btnSubmit.onclick()");
			HtmlPage menuPage = (HtmlPage) result.getNewPage();
			
			DiningHall hall = new DiningHall("Selleck");
			
			// Select the grid that contains all of the menus
			HtmlElement menuLists = (HtmlElement) menuPage.getByXPath( "//div[@class='wdn-grid-set']").get(1);
			System.out.println(menuLists.asXml());
			
			Menu menu = null;
			String category = null;
			for (DomElement menuList : menuLists.getChildElements()) {
				for (DomElement menuElement : menuList.getChildElements()) {
					switch (menuElement.getTagName()) {
						case "span":
							// Menu Item
							MenuItem menuItem = new MenuItem(menuElement.asText(), category);
							menu.addMenuItem(menuItem);
							break;
						case "h6":
							// Menu Category
							category = menuElement.asText();
							break;
						case "h4":
							// Meal name and hours
							
							// If a meal already exists, then add it to the dining hall and reset the category
							if(menu != null){
								hall.addMenu(menu);
								category = null;
							}
							
							// Parse the info to extract meal and times
							String info = menuElement.asText();
							String[] infoSplit = info.split("\n");
							String meal = "";
							if(infoSplit[0].contains("Breakfast")){
								meal = "Breakfast";
							} else if (infoSplit[0].contains("Lunch")){
								meal = "Lunch";
							} else if (infoSplit[0].contains("Dinner")){
								meal = "Dinner";
							}
							String[] hours = infoSplit[1].replace(" ", "").replace("(", "").replace(")", "").split("-");
							String fromTime = hours[0];
							String toTime = hours[1];
							
							
							menu = new Menu(new Date(), meal, fromTime, toTime);
							break;
						default:
							// Unknown, skip
							System.out.println("Unknown, skipping");
							break;
					}
				}
			}
			System.out.println("Done");
			
			
		} catch (Exception e) {
			
		}
	}

	private void loadMenu() {
		
		
		
	}
}
