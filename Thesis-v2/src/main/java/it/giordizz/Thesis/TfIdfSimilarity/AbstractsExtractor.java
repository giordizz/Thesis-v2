package it.giordizz.Thesis.TfIdfSimilarity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * 
 * @author giordano
 *
 * Recupera gli abstracts (dal dataset di dbpedia) delle pagine che costituiscono un campione rappresentativo,
 *  per ogni catregoria intermedia.
 * Genera l'indice lucene a partire dagli abstract di cui sopra.
 */

public class AbstractsExtractor {
	
	HashMap<Integer,String> pageIdsToTitle = new HashMap<Integer,String>();
	

	
	public  ArrayList<Pair<String, String>> getAbstracts(HashSet<Integer> pageIds) throws FileNotFoundException, IOException{
		
		System.err.println("getting abstracts");
		
		BufferedReader br = new BufferedReader(new FileReader("data/My_IDs"));
		String line;
		while( (line=br.readLine())!=null) {
			String[] pair = line.split("\t");
			pageIdsToTitle.put(Integer.parseInt(pair[1]), pair[0]);
		}
		br.close();
		
		
		
		HashSet<String> titles= getTitlesFromIds(pageIds);
		System.err.println("titles " + titles.size());
		
		System.err.println("mapping id to title complete");
		
//		HashSet<String> titles= new HashSet<String>(){{ add("Momentum_trader"); }};
		
		
		Model model = ModelFactory.createDefaultModel();
		model.read(new GZIPInputStream(new FileInputStream("data/long_abstracts_en.ttl.gz")),null,"TTL");

		StmtIterator iter;
		Statement stmt;
	
		iter = model.listStatements();

		
		ArrayList<Pair<String,String>> titlesAndAbstracts = new ArrayList<Pair<String,String>>();
		
		System.err.println("starting extracting abstracts");
		int contained = 0;
		while (iter.hasNext())  {
			stmt = iter.next();

			String[] subject= (stmt.getSubject().toString()).split("/");
			if (titles.contains(subject[subject.length-1])) {
				
				
				String text = stmt.getObject().asLiteral().getString();
				titlesAndAbstracts.add(new Pair<String,String>(subject[subject.length-1],text));
				contained++;
				
			}
				
		    
		}
		System.err.println("contained " + contained);

		
		return titlesAndAbstracts;
	}
	
	
	public  ArrayList<Pair<String, String>> getAllAbstracts() throws FileNotFoundException, IOException{
		

		
		System.err.println("mapping id to title complete");
		
		Model model = ModelFactory.createDefaultModel();
		model.read(new GZIPInputStream(new FileInputStream("data/long_abstracts_en.ttl.gz")),null,"TTL");

		StmtIterator iter;
		Statement stmt;
	
		iter = model.listStatements();

		
		ArrayList<Pair<String,String>> titlesAndAbstracts = new ArrayList<Pair<String,String>>();
		
		System.err.println("starting extracting abstracts");
		int contained = 0;
		while (iter.hasNext())  {
			stmt = iter.next();

			String[] subject= (stmt.getSubject().toString()).split("/");
				
			String text = stmt.getObject().asLiteral().getString();
			titlesAndAbstracts.add(new Pair<String,String>(subject[subject.length-1],text));
			contained++;
				
		    
		}
		
		return titlesAndAbstracts;
	}
	
	

	public void writeAbstractsToFile(ArrayList<Pair<String, String>> titlesAndAbstracts) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("data/titlesAndAbstracts.txt", "UTF-8");
		
		for ( Pair<String, String> titleAndAbstract : titlesAndAbstracts) 
			writer.printf("%s\n%s\n", titleAndAbstract.first,titleAndAbstract.second);
			
		System.err.println("data/titlesAndAbstracts.txt written");
		writer.close();
		
	}

	public ArrayList<Pair<String, String>> getAbstractsFromFile() throws IOException{
		
		ArrayList<Pair<String, String>> res = new ArrayList<Pair<String, String>>();
		
		BufferedReader br = new BufferedReader(new FileReader("data/titlesAndAbstracts.txt"));
		String line;
		while( (line=br.readLine())!=null) {
			res.add(new Pair<String, String>(line, br.readLine()));
			
		}
			
		br.close();
		
		return res;
		
	}
	
	
	private HashSet<String> getTitlesFromIds(HashSet<Integer> pageIds) {
		
		HashSet<String> titles= new HashSet<String>(pageIds.size());
		
		for (Integer pageId: pageIds)
			titles.add(pageIdsToTitle.get(pageId));
		
		return titles;
		
	}
	

	
	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader("data/articlesOfIntermediateCategories.txt"));
		
		HashSet<Integer> pageIds = new HashSet<Integer>();
//		ArrayList<Integer> pageIdsV = new ArrayList<Integer>();
		String line;
		while( (line=br.readLine())!=null) {
			String[] ids = line.split("\t");
			for (String id: ids){
				pageIds.add(Integer.parseInt(id));
//				pageIdsV.add(Integer.parseInt(id));
			}
		}
		br.close();

		System.err.println("number of id to index "+ pageIds.size());
//		System.err.println("number of id to index2 "+ pageIdsV.size());
		AbstractsExtractor ae = new AbstractsExtractor();
		ArrayList<Pair<String, String>> titlesAndAbstracts = ae.getAbstracts(pageIds);
		ae.writeAbstractsToFile(titlesAndAbstracts);
		
//		ArrayList<Pair<String, String>> titlesAndAbstracts = ae.getAllAbstracts();
//		
//		ArrayList<Pair<String, String>> titlesAndAbstracts = ae.getAbstractsFromFile();
		
		System.err.println("number of documents to index "+ titlesAndAbstracts.size());
		
		Indexer i = new Indexer();
		i.setUpIndex("./indexDir");
		i.addDocuments(titlesAndAbstracts);
		i.finalize();
		
	}
	
}
