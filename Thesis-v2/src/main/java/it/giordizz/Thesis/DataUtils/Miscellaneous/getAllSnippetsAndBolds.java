package it.giordizz.Thesis.DataUtils.Miscellaneous;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


/**
 * 
 * @author giordano
 * 
 * Recupera gli snippets e i bolds di ognuna delle 911 query del dataset.
 *
 */

public class getAllSnippetsAndBolds {
	
			  
			  

   public static void main(String[] args) throws IOException {
	   getAllSnippetsAndBolds g = new getAllSnippetsAndBolds();
	   g.createJson2();
   }

	private void createJson2() throws IOException {

	
		
		PrintWriter o = new PrintWriter("boldsAndSnippets_911.JSON");
		BufferedReader br = new BufferedReader(new FileReader("data/911queries.txt"));

		
		o.print("{");
		boolean globalFirst = true;
		
		String query="";

		while( (query=br.readLine())!=null) {
			
		   String queryEncoded =URLEncoder.encode(query,"UTF-8");
		   URL url = new URL("http://ferrax4.itc.unipi.it/smaph/rest/debug?Text=" + queryEncoded);
		   InputStream is = url.openStream();

		   System.err.println(query);
		
		   JSONObject queryResult = (JSONObject) JSONValue.parse(new InputStreamReader(is) );
		
		   JSONObject queryName =  (JSONObject)queryResult.get(query);
		   JSONObject phase1 =  (JSONObject)queryName.get("phase1");
		   JSONObject source1 = (JSONObject)phase1.get("source1");
		
		
		   ArrayList<JSONObject>  bolds = (ArrayList<JSONObject>) source1.get("bolds");
		   ArrayList<JSONObject> snippets = (ArrayList<JSONObject>) source1.get("snippets");
		   
		   if (globalFirst)
			   globalFirst=false;
		   else
			   o.print(",");
			   
		   
		
		   o.print("\""+query+"\":{\"bolds\":[");
		
		   boolean first = true;
		   for (JSONObject bold: bolds){
			   if (first)
				   first=false;
			   else
				   o.print(",");
			   
			   o.print("\"" + ( (String)((JSONObject) bold).get("bold")).replace("\"", "\\\"") +"\"" );
		
		   }
			   
		   o.print("],\"snippets\":[");
		   first = true;
		   for (JSONObject snippet: snippets){
			   if (first)
				   first=false;
			   else
				   o.print(",");
			   
			 
		
			   o.print("\"" + ( (String)((JSONObject) snippet).get("snippet")).replace("\"", "\\\"") +"\"" );
		   }
		   
		   
		   o.print("]}");

		   o.print("}");

		   o.close();

		}
	}
}


//public void createJson() throws IOException{
//
////	String[] qs = new String[]{"debug_no%20result","snippets"};
//	BufferedReader br = new BufferedReader(new FileReader("data/911queries.txt"));
//
//
//	PrintWriter o = new PrintWriter("boldsAndSnippets_911.JSON");
//   
//   
//	o.print("{");
//	boolean globalFirst = true;
//   
//	String query;
//	
//	//scott \& todd in the morning
//	while( (query=br.readLine())!=null) {
//	
//	   String queryEncoded =URLEncoder.encode(query,"UTF-8");
//	   URL url = new URL("http://ferrax4.itc.unipi.it/smaph/rest/debug?Text=" + ());
//	   InputStream is = url.openStream();
////	   URLEncoder.encode("random word �500 bank $", "UTF-8")
//	   System.err.println(query);
//
//	   JSONObject queryResult = (JSONObject) JSONValue.parse(new InputStreamReader(is) );
//
//	   JSONObject queryName =  (JSONObject)queryResult.get(query);
//	   JSONObject phase1 =  (JSONObject)queryName.get("phase1");
//	   JSONObject source1 = (JSONObject)phase1.get("source1");
//
//	
//	   ArrayList<JSONObject>  bolds = (ArrayList<JSONObject>) source1.get("bolds");
//	   ArrayList<JSONObject> snippets = (ArrayList<JSONObject>) source1.get("snippets");
//	   
//	   if (globalFirst)
//		   globalFirst=false;
//	   else
//		   o.print(",");
//		   
//	   
//
//	   o.print("\""+query+"\":{\"bolds\":[");
//
//	   boolean first = true;
//	   for (JSONObject bold: bolds){
//		   if (first)
//			   first=false;
//		   else
//			   o.print(",");
//		   
//		   o.print("\"" + ( (String)((JSONObject) bold).get("bold")).replace("\"", "\\\"") +"\"" );
//
//	   }
//		   
//	   o.print("],\"snippets\":[");
//	   first = true;
//	   for (JSONObject snippet: snippets){
//		   if (first)
//			   first=false;
//		   else
//			   o.print(",");
//		   
//		 
//
//		   o.print("\"" + ( (String)((JSONObject) snippet).get("snippet")).replace("\"", "\\\"") +"\"" );
//	   }
//	   
//	   
//	   o.print("]}");
//	}
//	
//	br.close();
//	o.print("}");
//   
//	o.close();
//}
   
