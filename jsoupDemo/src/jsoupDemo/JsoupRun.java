package jsoupDemo;
import java.io.File;
import java.io.IOException;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class JsoupRun {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File input = new File("/Users/nikhil/Desktop/temp.html");
		try {
			Document doc=Jsoup.parse(input,"UTF-8","http://example.com/");
			Elements title=doc.select("title");
			Elements para=doc.select("body");
			String ment = null;
			for (Element step : title) {
				ment=step.select("title").text();
				
				}
			String h = null;
	
			h=para.select("p").text();
		
				System.out.println(ment);
				System.out.println(h);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
