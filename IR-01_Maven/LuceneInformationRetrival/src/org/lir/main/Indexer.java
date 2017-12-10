package org.lir.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.Version;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lir.util.HtmlParser;
import org.tartarus.snowball.ext.PorterStemmer;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableFieldType;

public class Indexer {
	
	

	/**
	 * Indexes the given file using the given writer, or if a directory is given,
	 * recurses over files and directories found under the given directory.
	 *
	 */
	public void indexDocs(final IndexWriter writer, Path path,String query) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					try {
						indexDoc(writer, file, attrs.lastModifiedTime().toMillis(),query);
					} catch (IOException ignore) {
						// don't index files that can't be read.
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis(), query);
		}
	}

	/** Indexes a single document */
	public void indexDoc(IndexWriter writer, Path file, long lastModified,String query) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			// make a new, empty document
			Document doc = new Document();
			
			HtmlParser htmlParser = new HtmlParser();
			
			// Add the path of the file as a field named "path". Use a
			// field that is indexed (i.e. searchable), but don't tokenize
			// the field into separate words and don't index term frequency
			// or positional information:
			Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);

			// Add the last modified date of the file a field named "modified".
			// Use a LongPoint that is indexed (i.e. efficiently filterable with
			// PointRangeQuery). This indexes to milli-second resolution, which
			// is often too fine. You could instead create a number based on
			// year/month/day/hour/minutes/seconds, down the resolution you require.
			// For example the long value 2011021714 would mean
			// February 17, 2011, 2-3 PM.
			doc.add(new LongPoint("modified", lastModified));

			// Add the contents of the file to a field named "contents". Specify a Reader,
			// so that the text of the file is tokenized and indexed, but not stored.
			// Note that FileReader expects the file to be in UTF-8 encoding.
			// If that's not the case searching for special characters will fail.	
			org.jsoup.nodes.Document document = htmlParser.parseDoc(file.toString());
		
			String body = document.body().text().toLowerCase();
			//Removing the stop words from text
			String newBody = removeStopWords(body);
			String title = document.title();
			Element ele=document.body();
			String lowerCasedQuery = query.toLowerCase();
			String newQuery=lowerCasedQuery.replaceAll("[^a-zA-Z0-9 ]", "");
			boolean flag= body.contains(newQuery);
			StringBuilder ss = new StringBuilder();
			if (flag) {
				
				Elements thePara  =ele.select("p");
				thePara.select("sup").remove();
				
				for(int i=0;i<thePara.size();i++) {
					if(query.contains("*")) {
						
						Pattern p = Pattern.compile(lowerCasedQuery);
						String match = findMatches(p,thePara.get(i).text());
					if(match!=null) {
					if(thePara.get(i).text().toLowerCase().contains(match))
					{
						ss.append(thePara.get(i).text());
					}
					}
					}
					if(thePara.get(i).text().toLowerCase().contains(newQuery))
					{
						ss.append(thePara.get(i).text());
					}
				}
			}

			if(ss.toString()!=null) {
				Field summaryField = new TextField("summary", ss.toString(), Field.Store.YES);
				doc.add(summaryField);
			}
			
			//Adding body
			Field bodyField = new TextField("contents", newBody, Field.Store.NO);
			doc.add(bodyField);
			
			//Adding Title
			Field titleField = new StringField("title", title, Field.Store.YES);
			doc.add(titleField);
			

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				// New index, so we just add the document (no old document can be there):
				System.out.println("adding " + file);
				writer.addDocument(doc);
			} else {
				// Existing index (an old copy of this document may have been indexed) so
				// we use updateDocument instead to replace the old one matching the exact
				// path, if present:
				System.out.println("updating " + file);
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}

	private String findMatches(Pattern p, String text) {
		// TODO Auto-generated method stub
		Matcher m = p.matcher(text);

	     if (m.find()) {
	       return m.group();
	     }

		return null;
	}
	
	//Porter Stemmer implementation using Snowball Library
	public static  String applyPorterStemmer(String input) throws IOException {

        PorterStemmer stemmer = new PorterStemmer();
        stemmer.setCurrent(input);
        stemmer.stem();
        return stemmer.getCurrent();
    }
	
	//This is where we have implemented the method to allow removal of stopWords.We are currently using this method during the indexing process
	public static String removeStopWords(String body){
		//Default Set of stop words are being used
        CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
        TokenStream tokenStream = new StandardAnalyzer().tokenStream(null, new StringReader(body));
        tokenStream = new StopFilter(tokenStream, stopWords);
        StringBuilder sb = new StringBuilder();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        try {
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            String term = charTermAttribute.toString();
            sb.append(term + " ");
        }
        tokenStream.close();
        }
        catch(IOException e) {
        	e.printStackTrace();
        }
        
        return sb.toString();
    }
}
