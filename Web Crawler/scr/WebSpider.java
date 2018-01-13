import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class WebSpider {

    private static final int MAX_DEPTH = 3;
    private HashSet<String> links;
    public ArrayList SavedLinks;

    
    //Constructor for the class.
    public WebSpider() {
    	links = new HashSet<>();
    	SavedLinks =  new ArrayList<>();
    }

    
    public static void main(String[] args) {
    	ReadConfig configuration =  new ReadConfig();
    	WebSpider Crawler = new WebSpider();
    	Map propertyVals = new HashMap();
    	propertyVals = configuration.executeFileLoader("Crawler.properties");
    	
    	String URL = propertyVals.get("URL").toString();
    	int Depth = Integer.parseInt(propertyVals.get("Depth").toString());
    	
    	Crawler.extractWebpageLink( URL, Depth );
    	Crawler.writeToFile("CrawledURLS.txt"); 	
    }
    
    /**
     * 	Method for crawling through the webpages and extracting the Urls.
     * 	Storing the URL in ArrayList<> SavedLinks.
     * 	Param: URL to be crawled, Depth. 
     *	
     */
    
    public void extractWebpageLink(String URL, int depth) {
        if ((!links.contains(URL) && (URL != "" ) && (depth < MAX_DEPTH))) {
            System.out.println("Depth: " + depth + " [" + URL + "]");
            
            //Saving the URL in a ArrayList.
            SavedLinks.add(URL);
           
            try {            	
                links.add(URL);
                
                // Fetch the HTML code
                Document document = Jsoup.connect(URL).get();
                // Parse the HTML to extract links to other URLs
                Elements linksOnPage = document.select("a[href]");
                // Increment the depth.
                depth++;
              
                // For each extracted URL... recursively call the method extractWebpageLink().
                for (Element webpage : linksOnPage) {
                    extractWebpageLink(webpage.attr("abs:href"), depth);
                }

            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }

    
    
    /**
     * 	Method for Writing the Urls stored in the arraylist to a .txt file.
     * 	Param: File name to be stored. 
     *	
     */
    
    public void writeToFile(String filename) {
        FileWriter writer;
        
        try {
            writer = new FileWriter(filename);
            
            System.out.println("Writting URLS to " + filename + " ");
            
            //Iterating the URLs in the Array list and writting them to the file.   
            for(int i=0; i<SavedLinks.size(); i++) {
                try {
           
                    String Urls = "URL: " + SavedLinks.get(i) + " \n";
                    //save to file
                    writer.write(Urls);
                    writer.write(System.getProperty( "line.separator" ));
                            
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
            writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
}
