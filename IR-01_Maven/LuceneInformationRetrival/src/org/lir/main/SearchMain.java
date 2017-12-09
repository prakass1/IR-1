package org.lir.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

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

##############################Example###################################################
java -jar IR P01.jar [path to document folder] [path to index folder] [VS/OK] [query] #
########################################################################################
*/

/*
 * 
 * 
 * Team Members: 
 * SUBASH PRAKASH
 * NIKHIL
 * ANSTUP
 * 
 * Description: Perform a simple indexing and searching strategy using apache Lucene
 * 
 */
public class SearchMain {

	private static void usage() {
		System.out.println("The program should be executed as:" + "java -jar LuceneInformationRetrival-P01.jar"
				+ " [path to document folder] [path to index folder] [VS/OK] [query]");
	}

	static void intializationAndIndex(boolean create, String indexPath, Path docDir,String query) {
		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (create) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);

			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer. But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			// iwc.setRAMBufferSizeMB(256.0);

			IndexWriter writer = new IndexWriter(dir, iwc);
			Indexer indexer = new Indexer();
			indexer.indexDocs(writer, docDir,query);

			// NOTE: if you want to maximize search performance,
			// you can optionally call forceMerge here. This can be
			// a terribly costly operation, so generally it's only
			// worth it when your index is relatively static (ie
			// you're done adding documents to it):
			//
			// writer.forceMerge(1);

			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

	// Lets take the values required as shown above as below values:
	// arg[0] is [path to the doc folder] (this tells us that there should be a doc
	// folder)
	// arg[1] is [path to index folder] (this is the index folder) Need a small
	// clarificaiton
	// arg[2] is [VS/OK] (VS - Vector Space Model ........ OK - Okapi BM25)
	// arg[3] is [query] (this is the query to be search and ranked for)
	public static void main(String args[]) {

		if (args.length == 0) {
			usage();
		} else if (args[0] == null || args[1] == null || args[2] == null || args[3] == null) {
			usage();
		} else if (!(args[2].equals("OK")||args[2].equals("VS"))) {
			usage();
		}
			
		else {
			// Actual execution of the code

			String docFolder = args[0];
			String indexFolder = args[1];
			String model = args[2];
			String query = args[3];

			boolean create = true;

			// A folder check needs to added and then make the create to false if the
			// indexFolder exists
			boolean exists = new File(indexFolder).exists();

			if (exists) {
				System.out.println("Already the index directory is present, so updating...");
				create = false;
			}

			final Path docDir = Paths.get(docFolder);
			if (!Files.isReadable(docDir)) {
				System.out.println("Document directory '" + docDir.toAbsolutePath()
						+ "' does not exist or is not readable, please check the path");
				System.exit(0);
			}

			/*
			 * Intialize the index and perform lucene indexing
			 */
			intializationAndIndex(create, indexFolder, docDir,query);

			IndexSearch search = new IndexSearch(query);

			try {
				search.initSearch(indexFolder, model);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
