package org.lir.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.lir.model.Model;

public class HtmlParser {

	public Document parseDoc(String path) throws IOException {
		File input = new File(path);
		Document doc = Jsoup.parse(input, "UTF-8");
		return doc;
		
	}
	
	public void makeHTML(List<Model> model) throws FileNotFoundException {
		File fileToWrite = new File("output.html");
        PrintWriter pw = new PrintWriter(fileToWrite);
		String html= "<html><head><title>Output Result</title></head><body>";
		html+="<table border='1'><tr><th>RANK</th><th>PATH</th><th>TITLE</th><th>RELSCORE</th></tr>";
		for(Model m:model) {
			
			html+="<tr><td>" + m.getRank() + "</td><td>" + m.getPath() + "</td><td>" + m.getTitle() + "</td><td>" + m.getRelScore() + "</td></tr>";
		}
		
		html+="</table></body></html>";
		
		pw.write(html);
		pw.close();
	}
	
}