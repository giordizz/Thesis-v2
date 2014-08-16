package it.giordizz.Thesis.DataUtils.Miscellaneous;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * 
 * @author giordano
 *
 * Permette di generare un file di tante righe quante sono le categorie intermedie (122 meno una che non contiene pagine);
 * la riga i-esima identifica l'insieme degli ID delle pagine appartenente alla categoria i-esima. 
 *
 */
public class createArticlesToIndex {

	Map<Integer,Vector<Integer>> categories = new HashMap<Integer, Vector<Integer>>();
	Map<Integer,Vector<Integer>> reverse_categories = new HashMap<Integer, Vector<Integer>>();
	Map<Integer,Vector<Integer>> reverse_articles= new HashMap<Integer, Vector<Integer>>();
	
	TreeSet<Integer> intermediate_categories= new TreeSet<Integer> ();

	
	public void getData() throws IOException{
		GZIPInputStream	gzip2=null;
		
		gzip2 = new GZIPInputStream(new FileInputStream("data/wiki-categories-only-links-sorted.gz"));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(gzip2));
		
		String riga;
		while((riga=br2.readLine())!=null) {
			final String[] s=riga.split("\t");
			Vector<Integer> cur1 = categories.get(Integer.parseInt(s[0]));
			if (cur1==null) 
				categories.put(Integer.parseInt(s[0]),new Vector<Integer>(){{add(Integer.parseInt(s[1]));}});
			else {
				cur1.add(Integer.parseInt(s[1]));
//				categories.put(s[0],cur1);
			}
			
			// adding manually "Tradition" parents category 

			
			Vector<Integer> cur2 = reverse_categories.get(Integer.parseInt(s[1]));
			if (cur2==null) 
				reverse_categories.put(Integer.parseInt(s[1]),new Vector<Integer>(){{add(Integer.parseInt(s[0]));}});
			else {
				cur2.add(Integer.parseInt(s[0]));
//				reverse_categories.put(s[1],cur2);
			}			
			
		}	
		
		categories.put(31859274, new Vector<Integer>(){{
			add(715355);
			add(709574);
			add(40822338);
			add(1633936);
		}});
		
		br2.close();
		
		BufferedReader reader = new BufferedReader(new FileReader("data/new_intermediateCatSorted"));
		String line1;
		while( (line1=reader.readLine())!=null) {
			intermediate_categories.add(Integer.parseInt(line1));
		     //TODO
		}
		
		reader.close();
		
		
		GZIPInputStream	gzip1;
		
		gzip1 = new GZIPInputStream(new FileInputStream("data/wiki-article-categories-links-sorted.gz"));
		BufferedReader br1 = new BufferedReader(new InputStreamReader(gzip1));
		String line="";
		while((line=br1.readLine())!=null) {
			final String[] s=line.split("\t");
			Vector<Integer> cur = reverse_articles.get(Integer.parseInt(s[1]));
			if (cur==null) 
				reverse_articles.put(Integer.parseInt(s[1]),new Vector<Integer>(){{add(Integer.parseInt(s[0]));}});
			 else {
				cur.add(Integer.parseInt(s[0]));

			}
		}
		reverse_articles.put(31859274, new Vector<Integer>(){{
			add(173387);
			add(2688034);
			add(30759653);
			add(101140);
			add(30762076);
			add(802517);
			add(1459070);
			
		}});
		
		        
		br1.close();
		
		
	}
	
	
	public void createSiblingsOfIntermediateCatsFile() throws FileNotFoundException, UnsupportedEncodingException{
		
		PrintWriter writer = new PrintWriter("data/intermediateCatsSiblingsGroups.txt", "UTF-8");
		
		int count = 0;
		int count2 = 0;
		for ( Integer c: intermediate_categories){
			HashSet<Integer> siblings = new HashSet<Integer>();
			Vector<Integer> parents = categories.get(c);
			if (parents == null) {
				System.err.println("without parent " + c);
				continue;
			}
					
			for ( Integer p: parents){
				Vector<Integer> a = reverse_categories.get(p);
				if (a!=null)
					siblings.addAll(a);

			}
			siblings.removeAll(intermediate_categories);
			
			count+=siblings.size();
			
			String test = "";
			
			boolean first = true;
			for (Integer s : siblings){
				if (first) {
					first = false;
				} else {
					writer.print("\t");
					test+="\t";
				}
					
				writer.print(s);	
				test+=s;
			}
			
			count2 +=test.split("\t").length;
			writer.println();	
		
		}

		
		System.err.println("numero di categorie " + count);
		System.err.println("numero di categorie2 " + count2);
		writer.close();
		
			
	}
	
	
	public void extractArticlesOfIntermediateCategory() throws FileNotFoundException, UnsupportedEncodingException{
		
		PrintWriter writer = new PrintWriter("data/articlesOfIntermediateCategories.txt", "UTF-8");
		
		int count = 0;
		for ( Integer c: intermediate_categories){
			Vector<Integer> articlesOfCategory = reverse_articles.get(c);
			
			if (articlesOfCategory==null){
				System.err.println("Error: category with no articole in it: " + c);
				continue;
			}
			count+= articlesOfCategory.size();
			boolean first = true;
			for (Integer s : articlesOfCategory){
				if (first) {
					first = false;
				} else {
					writer.print("\t");

				}
					
				writer.print(s);	

			}

			writer.println();	
		
		}

		
		System.err.println("numero di articoli da indicizzare " + count);

		writer.close();
		
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		
		createArticlesToIndex C = new createArticlesToIndex();
		try {
			C.getData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		C.extractArticlesOfIntermediateCategory();
		
	}
	
	
	
	
}
