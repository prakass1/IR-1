package org.lir.controller;

/*
Program your own (command-line based) Information Retrieval system using Apache
Lucene1 version 7.1.0 and Java. Lucene is an open source search library that provides
standard functionality for analyzing, indexing, and searching text-based documents. The
following criteria have to be met by your Information Retrieval system. 

Your program should ...

1. Using Lucene, parse and index HTML documents that a given folder and its
subfolders contain. List all parsed files.

2. Consider the English language and use a stemmer for it (e.g. Porter Stemmer).

3. Select an available search index or create a new one (if not available in the chosen
directory).

4. Make possible for the user to choose the ranking model, Vector Space Model (VS)
or Okapi BM25 (OK).

5. Print a ranked list of relevant articles given a search query. The output should
contain 10 most relevant documents with their rank, title and summary, relevance
score and path.

6. Search multiple fields concurrently (multifield search): not only search the
document's text (body tag), but also its title.

The program must run without any corrections or modifications of the runtime
environment (Java 8 ) or source code! Create a jar-File named IR P01.jar. It should
process the input:

#############################Example###################################################
java -jar IR P01.jar [path to document folder] [path to index folder] [VS/OK] [query] #
#######################################################################################
*/

/*
 * 
 * 
 * Team Members:
 * 
 * Team Name:
 * 
 * Description:
 * 
 * TODO
 */
public class Searcher {
	
	private static void usage() {
		System.out.println("The program should be executed as:"
				+ "java -jar IR P01.jar"
				+ " [path to document folder] [path to index folder] [VS/OK] [query]");		
	}
	
	private static String VS = "VS";
	private static String OK = "OK";
	//Lets take the values required as shown above as below values:
	//arg[0] is [path to the doc folder] (this tells us that there should be a doc folder)
	//arg[1] is [path to index folder] (this is the index folder) Need a small clarificaiton
	//arg[2] is [VS/OK] (VS - Vector Space Model ........  OK - Okapi BM25)
	//arg[3] is [query] (this is the query to be search and ranked for)
	public static void main(String args[]) {
		

		
		if(args.length == 0) {
			usage();
		}
		else if(args[0] == null || args[1] == null || args[2] == null || args[3]==null) {
			usage();
		}
		else {
			//Actual execution of the code
			String docFolder = args[0];
			String indexFolder = args[1];
			String model = args[2];
			String query = args[3];	
			
			//Ideal to use Switch cases as well
			if(model.equals(VS)) {
				//Perform Vector Space Model Execution
			}
			else if(model.equals(OK)) {
				//Perform 
			}
			else {
				//Wrong model Mostly an exit
			}
		}
	}
}