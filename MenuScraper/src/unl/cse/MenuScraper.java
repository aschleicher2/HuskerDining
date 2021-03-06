package unl.cse;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				new MenuScraper();
			}
			
		}, 0, 7, TimeUnit.DAYS);
	}
	
	public MenuScraper() {
		try {
			// Establish a connection to the database
			connectToMenu();
			
			System.out.println("Successfully connected to menu");
			
			// Initialize the array that will store all of our data
			halls = new ArrayList<DiningHall>();
			
			// Begin scraping process, which will insert the data found into the halls array
			loadMenu();
			
			// Upload the data to the database
			DatabaseConnector.uploadMenuData(halls);
			
			System.out.println("Done!");
			
			// Print the menu for debugging
//			printMenu();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	private void printMenu() {
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
		
		// Find the datePicker so that we can advance through each day
		HtmlInput datePicker = (HtmlInput) page.getElementById("cphpageTitle_ctl00_txtdpMealDate");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date initialDate = null;
		try {
			initialDate = dateFormat.parse(datePicker.asText());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		List<HtmlOption> options = select.getOptions();
		for (HtmlOption option : options) {
			if(!option.asText().equals("Please Select...")){
				System.out.println("Scraping " + option.asText());
				// For now, just select Selleck which is value 23. Future phase will get all halls.
	//			HtmlOption option = select.getOptionByValue("23");
				select.setSelectedAttribute(option, true);
				
				DiningHall hall = new DiningHall(option.asText());
				
				
				
				// Initialize the button so that we can simulate it when selecting new dates and halls
				HtmlElement button = (HtmlElement) page.getElementById("cphpageTitle_btnSubmit");
				
				// Loop for 14 days worth of menus
				Date currentDate = new Date(initialDate.getTime());
				for (int i = 0; i < 14; i++) {
					// Increments the date by 1 if not the first iteration
					if(i != 0){
						Calendar c = Calendar.getInstance();
						c.setTime(currentDate);
						c.add(Calendar.DATE, 1);
						currentDate = c.getTime();
					}
					
					// Print the percentage done for user of program
					double percentDone = ((double)i/14)*100;
					System.out.printf("Scraping menus: %.2f%% => %s\n",percentDone,currentDate);
					
					// Set the date to the next day
					datePicker.setValueAttribute(dateFormat.format(currentDate));
					
					// Simulate pressing the GO button, keep trying if it fails
					HtmlPage menuPage = null;
					while (menuPage == null) {
						try {
							button.click();
							ScriptResult result = page.executeJavaScript("cphpageTitle_btnSubmit.onclick()");
							menuPage = (HtmlPage) result.getNewPage();
						} catch (Exception e) {
							System.out.println("Unable to load, trying again...");
						}
					}
					
					
					
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
									// Make sure the span is actually a MenuItem
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
										// Split the hours so that we can get a fromTime and a toTime
										String[] hours = infoSplit[1].replace(" ", "").replace("(", "").replace(")", "").split("-");
										fromTime = hours[0];
										toTime = hours[1];
									} catch (Exception e) {
										// Unable to get hours, most likely because there is no meal
									}
									
									
									// Initialize the menu with the data found
									menu = new Menu(currentDate, meal, fromTime, toTime);
									break;
								default:
									// Unknown element, skip
									break;
							}
						}
					}
					
					// Add the last menu since it hasn't been added
					if(menu!=null){
						hall.addMenu(menu);
					}
				}
				
				System.out.println("Finished scraping menus");
	
				
				// Finally, add the hall to the list of dining halls
				halls.add(hall);
			}
		}
	}
}
