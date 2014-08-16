package it.giordizz.Thesis.DataUtils.Miscellaneous;



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


/**
 * 
 * 
 * @author giordano
 * 
 * Genera il JSON contenente gli snippets e i bolds per le 911 query del dataset.
 *
 */
public class Gen_specificset_similarity_json {




	
	HashSet<String> set= new HashSet<String> ();;
	JSONObject json;
	ArrayList<String> queries = new ArrayList<String>();
	String setname;

	public Gen_specificset_similarity_json(String s) {
		setname=s;
		
		
	}
	public void getStatus() throws IOException{
	
		
		json= (JSONObject)JSONValue.parse(new InputStreamReader(new FileInputStream("data/boldsAndSnippets_911.JSON")));
		
		
		BufferedReader reader2 = new BufferedReader(new FileReader("dataset/"+ setname +"_set"));
		String line;
		while( (line=reader2.readLine())!=null) {
			
			set.add(line);
			
		     //TODO
		}

		reader2.close();
		
		BufferedReader reader3 = new BufferedReader(new FileReader("data/911FinalOrder.txt"));

		while( (line=reader3.readLine())!=null) {
			
			queries.add(line);
			
		     //TODO
		}

		reader3.close();		
		
		
		go();
		System.err.println("************** Status loaded! **************");
	}

	


	
	
	
	public void go() throws IOException{
		System.err.println("************** Start tagging! **************");
		

		int count=0;
		boolean overallFirst=true;
		
		
		JSONArray list = new JSONArray();
		for (String q:queries) {
			
			if (!set.contains(q)){
				continue;
			}
			
			set.remove(q);
			
			JSONObject ob = new JSONObject();
			
			JSONObject query = (JSONObject) json.get(q);
			ArrayList<String> bolds= (ArrayList<String>) query.get("bolds");			
			ArrayList<String> snippets= (ArrayList<String>) query.get("snippets");
			

			
			boolean first = true;
			
			JSONObject fields = new JSONObject();
			ob.put(q, fields);
			
//			JSONObject bold = new JSONObject();
			
			JSONArray boldss = new JSONArray();
			fields.put("bolds",boldss );
			for (String b : bolds) {
				
				boldss.add(b);
			}
			first = true;
			
			JSONArray snippetss = new JSONArray();
			fields.put("snippets",snippetss );
			for (String s : snippets) {
				snippetss.add(s);
				
				
			}
			++count;
			list.add(ob);


			
			System.out.println("query # " +count);
		}
		
        FileWriter file = new FileWriter("data/"+setname+"_similarity.JSON");
      
        file.write(list.toJSONString());
        
        file.flush();
        file.close();
		System.err.println(set.toString());

	}
		
		  

	
	
	public static void main (String args[]) throws IOException{

			
		Gen_specificset_similarity_json r = new Gen_specificset_similarity_json(args[0]);
		r.getStatus();
		


	}



}
