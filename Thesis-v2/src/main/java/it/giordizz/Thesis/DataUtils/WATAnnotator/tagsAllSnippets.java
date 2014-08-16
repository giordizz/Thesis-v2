package it.giordizz.Thesis.DataUtils.WATAnnotator;


import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



/**
 * 
 * @author giordano
 * 
 * Genera il JSON contenente per ogni query del dataset specificato le entitities;
 *  recuperare taggando la concatenazione degli snippets relativi a quella query.
 *
 */
public class tagsAllSnippets {
	WATAnnotator wikiSense ;

	
	
	
	public tagsAllSnippets() {
		wikiSense = new WATAnnotator("wikisense.mkapp.it", 80, "base", "COMMONNESS", "mw", "", "", false, false, false);
	}
	
	public void foo(String setName, String typeOfText) throws  IOException{
		
		ArrayList<JSONObject> queries= (ArrayList<JSONObject>)JSONValue.parse(new InputStreamReader(new FileInputStream("../data/" + setName + "_similarity.JSON")));
		int countQuery = 0;
		JSONArray list = new JSONArray();
		for ( JSONObject query: queries){
			System.out.println("queries # "+ ++countQuery);
			
			
			String allText = "";
			
			String queryText ="";
			for (Object q : query.entrySet()){
				queryText = ((Map.Entry<String,JSONObject>)  q).getKey();
				 ArrayList<String> texts = (ArrayList<String>) ((JSONObject) ((Map.Entry<String,JSONObject>)  q).getValue()).get(typeOfText);
				 for (String text : texts)
					 allText+=" " + text;
			} 
//			

			JSONObject o = new JSONObject();
			ArrayList<Integer> aux = wikiSense.solve(allText);
			o.put(queryText,aux);
			list.add(o);
			


			

		}
		
		
        FileWriter file = new FileWriter("../Thesis/data/"+setName+"_entitiesSnippets.JSON");
	      
        file.write(list.toJSONString());
        
        file.flush();
        file.close();
	}
	
	public static void main(String[] args) throws IOException {
		
		
		tagsAllSnippets t = new tagsAllSnippets();
		
		t.foo(args[0],"snippets");
	}
	
}



