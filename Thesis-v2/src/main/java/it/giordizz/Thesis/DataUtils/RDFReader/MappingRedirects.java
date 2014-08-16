package it.giordizz.Thesis.DataUtils.RDFReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MappingRedirects {
	static Map<String,String> dic = new HashMap<String, String>();
	
	
	public static void main(String[] args) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader("My_IDs_2"));
		String line;
		while( (line=reader.readLine())!=null) {
			 String[] pair=line.split("\t");
		     dic.put(pair[0],pair[1]);
		}
		
		reader.close();
		
		reader = new BufferedReader(new FileReader("My_article_categories"));
		
		int failed=0;
		while( (line=reader.readLine())!=null) {
			String[] pair=line.split("\t");
			String s1 =dic.get(pair[0]);
			String s2 =dic.get(pair[1]);
			if (s1==null||s2==null){
				if (s1==null) {
					failed++;
				}
				if (s2==null) {
					
						failed++;
					}
				continue;
			}	
		System.out.println(s1 + "\t" + s2);
		    
		}
			System.err.println(failed);
	}
}
