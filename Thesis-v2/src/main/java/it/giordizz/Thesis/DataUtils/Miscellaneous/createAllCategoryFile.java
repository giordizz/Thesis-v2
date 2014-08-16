package it.giordizz.Thesis.DataUtils.Miscellaneous;
	import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

/**
 * 
 * @author giordano
 * 
 * Genera l'insieme degli ID delle categorie di Wikipedia (circa 1M di categorie)
 *
 */
public class createAllCategoryFile {


	TreeSet<Integer> cats = new TreeSet<Integer>();
	
		public void getData() throws IOException{
			GZIPInputStream	gzip2=null;
		
			gzip2 = new GZIPInputStream(new FileInputStream("../dbpedia/wiki-categories-only-links-sorted.gz"));
			BufferedReader br2 = new BufferedReader(new InputStreamReader(gzip2));
			
			String riga;
			while((riga=br2.readLine())!=null) {
				final String[] s=riga.split("\t");
				
				cats.add(Integer.parseInt(s[0]));
				cats.add(Integer.parseInt(s[1]));
			}		
			
			br2.close();
			
		}
		
		
		public void createFile(){
			
			
			for ( Integer c: cats){
				System.out.println(c);				
			}
			System.err.println("numero di cat "+cats.size());
				
		}
		
		public static void main(String[] args){
			
			createAllCategoryFile C = new createAllCategoryFile();
			try {
				C.getData();
			} catch (IOException e) {
				e.printStackTrace();
			}
			C.createFile();
			
		}
		
		
		
		
}


