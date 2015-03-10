package unl.cse;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

public class MenuScraper {
	private static String menuURL = "http://menu.unl.edu";
	private HtmlPage page;
	private ArrayList<DiningHall> halls;
	
	public static void main(String[] args) {
		MenuScraper ms = new MenuScraper();
	}
	
	public MenuScraper() {
		try {
			connectToMenu();
			
			halls = new ArrayList<DiningHall>();
			
			loadMenu();
			System.out.println("Menu successfully scraped and loaded");
			testMenu();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DatabaseConnector.uploadMenuData(halls);
	}

	private void testMenu() {
		for (DiningHall hall : halls) {
			System.out.println(hall.getName());
			for (Menu menu : hall.getMenus()) {
				System.out.println(menu.getMeal() + " " + menu.getFromTime() + " - " + menu.getToTime());
				for (MenuItem menuItem : menu.getMenuItems()) {
					System.out.println(menuItem.getCategory() + ": " + menuItem.getName());
				}
				System.out.println();
			}
			System.out.println();
		}
	}

	private void connectToMenu() throws Exception{
		// Silence javascript errors
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		
		// Initialize a new web client acting like Chrome
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
		
		// Get the page from the provided menu URL
		page = webClient.getPage(menuURL);
	}

	private void loadMenu() throws IOException {
		// Select the drop-down for the list of dining halls
		HtmlSelect select = (HtmlSelect) page.getElementById("cphpageTitle_gComplex");
		
		// For now, just select Selleck which is value 23. Future phase will get all halls.
		HtmlOption option = select.getOptionByValue("23");
		select.setSelectedAttribute(option, true);
		
		DiningHall hall = new DiningHall("Selleck");
		
		// Find the datePicker so that we can advance through each day
		HtmlInput datePicker = (HtmlInput) page.getElementById("cphpageTitle_ctl00_txtdpMealDate");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date currentDate = null;
		try {
			currentDate = dateFormat.parse(datePicker.asText());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Initialize the button so that we can simulate it when selecting new dates and halls
		HtmlElement button = (HtmlElement) page.getElementById("cphpageTitle_btnSubmit");
		
		// Loop for 14 days worth of menus
		for (int i = 0; i < 14; i++) {
			// Increments the date by 1 if not the first iteration
			if(i != 0){
				Calendar c = Calendar.getInstance();
				c.setTime(currentDate);
				c.add(Calendar.DATE, 1);
				currentDate = c.getTime();
			}
			
			// Set the date to the next day
			datePicker.setValueAttribute(dateFormat.format(currentDate));
			
			// Simulate pressing the GO button
			button.click();
			ScriptResult result = page.executeJavaScript("cphpageTitle_btnSubmit.onclick()");
			HtmlPage menuPage = (HtmlPage) result.getNewPage();
			
			
			// Select the grid that contains all of the menus
			HtmlElement menuLists = (HtmlElement) menuPage.getByXPath( "//div[@class='wdn-grid-set']").get(1);
			
			Menu menu = null;
			String category = null;
			
			
			// Loop through each menuList and create the menus as it is going through
			for (DomElement menuList : menuLists.getChildElements()) {
				for (DomElement menuElement : menuList.getChildElements()) {
					switch (menuElement.getTagName()) {
						case "span":
							// Menu Item
							if(menuElement.getAttribute("class").equals("MenuItem")){
								MenuItem menuItem = new MenuItem(menuElement.asText(), category);
								menu.addMenuItem(menuItem);
							}
							
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
							
							String fromTime = null;
							String toTime = null;
							try{
								String[] hours = infoSplit[1].replace(" ", "").replace("(", "").replace(")", "").split("-");
								fromTime = hours[0];
								toTime = hours[1];
							} catch (Exception e) {
								// Unable to get hours, most likely because there is no meal
							}
							
							
							// Initialize the menu with the data
							menu = new Menu(currentDate, meal, fromTime, toTime);
							break;
						default:
							// Unknown, skip
							break;
					}
				}
			}
			
			// Add the last menu since it hasn't been added
			if(menu!=null){
				hall.addMenu(menu);
			}
		}

		
		// Finally, add the hall to the list of dining halls
		halls.add(hall);
	}
}
