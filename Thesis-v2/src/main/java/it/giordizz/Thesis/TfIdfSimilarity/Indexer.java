package it.giordizz.Thesis.TfIdfSimilarity;




import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


/**
 * 
 * @author giordano
 *
 * Fornisce i metodi per la creazione dell'indice lucene e per l'aggiunta dei documenti da indicizzare.
 *
 */
public class Indexer {


	
	private IndexWriter writer;
	private int numDocsWritten = 0;
	private FieldType fieldForText; 
	
	public void setUpIndex(String path) throws IOException {
		 
		BufferedReader br = new BufferedReader(new FileReader("data/stopwords.txt"));
		
		CharArraySet stopWords = new CharArraySet(Version.LUCENE_4_9,173,true);

		String line;
		while ((line = br.readLine()) != null) 
			stopWords.add(line);
		br.close();
		
		Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_4_9,stopWords);
		
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
		File indexDir = new File(path);
		writer = new IndexWriter(FSDirectory.open(indexDir), iwc);
		
		
		fieldForText = new FieldType();
		fieldForText.setIndexed(true);
		fieldForText.setTokenized(true);
		fieldForText.setStored(true);
		fieldForText.setOmitNorms(true);
		fieldForText.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		fieldForText.setStoreTermVectors(true);
		fieldForText.freeze();
		
		
		
		
	}
	
	public void addDocuments(ArrayList<Pair<String, String>> titlesAndAbstracts) throws IOException {
		
		for ( Pair<String, String> titleAndAbstract : titlesAndAbstracts) { 
			addDocument(titleAndAbstract.first, titleAndAbstract.second);
			numDocsWritten ++;
			System.err.println("written doc # " + numDocsWritten);
		}
				
	}
	
	
	public void addDocument(String title, String text) throws IOException {
		Document document = new Document();

		
		document.add(new StringField("title", title, Field.Store.YES));
		document.add(new Field("abstract", text, fieldForText));
		
		writer.addDocument(document);
		
	}
	
	public void finalize() throws IOException{
		writer.close();
		System.err.println("index written in indexDir");
	}
	
	


}
