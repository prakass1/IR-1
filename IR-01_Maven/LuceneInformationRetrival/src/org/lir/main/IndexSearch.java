package org.lir.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.lir.model.Model;

public class IndexSearch {

	String field = "contents";
	String queryString = null; // this will come from the cmd parameter
	int repeat = 0;
	private static String VS = "VS";
	private static String OK = "OK";
	boolean raw = false;
	//looking for about 10 keywords in the page at least
	int hitsPerPage = 10;

	public IndexSearch(String queries) {
		if (queries != null) {
			this.queryString = queries;
		}
	}

	public void initSearch(String index, String mod) throws IOException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		if (mod.equalsIgnoreCase(OK)) {
			System.out.println("Calculating BM25 Similarity");
			BM25Similarity similarity = new BM25Similarity();
			searcher.setSimilarity(similarity);
		} else if(mod.equalsIgnoreCase(VS)) {
			System.out.println("Calculating TFIDF Similarity");
			//This is TFIDF as ClassicSimilarity implements from TFIDFSimilarity
			ClassicSimilarity sim = new ClassicSimilarity();
			searcher.setSimilarity(sim);
		}
		else {
			System.out.println("Invalid Similarity provided");
			System.exit(0);
		}

		Analyzer analyzer = new StandardAnalyzer();
		try {

			BufferedReader in = null;

			if (this.queryString != null) {
				in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
			}

			QueryParser parser = new QueryParser(field, analyzer);
			String line = queryString != null ? queryString : in.readLine();

			if (line == null || line.length() == -1) {
				System.out.println("The queryString cannot be empty" + line);
			}

			line = line.trim();

			Query query = parser.parse(line);
			 int tf = searcher.count(query);
			 System.out.println("Term Freq" + tf);
			System.out.println("Searching for: " + query.toString(field));

			List<Model> m = doSearch(in, searcher, query, hitsPerPage);

			System.out.println("|| RANK || PATH || Score ||");
			for (Model model : m) {
				// For each model obtain use the path to parse the html file and
				// Search the title and summary (Here: Summary should relevant to the query) and
				// print it out
				// All implementation for parsing should go into utility package
				System.out.println("|| " + model.getRank() + " ||" + model.getPath() + " || " + model.getRelScore() + " ||");
			}

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			reader.close();
		}

	}

/**
 * 
 * This method performs a search for the query in each document by the search model and hits defined. If there is are docs matching the model is constructed and 
 * returned back as an ArrayList<Model>
 * 
 * @param in
 * @param searcher
 * @param query
 * @param hitsPerPage
 * @return List<Model> {@link#Model}
 * @throws IOException
 */
	private List<Model> doSearch(BufferedReader in, IndexSearcher searcher, Query query, int hitsPerPage)
			throws IOException {

		//Collect enough documents based on the hitsPerPage
		TopDocs results = searcher.search(query, hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;
		List<Model> model = new ArrayList<Model>();
		int numTotalHits = Math.toIntExact(results.totalHits);
		System.out.println(numTotalHits + " total matching documents");

		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);

		if (end > hits.length) {
			System.out.println(
					"Only results 1 - " + hits.length + " of " + numTotalHits + "total matching documents collected.");
			System.out.println("Collect more (y/n) ?");
			String line = in.readLine();
			if (line.length() == 0 || line.charAt(0) == 'n') {
				System.exit(0);
			}
			hits = searcher.search(query, numTotalHits).scoreDocs;
		}
		for (int i = start; i < end; i++) {
			
			Document doc = searcher.doc(hits[i].doc);
			float docScore = hits[i].score;
			Model m = new Model();
			String path = doc.get("path");
			if (path != null) {
				//System.out.println((i + 1) + ". " + path);
				m.setRank(i + 1);
				m.setPath(path);
				//Explanation explain = searcher.explain(query, i + 1);
				 //String desc = explain.getDescription();
				 m.setRelScore(docScore);
				//System.out.println("Desc " + desc);
				System.out.println("Score " + docScore);
				System.out.println("Explanations:: " + searcher.explain(query, i + 1));
				/*//String title = doc.get("title");
				if (title != null) {
					System.out.println("   Title: " + doc.get("title"));

				}*/
			} else {
				System.out.println((i + 1) + ". " + "No path for this document");
			}
			if (m.getPath() != null) {
				model.add(m);
			}
		}
		end = Math.min(numTotalHits, start + hitsPerPage);
		return model;
	}
}
