import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ReadConfig {
	
	public Map<String, String> factorVals;
	public List<String> factors; 

	
	
    /**
     * 	Method for Reading the URL and Depth Value from the property files.
     * 	Param: Name of the property file. 
     *	Return: Map of Property and their value.
     */
	public Map executeFileLoader(String propertyFile) {
		
		Properties propertyVals = new Properties();
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertyFile);
			propertyVals.load(inputStream);
				
			String URL =	propertyVals.getProperty("URL");
			String Depth =	propertyVals.getProperty("Depth");
			factorVals = new HashMap<String, String>();
			
			for(int i=0; i<2; i++)
			{
				if(i==0)
					{
						factorVals.put("URL", URL);
					}	
				else
					{
						factorVals.put("Depth", Depth);
					}
			}
		
		} 
		catch (IOException e) {
			System.out.println("File not Found.");
			e.printStackTrace();
		}
		return factorVals;	
	}
}
